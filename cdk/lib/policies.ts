import {GuPolicy} from "@guardian/cdk/lib/constructs/iam";
import {GuStack} from "@guardian/cdk/lib/constructs/core";
import {Effect, PolicyStatement} from "aws-cdk-lib/aws-iam";

export function Policies(scope:GuStack) {
    return [
        new GuPolicy(scope, "PrivateBucketAccess", {
            statements: [
                new PolicyStatement({
                    effect: Effect.ALLOW,
                    resources: [
                        "arn:aws:s3::*:content-api-sanity-tests-dist/*",
                        "arn:aws:s3::*:content-api-es-snapshots/*",
                        `arn:aws:s3::*:content-api-config/content-api-sanity-tests/${scope.stage}/sanity-tests/*`
                    ],
                    actions: ["s3:GetObject", "s3:ListBucket"]
                })
            ]
        }),
        new GuPolicy(scope, "CloudwatchPolicy", {
            statements: [
                new PolicyStatement({
                    effect: Effect.ALLOW,
                    resources: [
                        "*"
                    ],
                    actions: [
                        "cloudwatch:PutMetricData"
                    ]
                })
            ]
        }),
        new GuPolicy(scope, "EC2Access", {
            statements: [
                new PolicyStatement({
                    effect: Effect.ALLOW,
                    actions: [
                        "ec2:DescribeInstances",
                        "autoscaling:DescribeAutoScalingGroups",
                        "autoscaling:DescribeAutoScalingInstances",
                    ],
                    resources: ["*"]
                })
            ]
        })
    ]
}