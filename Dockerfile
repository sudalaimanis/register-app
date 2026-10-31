# ---- Stage 1: build the WAR (no local Java/Maven needed) ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY server/pom.xml server/
COPY webapp/pom.xml webapp/
RUN mvn -q -B dependency:go-offline || true
COPY server server
COPY webapp webapp
RUN mvn -q -B clean package

# ---- Stage 2: runtime ----
FROM tomcat:11.0-jdk17-temurin
# Start from an empty webapps dir: the stock manager/examples apps are not needed
RUN rm -rf /usr/local/tomcat/webapps/* \
 && useradd --system --no-create-home --shell /usr/sbin/nologin appuser \
 && chown -R appuser:appuser /usr/local/tomcat
COPY --from=build --chown=appuser:appuser /build/webapp/target/webapp.war /usr/local/tomcat/webapps/webapp.war
USER appuser
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD curl -fsS http://localhost:8080/webapp/api/registrations || exit 1
