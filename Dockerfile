FROM adoptopenjdk/openjdk11

RUN apt-get update \
 && apt-get install --no-install-recommends --yes \
    bash \
    git \
 && rm -rf /var/lib/apt/lists/*

SHELL ["/bin/bash", "-c"]

WORKDIR "/home/"

# Requires:
#     ./gradlew installDist
COPY ccbench.tiger/build/install/tiger.spoofax3.ccbench/ ccbench/

ENTRYPOINT ["ccbench/bin/tiger.spoofax3.ccbench"]
