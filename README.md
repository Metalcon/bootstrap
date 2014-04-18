metalcon
========

Copy config.sample.sh to config.sh and set your variables. In particular set `METALCON_PREFIX` to 
the directory where all metalcon repositories should be placed.

Next step is to run

     ./installServices.sh
 
to insall all components, that have been configured in the config.sh file.

For each service additional configuration is necessary. In particular 

    mvn compile

should be executed for each java serive.


Open `supervisord.conf` and change the directories of your components and also change the password for the buildin webserver.
run 
* `supervisord -c supervisord.conf`

If you have to install supervisord don't use the version from your linux distro but rather use

    sudo easy_install supervisor
    
which comes with python.  
You might have to install easy_install for your python version:

    sudo apt-get install python-setuptools

In order to see the servics you are running go to [localhost:9001](localhost:9001)

* `./bootstrap.sh` - starts and imports data to all enabled components


