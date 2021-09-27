package com.debreuckneirynck.extractrenderingreport.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Report {

  @XmlElement(name="rendering")
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

  public List<Rendering> getRenderings() {
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

    /*get the number of renderings and set to count*/
    summary.setCount(renderingList.size());

    /*get the number of duplicated renderings and set to duplicates*/
    summary.setDuplicates(
        renderingList.stream()
          .filter(rendering -> rendering.getStart().size() > 1)
          .map(x -> x.getStart().size() - 1)
          .reduce(0, (l, r) -> l + r)
    );

    /*get the number of startRenderings without get and set to unnecessary*/
    summary.setUnnecessary( 
        renderingList.stream()
        .filter(rendering -> rendering.getGet().isEmpty())
        .count()
    );
  }
 
  @Override
  public String toString() {
    return "Report [ \n\t renderingList= \n\t\t" + renderingList + ", \n\t summary=" + summary + "]";
  }

}
