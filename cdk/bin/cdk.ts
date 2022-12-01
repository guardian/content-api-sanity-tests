import "source-map-support/register";
import { App } from "aws-cdk-lib";
import { SanityTests } from "../lib/sanity-tests";

const app = new App();
new SanityTests(app, "SanityTests-PROD", { stack: "content-api-sanity-tests", stage: "PROD" });
new SanityTests(app, "SanityTests-CODE", { stack: "content-api-sanity-tests", stage: "CODE" });
