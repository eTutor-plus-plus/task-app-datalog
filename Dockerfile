# Optimize JAR
FROM eclipse-temurin:21-jre AS builder
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# Build final image
FROM eclipse-temurin:21-jre
EXPOSE 8081

# Copy layered JAR
WORKDIR /app
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./
COPY ./bin/dlv-linux.bin /usr/bin/dlv

# Spring
ENV SPRING_PROFILES_ACTIVE=prod
ENV DATALOG_EXE=/usr/bin/dlv

ENTRYPOINT ["java", "-Xmx6g", "org.springframework.boot.loader.launch.JarLauncher"]
