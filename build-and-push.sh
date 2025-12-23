#!/usr/bin/env bash
set -euo pipefail

echo "AWS ECS 이미지 빌드 및 푸시 스크립트 (Unix)"
echo "사용법: .env 파일에 AWS_ACCOUNT_ID와 AWS_REGION을 설정하고 ./build-and-push.sh 실행"

if [[ ! -f .env ]]; then
  cat <<'EOF'
Error: .env file not found.
Please create .env file with the following content:
  AWS_ACCOUNT_ID=123456789012
  AWS_REGION=ap-northeast-2
  ECR_REPOSITORY_NAME=final-be (optional)
  IMAGE_TAG=latest (optional)
EOF
  exit 1
fi

set -o allexport
source .env
set +o allexport

trim() {
  local var="$1"
  var="${var#"${var%%[![:space:]]*}"}"
  var="${var%"${var##*[![:space:]]}"}"
  printf '%s' "$var"
}

AWS_ACCOUNT_ID="$(trim "${AWS_ACCOUNT_ID:-}")"
AWS_REGION="$(trim "${AWS_REGION:-}")"
ECR_REPOSITORY_NAME="$(trim "${ECR_REPOSITORY_NAME:-}")"
IMAGE_TAG="$(trim "${IMAGE_TAG:-}")"

if [[ -z "${AWS_ACCOUNT_ID}" ]]; then
  echo "Error: AWS_ACCOUNT_ID is required in .env file"
  exit 1
fi

AWS_REGION="${AWS_REGION:-ap-northeast-2}"
ECR_REPOSITORY_NAME="${ECR_REPOSITORY_NAME:-final-be}"
IMAGE_TAG="${IMAGE_TAG:-latest}"
ECR_REPOSITORY_URI="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
ECR_REPOSITORY_URI="${ECR_REPOSITORY_URI//[[:space:]]/}"
ECR_REPOSITORY_URI="${ECR_REPOSITORY_URI}/${ECR_REPOSITORY_NAME}"

echo "=========================================="
echo "Building Docker image..."
echo "Repository: ${ECR_REPOSITORY_URI}"
echo "Tag: ${IMAGE_TAG}"
echo "=========================================="

docker build -t "${ECR_REPOSITORY_NAME}:${IMAGE_TAG}" .

echo "Logging in to Amazon ECR..."
aws ecr get-login-password --region "${AWS_REGION}" | docker login --username AWS --password-stdin "${ECR_REPOSITORY_URI}"

if ! aws ecr describe-repositories --repository-names "${ECR_REPOSITORY_NAME}" --region "${AWS_REGION}" > /dev/null 2>&1; then
  echo "Creating ECR repository: ${ECR_REPOSITORY_NAME}"
  aws ecr create-repository --repository-name "${ECR_REPOSITORY_NAME}" --region "${AWS_REGION}"
fi

echo "Tagging image..."
docker tag "${ECR_REPOSITORY_NAME}:${IMAGE_TAG}" "${ECR_REPOSITORY_URI}:${IMAGE_TAG}"

echo "Pushing image to ECR..."
docker push "${ECR_REPOSITORY_URI}:${IMAGE_TAG}"

echo "=========================================="
echo "Successfully pushed image to ECR!"
echo "Image URI: ${ECR_REPOSITORY_URI}:${IMAGE_TAG}"
echo "=========================================="

