FROM gradle:8.13-jdk21-alpine AS builder
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
COPY --from=builder /app/entrypoint.sh entrypoint.sh
RUN chmod +x ./entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["./entrypoint.sh"]
