source config.sh

cd $UMS_PATH
mvn compile
mvn exec:java -Dexec.mainClass="de.metalcon.urlmappingserver.UrlMappingServer" -Dexec.args="$UMS_CONFIG/config.txt"
