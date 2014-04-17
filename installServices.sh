configFile="config.sh"

if [ ! -e "$configFile" ]
then
	echo "File not found: $configFile"
	echo "Try 'cp config.{sample.sh,sh}'"
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
