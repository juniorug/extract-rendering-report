package com.debreuckneirynck.extractrenderingreport;

import com.debreuckneirynck.extractrenderingreport.service.ExtractReportService;

public class App {
  public static void main(String[] args) {

    if (args.length == 0 || args[0] == null || args[0].trim().isEmpty()) {
      System.out.println("You need to specify a file path!");
      return;
    }
    ExtractReportService extractReportService = new ExtractReportService();
    extractReportService.processFile(args[0]);
  }
}
