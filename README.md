# CashRegisterConverter

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

The Cash Register Converter gets a csv file containing orders and returns a xml file in the OpenTrans format which 
is readable by the accounting software Lexware. It has a user interface for easier operation.

## For developers

### Run the program

````shell script
mvn javafx:run
````

### Build a jar file

````shell script
mvn package
````

## For users

### Run the program:

- download the Java Runtime Environment 14 and install it:
    - [for x64 systems](https://github.com/AdoptOpenJDK/openjdk14-binaries/releases/download/jdk-14.0.2%2B12/OpenJDK14U-jre_x64_windows_hotspot_14.0.2_12.msi)
    - [for x86 systems](https://github.com/AdoptOpenJDK/openjdk14-binaries/releases/download/jdk-14.0.2%2B12/OpenJDK14U-jre_x86-32_windows_hotspot_14.0.2_12.msi)
- download the file ``tbd``
- Start the program by executing the file

## Version overview:

 Version | Date | Changes
 ------- | ---- | -------
1.0-SNAPSHOT | 15.02.2021 | initial Release version 
1.1 | 20.09.2022 | - All column after the third where shifted by one <br /> - Multi country support: if the country is not Germany, the tax is zero
1.2 | 17.04.2024 | - Columns in the csv where shifted again <br /> - Besides "EC" the string "Karte" also counts as card payment <br /> - The cancellation column is not case sensitive anymore


