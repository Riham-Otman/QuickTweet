#!/bin/bash

docker buildx build --platform linux/amd64 --push \
  --tag csci3130projectregistry.azurecr.io/spring-backend:latest \
  -f Dockerfile .