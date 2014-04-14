source config.sh

cd $SDD_PATH
mvn clean compile exec:java -Dexec.mainClass="de.metalcon.sdd.StaticDataDelivery" -Dexec.args="$SDD_CONFIG/config.xml"
