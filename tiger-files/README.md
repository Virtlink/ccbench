# Tiger Benchmark
A suite of benchmark files for Tiger, big and small. All files should parse and analyze correctly.

- `examples` - A selection of files from `org.metaborg.lang.tiger.example` that parse and analyze correctly.
- `synthetic` - Synthetic large Tiger files derived from `org.metaborg.lang.tiger.example`.


## Benchmarking
Running the benchmarks consists of:

0.  Building `ccbench.tiger`.
1.  Generating the test cases.
2.  Benchmarking the test cases.
3.  Plotting the results.

Only the last two steps need to be repeated when the implementation changes.

### Building CCBench Tiger
From the repository root directory:

```shell
./gradlew installDist
```

### Generating Test Cases
From the repository root directory:

```shell
./ccbench.tiger/build/install/ccbench.tiger/bin/ccbench.tiger \
  build Tiger \
  -p tiger-files/ \
  -o tiger-tests/
```

### Benchmarking Test Cases
From the repository root directory:

```shell
./ccbench.tiger/build/install/ccbench.tiger/bin/ccbench.tiger \
  run tiger-laptop \
  -p tiger-files/ \
  -i tiger-tests/Tiger.yml \
  -o tiger-results/
  -w 100 \
  --seed 12345
```

### Plotting Results
From the repository root directory:

```shell
./ccbench.tiger/build/install/ccbench.tiger/bin/ccbench.tiger \
  plot \
  -i 202112212121-tiger-laptop.csv \
  -o 202112212121-tiger-laptop.pdf
```
