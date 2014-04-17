# (re-)install and start services
./installServices.sh

# wait until services started
echo "press ENTER when services running"
read nothing

# import data
mvn clean compile exec:java
