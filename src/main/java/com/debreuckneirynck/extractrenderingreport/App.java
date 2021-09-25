package com.debreuckneirynck.extractrenderingreport;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class App {
  public static void main(String[] args) {

    if (args.length == 0 || args[0] == null || args[0].trim().isEmpty()) {
      System.out.println("You need to specify a file path!");
      return;
    }
    try (InputStream inputStream = new FileInputStream(args[0]);
        Scanner sc = new Scanner(inputStream, StandardCharsets.UTF_8)) {

      while (sc.hasNextLine()) {
        String line = sc.nextLine();
        System.out.println(line);
      }
      // note that Scanner suppresses exceptions
      if (sc.ioException() != null) {
        throw sc.ioException();
      }
    } catch (FileNotFoundException e) {
      System.out.println("Non-existent file!");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
