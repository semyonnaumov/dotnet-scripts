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

RUN ./gradlew clean build -x test
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)


##################################################################
######################### FINAL STAGE ############################
##################################################################
FROM eclipse-temurin:17-jdk as final

ARG DEPENDENCY=/workspace/app/build/dependency

# Copy app
COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app

# Copy launcher
COPY --chown=root docker /app

RUN ["chmod", "+x", "/app/run.sh"]

# Env variables for java launcher
ENV     CPU_CORES 4
ENV     JAVA_XMS 256m
ENV     JAVA_XMX 4G

ENTRYPOINT ["/app/run.sh"]