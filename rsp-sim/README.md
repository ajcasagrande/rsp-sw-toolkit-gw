# rsp-sim
A simple simulator intended to generate dummy data for Gateway testing.

This tool is intended to aid the Gateway developer by providing simulated Intel® RFID Sensor Platform (Intel® RSP) messaging in order to demonstrate the use of the API to collect and process RFID tag read information. **_THIS TOOL DOES NOT IMPLEMENT THE ENTIRE API._**


## Install the Intel® RSP Software Toolkit - Gateway

Follow the instructions here (https://github.com/intel/rsp-sw-toolkit-gw/) to install and run the Gateway.


## Execute the RSP Simulator script

Instead of connecting real hardware (i.e. "Connect an Intel® RFID Sensor Platform on the same network segment as the gateway."), us the "rsp-sim.sh" script and the files created by the "generate-tags-file.sh" script to simulate a number of Intel® RSP's with RFID tags in view.  Tags can be virtually moved around the deployment by moving the EPC entry from on tag file to the other.


### Install Dependencies

Additional dependencies need to be installed in order to use the RSP simulation script.
```
:~$ sudo apt install mosquitto-clients
```


### Create Tag Files

In order to simulate the presence and location of RFID tags, the use the "generate-tags-file.sh" script.  This script produces text files containing the specified number of SGTIN96 encoded EPC values.  For help, execute the script with no arguments to see usage information.

```
:~$ ./generate-tags-file.sh 

This script generates the tags_in_view_of_sensor_ files used with
the Intel RFID Sensor Platform simulation script (sensor-sim.sh).

Usage: generate-tags-file.sh <index> <start> <count>
where...
<index> is the RSP index between 0 and 99 (0-based index)
<start> is the starting point between 0 and (9999 - <count>)
<count> is the number of unique EPC's between 0 and (9999 - <base>)

The script produces the ouput file - tags_in_view_of_rsp_<index>
```

Decide up front how many RSP's you would like to simulate and create tag files for those RSP's you would like read tags from.  For example, if you want to simulate five RSP's and want ten tags in front of the first, fourth and fifth RSP, use the "generate-tags-file.sh" script at RSP indexes 0, 3 and 4 as follows...

```
:~$ ./generate-tags-file.sh 0 0 10
Storing output in tags_in_view_of_rsp_0
:~$ ./generate-tags-file.sh 3 10 10
Storing output in tags_in_view_of_rsp_3
:~$ ./generate-tags-file.sh 4 20 10
Storing output in tags_in_view_of_rsp_4
:~$ 
```


### Run the Simulator

To simulate the presence of RSP's, execute the "rsp-sim.sh" script.  For help, execute the script with no arguments to see usage information.

```
:~$ ./rsp-sim.sh 

This script simulates the API messages between the specified number of
Intel RFID Sensor Platforms (RSP) and the Intel RSP SW Toolkit - Gateway.

Usage: rsp-sim.sh <count>
where <count> is the number of RSP's to simulate.

This script depends on the mosquitto-clients package being installed.
Run 'sudo apt install mosquitto-clients' to install.

NOTE: The Intel RSP SW Toolkit - Gateway must be running BEFORE
      attempting to execute this script.

:~$ ./rsp-sim.sh 5
Creating default rsp data...
Press CTRL-C to disconnect.
```


## Use the Gateway Command Line Interface (CLI)

Use the Gateway CLI to control the simulated RSP's just as you would the real hardware devices.

The following example demonstrates the setup of the five RSP devices in two facilities (sales-floor and back-stock).  Of the three RSP's on the sales-floor, one is configured to monitor an EXIT and one is configured as a Point-of-Sale (POS) device.


```
:~$ ssh -p5222 gwconsole@localhost
Password authentication
Password: gwconsole

RFID Gateway console session

<tab> to view available commands
'clear' to clear the screen/console
'quit' to end

rfid-gw> sensor show
--------------------------------------------------------------------------------------------
device     connect      reading    behavior                  facility           personality

RSP-150000 CONNECTED    STOPPED    Default                   UNKNOWN            
RSP-150001 CONNECTED    STOPPED    Default                   UNKNOWN            
RSP-150002 CONNECTED    STOPPED    Default                   UNKNOWN            
RSP-150003 CONNECTED    STOPPED    Default                   UNKNOWN            
RSP-150004 CONNECTED    STOPPED    Default                   UNKNOWN            
--------------------------------------------------------------------------------------------
rfid-gw> sensor set.facility sales-floor RSP-150000 RSP-150001 RSP-150002
--------------------------------------------------------------------------------------------
RSP-150000 - true
RSP-150001 - true
RSP-150002 - true
--------------------------------------------------------------------------------------------
rfid-gw> sensor set.facility back-stock RSP-150003 RSP-150004
--------------------------------------------------------------------------------------------
RSP-150003 - true
RSP-150004 - true
--------------------------------------------------------------------------------------------
rfid-gw> sensor set.personality EXIT RSP-150001
--------------------------------------------------------------------------------------------
set.personality : OK
RSP-150001 CONNECTED    STOPPED    Default                   sales-floor        EXIT
--------------------------------------------------------------------------------------------
rfid-gw> sensor set.personality POS RSP-150002
--------------------------------------------------------------------------------------------
set.personality : OK
RSP-150002 CONNECTED    STOPPED    Default                   sales-floor        POS
--------------------------------------------------------------------------------------------
rfid-gw> scheduler activate.all.on 
--------------------------------------------------------------------------------------------
completed
--------------------------------------------------------------------------------------------
rfid-gw> sensor show
--------------------------------------------------------------------------------------------
device     connect      reading    behavior                  facility           personality

RSP-150000 CONNECTED    STARTED    ClusterAllOn_PORTS_1              sales-floor        
RSP-150001 CONNECTED    STARTED    ClusterAllOn_PORTS_1              sales-floor        EXIT
RSP-150002 CONNECTED    STARTED    ClusterAllOn_PORTS_1              sales-floor        POS
RSP-150003 CONNECTED    STARTED    ClusterAllOn_PORTS_1              back-stock         
RSP-150004 CONNECTED    STARTED    ClusterAllOn_PORTS_1              back-stock         
--------------------------------------------------------------------------------------------
rfid-gw> inventory detail
--------------------------------------------------------------------------------------------
30143639F8419145BEEF0000, 0, P, RSP-150000-0, 00:00:00:259, sales-floor
30143639F8419145BEEF0001, 0, P, RSP-150000-0, 00:00:00:244, sales-floor
30143639F8419145BEEF0002, 0, P, RSP-150000-0, 00:00:00:235, sales-floor
30143639F8419145BEEF0003, 0, P, RSP-150000-0, 00:00:00:226, sales-floor
30143639F8419145BEEF0004, 0, P, RSP-150000-0, 00:00:00:219, sales-floor
30143639F8419145BEEF0005, 0, P, RSP-150000-0, 00:00:00:213, sales-floor
30143639F8419145BEEF0006, 0, P, RSP-150000-0, 00:00:00:206, sales-floor
30143639F8419145BEEF0007, 0, P, RSP-150000-0, 00:00:00:199, sales-floor
30143639F8419145BEEF0008, 0, P, RSP-150000-0, 00:00:00:192, sales-floor
30143639F8419145BEEF0009, 0, P, RSP-150000-0, 00:00:00:186, sales-floor
30143639F8419145BEEF0010, 0, P, RSP-150003-0, 00:00:00:257, back-stock
30143639F8419145BEEF0011, 0, P, RSP-150003-0, 00:00:00:244, back-stock
30143639F8419145BEEF0012, 0, P, RSP-150003-0, 00:00:00:236, back-stock
30143639F8419145BEEF0013, 0, P, RSP-150003-0, 00:00:00:230, back-stock
30143639F8419145BEEF0014, 0, P, RSP-150003-0, 00:00:00:224, back-stock
30143639F8419145BEEF0015, 0, P, RSP-150003-0, 00:00:00:214, back-stock
30143639F8419145BEEF0016, 0, P, RSP-150003-0, 00:00:00:209, back-stock
30143639F8419145BEEF0017, 0, P, RSP-150003-0, 00:00:00:203, back-stock
30143639F8419145BEEF0018, 0, P, RSP-150003-0, 00:00:00:197, back-stock
30143639F8419145BEEF0019, 0, P, RSP-150003-0, 00:00:00:187, back-stock
30143639F8419145BEEF0020, 0, P, RSP-150004-0, 00:00:00:255, back-stock
30143639F8419145BEEF0021, 0, P, RSP-150004-0, 00:00:00:243, back-stock
30143639F8419145BEEF0022, 0, P, RSP-150004-0, 00:00:00:235, back-stock
30143639F8419145BEEF0023, 0, P, RSP-150004-0, 00:00:00:228, back-stock
30143639F8419145BEEF0024, 0, P, RSP-150004-0, 00:00:00:222, back-stock
30143639F8419145BEEF0025, 0, P, RSP-150004-0, 00:00:00:215, back-stock
30143639F8419145BEEF0026, 0, P, RSP-150004-0, 00:00:00:209, back-stock
30143639F8419145BEEF0027, 0, P, RSP-150004-0, 00:00:00:203, back-stock
30143639F8419145BEEF0028, 0, P, RSP-150004-0, 00:00:00:198, back-stock
30143639F8419145BEEF0029, 0, P, RSP-150004-0, 00:00:00:191, back-stock
--------------------------------------------------------------------------------------------
rfid-gw> 
```


## Monitor the Upstream MQTT Topics for Events and Alerts

The mosquitto-clients utilities can also be used to monitor the upstream Inventory Events and Device Alerts that are sent from the Gateway.  These events and alerts are typically monitored by the customer application software directly or by a specific Cloud Connector that forwards the JSON data to a specific cloud server (i.e. Microsoft® Azure IoT Hub or AWS Greengrass Database).  Please refer to section 4.5 of the Intel® RSP SW Toolkit - Gateway, Installation & User's Guide to see how tag read data is processed. 


### ARRIVAL Events

When a tag is read for the first time by any RSP in a facility, an ARRIVAL event is generated and the tag transitions to the "PRESENT" (P) state.  To simulate this, monitor the upstream MQTT topics and add a new EPC to end of the tags_in_view_of_rsp_0 file.


```
:~$ mosquitto_sub -t rfid/gw/#

{"jsonrpc":"2.0","method":"inventory_event","params":{"sent_on":1551811580345,"gateway_id":"debian-vbox","data":[{"facility_id":"sales-floor","epc_code":"30143639F8419145BEEF0042","tid":"0","epc_encode_format":"tbd","event_type":"arrival","timestamp":1551811580340,"location":"RSP-150000-0"}]}}
```
```
rfid-gw> inventory detail *0042
--------------------------------------------------------------------------------------------
30143639F8419145BEEF0042, 0, P, RSP-150001-0, 00:00:00:688, sales-floor
--------------------------------------------------------------------------------------------
rfid-gw> 
```


### DEPARTED Events

There are three situations under which DEPARTED events are generated.  In the first situation, a tag in view of an RSP that has been given an "EXIT" personality transitions to the "EXITING" (E) state and remains in that state until the tag is no longer read by any RSP in the facility for the specified timeout period (see section 5.3, page 26).  The tag then transitions to the "DEPARTED_EXIT" (DX) state.  To simulate this using the example configuration, move one of the EPC values from the tags_in_view_of_rsp_0 file to the tags_in_view_of_rsp_1 file.  Save both files.  After a few seconds, delete that EPC value from tags_in_view_of_rsp_1 file while monitoring the upstream MQTT topics.

```
rfid-gw> inventory detail *0042
--------------------------------------------------------------------------------------------
30143639F8419145BEEF0042, 0, E, RSP-150001-0, 00:00:30:369, sales-floor
--------------------------------------------------------------------------------------------
rfid-gw>
```
```
:~$ mosquitto_sub -t rfid/gw/#

{"jsonrpc":"2.0","method":"inventory_event","params":{"sent_on":1551813028484,"gateway_id":"debian-vbox","data":[{"facility_id":"sales-floor","epc_code":"30143639F8419145BEEF0042","tid":"0","epc_encode_format":"tbd","event_type":"departed","timestamp":1551812997797,"location":"RSP-150001-0"}]}}
```
```
rfid-gw> inventory detail *0042
--------------------------------------------------------------------------------------------
30143639F8419145BEEF0042, 0, DX, RSP-150001-0, 00:00:33:092, sales-floor
--------------------------------------------------------------------------------------------
rfid-gw> 
```

In the second situation, a tag in view of an RSP that has been given a "POS" personality immediately transitions to the "DEPARTED_POS" (DP) state and remains in that state for the specified timeout period (see section 5.3, page 26), even if the tag continues to be read by other RSP's in any facility.  To simulate this using the example configuration, move one of the EPC values from the tags_in_view_of_rsp_0 file to the tags_in_view_of_rsp_2 file.  Save both files and monitor the upstream MQTT topics.

```
:~$ mosquitto_sub -t rfid/gw/#

{"jsonrpc":"2.0","method":"inventory_event","params":{"sent_on":1551813764414,"gateway_id":"debian-vbox","data":[{"facility_id":"sales-floor","epc_code":"30143639F8419145BEEF0009","tid":"0","epc_encode_format":"tbd","event_type":"departed","timestamp":1551813764392,"location":"RSP-150000-0"}]}}
```
```
rfid-gw> inventory detail *0009
--------------------------------------------------------------------------------------------
30143639F8419145BEEF0009, 0, DP, RSP-150002-0, 00:00:01:906, sales-floor
--------------------------------------------------------------------------------------------
rfid-gw> 
```

In the third situation, a tag in view of an RSP in one facility that moves to the view of an RSP in a different facility will remain in the "PRESENT" (P) state but the gateway will generate a DEPARTED event from the one facility and an ARRIVAL event in the other.  To simulate this using the example configuration, move one of the EPC values from the tags_in_view_of_rsp_3 file to the tags_in_view_of_rsp_0 file.  Save both files and monitor the upstream MQTT topics.

```
:~$ mosquitto_sub -t rfid/gw/#

{"jsonrpc":"2.0","method":"inventory_event","params":{"sent_on":1551814155623,"gateway_id":"debian-vbox","data":[{"facility_id":"back-stock","epc_code":"30143639F8419145BEEF0019","tid":"0","epc_encode_format":"tbd","event_type":"departed","timestamp":1551814154524,"location":"RSP-150003-0"},{"facility_id":"sales-floor","epc_code":"30143639F8419145BEEF0019","tid":"0","epc_encode_format":"tbd","event_type":"arrival","timestamp":1551814155617,"location":"RSP-150000-0"}]}}
```


### MOVED Events

Moved events are generated when a tag moves from the view of one RSP to the view of another RSP in the same facility.  To simulate this using the example configuration, move one of the EPC values from the tags_in_view_of_rsp_3 file to the tags_in_view_of_rsp_4 file.  Save both files and monitor the upstream MQTT topics.

```
:~$ mosquitto_sub -t rfid/gw/#

{"jsonrpc":"2.0","method":"inventory_event","params":{"sent_on":1551814487109,"gateway_id":"debian-vbox","data":[{"facility_id":"back-stock","epc_code":"30143639F8419145BEEF0018","tid":"0","epc_encode_format":"tbd","event_type":"moved","timestamp":1551814487105,"location":"RSP-150004-0"}]}}
```


### Device Alerts

Device Alerts are analogous to SNMP Traps, they are generated whenever a condition of concern occurs.  Like SNMP Traps, Device Alerts have a Severity Level associated with them: Info, Warning, Critical, Urgent.  An example of an informational alert would be when RSP devices initially connect to the Gateway.  An example of an alert that might cause a troubleshooting ticket would when an RSP device fails completely and stopd sending Heartbeats to the Gateway.  To simulate a sensor sending an alert, use the Gateway CLI to send a "set_device_alert" command.  This will cause rsp-sim to send a Device Alert with the same parameters.

```
rfid-gw> sensor set.alert.threshold HighCpuUsage warning 75 RSP-150001
--------------------------------------------------------------------------------------------
RSP-150001 - true
--------------------------------------------------------------------------------------------
rfid-gw> 
```
```
:~$ mosquitto_sub -t rfid/gw/#

{"jsonrpc":"2.0","method":"device_alert","params":{"sent_on":1552599549101,"device_id":"RSP-150001","facility_id":"sales-floor","alert_number":103,"alert_description":"HighCpuUsage","severity":"warning","optional":{"string":"percent","number":25}}}
```
