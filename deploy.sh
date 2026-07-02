#!/bin/bash

BUCKET_NAME="vacation-planner-apk"
REGION="us-east-2"

echo "Deploying backend files to AWS S3..."

aws s3 sync ./backend s3://$BUCKET_NAME --region $REGION

echo "Deployment complete."