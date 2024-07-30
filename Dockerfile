FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY ./target/*.jar /app/baoounao.jar

EXPOSE 80

CMD ["java", "-jar", "baoounao.jar"]
