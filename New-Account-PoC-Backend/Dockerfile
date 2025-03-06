# 选择 JDK 作为基础镜像
FROM openjdk:17-jdk-slim
WORKDIR /app
# 复制 Spring Boot 可执行 JAR 文件
COPY target/New-Account-PoC-Backend-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
# 运行 Spring Boot 应用
ENTRYPOINT ["java", "-jar", "app.jar"]