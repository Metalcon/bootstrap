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
  if [ ! -e $SDD_PATH ]
  then
    echo "[ERROR] static data delivery server is missing"
    echo "you can pull the server via"
    echo "git clone https://github.com/Metalcon/staticDataDeliveryServer.git"
  fi
  
  pushd $SDD_PATH
  ./install.sh
  popd > /dev/null
fi

# install URL mapping server
if $UMS_ENABLED; then
  if [ ! -e $UMS_PATH ]
  then
    echo "[ERROR] URL mapping server is missing"
    echo "you can pull the server via"
    echo "git clone https://github.com/Metalcon/urlMappingServer.git"
  fi

  pushd $UMS_PATH
  ./install.sh
  popd > /dev/null
fi

# install image gallery server
if $IGS_ENABLED; then
  if [ ! -e $IGS_PATH ]
  then
    echo "[ERROR] image gallery server is missing"
    echo "you can pull the server via"
    echo "git clone https://github.com/Metalcon/imageGalleryServer.git"
  fi
  
  pushd $IGS_PATH
  ./install.sh
  popd > /dev/null
fi

# install like button server
if $LIKE_ENABLED; then
  if [ ! -e $LIKE_PATH ]
  then
    echo "[ERROR] like button server is missing"
    echo "you can pull the server via"
    echo "git clone https://github.com/Metalcon/likeButtonServer.git"
  fi

  pushd $LIKE_PATH
  ./install.sh
  popd > /dev/null
fi

# install UID service
if [ ! -e $UID_PATH ]
then
  echo "[ERROR] MUID service is missing"
  echo "you can pull it via"
  echo "git clone https://github.com/Metalcon/muid.git"
fi

pushd $UID_PATH
./install.sh
popd > /dev/null

echo "directory for log files is \"$LOG_DIR\""
if [ ! -e "$LOG_DIR" ]
then
	echo "directory not present, creating..."
	sudo mkdir -p $LOG_DIR
fi
