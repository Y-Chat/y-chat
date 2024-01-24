#!/bin/zsh

# Social DB
kubectl apply -f social-db/social-db-deploy/social-db-pv.yaml
kubectl apply -f social-db/social-db-deploy/social-db-pvc.yaml
kubectl apply -f social-db/social-db-deploy/social-db-config.yaml
kubectl apply -f social-db/social-db-deploy/social-db-secret.yaml
kubectl apply -f social-db/social-db-deploy/social-db-deploy.yaml
kubectl apply -f social-db/social-db-deploy/social-db-service.yaml

# pgAdmin
kubectl apply -f social-db/pgadmin-deploy/pgadmin-config.yaml
kubectl apply -f social-db/pgadmin-deploy/pgadmin-secret.yaml
kubectl apply -f social-db/pgadmin-deploy/pgadmin-deploy.yaml
kubectl apply -f social-db/pgadmin-deploy/pgadmin-service.yaml
