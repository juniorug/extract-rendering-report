package com.debreuckneirynck.extractrenderingreport.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import com.debreuckneirynck.extractrenderingreport.model.Rendering;
import com.debreuckneirynck.extractrenderingreport.model.Report;

public class ExtractReportService {

  private static final String EXECUTING_REQUEST_STARTRENDERING = "[ServiceProvider]: Executing request startRendering";
  private static final String SERVICE_STARTRENDERING_RETURNED = "Service startRendering returned ";
  private static final String EXECUTING_REQUEST_GETRENDERING_WITH_ARGUMENTS = "Executing request getRendering with arguments ";

  public void processFile(String filename) {
    Report report = new Report();
    try (InputStream inputStream = new FileInputStream(filename);
        Scanner sc = new Scanner(inputStream, StandardCharsets.UTF_8)) {

      while (sc.hasNextLine()) {
        String line = sc.nextLine();
        int workingThread = StringUtils.containsIgnoreCase(line, "WorkerThread-")
            ? Integer.parseInt(StringUtils.substringBetween(line, "[", "]").split("-")[1])
            : -1;
        if (line.contains(EXECUTING_REQUEST_STARTRENDERING)) {
          // System.out.println("workingThread: " + workingThread);

          String[] documentDetails = StringUtils
              .substringBetween(StringUtils.substringAfter(line, EXECUTING_REQUEST_STARTRENDERING), "[", "]")
              .replaceAll("\\s+", "").split(",");
          int documentId = Integer.parseInt(documentDetails[0]);
          int page = Integer.parseInt(documentDetails[1]);
          // System.out.println("documentId: " + documentId + " page: " + page);

          Rendering rendering = new Rendering();
          rendering.setDocument(documentId);
          rendering.setPage(page);
          rendering.setWorkingThread(workingThread);
          report.addRendering(rendering);

        }
        if (line.contains(SERVICE_STARTRENDERING_RETURNED)) {
          String startRenderingTimestamp = StringUtils.substringBefore(line,  " [WorkerThread");
          String uid = StringUtils.substringAfter(line, SERVICE_STARTRENDERING_RETURNED);
          report.getRenderingList().stream()
            .filter(r -> r.getWorkingThread() == workingThread)
            .filter(r -> r.getUid().equals(""))
            .findFirst()
            .ifPresent(r -> {  
              r.setUid(uid);
              r.getStart().add(startRenderingTimestamp);
            });
        }
        
        if (line.contains(EXECUTING_REQUEST_GETRENDERING_WITH_ARGUMENTS)) {
          String getRenderingTimestamp = StringUtils.substringBefore(line,  " [WorkerThread");
          String uid = StringUtils
              .substringBetween(StringUtils.substringAfter(line, EXECUTING_REQUEST_GETRENDERING_WITH_ARGUMENTS), "[", "]");
          report.getRenderingList().stream()
            .filter(r -> r.getUid().equals(uid))
            .findFirst()
            .ifPresent(r -> r.getGet().add(getRenderingTimestamp));
        }
      }
      report.generateSummary();
      System.out.println(report);
      // note that Scanner suppresses exceptions
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
}
