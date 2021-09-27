package com.debreuckneirynck.extractrenderingreport.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.debreuckneirynck.extractrenderingreport.model.Rendering;
import com.debreuckneirynck.extractrenderingreport.model.Report;

public class ExtractReportService {

  private static final Logger LOGGER = Logger.getLogger( ExtractReportService.class.getName() );
  
  private static final String EXECUTING_REQUEST_START_RENDERING = "[ServiceProvider]: Executing request startRendering";
  private static final String SERVICE_START_RENDERING_RETURNED = "Service startRendering returned ";
  private static final String EXECUTING_REQUEST_GET_RENDERING_WITH_ARGUMENTS = "Executing request getRendering with arguments ";
  private static final String OPEN_BRACKETS = "[";
  private static final String CLOSE_BRACKETS = "]";
  private static final String WORKER_THREAD = " [WorkerThread";
  private static final String EMPTY_STRING = "";
  
  private List<Rendering> renderingList; 
  
  /**
   * Returns a Report object based on processing file algorithm logic: Read the
   * .log file line by line Look for Executing request startRendering. Look for
   * Service startRendering returned UID. Look for Executing request getRendering.
   * Remove duplicates for renderingList summarizing it. Generate the Report
   * Summary
   * 
   * @param filename the file to be processed
   * @return the processed Report
   */
  public Report processFile(String filename) {
    Report report = new Report();
    renderingList = new ArrayList<>();
    
    try (InputStream inputStream = new FileInputStream(filename);
        Scanner sc = new Scanner(inputStream, StandardCharsets.UTF_8)) {

      while (sc.hasNextLine()) {
        String line = sc.nextLine();
        int workingThread = StringUtils.containsIgnoreCase(line, "WorkerThread-")
            ? Integer.parseInt(StringUtils.substringBetween(line, OPEN_BRACKETS, CLOSE_BRACKETS).split("-")[1])
            : -1;

        /* Look for Executing request startRendering and create a rendering*/
        if (line.contains(EXECUTING_REQUEST_START_RENDERING)) {
          createRendering(line,workingThread);
        }

        /* Look for Service startRendering returned UID and add start to the rendering*/
        if (line.contains(SERVICE_START_RENDERING_RETURNED)) {
          addStartRendering(line, workingThread);
        }

        /* Look for Executing request getRendering and add get to the rendering*/
        if (line.contains(EXECUTING_REQUEST_GET_RENDERING_WITH_ARGUMENTS)) {
          addGetRendering(line);
        }
      }

      /* Remove duplicates grouping it and generates the summary */
      report.setRenderingList(removeDuplicates());
      report.generateSummary();

      if (sc.ioException() != null) {
        throw sc.ioException();
      }
    } catch (FileNotFoundException e) {
      LOGGER.severe("Non-existent file!");
    } catch (NumberFormatException | IOException e) {
      e.printStackTrace();
    }
    
    return report;  
  }

  /**
   * Generates the rendering item when the text Executing request startRendering is found.
   * Extracts the documentId and page from the current line and set these information in rendering.
   * Add rendering to the renderingList.
   * 
   * @param line the line that contains EXECUTING_REQUEST_START_RENDERING text
   * @param workingThread the current working thread
   */
  private void createRendering(String line, int workingThread) {

    String[] documentDetails = StringUtils
        .substringBetween(StringUtils.substringAfter(line, EXECUTING_REQUEST_START_RENDERING), OPEN_BRACKETS, CLOSE_BRACKETS)
        .replaceAll("\\s+", EMPTY_STRING).split(",");
    int documentId = Integer.parseInt(documentDetails[0]);
    int page = Integer.parseInt(documentDetails[1]);

    Rendering rendering = new Rendering();
    rendering.setDocument(documentId);
    rendering.setPage(page);
    rendering.getWorkingThreadList().add(workingThread);
    renderingList.add(rendering);
  }
  
  /**
   * Add a new entry in the startList when a Service startRendering is found .
   * Filter the renderingList to get the rendering from workingThread and UID
   * Extracts the timestamp from the current line and set these information in rendering startList.
   * 
   * @param line the line that contains EXECUTING_REQUEST_START_RENDERING text
   * @param workingThread the current working thread
   */
  private void addStartRendering(String line, int workingThread) {
    
    String startRenderingTimestamp = StringUtils.substringBefore(line, WORKER_THREAD);
    String uid = StringUtils.substringAfter(line, SERVICE_START_RENDERING_RETURNED);

    renderingList.stream().filter(r -> r.getWorkingThreadList().contains(workingThread))
        .filter(r -> r.getUid().equals(EMPTY_STRING) || r.getUid().equals(uid)).findFirst().ifPresent(r -> {
          r.setUid(uid);
          r.getStart().add(startRenderingTimestamp);
        });
  }

  /**
   * Add a new entry in the getList when a request getRendering is found .
   * Filter the renderingList to get the rendering from UID
   * Extracts the timestamp from the current line and set these information in rendering getList.
   * 
   * @param line the line that contains EXECUTING_REQUEST_START_RENDERING text
   */
  private void addGetRendering(String line) {
    String getRenderingTimestamp = StringUtils.substringBefore(line, WORKER_THREAD);
    String uid = StringUtils.substringBetween(
        StringUtils.substringAfter(line, EXECUTING_REQUEST_GET_RENDERING_WITH_ARGUMENTS), OPEN_BRACKETS, CLOSE_BRACKETS);

    renderingList.stream().filter(r -> r.getUid().equals(uid)).findFirst()
        .ifPresent(r -> r.getGet().add(getRenderingTimestamp));
  }
  
  /**
   * Returns a Rendering list grouping renderings with same UID.
   * First creates a map grouping renderings by UID
   * Then, if there's more than one rendering for same UID, creates a single rendering with the information from the repeated entries
   * convert the map values to an arrayList.
   *  
   * @return    an arrayList of renderings without repeated UID
   */
  private List<Rendering> removeDuplicates() {
    
    /* group renderings by UID*/
    Map<String, List<Rendering>> map = renderingList.stream()
        .collect(Collectors.groupingBy(Rendering::getUid));
    
    Map<String, Rendering> singleEntryMap = new HashMap<>();
    for (Map.Entry<String, List<Rendering>> pair : map.entrySet()) {
      List<Rendering> renderingListWithSameUid = pair.getValue();
      
      /* if more than one rendering with same UID, a single rendering will be created*/
      if (renderingListWithSameUid.size() > 1) {
        Rendering rendering = new Rendering();
        int index = 0;
        for(Rendering duplicated : renderingListWithSameUid) {
          if(index == 0) {
            rendering.setDocument(duplicated.getDocument());
            rendering.setPage(duplicated.getPage());
            rendering.setUid(duplicated.getUid());
          }
          
          rendering.getWorkingThreadList().addAll(duplicated.getWorkingThreadList());
          rendering.getGet().addAll(duplicated.getGet());
          rendering.getStart().addAll(duplicated.getStart());
          index ++;
        }
        singleEntryMap.put(pair.getKey(), rendering);
        
      } else if (renderingListWithSameUid.size() == 1) {
        singleEntryMap.put(pair.getKey(), renderingListWithSameUid.get(0));
      }
    }
    return new ArrayList<>(singleEntryMap.values());
  }

}
