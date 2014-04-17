metalcon
========

Copy config.sample.sh to config.sh and set your variables.
in particular `METALCON_PREFIX=` to the working space in which your metalcon repositories are placed.

run
* `./installServices.sh` - installs all enabled components

Open `supervisord.conf` and change the directories of your components and also change the password for the buildin webserver.
run 
* `supervisord -c supervisord.conf`

If you have to install supervisord don't use the version from your linux distro but rather use `sudo easy_install supervisord` which comes with python.  
You might have to install easy_install for your python version:

    sudo apt-get install python-setuptools

In order to see the servics you are running go to [localhost:9001](localhost:9001)

* `./bootstrap.sh` - starts and imports data to all enabled components


