source config.sh

# open services in new terminals
if [ $SDD_ENABLED ]
then
  gnome-terminal -x ./runSdd.sh
fi

if [ $UMS_ENABLED ]
then
  gnome terminal -x ./runUms.sh
fi