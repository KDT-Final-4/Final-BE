#!/usr/bin/env node
import "source-map-support/register";
import * as path from "path";
import * as dotenv from "dotenv";
import * as cdk from "aws-cdk-lib";
import { InfraStack } from "../lib/infra-stack";

dotenv.config({
  path: path.resolve(__dirname, "..", "..", ".env"),
});

const app = new cdk.App();

new InfraStack(app, "FinalBeInfra", {
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT,
    region: process.env.CDK_DEFAULT_REGION,
  },
});

