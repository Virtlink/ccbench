#!/usr/bin/env bash
set -o errexit
set -o pipefail
set -o noclobber
set -o nounset
set -o xtrace

# Get the script directory
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd "$DIR"

# Build and Copy CCBench
echo "Building CCBench"
./gradlew installDist
echo "Copying CCBench"
rsync -av ./ccbench.chocopy/build/install/ccbench.chocopy/ w2019:ccbench.chocopy

# Copy the source project and generated tests
echo "Copying source project"
rsync -av ~/repos/ccbench-chocopy/ w2019:chocopy-benchmark/ \
  --exclude .git \
  --exclude results

echo "Done!"

# Execute the tests using:
#./ccbench.chocopy/bin/ccbench.chocopy \
#  run chocopy-w2019 \
#  -p chocopy-benchmark/project/ \
#  -i chocopy-benchmark/cases/ChocoPy.yml \
#  -o chocopy-benchmark/results/ \
#  -w 100 \
#  --seed 12345
