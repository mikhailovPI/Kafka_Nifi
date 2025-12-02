package ru.myproject;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserMessage {

  private String id;
  private String name;

  public UserMessage() {}

  public UserMessage(String id, String name) {
    this.id = id;
    this.name = name;
  }

  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "UserMessage{id='" + id + "', name='" + name + "'}";
  }
}
