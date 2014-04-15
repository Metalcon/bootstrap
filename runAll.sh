source config.sh

# open services in new terminals
if [ $SDD_ENABLED ]
then
  ./runSdd.sh &
fi

if [ $UMS_ENABLED ]
then
  ./runUms.sh &
fi