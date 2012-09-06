A simple Java desktop project showing how to parse a SIRI JSON response using Jackson.

Two sample JSON files are included, based on the responses from the MTA BusTime API (http://bustime.mta.info/wiki/Developers/SIRIIntro):

1. stop-monitoring.json - real-time information about vehicles serving a particular stop
2. vehicle-monitoring.json - real-time information about one, many, or all vehicles tracked by the system

Usage of this project from the command-line is:
java JacksonSiriParserExample path-to-JSON-file

where "path-to-JSON-file" is the full path, including the file name, to either of the example files included in this project.
