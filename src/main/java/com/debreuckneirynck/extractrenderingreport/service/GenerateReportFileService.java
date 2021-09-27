package com.debreuckneirynck.extractrenderingreport.service;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.debreuckneirynck.extractrenderingreport.model.Report;

public class GenerateReportFileService {

  private GenerateReportFileService() {
    throw new IllegalStateException("Utility class");
  }
  
  /**
   * Generates a .xml file from the given report
   * 
   * @param report the processed Report
   */
  public static void generateXmlFile(Report report) {
    File theDir = new File("output/");
    File xmlFile = new File("output/report.xml");
    if (!theDir.exists()) theDir.mkdirs();
    
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(Report.class);
      Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
      jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      jaxbMarshaller.marshal(report, xmlFile);
    } catch (JAXBException e) {
      e.printStackTrace();
    }
  }
}
