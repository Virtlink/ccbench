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
echo "Building CCBench Tiger"
./gradlew installDist
echo "Copying CCBench Tiger"
rsync -av ./ccbench.tiger/build/install/ccbench.tiger/ w2019:ccbench.tiger

# Copy the source project and generated tests
echo "Copying Tiger source project"
rsync -av ~/repos/ccbench-tiger/ w2019:tiger-benchmark/ \
  --exclude .git \
  --exclude results

echo "Done!"

# Execute the tests using:
# JAVA_OPTS="-Xss16M -Xms512M -Xmx4G -XX:ActiveProcessorCount=4" \
# ./ccbench.tiger/bin/ccbench.tiger \
#   run tiger-4g-w2019 \
#   -p tiger-benchmark/project/ \
#   -i tiger-benchmark/cases/Tiger.yml \
#   -o tiger-benchmark/results/ \
#   -w 100 \
#   --seed 12345
