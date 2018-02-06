#!/usr/bin/env bash

export AWS_DEFAULT_REGION="ap-northeast-1"
TMP_CREDENTIALS=$(aws sts get-session-token --duration-seconds 900) # request temporary credentials for 15 minutes

# uses jq (https://stedolan.github.io/jq/)
export AWS_ACCESS_KEY_ID=$(echo ${TMP_CREDENTIALS} | jq -r '.Credentials.AccessKeyId')
export AWS_SECRET_ACCESS_KEY=$(echo ${TMP_CREDENTIALS} | jq -r '.Credentials.SecretAccessKey')
export AWS_SESSION_TOKEN=$(echo ${TMP_CREDENTIALS} | jq -r '.Credentials.SessionToken')

docker run --rm \
	-e AWS_DEFAULT_REGION=$AWS_DEFAULT_REGION \
	-e AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID \
	-e AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY \
	-e AWS_SESSION_TOKEN=$AWS_SESSION_TOKEN \
	-v "$PWD/build/docker":/var/task lambci/lambda:java8 "$@"
