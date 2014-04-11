source config.sh

cd $UMS_PATH
mvn exec:java -Dexec.mainClass="de.metalcon.urlmappingserver.UrlMappingServer" -Dexec.args="$UMS_CONFIG/config.txt"
