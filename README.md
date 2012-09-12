A simple Java desktop project showing how to parse a SIRI JSON or XML responses using Jackson.

Two sample JSON files and sample XML files are included, based on the responses from the MTA BusTime API (http://bustime.mta.info/wiki/Developers/SIRIIntro):

1. stop-monitoring.json or stop-monitoring.xml - real-time information about vehicles serving a particular stop
2. vehicle-monitoring.json or vehicle-monitoring.xml - real-time information about one, many, or all vehicles tracked by the system

Usage of this project from the command-line is:

java JacksonSiriParserExample path-to-siri-file-to-parse

...where "path-to-siri-file-to-parse" is the full path, including the file name, to an example files included in this project.

NOTE: This project has a dependency on the SIRI POJO classes, which can be found here:
https://github.com/CUTR-at-USF/onebusaway-siri-api-v13-pojos
