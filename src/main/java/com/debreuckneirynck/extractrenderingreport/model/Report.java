package com.debreuckneirynck.extractrenderingreport.model;

import java.util.ArrayList;
import java.util.List;

public class Report {

  private List<Rendering> renderingList;
  private Summary summary;

  public Report() {
    this.renderingList = new ArrayList<>();
    this.summary = new Summary();
  }

  public Report(List<Rendering> renderingList, Summary summary) {
    this.renderingList = renderingList;
    this.summary = summary;
  }

  public List<Rendering> getRenderingList() {
    return renderingList;
  }

  public void setRenderingList(List<Rendering> renderingList) {
    this.renderingList = renderingList;
  }

  public Summary getSummary() {
    return summary;
  }

  public void setSummary(Summary summary) {
    this.summary = summary;
  }
  
  public void addRendering(Rendering rendering) {
    renderingList.add(rendering);
  }
  
  public void generateSummary() {
    summary.setCount(renderingList.size());
    //TODO 
    summary.setDuplicates(0);
    /*
     * renderingList.stream() .filter(rendering -> rendering.getStart().size() == 1)
     * .forEach(System.out::println);
     */
    
    summary.setUnnecessary( 
        renderingList.stream()
        .filter(rendering -> rendering.getStart().isEmpty())
        .count()
        );
  }
 
  @Override
  public String toString() {
    return "Report [ \n\t renderingList= \n\t\t" + renderingList + ", \n\t summary=" + summary + "]";
  }

}
