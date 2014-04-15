DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $DIR
source config.sh

cd $SDD_PATH
mvn clean compile exec:java -Dexec.args="sdd $SDD_CONFIG/config.xml"
