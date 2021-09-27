package com.debreuckneirynck.extractrenderingreport;

import java.util.logging.Logger;

import com.debreuckneirynck.extractrenderingreport.model.Report;
import com.debreuckneirynck.extractrenderingreport.service.ExtractReportService;
import com.debreuckneirynck.extractrenderingreport.service.GenerateReportFileService;

public class App {

  private static final Logger LOGGER = Logger.getLogger(App.class.getName());

  /**
   * This main class is responsible for process the .log file and generate the
   * summarized .xml file
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {

    if (args.length == 0 || args[0] == null || args[0].trim().isEmpty()) {
      LOGGER.severe("You need to specify a file path!");
      return;
    }
    /* processes the given .log file and generate a Report instance */
    ExtractReportService extractReportService = new ExtractReportService();
    Report report = extractReportService.processFile(args[0]);

    /* Generates an output .xml file from the given processed report */
    GenerateReportFileService.generateXmlFile(report);
    LOGGER.info("report file succesfully created! Please check /output/report.xml");
  }
}
