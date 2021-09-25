# extract-rendering-report
A tool that "scans" log files and extract information from it.


### How it works

This tool receives a .log file as argument, processes it and generate XML's for each document ID. The generated files will be located at the  `output` folder.


### Tasks to accomplish the objective
- Create the Maven project using archetype
- Create the Main class that will receive the file path as argument
- Create the model class based on the Report structure
- Create test classes
- Create the ExtractReportService class that will be responsible for processing the file and extract the required data
- Create GenerateReportService class to convert the extracted information and generate the XML files


### Build

```
mvn clean install
```

### Run

```
java -jar extract-rendering-report.jar "server.log"
```
