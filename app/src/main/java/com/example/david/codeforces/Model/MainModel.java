package com.example.david.codeforces.Model;

public class MainModel {
    private String Id;
    private String Name;
    private int Count;
    private String Type;
    private String Tags;

    public String getTags() {
        return Tags;
    }

    public void setTags(String tags) {
        Tags = tags;
    }

    public MainModel(String id, String type, String name, int count, String tags) {
        Id = id;
        Type = type;
        Name = name;
        Count = count;
        Tags = tags;
    }

    public String getType() {
        return Type;

    }

    public void setType(String type) {
        Type = type;
    }

    public void setId(String  id) {
        Id = id;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setCount(int count) {
        Count = count;
    }

    public String getId() {

        return Id;
    }

    public String getName() {
        return Name;
    }

    public int getCount() {
        return Count;
    }
}
