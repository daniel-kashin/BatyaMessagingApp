package com.danielkashin.batyamessagingapp.model.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Кашин on 25.12.2016.
 */

public class WordsData {

  @SerializedName("total_words")
  @Expose
  private long totalWords;

  @SerializedName("correct_words")
  @Expose
  private long correctWords;

  public long getTotalWords() {
    return totalWords;
  }

  public long getCorrectWords() {
    return correctWords;
  }
}
