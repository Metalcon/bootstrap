DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $DIR
source config.sh

cd $UMS_PATH
mvn clean compile exec:java -Dexec.args="$UMS_CONFIG/config.txt"
