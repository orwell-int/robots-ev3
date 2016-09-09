[![Build Status](https://travis-ci.org/orwell-int/robots-ev3.svg?branch=master)](https://travis-ci.org/orwell-int/robots-ev3) [![Stories in Ready](https://badge.waffle.io/orwell-int/robots-ev3.png?label=ready&title=Ready)](https://waffle.io/orwell-int/robots-ev3) [![Coverage Status](https://coveralls.io/repos/orwell-int/robots-ev3/badge.svg?branch=master)](https://coveralls.io/r/orwell-int/robots-ev3?branch=master)
robots-ev3
============

This is the code running on the EV3 robots in the ORWELL project.

Checkout the code
-----------------
Get the sources
```
git clone git@github.com:orwell-int/robots-ev3.git
```

Get the submodules
```
git submodule update --init --recursive
```

Prerequiste: have jdk-7+ installed on your machine
```
javac -version
>javac 1.7.xxx

java -version                                                         
>java version "1.7.xxx"
```

Run with maven
--------------
Install maven
```
sudo apt-get install maven
```

Download leJOS tar.gz
```
wget -nc --no-check-certificate http://sourceforge.net/projects/ev3.lejos.p/files/0.9.1-beta/leJOS_EV3_0.9.1-beta.tar.gz/download -O ./leJOS_EV3_0.9.1-beta.tar.gz
tar -xvf leJOS_EV3_0.9.1-beta.tar.gz
export EV3_HOME=leJOS_EV3_0.9.1-beta
```

Configure your hardware setup
```
Copy the robots-ev3-module/src/main/resources/tank.defaults.ini to tank.config.ini
Edit it so as to reflect your own hardware and proxy configuration
Copy it on your EV3 in ${brick.home} (should be /home/root if you did not edit the pom.xml)
```

Edit robots-ev3-module/pom.xml
```
Change the value of <brick.host>YOUR_EV3_IP_HERE</brick.host>
```

Run maven for install (upload code on robot) and run code on robot
```
mvn validate -q
mvn install antrun:run
```

Run without maven, once you have the jar
----------------------------------------
You can also run the jar created by the install to start the application from the command line on the robot itself (through ssh)
Provided you already copied the tank.config.ini and the jar on the target
```
jrun -cp robots-ev3-module-0.1.0-jar-with-dependencies.jar orwell.tank.RemoteRobot -f tank.config.ini
```