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

local setup for coveralls
-------------------------
Run with maven
--------------
Prerequiste: have jdk-7+ installed on your machine
```
javac -version
>javac 1.7.xxx

java -version                                                         
>java version "1.7.xxx"
```

Install maven:
```
sudo apt-get install maven
```

Download leJOS tar.gz
```
wget -nc --no-check-certificate http://sourceforge.net/projects/ev3.lejos.p/files/0.9.1-beta/leJOS_EV3_0.9.1-beta.tar.gz/download -O ./leJOS_EV3_0.9.1-beta.tar.gz
tar -xvf leJOS_EV3_0.9.1-beta.tar.gz
export EV3_HOME=leJOS_EV3_0.9.1-beta
```

TEMPORARY DIRTY HACK to configure your setup
```
Provide your Proxy-Robots IP in the RemoteRobot.java class
Provide the IP of your robot in the robots-ev3-module/pom.xml

Connect RFID to S1
Connect NXT US sensor to S4 (or disable it un RemoteRobot.java)
Connect left motor to C port
Connect right motor to D port
```

Run maven for install (download code on robot) and run code on robot
```
mvn validate -q
mvn install antrun:run
```

To update the coveralls status, export your repo token in the following environment variable:
(You will find it on https://coveralls.io/r/orwell-int/robots-ev3)
```
export COVERALLS_REPO_TOKEN=yourToken
```

To update the coveralls status, export your repo token in the following environment variable:
(You will find it on https://coveralls.io/r/orwell-int/robots-ev3)
```
mvn clean cobertura:cobertura coveralls:report
```

You can also run the jar created by the install to start the application from the command line on the robot itself (by ssh)
```
jrun -cp robots-ev3-module-0.1.0-jar-with-dependencies.jar orwell.tank.RemoteRobot
```