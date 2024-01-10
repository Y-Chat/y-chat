# Y-Chat
## About
Y-Chat is the university project of Benedikt Strobel, Ben Riegel, and Niklas Mamtschur as part of the practical course "Large Scale Software Architectures: Analysis, Design, and Implementation" at the Technical University of Munich.
The objective of this project is to plan, develop, and implement the architecture of a messaging app from scratch with a special focus on learning as much as possible about large-scale software architectures.
## Responsibilities
- Ben Riegel: Frontend, Kafka Service, Payment Service
- Benedikt Strobel: Message Service/DB, API Gateway, Notification Service
- Niklas Mamtschur: Social Service/DB, Media Service, Auth Service
- Everyone: Docker/Kubernetes

## Local Dev Setup
### Prerequisites
- Make sure you are **running on linux or macOS**
- Install docker and docker compose and make sure the services are running.
### Usage
- Run ```docker compose up```
- To develop a service locally
    - Comment out the service in the docker-compose.yml
    - Make sure to edit the redirect target for the service in the api-gateway environment of the docker-compose.yml

## Local Kubernetes Setup
Install Docker Desktop and enable Kubernetes in the settings.

kubectl apply -f mongo-config.yaml
kubectl apply -f mongo-secret.yaml
kubectl apply -f mongo.yaml
kubectl apply -f webapp.yaml
