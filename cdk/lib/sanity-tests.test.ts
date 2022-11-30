import { App } from "aws-cdk-lib";
import { Template } from "aws-cdk-lib/assertions";
import { SanityTests } from "./sanity-tests";

describe("The SanityTests stack", () => {
  it("matches the snapshot", () => {
    const app = new App();
    const stack = new SanityTests(app, "SanityTests", { stack: "content-api-sanity-tests", stage: "TEST" });
    const template = Template.fromStack(stack);
    expect(template.toJSON()).toMatchSnapshot();
  });
});
