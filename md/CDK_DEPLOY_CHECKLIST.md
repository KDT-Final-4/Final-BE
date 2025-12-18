## 태스크 수 설정 방법

### 방법 1: 기본값 사용 (권장)
기본값이 이미 **1**로 설정되어 있으므로, 별도 설정 없이 배포하면 됩니다:

```bash
cd infra
npx cdk deploy
```

### 방법 2: 명시적으로 파라미터 전달
태스크 수를 명시적으로 지정하려면:

```bash
cd infra
npx cdk deploy --parameters EcsDesiredCount=1
```

### 방법 3: 여러 태스크 실행
고가용성을 위해 여러 태스크를 실행하려면:

```bash
cd infra
npx cdk deploy --parameters EcsDesiredCount=2
```

## 배포 전 필수 확인 사항

### 1. 환경 변수 설정

프로젝트 루트의 `.env` 파일 또는 배포 시 환경 변수 설정:

```bash
# 필수
BACKEND_IMAGE_URI=123456789012.dkr.ecr.ap-northeast-2.amazonaws.com/final-be:latest

# 선택사항 (기본값 사용 가능)
JWT_SECRET=your-secret-key
PYTHON_URL=http://your-python-url
FRONT_URL=https://your-vercel-app.vercel.app
```

### 2. AWS 자격 증명 확인

```bash
aws sts get-caller-identity
```

### 3. CDK Bootstrap 확인

```bash
cd infra
npx cdk bootstrap aws://$AWS_ACCOUNT_ID/$AWS_REGION
```

## 배포 명령어

### 기본 배포 (태스크 수 1개)

```bash
cd infra

# Windows (CMD)
set BACKEND_IMAGE_URI=123456789012.dkr.ecr.ap-northeast-2.amazonaws.com/final-be:latest
set JWT_SECRET=your-secret-key
set PYTHON_URL=http://your-python-url
set FRONT_URL=https://your-vercel-app.vercel.app
npx cdk deploy

# Linux/Mac
export BACKEND_IMAGE_URI="123456789012.dkr.ecr.ap-northeast-2.amazonaws.com/final-be:latest"
export JWT_SECRET="your-secret-key"
export PYTHON_URL="http://your-python-url"
export FRONT_URL="https://your-vercel-app.vercel.app"
npx cdk deploy
```

### 태스크 수 명시적 지정

```bash
# 태스크 수 1개
npx cdk deploy --parameters EcsDesiredCount=1

# 태스크 수 2개 (고가용성)
npx cdk deploy --parameters EcsDesiredCount=2

# 태스크 중지 (비용 절감)
npx cdk deploy --parameters EcsDesiredCount=0
```

## 배포 후 확인

### 1. 스택 상태 확인

```bash
aws cloudformation describe-stacks \
  --stack-name FinalBeInfra \
  --query "Stacks[0].StackStatus" \
  --region ap-northeast-2
```

### 2. ECS 서비스 상태 확인

```bash
aws ecs describe-services \
  --cluster Cluster \
  --services BE-Service \
  --region ap-northeast-2 \
  --query "services[0].{desiredCount:desiredCount,runningCount:runningCount}"
```

### 3. 태스크 수 확인

```bash
aws ecs list-tasks \
  --cluster Cluster \
  --service-name BE-Service \
  --region ap-northeast-2 \
  --query "taskArns | length(@)"
```

