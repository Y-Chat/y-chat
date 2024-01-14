#!/bin/zsh

# Social DB
kubectl apply -f social-db/social-db-kube/social-db-config.yaml
kubectl apply -f social-db/social-db-kube/social-db-config.yaml
kubectl apply -f social-db/social-db-kube/social-db-config.yaml
kubectl apply -f social-db/social-db-kube/social-db-secret.yaml
kubectl apply -f social-db/social-db-kube/social-db-deploy.yaml
kubectl apply -f social-db/social-db-kube/social-db-service.yaml

# pgAdmin
kubectl apply -f social-db/pgadmin-kube/pgadmin-config.yaml
kubectl apply -f social-db/pgadmin-kube/pgadmin-secret.yaml
kubectl apply -f social-db/pgadmin-kube/pgadmin-deploy.yaml
kubectl apply -f social-db/pgadmin-kube/pgadmin-service.yaml
