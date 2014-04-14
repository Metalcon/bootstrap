source config.sh

cd $UMS_PATH
mvn clean exec:java -Dexec.args="$UMS_CONFIG/config.txt"
