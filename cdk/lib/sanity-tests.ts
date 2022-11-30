import type {GuStackProps} from "@guardian/cdk/lib/constructs/core";
import {GuLoggingStreamNameParameter, GuParameter, GuStack} from "@guardian/cdk/lib/constructs/core";
import type {App} from "aws-cdk-lib";
import {aws_ssm, Stack} from "aws-cdk-lib";
import {GuEc2App} from "@guardian/cdk";
import {InstanceClass, InstanceSize, InstanceType, Peer, Vpc} from "aws-cdk-lib/aws-ec2";
import {AccessScope} from "@guardian/cdk/lib/constants";
import fs from "fs";
import {GuVpc} from "@guardian/cdk/lib/constructs/ec2";
import {useArmInstance} from "./constants";

export class SanityTests extends GuStack {
  constructor(scope: App, id: string, props: GuStackProps) {
    super(scope, id, props);

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

    const guApp = new GuEc2App(this, {
      access: {
        scope: AccessScope.INTERNAL,
        cidrRanges: [Peer.ipv4("10.0.0.0/8")],
      },
      app: "sanity-tests",
      applicationPort: 9000,
      certificateProps: {
        domainName: this.stage=="CODE" ? "sanity-tests.capi.code.dev-gutools.co.uk" : "sanity-tests.capi.gutools.co.uk",
        hostedZoneId: hostedZone,
      },
      instanceType: InstanceType.of(useArmInstance ? InstanceClass.T4G : InstanceClass.T3, InstanceSize.MICRO),
      monitoringConfiguration: {
        noMonitoring: true,
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
