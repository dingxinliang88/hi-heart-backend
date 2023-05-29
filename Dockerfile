FROM openjdk:11-jdk-slim as builder
# Copy local code to the container image.
WORKDIR /app
VOLUME /tmp

ADD hi-heart-backend-1.0-SNAPSHOT.jar hi-heart-backend.jar

# Run the web service on container startup.
CMD ["java","-jar","/app/hi-heart-backend.jar","--spring.profiles.active=prod"]
