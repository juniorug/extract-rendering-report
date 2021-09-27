package com.debreuckneirynck.extractrenderingreport.service;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.debreuckneirynck.extractrenderingreport.model.Report;

@ExtendWith(MockitoExtension.class)  
public class ExtractReportServiceTest {
  
  @Mock
  private Report report;
  
  @InjectMocks
  private ExtractReportService extractReportService;
  
  private static String VALID_SERVER_FILE = "src/main/resources/server.log";
  
  @Before
  public void init() throws IOException {
      extractReportService = new ExtractReportService();
  }
  
  @Test
  public void testReportCreation() {
      givenExistingInputLogFile();
      whenProcessFileCalled();
      thenReportShouldNotBeNull();
  }
  
  @Test
  public void testNonExistingInputLogFile() {
    givenNonExistingInputLogFile();
    whenNonExistingInputLogFileProcessFileCalled();
    thenShouldReturnEmptyRenderingList();
  }
  
  @Test
  public void testGetNumberOfRenderings() {
      givenExistingInputLogFile();
      whenProcessFileCalled();
      thenShouldValidateNumberOfRenderings();
     
  }
  
  @Test
  public void testGetDuplicates() {
      givenExistingInputLogFile();
      whenProcessFileCalled();
      thenShouldValidateNumberOfDuplicates();
     
  }
  
  @Test
  public void testGetNumberOfUnecessary() {
      givenExistingInputLogFile();
      whenProcessFileCalled();
      thenShouldValidateNumberOfUnnecessaries();
     
  }

  @Test
  public void testGetStartList() {
      givenExistingInputLogFile();
      whenProcessFileCalled();
      thenShouldValidateGetStartList();
     
  }

  @Test
  public void testGetGetList() {
      givenExistingInputLogFile();
      whenProcessFileCalled();
      thenShouldValidateGetGetList();
     
  }

  /*Given Methods*/
  private void givenExistingInputLogFile() {
    
    report = extractReportService.processFile(VALID_SERVER_FILE);
  }
  
  private void givenNonExistingInputLogFile() {
    
    report = extractReportService.processFile("src/main/resources/blah.log");
  }
  
  /* When Methods*/
  private void whenProcessFileCalled() {
    extractReportService = spy(new ExtractReportService());
    report = getReport();
    when(extractReportService.processFile(VALID_SERVER_FILE)).thenReturn(report);
  }
  
  private void whenNonExistingInputLogFileProcessFileCalled() {
    extractReportService = spy(new ExtractReportService());
    report = mock(Report.class);
    when(extractReportService.processFile(VALID_SERVER_FILE)).thenReturn(report);
  }
  /* Then methods*/ 
  private void thenReportShouldNotBeNull() {
    assertNotNull(report);
  }
  
  private void thenShouldValidateNumberOfRenderings() {
    assertEquals(873, report.getSummary().getCount());
  }
  
  private void thenShouldReturnEmptyRenderingList() {
    assertEquals(0, report.getRenderings().size());
  }
  
  private void thenShouldValidateNumberOfDuplicates() {
    assertEquals(65, report.getSummary().getDuplicates());
  }
  
  private void thenShouldValidateNumberOfUnnecessaries() {
    assertEquals(203, report.getSummary().getUnnecessary());
  }
  
  private void thenShouldValidateGetStartList() {
    long getCount = report.getRenderings().stream()
    .filter(rendering -> rendering.getUid().equals("1286380911373-1657"))
    .map(rendering -> rendering.getStart())
    .mapToInt(List::size)
    .sum();
    assertEquals(9L, getCount);
  }
  
  private void thenShouldValidateGetGetList() {
    long getCount = report.getRenderings().stream()
    .filter(rendering -> rendering.getUid().equals("1286377106198-8002"))
    .map(rendering -> rendering.getGet())
    .mapToInt(List::size)
    .sum();
    assertEquals(26L, getCount);
  }
  /*Aux Methods*/
  private Report getReport() {
    return extractReportService.processFile(VALID_SERVER_FILE);
  }
}
