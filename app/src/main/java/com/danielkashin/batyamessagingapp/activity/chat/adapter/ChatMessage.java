package com.danielkashin.batyamessagingapp.activity.chat.adapter;

import com.danielkashin.batyamessagingapp.lib.TimestampHelper;

/**
 * Created by Кашин on 18.11.2016.
 */

public class ChatMessage {

  private String content;
  private String time;
  private String sender;
  private long timestamp;
  private Direction direction;
  private String guid;

  public ChatMessage(String content, String sender, long timestamp, Direction direction, String guid) {
    this.content = content;
    this.timestamp = timestamp;
    this.time = TimestampHelper.formatTimestampToTime(timestamp);
    this.sender = sender;
    this.direction = direction;
    this.guid = guid;
  }

  public String getContent() {
    return content;
  }

  public String getTime() {
    return time;
  }

  public Direction getDirection() {
    return direction;
  }

  public String getGuid() {
    return guid;
  }

  public String getSender() {
    return sender;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public enum Direction {
    Incoming(0),
    Outcoming(1),
    System(2);

    private int intValue;

    Direction(int value) {
      intValue = value;
    }

    public int getIntValue() {
      return intValue;
    }
  }
}
