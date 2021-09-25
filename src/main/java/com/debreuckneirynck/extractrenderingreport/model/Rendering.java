package com.debreuckneirynck.extractrenderingreport.model;

import java.util.ArrayList;
import java.util.List;

public class Rendering {

  private int document;
  private int page;
  private String uid;
  private List<String> start;
  private List<String> get;

  public Rendering() {
    this.document = 0;
    this.page = 0;
    this.uid = "";
    this.start = new ArrayList<>();
    this.get = new ArrayList<>();
  }

  public Rendering(int document, int page, String uid, List<String> start, List<String> get) {
    this.document = document;
    this.page = page;
    this.uid = uid;
    this.start = start;
    this.get = get;
  }

  public int getDocument() {
    return document;
  }

  public void setDocument(int document) {
    this.document = document;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public List<String> getStart() {
    return start;
  }

  public void setStart(List<String> start) {
    this.start = start;
  }

  public List<String> getGet() {
    return get;
  }

  public void setGet(List<String> get) {
    this.get = get;
  }

}
