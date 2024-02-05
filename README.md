# Y-Chat
## About
Y-Chat is the university project of Benedikt Strobel, Ben Riegel, and Niklas Mamtschur as part of the practical course "Large Scale Software Architectures: Analysis, Design, and Implementation" at the Technical University of Munich.
The objective of this project is to plan, develop, and implement the architecture of a messaging app from scratch with a special focus on learning as much as possible about large-scale software architectures.
## Responsibilities
- Ben Riegel: Frontend, Kafka Service, Payment Service, Deployment, Media Service, Auth Service
- Benedikt Strobel: Message Service/DB, API Gateway, Notification Service, Calling Service, Calling Frontend
- Niklas Mamtschur: Social Service/DB

## Local Dev Setup
### Prerequisites
- Make sure you are **running on linux or macOS**
- Install docker and docker compose.
- The following services require the firebase credentials at ```src/main/resources/firebase-service-credentials.json```
  - Notification Service
  - Media Service
### Usage
- Run ```docker compose up``` to start the entire Y-chat ecosystem. The frontend will be exposed on [localhost:3000](http://localhost:3000/)
- To develop a service locally
    - Comment out the service in the docker-compose.yml
    - Make sure to edit the redirect target for the service in the api-gateway environment of the docker-compose.yml
