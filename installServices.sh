source config.sh

# install static data delivery server
if [ $SDD_ENABLED ]
then
  sudo mkdir -p $SDD_CONFIG
  sudo chown -R $RIGHTS $SDD_CONFIG
  cp sddConfig.xml $SDD_CONFIG/config.xml
  # reset databases
  rm -rf $SDD_CONFIG/{leveldb,neo4j}
fi

# install URL mapping server
if [ $UMS_ENABLED ]
then
  sudo mkdir -p $UMS_CONFIG
  sudo chown -R $RIGHTS $UMS_CONFIG
  cp umsConfig.txt $UMS_CONFIG/config.txt
  # reset gallery database
  rm -rf $UMS_CONFIG/db
fi
