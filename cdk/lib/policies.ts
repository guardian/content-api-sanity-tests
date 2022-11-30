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
                    ],
                    actions: ["s3:GetObject", "s3:ListBucket"]
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