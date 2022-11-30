import type {GuStackProps} from "@guardian/cdk/lib/constructs/core";
import {GuLoggingStreamNameParameter, GuParameter, GuStack} from "@guardian/cdk/lib/constructs/core";
import type {App} from "aws-cdk-lib";
import {aws_ssm, Duration, Stack} from "aws-cdk-lib";
import {GuEc2App} from "@guardian/cdk";
import {InstanceClass, InstanceSize, InstanceType, Peer, Vpc} from "aws-cdk-lib/aws-ec2";
import {AccessScope} from "@guardian/cdk/lib/constants";
import fs from "fs";
import {GuVpc} from "@guardian/cdk/lib/constructs/ec2";
import {cloudwatchMetricNamespace, useArmInstance} from "./constants";
import {Policies} from "./policies";
import {Alarm, ComparisonOperator, Metric, Statistic} from "aws-cdk-lib/aws-cloudwatch";
import {SnsAction} from "aws-cdk-lib/aws-cloudwatch-actions";
import {Topic} from "aws-cdk-lib/aws-sns";

export class SanityTests extends GuStack {
  constructor(scope: App, id: string, props: GuStackProps) {
    super(scope, id, props);

    const urgentAlarmTopicArn = aws_ssm.StringParameter.fromStringParameterName(this, "urgent-alarm-arn", "/account/content-api-common/alarms/urgent-alarm-topic");
    const nonUrgentAlarmTopicArn = aws_ssm.StringParameter.fromStringParameterName(this, "non-urgent-alarm-arn", "/account/content-api-common/alarms/non-urgent-alarm-topic");

    const vpcId = aws_ssm.StringParameter.valueForStringParameter(this, this.getVpcIdPath());
    const vpc = Vpc.fromVpcAttributes(this, "vpc", {
      vpcId: vpcId,
      availabilityZones: ["eu-west-1a","eu-west-1b" ,"eu-west-1c"]
    });

    const subnetsList = new GuParameter(this, "subnets", {
      description: "Subnets to deploy into",
      default: this.getDeploymentSubnetsPath(),
      fromSSM: true,
      type: "List<String>"
    });
    const deploymentSubnets = GuVpc.subnets(this, subnetsList.valueAsList);

    const hostedZone = aws_ssm.StringParameter.valueForStringParameter(this, `/account/services/capi.gutools/${this.stage}/hostedzoneid`);

    const userDataRaw = fs.readFileSync("./instance-startup.sh").toString('utf-8');
    const userData = userDataRaw
        .replace(/\$\{Stage}/g, this.stage)
        .replace(/\$\{Stack}/g, this.stack)
        .replace(/\$\{LoggingKinesisStream}/g, GuLoggingStreamNameParameter.getInstance(this).valueAsString)
        .replace(/\$\{AWS::Region}/g, Stack.of(this).region);

    new GuEc2App(this, {
      access: {
        scope: AccessScope.INTERNAL,
        cidrRanges: [Peer.ipv4("10.0.0.0/8")],
      },
      app: "sanity-tests",
      roleConfiguration: {
        additionalPolicies: Policies(this),
      },
      applicationPort: 9000,
      certificateProps: {
        domainName: this.stage=="CODE" ? "sanity-tests.capi.code.dev-gutools.co.uk" : "sanity-tests.capi.gutools.co.uk",
        hostedZoneId: hostedZone,
      },
      instanceType: InstanceType.of(useArmInstance ? InstanceClass.T4G : InstanceClass.T3, InstanceSize.MICRO),
      monitoringConfiguration: {
        snsTopicName: urgentAlarmTopicArn.stringValue,
        http5xxAlarm: false,
        unhealthyInstancesAlarm: true,
      },
      privateSubnets: deploymentSubnets,
      publicSubnets: deploymentSubnets,
      scaling: {
        minimumInstances: 1,
        maximumInstances: 2,
      },
      userData: userData,
      vpc,
    });

    const alarm = new Alarm(this, "NotEnoughSuccessfulTestsAlarm", {
      actionsEnabled: true,
      alarmDescription: 'Fewer than 100 tests in 5 minutes (we expect to run at least 14 tests every 30 seconds)',
      alarmName: `content-api-sanity-tests-${this.stage}-alarm-not-enough-successful-tests`,
      evaluationPeriods: 5,
      comparisonOperator: ComparisonOperator.LESS_THAN_THRESHOLD,
      metric: new Metric({
        period: Duration.minutes(2),  //the CDK is quite prescriptive about the allowed durations
        metricName: "SuccessfulTests",
        namespace: cloudwatchMetricNamespace,
        statistic: Statistic.SUM,
      }),
      threshold: 100
    });
    const alarmTopic = Topic.fromTopicArn(this, "AlarmTopic", nonUrgentAlarmTopicArn.stringValue);
    alarm.addAlarmAction(new SnsAction(alarmTopic));
  }

  getAccountPath(elementName: string) {
    const basePath = "/account/vpc";
    if(this.stack.includes("preview")) {
      return this.stage=="CODE" ? `${basePath}/CODE-preview/${elementName}` : `${basePath}/PROD-preview/${elementName}`;
    } else {
      return this.stage=="CODE" ? `${basePath}/CODE-live/${elementName}` : `${basePath}/PROD-live/${elementName}`;
    }
  }

  getVpcIdPath() {
    return this.getAccountPath("id");
  }

  getDeploymentSubnetsPath() {
    return this.getAccountPath("subnets")
  }
}
