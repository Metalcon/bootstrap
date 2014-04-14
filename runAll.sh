source config.sh

# open services in new terminals
if [ $USE_SDD ]
  then gnome-terminal -x ./runSdd.sh
fi

if [ $USE_UMS ]
  then gnome terminal -x ./runUms.sh
fi