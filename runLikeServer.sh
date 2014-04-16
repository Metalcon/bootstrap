source config.sh

cd $LIKE_PATH
mvn compile
mvn exec:java -Dexec.args=""

