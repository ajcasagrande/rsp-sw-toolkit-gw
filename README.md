# Gateway

The features and functionality included in this reference design are 
intended to showcase the capabilities of the Intel® RFID Sensor Platform (Intel® RSP) 
by demonstrating the use of the API to collect and process RFID tag read information.  

##### _THIS SOFTWARE IS NOT INTENDED TO BE A COMPLETE END-TO-END INVENTORY MANAGEMENT SOLUTION._  

The Gateway is a Java application built with Gradle. As such, it can run on any OS that supports 
a Java Runtime Environment version 8 or greater. The following instructions will get you a copy 
of the project up and running on your local machine for development and testing purposes.  

#### Getting Started Windows

Instructions for build and installation in a Windows environment can be found in the 
[Installation & User's Guide](docs/338443-002_Intel-RSP-SW-Toolkit-Gateway.pdf)

#### Getting Started Linux (recommended)
The following instructions assume an Ubuntu 18.04 installation.


##### Download (Linux)
```bash
#-- install development dependencies
sudo apt-get install default-jdk git gradle

#-- install runtime dependencies
sudo apt-get install mosquitto mosquitto-clients avahi-daemon ntp ssh

#-- create expected directories for the use case examples and documentation
mkdir -p ~/projects

#-- clone the reference source code
cd ~/projects
git clone https://github.com/intel/rsp-sw-toolkit-gw.git
```

##### Build / Deploy (Linux)
```bash
cd ~/projects/rsp-sw-toolkit-gw

#-- the gradle build script will trigger a full build
#-- and a local deployment to the directory ~/deploy/rsp-sw-toolkit-gw 
#-- with the following command
gradle clean deploy

#-- !!!! CERTIFICATES ARE NEEDED !!!!
#-- the sensors connect securely to the gateway using a self signed certificate.
#-- a convenience script will generate these credentials in order to get the
#-- reference deployment up and running quickly.
mkdir -p ~/deploy/rsp-sw-toolkit-gw/cache
cd ~/deploy/rsp-sw-toolkit-gw/cache
~/deploy/rsp-sw-toolkit-gw/gen_keys.sh
```

##### Run (Linux)
A shell script is provided to start the application in the foreground. 
```bash
cd ~/deploy/rsp-sw-toolkit-gw
~/deploy/rsp-sw-toolkit-gw/run.sh
```
Connect an Intel&reg; RFID Sensor Platforms on the same network segment as the gateway. 
The sensor will listen for a gateway announcement and initiate a connection. 
As the senor's connect, tthe gateway will schedule them to read RFID tags in sequence
(one at a time).

##### Monitor

###### --- Web Admin http://localhost:8080/web-admin/
The gateway provides a web based administration interface for configuration and monitoring. 
The home page is a dashboard presentint status of several components 
of the gateway including the number of sensors connected and a summary
of the number of tags being read.

###### --- Command Line Interface
The gateway provides a command line interface (CLI) for configuration and monitoring. 
To view the supported commands and parameters, use the tab completion feature that is included.
To view command help, enter the command and press enter.

Connect to the gateway's CLI and explore as follows.
```bash
ssh -p5222 gwconsole@localhost
password: gwconsole
    
#-- view sensor information 
rfid-gw> sensor show
------------------------------------------
device     connect      reading    behavior  facility    personality  aliases

RSP-150000 CONNECTED    STOPPED    Default   SalesFloor               [RSP-150000-0, RSP-150000-1, RSP-150000-2, RSP-150000-3]
RSP-150004 CONNECTED    STOPPED    Default   SalesFloor  EXIT         [RSP-150004-0, RSP-150004-1, RSP-150004-2, RSP-150004-3]
RSP-150005 CONNECTED    STOPPED    Default   BackStock                [RSP-150005-0, RSP-150005-1, RSP-150005-2, RSP-150005-3]
------------------------------------------

#-- view inventory information
rfid-gw> inventory summary 
------------------------------------------
- Total Tags: 26

- State
      26 PRESENT

- Last Seen
      26 within_last_01_min
------------------------------------------
```

###### --- MQTT
Open a terminal window and subscribe to the gateway events topic in order to monitor 
tag events as produced by the gateway.

```bash
#-- monitor the upstream events topic
mosquitto_sub -t rfid/gw/events
```

# Use Cases
Explore the reference use cases that are included.  
[Retail](examples/use-cases/retail)
