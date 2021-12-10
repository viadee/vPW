FROM adoptopenjdk/openjdk11:alpine
RUN addgroup -g 1000  -S appuser && adduser -u 1000  -S appuser -G appuser
USER appuser

VOLUME /tmp

ADD target/analyzer.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
