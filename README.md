A simple Java desktop project showing and XML parsing error that happens with XML but not with JSON, using the same POJOs.

Two simplified files (one JSON and one XML) are included, based on the responses from the MTA BusTime API (http://bustime.mta.info/wiki/Developers/SIRIIntro):

- vehicle-monitoring-simple.json or vehicle-monitoring-simple.xml - real-time information about one, many, or all vehicles tracked by the system

Usage of this project from the command-line is:

java SiriParserJacksonErrorDemo path-to-siri-file-to-parse

...where "path-to-siri-file-to-parse" is the full path, including the file name, to an example files included in this project.
