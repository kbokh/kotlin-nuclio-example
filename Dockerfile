ARG NUCLIO_LABEL=1.6.6
ARG NUCLIO_ARCH=amd64
ARG NUCLIO_BASE_IMAGE=openjdk:11-jre-slim
ARG NUCLIO_ONBUILD_IMAGE=quay.io/nuclio/handler-builder-java-onbuild:${NUCLIO_LABEL}-${NUCLIO_ARCH}

# Supplies processor, handler.jar
FROM ${NUCLIO_ONBUILD_IMAGE} as builder

# Supplies uhttpc, used for healthcheck
FROM nuclio/uhttpc:0.0.1-amd64 as uhttpc

# From the base image
FROM ${NUCLIO_BASE_IMAGE}

# Copy required objects from the suppliers
COPY --from=builder /home/gradle/bin/processor /usr/local/bin/processor
COPY --from=builder /home/gradle/src/wrapper/build/libs/nuclio-java-wrapper.jar /opt/nuclio/nuclio-java-wrapper.jar
COPY --from=uhttpc /home/nuclio/bin/uhttpc /usr/local/bin/uhttpc

# Readiness probe
HEALTHCHECK --interval=1s --timeout=3s CMD /usr/local/bin/uhttpc --url http://127.0.0.1:8082/ready || exit 1

# Run processor with configuration and platform configuration
CMD [ "processor" ]