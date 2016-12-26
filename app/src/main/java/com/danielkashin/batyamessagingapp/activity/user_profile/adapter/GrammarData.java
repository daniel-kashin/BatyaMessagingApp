package com.danielkashin.batyamessagingapp.activity.user_profile.adapter;

import java.text.DecimalFormat;

/**
 * Created by Кашин on 25.12.2016.
 */

public class GrammarData {

  private long countRight;
  private long countAll;
  private double percent;
  private String date;

  public GrammarData(long countRight, long countAll, String date){
    this.countAll = countAll;
    this.countRight = countAll - countRight;
    this.date = date;

    if (countAll > 0) {
      this.percent = Math.floor(this.countRight / (double) countAll * 100 * 100) / 100;
    } else {
      this.percent = 0;
    }
  }

  public long getCountRight(){
    return countRight;
  }

  public long getCountAll(){
    return countAll;
  }

  public double getPercent(){
    return percent;
  }

  public String getDate() {
    return date;
  }
}
