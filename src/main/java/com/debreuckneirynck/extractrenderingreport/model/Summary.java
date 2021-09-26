package com.debreuckneirynck.extractrenderingreport.model;

public class Summary {

  private int count;
  private int duplicates;
  private long unnecessary;

  public Summary() {
    this.count = 0;
    this.duplicates = 0;
    this.unnecessary = 0;
  }

  public Summary(int count, int duplicates, long unnecessary) {
    super();
    this.count = count;
    this.duplicates = duplicates;
    this.unnecessary = unnecessary;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public int getDuplicates() {
    return duplicates;
  }

  public void setDuplicates(int duplicates) {
    this.duplicates = duplicates;
  }

  public long getUnnecessary() {
    return unnecessary;
  }

  public void setUnnecessary(long unnecessary) {
    this.unnecessary = unnecessary;
  }

  @Override
  public String toString() {
    return "Summary [count=" + count + ", duplicates=" + duplicates + ", unnecessary=" + unnecessary + "]";
  }
  
  
}
