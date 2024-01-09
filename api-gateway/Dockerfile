FROM gradle:latest AS build
WORKDIR /build
COPY . .
RUN ./gradlew build -x test

FROM openjdk:17-oracle
WORKDIR /app
VOLUME /tmp
COPY --from=build /build/build/libs/ychat*SNAPSHOT.jar /app/app.jar
# TODO remove static environment variables in the future
ENV ychat.gateway.redirect.target.auth=https://www.google.de ychat.gateway.redirect.target.social=https://www.google.de ychat.gateway.redirect.target.messaging=https://www.google.de ychat.gateway.redirect.target.notification=https://www.google.de ychat.gateway.redirect.target.payment=https://www.google.de
ENTRYPOINT ["java", "-jar", "app.jar"]