FROM gradle:latest AS build
WORKDIR /parent/build

# Needs to be first so these layers can be reused
# Installing node because both need to be present for running the generators
ENV NODE_VERSION=18.16.0
RUN apt install -y curl
RUN curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
ENV NVM_DIR=/root/.nvm
RUN . "$NVM_DIR/nvm.sh" && nvm install ${NODE_VERSION}
RUN . "$NVM_DIR/nvm.sh" && nvm use v${NODE_VERSION}
RUN . "$NVM_DIR/nvm.sh" && nvm alias default v${NODE_VERSION}
ENV PATH="/root/.nvm/versions/node/v${NODE_VERSION}/bin/:${PATH}"
RUN node --version
RUN npm --version
# Done installing
COPY ./package.json ./
COPY ./package-lock.json ./
WORKDIR /parent
COPY ../openapi.yml ./
COPY ../asyncapi.yml ./
WORKDIR /parent/build
RUN npm install
RUN npm run generate
# All layers above this should be cached and only be redone when the package.json/package-lock.json/openapi.yml/asyncapi.yml change
COPY . .


RUN ./gradlew build -x test

FROM openjdk:17-oracle
# openjdk:17-alpine does not exist for M1 macs!
WORKDIR /app
VOLUME /tmp
COPY --from=build /parent/build/build/libs/test*SNAPSHOT.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]