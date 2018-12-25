FROM maven:3.6-jdk-11 AS build-env
WORKDIR /build
COPY . /build
RUN mvn package -Dmaven.test.skip

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build-env /build/target/maven-repo.jar /app
ENV JAVA_OPTS=""
VOLUME /app/maven
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app/maven-repo.jar" ]