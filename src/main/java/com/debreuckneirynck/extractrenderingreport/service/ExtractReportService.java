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
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.debreuckneirynck.extractrenderingreport.model.Rendering;
import com.debreuckneirynck.extractrenderingreport.model.Report;

public class ExtractReportService {

  private static final String EXECUTING_REQUEST_START_RENDERING = "[ServiceProvider]: Executing request startRendering";
  private static final String SERVICE_START_RENDERING_RETURNED = "Service startRendering returned ";
  private static final String EXECUTING_REQUEST_GET_RENDERING_WITH_ARGUMENTS = "Executing request getRendering with arguments ";

  private Report report;
  private List<String> uidList;
  private List<Rendering> renderingList;
  private Map<String, Rendering> renderingMap; 
  
  public void processFile(String filename) {
    report = new Report();
    uidList = new ArrayList<>();
    renderingList = new ArrayList<>();
    renderingMap = new HashMap<>();
    
    try (InputStream inputStream = new FileInputStream(filename);
        Scanner sc = new Scanner(inputStream, StandardCharsets.UTF_8)) {

      while (sc.hasNextLine()) {
        String line = sc.nextLine();
        int workingThread = StringUtils.containsIgnoreCase(line, "WorkerThread-")
            ? Integer.parseInt(StringUtils.substringBetween(line, "[", "]").split("-")[1])
            : -1;
        
        if (line.contains(EXECUTING_REQUEST_START_RENDERING)) {
          createRendering(line,workingThread);
        }
        
        if (line.contains(SERVICE_START_RENDERING_RETURNED)) {
          addStartRendering(line, workingThread);
        }

        if (line.contains(EXECUTING_REQUEST_GET_RENDERING_WITH_ARGUMENTS)) {
          addGetRendering(line);
        }
      }
      
      // System.out.println(report);
      renderingList.stream().filter(r -> r.getUid().equals("1286380911373-1657")).forEach(System.out::println);
      Map<String, Long> counted = uidList.stream()
          .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

      /*
       * counted.entrySet().stream() //.parallel() .filter(map -> map.getValue() > 2)
       * .forEach(System.out::println);
       */

      // System.out.println(counted);
      // note that Scanner suppresses exceptions
      
      Map<String, List<Rendering>> renderingPerUID = renderingList.stream()
          .collect(Collectors.groupingBy(Rendering::getUid));
      //System.out.println("XXXXXXXXXXXXXx");
      renderingPerUID.entrySet().stream().parallel() .filter(map -> map.getKey().equals("1286380911373-1657")).forEach(System.out::println);
      //System.out.println(RenderingPerUID);
      System.out.println("YYYYYYYY");
      //removeDuplicates(RenderingPerUID).entrySet().stream().parallel().filter(map -> map.getKey().equals("1286380911373-1657")).forEach(System.out::println);
      removeDuplicates(renderingPerUID).entrySet().stream().parallel().forEach(System.out::println);
      
      System.out.println("ZZZZZZZZZZZZZZZZZZ");
      //renderingMap.entrySet().stream().parallel() .filter(map -> map.getKey().equals("1286380911373-1657")).forEach(System.out::println);
      //System.out.println(RenderingPerUID);
      Map<String, Rendering> singleMap = removeDuplicates(renderingPerUID);
      //singleMap.entrySet().stream().parallel().forEach(System.out::println);
      report.setRenderingList(new ArrayList<>(singleMap.values()));
      report.generateSummary();
      System.out.println(report);
      if (sc.ioException() != null) {
        throw sc.ioException();
      }
    } catch (NumberFormatException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      System.out.println("Non-existent file!");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void createRendering(String line, int workingThread) {
    // System.out.println("workingThread: " + workingThread);

    String[] documentDetails = StringUtils
        .substringBetween(StringUtils.substringAfter(line, EXECUTING_REQUEST_START_RENDERING), "[", "]")
        .replaceAll("\\s+", "").split(",");
    int documentId = Integer.parseInt(documentDetails[0]);
    int page = Integer.parseInt(documentDetails[1]);
    // System.out.println("documentId: " + documentId + " page: " + page);

    Rendering rendering = new Rendering();
    rendering.setDocument(documentId);
    rendering.setPage(page);
    rendering.getWorkingThreadList().add(workingThread);
    renderingList.add(rendering);
    //report.addRendering(rendering);
  }
  
  private void addStartRendering(String line, int workingThread) {
    
    String startRenderingTimestamp = StringUtils.substringBefore(line, " [WorkerThread");
    String uid = StringUtils.substringAfter(line, SERVICE_START_RENDERING_RETURNED);
    
    /*
     * if (!renderingMap.containsKey(uid)) { renderingMap.put(uid,
     * renderingList.stream() .filter(r ->
     * r.getWorkingThreadList().contains(workingThread)) .filter(r ->
     * r.getUid().equals("") || r.getUid().equals(uid)) .findAny() .orElse(null)); }
     * 
     * Rendering existing = renderingMap.get(uid);
     * existing.getWorkingThreadList().add(workingThread);
     * existing.getStart().add(startRenderingTimestamp);
     */
    
    if (uid.equals("1286380911373-1657")) {
      System.out.println("start rendering with id: " + uid + " workingthread: " + workingThread);
    }
    uidList.add(uid);

    renderingList.stream().filter(r -> r.getWorkingThreadList().contains(workingThread))
        .filter(r -> r.getUid().equals("") || r.getUid().equals(uid)).findFirst().ifPresent(r -> {
          r.setUid(uid);
          r.getStart().add(startRenderingTimestamp);
        });
  }
  
  private void addGetRendering(String line) {
    String getRenderingTimestamp = StringUtils.substringBefore(line, " [WorkerThread");
    String uid = StringUtils.substringBetween(
        StringUtils.substringAfter(line, EXECUTING_REQUEST_GET_RENDERING_WITH_ARGUMENTS), "[", "]");
    renderingList.stream().filter(r -> r.getUid().equals(uid)).findFirst()
        .ifPresent(r -> r.getGet().add(getRenderingTimestamp));
  }
  
  private Map<String, Rendering> removeDuplicates(Map<String, List<Rendering>> map) {
    
    Map<String, Rendering> singleEntryMap = new HashMap<>();
    for (Map.Entry<String, List<Rendering>> pair : map.entrySet()) {
      List<Rendering> renderingListWithSameUid = pair.getValue();
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
        
      } else  if (renderingListWithSameUid.size() == 1) {
        singleEntryMap.put(pair.getKey(), renderingListWithSameUid.get(0));
      }
    }
    return singleEntryMap;
  }
}
