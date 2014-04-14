source config.sh

cd $SDD_PATH
mvn compile
mvn exec:java -Dexec.mainClass="de.metalcon.sdd.Main" -Dexec.args="sdd $SDD_CONFIG/config.xml"
