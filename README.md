# CCBench

This performs a code completion benchmark test. The inputs are files of the test language (Tiger, WebDSL), with a placeholder somewhere in the file, and the cursor offset of the placeholder to be completed.

For each completion, it measures the time from invocation until getting results. We may or may not have to include the time required for parsing and initial analysis.

## Build
This project depends on several Spoofax 3 projects. Easiest is to include the [Spoofax 3][1] `devenv` as an included build when building on the command line:

```sh
./gradlew --include-build ../devenv build 
```

To create an installation of this project:

```sh
./gradlew --include-build ../devenv installDist
```

Or add this line to `settings.gradle.kts`:

```kotlin
includeBuild("../devenv")
```


## Run

### Copy files to Remote Server

```bash
# Copy ccbench.tiger
# - in the ccbench source directory:
./gradlew installDist
rsync -av ./ccbench.tiger/build/install/ccbench.tiger/ w2019:ccbench.tiger

# Copy the source project
# - in the Tiger repo directory:
rsync -av ./tiger-benchmark/ w2019:tiger-benchmark/

# (Optionally) copy the test files
# - in the directory where the test files where generated
rsync -av ./tiger-tests/ w2019:tiger-tests/

# On webdsl-2018:

# (Optionally) build tests
# - in the ccbench source directory
./ccbench.tiger/bin/ccbench.tiger \
  build Tiger \
  -p tiger-benchmark/ \
  -o tiger-tests/

# Run tests
# - in the ccbench source directory
./ccbench.tiger/bin/ccbench.tiger \
  run tiger-w2019-fast-nodelay-nodistinct \
  -p tiger-benchmark/ \
  -i tiger-tests/Tiger.yml \
  -o tiger-results/ \
  -w 100 \
  --seed 12345
  
# Plot the results
/Users/daniel/repos/spoofax3/ccbench/ccbench.tiger/build/install/ccbench.tiger/bin/ccbench.tiger plot \
  -i 20211112173932-tiger-w2019-fast-nodelay-nodistinct.csv \
  -i 20211111173327-tiger-w2019-fast.csv \
  -o 20211112-tiger-w2019-distinct-vs-nodistict.pdf
```

> Note: Add `-s 100` to run only 100 tests.

## Clone Tiger Repo
```bash
git clone --depth 1 git@github.com:MetaBorgCube/metaborg-tiger.git tiger
```

## Copy results back to local machine

```bash
scp w2019:tiger-results/20211109204451-tiger-w2019.\* .
```

## How to make a language ready for code completion?

- Add rules matching explicit placeholders

    ```
    rules
      typeOfExp(_, Exp-Plhdr()) = _.
    ```
    
- Do *not* add catch-all rules. The following rules interfere with code completion:

    ```
    typeOfExp(_, _) = UNDEFINED() :- try { false } | warning $[This is not yet implemented]. 
    ```

  (This is what happens in WebDSL Statix spec.)


## Profiling in IntelliJ
To profile, use a profiler on a _Kotlin Run Configuration_ with the following settings:

- Module: `ccbench.ccbench.tiger.main`
- Class: `mb.ccbench.tiger.MainKt`
- Program Arguments:
  ```
  run tiger-laptop \
    -p /Users/daniel/repos/tiger/tiger-benchmark/ \
    -i /Users/daniel/repos/tiger/tiger-tests/Tigercopy.yml \
    -o /Users/daniel/repos/tiger/tiger-results/ \
    --seed 12345 -s 1
  ```



[1]: git@github.com:metaborg/devenv.git
