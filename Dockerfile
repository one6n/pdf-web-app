FROM eclipse-temurin:11
EXPOSE 8080
RUN mkdir /opt/app
COPY ./target/pdf-web-app-0.0.1.jar /opt/app
CMD ["java", "-jar", "/opt/app/pdf-web-app-0.0.1.jar"]