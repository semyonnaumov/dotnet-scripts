#################################################################
####################### BUILD STAGE #############################
#################################################################
FROM eclipse-temurin:17-jdk as builder

WORKDIR /workspace/app

COPY gradle gradle
COPY gradlew .
COPY settings.gradle .
COPY build.gradle .
COPY src src

RUN ./gradlew clean build
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)


##################################################################
######################### FINAL STAGE ############################
##################################################################
FROM docker:20-dind as final

# Install JDK
RUN apk --no-cache add openjdk17 \
    --repository="https://dl-cdn.alpinelinux.org/alpine/edge/community" && \
    java --version

ARG DEPENDENCY=/workspace/app/build/dependency

# Copy app
COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app

# Copy launcher
COPY --chown=root docker /app

RUN ["chmod", "+x", "/app/run.sh"]
RUN ["chmod", "+x", "/app/run-java.sh"]

# Env variables for java launcher
ENV     CPU_CORES 4
ENV     JAVA_XMS 256m
ENV     JAVA_XMX 4G

ENTRYPOINT ["/app/run.sh"]