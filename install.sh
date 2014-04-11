source config.sh

# Install sdd
sudo mkdir -p $SDD_CONFIG
sudo chown -R $RIGHTS $SDD_CONFIG
cp sddConfig.xml $SDD_CONFIG/config.xml
rm -rf $SDD_CONFIG/{leveldb,neo4j}

# Install ums
sudo mkdir -p $UMS_CONFIG
sudo chown -R $RIGHTS $UMS_CONFIG
cp umsConfig.txt $UMS_CONFIG/config.txt
rm -rf $UMS_CONFIG/db
