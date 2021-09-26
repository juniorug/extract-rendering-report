package com.debreuckneirynck.extractrenderingreport.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

  public void processFile(String filename) {
    report = new Report();
    uidList = new ArrayList<>();

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
      report.generateSummary();
      // System.out.println(report);
      report.getRenderingList().stream().filter(r -> r.getUid().equals("1286380911373-1657")).forEach(System.out::println);
      Map<String, Long> counted = uidList.stream()
          .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

      /*
       * counted.entrySet().stream() //.parallel() .filter(map -> map.getValue() > 2)
       * .forEach(System.out::println);
       */

      // System.out.println(counted);
      // note that Scanner suppresses exceptions
      
      Map<String, List<Rendering>> RenderingPerUID = report.getRenderingList().stream()
          .collect(Collectors.groupingBy(Rendering::getUid));
      System.out.println("XXXXXXXXXXXXXx");
      RenderingPerUID.entrySet().stream().parallel() .filter(map -> map.getKey().equals("1286380911373-1657")).forEach(System.out::println);
      //System.out.println(RenderingPerUID);
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
    report.addRendering(rendering);
  }
  
  private void addStartRendering(String line, int workingThread) {
    
    String startRenderingTimestamp = StringUtils.substringBefore(line, " [WorkerThread");
    String uid = StringUtils.substringAfter(line, SERVICE_START_RENDERING_RETURNED);
    if (uid.equals("1286380911373-1657")) {
      System.out.println("start rendering with id: " + uid + " workingthread: " + workingThread);
    }
    uidList.add(uid);

    report.getRenderingList().stream().filter(r -> r.getWorkingThreadList().contains(workingThread))
        .filter(r -> r.getUid().equals("") || r.getUid().equals(uid)).findFirst().ifPresent(r -> {
          r.setUid(uid);
          r.getStart().add(startRenderingTimestamp);
        });
  }
  
  private void addGetRendering(String line) {
    String getRenderingTimestamp = StringUtils.substringBefore(line, " [WorkerThread");
    String uid = StringUtils.substringBetween(
        StringUtils.substringAfter(line, EXECUTING_REQUEST_GET_RENDERING_WITH_ARGUMENTS), "[", "]");
    report.getRenderingList().stream().filter(r -> r.getUid().equals(uid)).findFirst()
        .ifPresent(r -> r.getGet().add(getRenderingTimestamp));
  }
}
