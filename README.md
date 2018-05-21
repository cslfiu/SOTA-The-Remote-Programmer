# The Remote Programmer
> *Yet Another Component of The SOTA Framework*

The third component of the SOTA framework is the remote programmer .  It  is  responsible  to  get  firmware  from  the user  and  send  the  firmware  to target  micro-controller  in  a  secure way.  It  has  a embedded TCP  server  to  create  connection  with  the communication  module  to  send  firmware  packets  to  target micro-controller  of  IoT  device.  It  supports  a  multi-device programming  capability  in  a  secure  way  by  utilizing  from JAVA  Thread  library  and  the  AES  128  bit  CBC  symmetric encryption algorithm. 

Currently, It can program ATMEL 8 Bit micro-controllers that has the SOTA bootloader over-the-air. 

## Getting Started

The remote programmer programs target micro-controllers in IoT device; therefore, most of The Remote Programmer's operations will be related reading firmware and sending firmware packets to target micro-controller.

### Prerequisites

Since it does not have user-friendly interface in it, you will need to compile project after adding your target micro-controllers.
```
JAVA Development Kit 8 or newer
Internet Access
```

### Compiling Project
The Remote Programmer is a Gradle based project, you can easliy import to your system. 
After copying project files into your system, you can run The Remote Programmer invoking following command in the terminal that is on same folder path.

    ./gradlew sotaRun

If you want to add your IoT device as a target device, you need to register your micro-controller of your IoT device as well as specifying location of new firmware.

      main.addDevice("A427","10.109.189.0",SOTAGlobals.OTA_MODE.FULL_CONFIDENTIALITY_WITH_DEFAULT_INTEGRITY,SOTAGlobals.AUTH_MODE.ON,"/Blink.ino.hex");
    

**The first argument** is name of target IoT device, it helps to identify devices easily from device list set.
**The second argument** is IP address of target device in string format.

**The third argument,** SOTAGlobals.OTA_MODE.FULL_CONFIDENTIALITY_WITH_DEFAULT_INTEGRITY* represents that the remote programmer encrypt over-the-air programming firmware packets to provide data integrity and confidentiality. Currently, The Remote Programmer supports only AES 128 Bit CBC symmetric encryption to provide data confidentiality also XOR based checksum is used for data integrity.

**The fourth argument,** SOTAGlobals.AUTH_MODE.ON* represents authentication module in The Remote Programmer enforces initiate authentication with micro-controller in IoT device. If user provides *SOTAGlobals.AUTH_MODE.OFF*, user will close authentication mechanism and entire over-the-air programming turns into vulnerable firmware update process.

**The fifth argument** is a location of new firmware in string format.

## Built With

* [Gradle](https://gradle.org) - Dependecy Managment
* [Apache Commons](https://commons.apache.org) - Reusable JAVA Code
* [Log4j](https://logging.apache.org/log4j/2.x/) - JAVA based logging utility


## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments
This is a open-source project, we appreciate any contribution from contributors. If you want to improve The SOTA Framework, feel free to fork and create pull request!

