package com.debreuckneirynck.extractrenderingreport.model;

public class Report {

  private Rendering rendering;
  private Summary summary;

  public Report() {
    this.rendering = new Rendering();
    this.summary = new Summary();
  }

  public Report(Rendering rendering, Summary summary) {
    this.rendering = rendering;
    this.summary = summary;
  }

  public Rendering getRendering() {
    return rendering;
  }

  public void setRendering(Rendering rendering) {
    this.rendering = rendering;
  }

  public Summary getSummary() {
    return summary;
  }

  public void setSummary(Summary summary) {
    this.summary = summary;
  }

}
