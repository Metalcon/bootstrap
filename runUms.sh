source config.sh

cd $UMS_PATH
mvn clean compile exec:java -Dexec.args="$UMS_CONFIG/config.txt"
