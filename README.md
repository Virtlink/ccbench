# CCBench

This performs a code completion benchmark test. The inputs are files of the test language (Tiger, WebDSL), with a placeholder somewhere in the file, and the cursor offset of the placeholder to be completed.

For each completion, it measures the time from invocation until getting results. We may or may not have to include the time required for parsing and initial analysis.

## Build
This project depends on several Spoofax 3 projects. Easiest is to build [Spoofax 3][1] and install all its dependencies locally:

```sh
cd devenv/
./gradlew publishAllToMavenLocal
```

> *Note*: By default Gitonium generates the wrong version numbers for snapshot builds, such as `develop-SNAPSHOT`.
> Gradle will not sort them correctly and will let older releases take precedence. Add Gitonium to the `repo.properties`,
> go into the project, and change the version string to something like `10.0.0-$branch-SNAPSHOT`.

To create an installation of this project:

```sh
./gradlew installDist
```

When using Docker, additionally:
```bash
docker build . --tag ccbench-tiger
```

## Run
With `tiger/` the tiger repository,
and `(pwd)/tiger-tests/` the absolute tests directory,
and `(pwd)/tiger-output/` the absolute output directory,
and `ccbench.tiger/build/install/ccbench.tiger/bin/ccbench.tiger` the build script:

### Prepare live

```bash
# In the ccbench source directory
./ccbench.tiger/build/install/ccbench.tiger/bin/ccbench.tiger \
  prepare Tiger \
  -p tiger/org.metaborg.lang.tiger.example/ \
  -o tiger-tests/
```

### Prepare in Docker

```fish
docker run -it --rm \
  -v ~/repos/tiger/:/home/tiger/ \
  -v (pwd)/tiger-tests/:/home/tiger-tests/ \
  -v (pwd)/tiger-output/:/home/tiger-output/ \
  ccbench-tiger \
  prepare Tiger \
  -p /home/tiger/org.metaborg.lang.tiger.example/ \
  -o /home/tiger-tests/
```

### Run live

```bash
# In the ccbench source directory
./ccbench.tiger/build/install/ccbench.tiger/bin/ccbench.tiger \
  run Tiger \
  -p tiger/org.metaborg.lang.tiger.example/ \
  -i tiger-tests/Tiger.yml \
  -o tiger-output/ \
  --seed 123456
```

> Note: Add `-s 100` to run only 100 tests.
>
### Run in Docker

```fish
docker run -t -d \
  -v ~/repos/tiger/:/home/tiger/ \
  -v (pwd)/tiger-tests/:/home/tiger-tests/ \
  -v (pwd)/tiger-output/:/home/tiger-output/ \
  --name ccbench-tiger-run \
  ccbench-tiger \
  run Tiger \
  -p /home/tiger/org.metaborg.lang.tiger.example/ \
  -i /home/tiger-tests/Tiger.yml \
  -o /home/tiger-output/ \
  --seed 123456
docker rm ccbench-tiger-run
```

> Note: Add `-s 100` to run only 100 tests.

To see the logs of the container:

```bash
docker logs -f ccbench-tiger-run
```

## Copy CCBench to remote machine
To copy CCBench to a remote machine:

```bash
# In the ccbench source directory
rsync -av ./ccbench.tiger/build/install/ccbench.tiger/ w2018:ccbench.tiger
```

## Copy Dockerfile to remote machine
To copy this image to a remote machine:

```bash
docker save ccbench-tiger | gzip | pv | ssh w2018 'gunzip | docker load'
```

Or, probably slower:
```bash
docker save ccbench-tiger | bzip2 | pv | ssh w2018 docker load
```

## Clone Tiger Repo
```bash
git clone --depth 1 git@github.com:MetaBorgCube/metaborg-tiger.git tiger
```

## Copy results back to local machine

```bash
scp w2018:tiger-output/Tiger.csv Tiger.csv
```


[1]: git@github.com:metaborg/devenv.git
