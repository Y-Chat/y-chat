# Y-Chat
## Context
Y-Chat is a messanger app and emerged from a university project as part of the course "Large Scale Software Architectures" at the Technical University of Munich.<br />
The objective of this project was to plan, develop, and implement the architecture of an application from scratch with a special focus on high scalability.<br />

## Production Environment
The deployed production environment is available at [y-chat.net](https://y-chat.net)<br />
Feel free to sign up  & test the app there!<br />

## Table of contents
[Main Responsibilities](#Main-Responsibilities)<br />
[Features](#Features)<br />
[Project Structure](#Project-Structure)<br />
[How to run](#How-to-run)<br />
[Deployment](#Deployment)<br />

## Main Responsibilities
- Ben Riegel: Frontend, Kafka Service, Payment Service, Deployment, Media Service, Auth Service
- Benedikt Strobel: Message Service, API Gateway, Notification Service & Frontend, Calling Service & Frontend
- Niklas Mamtschur: Social Service

## Features

Essentials features:
- Account and settings
- Groups
- Contacts
- Messages

Optional fetures:

- Calling
- Offline Web Push Notifications

Not implemented optional feature:

- Payment

## Documentation

![ComponentDiagram](https://github.com/Y-Chat/y-chat/assets/50946590/4c80076d-9bde-4fd6-aece-8b473efa2b11)

### How to run
#### Prerequisites
- Install docker and docker compose.

#### Run Locally
If you simply want to run the Y-Chat front- and backend locally, run ```docker compose up``` in the root directory of the repository. After pulling all services (linux/amd64 and linux/arm64) from DockerHub, the frontend will be exposed on [localhost:3000](http://localhost:3000/)

#### Develop Locally
The following services **require firebase credentials** (which are not checked in for security reasons) at ```src/main/resources/firebase-service-credentials.json```
    - Notification Service
    - Media Service
    - Social Service

Run ```docker compose -f docker-compose-build.yml up``` to start the entire Y-chat ecosystem. The frontend will be exposed on [localhost:3000](http://localhost:3000/)

To develop a service locally:
- Stop the api gateway services and all services you want to develop locally
- Start the api gateway and all services you want to develop locally
    - For Java services: Run ```./gradlew bootRun --args='--spring.profiles.active=dev'```
    - For the Frontend service: Run ```npm install``` and ```npm start```

### Deployment
To save GitHub Action free minutes, front- and backend are **NOT** automatically deployed when new features are merged into the main branch. To trigger a deployment, execute the corresponding GitHub action manually:
- [Deploy Backend](https://github.com/Y-Chat/y-chat/actions/workflows/deploy-backend.yml)
- [Deploy Frontend](https://github.com/Y-Chat/y-chat/actions/workflows/deploy-frontend.yml)

After the backend containers have been built and pushed to [DockerHub](https://hub.docker.com/u/mrsarius), the deployments on Google Kubernetes Engine (GKE) will recognise the changes and gradually roll out the updated containers to its pods.

The backend is available at [https://api.y-chat.net](https://api.y-chat.net/).

The frontend is deployed using [firebase hosting](https://firebase.google.com/docs/hosting) and is available at [https://y-chat.net](https://y-chat.net/). 


### Project Structure

#### API Gateway
Language: Java<br />
Framework: Springboot<br />
Database: -<br />

Purpose: Serving as single point of entry into the backend system. Forwards all calls to the appropriate backend services
#### Calling Service
[OpenAPI Documentation](https://rest.wiki/?https://raw.githubusercontent.com/Y-Chat/y-chat-api/767f878a714dda90555bd6b3aa822452be97a15d/calling/openapi.yml)<br />
Language: Java<br />
Framework: Springboot<br />
Database: MongoDB<br />

Purpose: Handles establishing, accepting & denying calls. Handles signaling between call participants.
#### Media Service
[OpenAPI Documentation](https://rest.wiki/?https://raw.githubusercontent.com/Y-Chat/y-chat-api/767f878a714dda90555bd6b3aa822452be97a15d/media/openapi.yml)<br />
Language: Java<br />
Framework: Springboot<br />
"Database": Firebase/Google Cloud Storage<br />

Purpose: Handles uploading files to cloud storage container. Handles authentification/signing of links to media files
#### Messaging Service
[OpenAPI Documentation](https://rest.wiki/?https://raw.githubusercontent.com/Y-Chat/y-chat-api/767f878a714dda90555bd6b3aa822452be97a15d/messaging/openapi.yml)<br />
Language: Java<br />
Framework: Springboot<br />
Database: MongoDB<br />

Purpose: Handles messaging logic and storage
#### Notification Service
[OpenAPI Documentation](https://rest.wiki/?https://raw.githubusercontent.com/Y-Chat/y-chat-api/767f878a714dda90555bd6b3aa822452be97a15d/notification/openapi.yml)<br />
Language: Java<br />
Framework: Springboot<br />
Database: MongoDB, Firestore<br />

Purpose: Handles Server -> Client Communication. Using Firebase Cloud Messaging for offline notifications. Using Firestore to persist "online" events (when client is active). Clients then use Firestore subscriptions to be notified of new events.
#### Social Service
[OpenAPI Documentation](https://rest.wiki/?https://raw.githubusercontent.com/Y-Chat/y-chat-api/767f878a714dda90555bd6b3aa822452be97a15d/social/openapi.yml)<br />
Language: Java<br />
Framework: Springboot<br />
Database: Postgres<br />

Purpose: Handles user account information, relationships between users and groups.
#### Frontend<br />
Language: Java/Typescript<br />
Framework: ReactJS<br />

Purpose: Handles connection to backend services. And serves as visual interface to provide the chat app functionality.<br />
#### Api Specs
Language: OpenAPI / Swagger<br />

Purpose: Specifies all outward and inward facing APIs. These specifications are used by the frontend to generate wrappers to talk to the backend services and are used by backend services to generate wrappers to talk to other backend services.<br />
#### Kubernetes (k8s)
Purpose: Contains the kubernetes [deployment files](https://github.com/Y-Chat/y-chat/tree/main/k8s).<br />



