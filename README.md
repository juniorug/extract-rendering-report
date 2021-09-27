# extract-rendering-report
A tool that "scans" log files and extract information from it.


### How it works

This tool receives a .log file as argument, processes it and generate one XML with the process report. The generated file will be located at the  `output` folder.


### Tasks to accomplish the objective
- Create the Maven project using archetype
- Create the Main class that will receive the file path as argument
- Create the model class based on the Report structure
- Create test classes
- Create the ExtractReportService class that will be responsible for processing the file and extract the required data
- Create GenerateReportService class to convert the extracted information and generate the XML files

### Dependencies

- JDK 11+ 
- Maven 3.0+

### Build

```
mvn clean compile assembly:single
```

### Run

```
java -jar target/extract-rendering-report.jar "path/to/server.log"
```

### Example

```
java -jar target/extract-rendering-report.jar /home/junior/Downloads/server.log
```