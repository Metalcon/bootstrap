source config.sh

cd $SDD_PATH
mvn compile
mvn exec:java -Dexec.mainClass="de.metalcon.sdd.StaticDataDelivery" -Dexec.args="$SDD_CONFIG/config.xml"
