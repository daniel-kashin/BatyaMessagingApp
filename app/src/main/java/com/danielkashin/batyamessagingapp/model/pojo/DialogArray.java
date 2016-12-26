package com.danielkashin.batyamessagingapp.model.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Кашин on 25.11.2016.
 */

public class DialogArray {

  @SerializedName("dialogs")
  @Expose
  public ArrayList<PairLastMessageDialogId> dialogs;

  public DialogArray(ArrayList<PairLastMessageDialogId> dialogs) {
    this.dialogs = dialogs;
  }

  public ArrayList<PairLastMessageDialogId> getDialogs() {
    return dialogs;
  }
}