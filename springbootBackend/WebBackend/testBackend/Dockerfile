FROM openjdk:17-jdk

WORKDIR /app4

COPY target/*.jar app4.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app4.jar"]
