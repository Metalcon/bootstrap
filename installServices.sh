configFile="config.sh"

if [ ! -e "$configFile" ]
then
	echo "configuration file not found: '$configFile'"
	echo "edit 'sample-config.sh' to match your needs and do"
	echo "cp sample-config.sh $configFile"
	exit
fi

source $configFile

# install static data delivery server
if $SDD_ENABLED; then
  pushd $SDD_PATH
  ./install.sh
  popd > /dev/null
fi

# install URL mapping server
if $UMS_ENABLED; then
  pushd $UMS_PATH
  ./install.sh
  popd > /dev/null
fi

# install image gallery server
if $IGS_ENABLED; then
  pushd $IGS_PATH
  ./install.sh
  popd > /dev/null
fi

# install like button server
if $LIKE_ENABLED; then
  pushd $LIKE_PATH
  ./install.sh
  popd > /dev/null
fi

# install uid stuff
pushd $UID_PATH
./install.sh
popd > /dev/null

echo "directory for log files is \"$LOG_DIR\""
if [ ! -e "$LOG_DIR" ]
then
	echo "directory not present, creating..."
	sudo mkdir -p $LOG_DIR
fi
