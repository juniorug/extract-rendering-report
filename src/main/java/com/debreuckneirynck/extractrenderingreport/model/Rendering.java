package com.debreuckneirynck.extractrenderingreport.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.FIELD)
public class Rendering {

  private int document;
  
  private int page;
  
  @XmlTransient
  private List<Integer> workingThreadList;
  
  private String uid;
  
  @XmlElement(name = "start")
  private List<String> start;
  
  @XmlElement(name = "get")
  private List<String> get;

  public Rendering() {
    this.document = 0;
    this.page = 0;
    this.workingThreadList = new ArrayList<>();
    this.uid = "";
    this.start = new ArrayList<>();
    this.get = new ArrayList<>();
  }

  public Rendering(int document, int page, List<Integer> workingThread, String uid, List<String> start, List<String> get) {
    this.document = document;
    this.page = page;
    this.workingThreadList = workingThread;
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

  public List<Integer> getWorkingThreadList() {
    return workingThreadList;
  }

  public void setWorkingThreadList(List<Integer> workingThreadList) {
    this.workingThreadList = workingThreadList;
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

  @Override
  public String toString() {
    return "[ document=" + document + ", page=" + page + ", workingThread=" + workingThreadList + ", uid=" + uid
        + ", start=" + start + ", get=" + get + "]\n";
  }

}
