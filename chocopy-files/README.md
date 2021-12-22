# ChocoPy Benchmark
A suite of benchmark files for ChocoPy, big and small. All files should parse and analyze correctly.


- `pa1` - All `.py` files from [`cs164berkeley/pa1-chocopy-parser`](
https://github.com/cs164berkeley/pa1-chocopy-parser/)`/src/test/data/pa1/`.
- `pa2` - All `.py` files from [`cs164berkeley/pa2-chocopy-semantic-analysis`](https://github.com/cs164berkeley/pa2-chocopy-semantic-analysis)`/src/test/data/pa2/`.
- `pa3` - All `.py` files from [`cs164berkeley/pa3-chocopy-code-generation`](https://github.com/cs164berkeley/pa3-chocopy-code-generation)`/src/test/data/pa3/`.


## Benchmarking
Running the benchmarks consists of:

0.  Building `ccbench.chocopy`.
1.  Generating the test cases.
2.  Benchmarking the test cases.
3.  Plotting the results.

Only the last two steps need to be repeated when the implementation changes.

### Building CCBench ChocoPy
From the repository root directory:

```shell
./gradlew installDist
```

### Generating Test Cases
From the repository root directory:

```shell
./ccbench.chocopy/build/install/ccbench.chocopy/bin/ccbench.chocopy \
  build ChocoPy \
  -p chocopy-files/ \
  -o chocopy-tests/
```

### Benchmarking Test Cases
From the repository root directory:

```shell
./ccbench.chocopy/build/install/ccbench.chocopy/bin/ccbench.chocopy \
  run chocopy-laptop \
  -p chocopy-files/ \
  -i chocopy-tests/ChocoPy.yml \
  -o chocopy-results/
  -w 100 \
  --seed 12345
```

### Plotting Results
From the repository root directory:

```shell
./ccbench.chocopy/build/install/ccbench.chocopy/bin/ccbench.chocopy \
  plot \
  -i 202112212121-chocopy-laptop.csv \
  -o 202112212121-chocopy-laptop.pdf
```
