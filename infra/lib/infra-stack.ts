import {
  aws_cloudwatch as cloudwatch,
  aws_ec2 as ec2,
  aws_ecs as ecs,
  aws_elasticloadbalancingv2 as elbv2,
  aws_iam as iam,
  aws_rds as rds,
  aws_secretsmanager as secretsmanager,
  Duration,
  RemovalPolicy,
  Stack,
  StackProps,
  CfnParameter,
} from "aws-cdk-lib";
import { Construct } from "constructs";

export class InfraStack extends Stack {
  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);

    const desiredCountParam = new CfnParameter(this, "EcsDesiredCount", {
      type: "Number",
      default: 1,
      description:
        "Set to 0 to keep BE tasks stopped and enter 2 or more when you want to restart them.",
    });
    const desiredCount = Math.max(0, desiredCountParam.valueAsNumber);

    const namePrefixParam = new CfnParameter(this, "ResourcePrefix", {
      type: "String",
      default: "Prod",
      description:
        "Prefix to prepend to resource names (e.g., Test, Prod) so test and production environments are clearly differentiated.",
    });
    const resourcePrefix = namePrefixParam.valueAsString;

    const backendImageUri =
      process.env.BACKEND_IMAGE_URI || this.node.tryGetContext("backendImageUri");
    if (!backendImageUri) {
      throw new Error(
        "BACKEND_IMAGE_URI must be provided via env or CDK context."
      );
    }

    // Additional environment variables for backend
    const jwtSecret =
      process.env.JWT_SECRET || this.node.tryGetContext("jwtSecret") || "default-jwt-secret-change-in-production";
    const pythonUrl =
      process.env.PYTHON_URL || this.node.tryGetContext("pythonUrl") || "";
    const frontUrl =
      process.env.FRONT_URL || this.node.tryGetContext("frontUrl") || "";

    const vpc = new ec2.Vpc(this, "Vpc", {
      maxAzs: 2,
      natGateways: 1,
      subnetConfiguration: [
        {
          name: "public",
          subnetType: ec2.SubnetType.PUBLIC,
        },
        {
          name: "private",
          subnetType: ec2.SubnetType.PRIVATE_WITH_EGRESS,
        },
      ],
    });

    const cluster = new ecs.Cluster(this, "Cluster", {
      vpc,
      clusterName: "Cluster",
    });

    const albSG = new ec2.SecurityGroup(this, "AlbSecurityGroup", {
      vpc,
      description: "ALB security group that permits internet traffic",
      allowAllOutbound: true,
    });
    albSG.addIngressRule(ec2.Peer.anyIpv4(), ec2.Port.tcp(80), "Allow HTTP");

    const backendSG = new ec2.SecurityGroup(this, "BackendServiceSecurityGroup", {
      vpc,
      description: "Permits ALB and RDS access to the backend",
      allowAllOutbound: true,
    });
    backendSG.addIngressRule(albSG, ec2.Port.tcp(80), "ALB to BE");

    const dbSG = new ec2.SecurityGroup(this, "DatabaseSecurityGroup", {
      vpc,
      description: "Allows backend ECS to connect to the Oracle RDS instance",
      allowAllOutbound: true,
    });
    dbSG.addIngressRule(backendSG, ec2.Port.tcp(1521), "BE to Oracle");

    const dbSecret = new secretsmanager.Secret(this, "RdsAdminSecret", {
      description: "Admin credentials for connecting to the RDS instance",
      generateSecretString: {
        secretStringTemplate: JSON.stringify({ username: "finaladmin" }),
        generateStringKey: "password",
        excludePunctuation: true,
        passwordLength: 16,
      },
    });

    const dbSubnetGroup = new rds.SubnetGroup(this, "DatabaseSubnetGroup", {
      description: "Subnet group for the private RDS deployment",
      removalPolicy: RemovalPolicy.SNAPSHOT,
      vpc,
      vpcSubnets: { subnetType: ec2.SubnetType.PRIVATE_WITH_EGRESS },
    });

    const dbInstance = new rds.DatabaseInstance(this, "RdsInstance", {
      engine: rds.DatabaseInstanceEngine.oracleEe({
        version: rds.OracleEngineVersion.VER_19,
      }),
      instanceType: ec2.InstanceType.of(ec2.InstanceClass.BURSTABLE3, ec2.InstanceSize.SMALL),
      credentials: rds.Credentials.fromSecret(dbSecret),
      multiAz: false,
      allocatedStorage: 20,
      maxAllocatedStorage: 100,
      publiclyAccessible: false,
      vpc,
      vpcSubnets: { subnetType: ec2.SubnetType.PRIVATE_WITH_EGRESS },
      securityGroups: [dbSG],
      databaseName: "finalbe",
      removalPolicy: RemovalPolicy.DESTROY,
      deletionProtection: false,
      subnetGroup: dbSubnetGroup,
      storageEncrypted: true,
      port: 1521,
    });

    const logPrefix = "final-be";

    // Execution Role for ECR image pull
    const executionRole = new iam.Role(this, "EcsTaskExecutionRole", {
      assumedBy: new iam.ServicePrincipal("ecs-tasks.amazonaws.com"),
      managedPolicies: [
        iam.ManagedPolicy.fromAwsManagedPolicyName(
          "service-role/AmazonECSTaskExecutionRolePolicy"
        ),
      ],
    });
    // Grant ECR permissions
    executionRole.addToPolicy(
      new iam.PolicyStatement({
        effect: iam.Effect.ALLOW,
        actions: ["ecr:GetAuthorizationToken"],
        resources: ["*"],
      })
    );
    executionRole.addToPolicy(
      new iam.PolicyStatement({
        effect: iam.Effect.ALLOW,
        actions: ["ecr:BatchCheckLayerAvailability", "ecr:GetDownloadUrlForLayer", "ecr:BatchGetImage"],
        resources: ["*"],
      })
    );

    const backendTask = new ecs.FargateTaskDefinition(
      this,
      "BackendTaskDef",
      {
        cpu: 1024,
        memoryLimitMiB: 2048,
        executionRole: executionRole,
      }
    );
    const backendContainer = backendTask.addContainer("BackendContainer", {
      image: ecs.ContainerImage.fromRegistry(backendImageUri),
      logging: ecs.LogDrivers.awsLogs({
        streamPrefix: `${logPrefix}-backend`,
      }),
      environment: {
        SPRING_DATASOURCE_URL: `jdbc:oracle:thin:@${dbInstance.dbInstanceEndpointAddress}:${dbInstance.dbInstanceEndpointPort}/finalbe`,
        SPRING_DATASOURCE_USERNAME: "finaladmin",
        SPRING_DOCKER_COMPOSE_ENABLED: "false",
        SPRING_DOCKER_COMPOSE_SKIP_IN_BACKGROUND: "true",
        JWT_SECRET: jwtSecret,
        PYTHON_URL: pythonUrl,
        FRONT_URL: frontUrl,
      },
      secrets: {
        SPRING_DATASOURCE_PASSWORD: ecs.Secret.fromSecretsManager(dbSecret, "password"),
      },
    });
    backendContainer.addPortMappings({ containerPort: 8080 });

    const backendService = new ecs.FargateService(this, "BE-Service", {
      cluster,
      taskDefinition: backendTask,
      assignPublicIp: false,
      desiredCount,
      securityGroups: [backendSG],
      vpcSubnets: { subnetType: ec2.SubnetType.PRIVATE_WITH_EGRESS },
      healthCheckGracePeriod: Duration.seconds(60),
      minHealthyPercent: 100,
      maxHealthyPercent: 200,
    });
    const backendCfn = backendService.node.defaultChild as ecs.CfnService;
    backendCfn.deploymentConfiguration = {
      deploymentCircuitBreaker: {
        enable: true,
        rollback: true,
      },
      maximumPercent: 200,
      minimumHealthyPercent: 100,
    };

    const alb = new elbv2.ApplicationLoadBalancer(
      this,
      "ApplicationLoadBalancer",
      {
        vpc,
        internetFacing: true,
        securityGroup: albSG,
        loadBalancerName: "ALB",
      }
    );

    const listener = alb.addListener("HttpListener", {
      protocol: elbv2.ApplicationProtocol.HTTP,
      port: 80,
      open: true,
    });

    // Backend target group as default (all traffic goes to backend)
    const backendTargetGroup = listener.addTargets("BackendTargetGroup", {
      port: 80,
      targets: [
        backendService.loadBalancerTarget({
          containerName: "BackendContainer",
          containerPort: 8080,
        }),
      ],
      healthCheck: {
        path: "/actuator/health",
        interval: Duration.seconds(30),
      },
    });

    const backend5xxAlarm = new cloudwatch.Alarm(this, "BackendFiveXXAlarm", {
      metric: backendTargetGroup.metric("HTTPCode_Target_5XX_Count", {
        period: Duration.minutes(1),
        statistic: "sum",
      }),
      threshold: 1,
      evaluationPeriods: 1,
      treatMissingData: cloudwatch.TreatMissingData.NOT_BREACHING,
      alarmDescription: "API 오류가 감지되면 자동 롤백 트리거",
    });

    // AI batch jobs will run via ECS tasks later; Lambda and event targets removed for now.
  }
}

