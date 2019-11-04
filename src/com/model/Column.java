package com.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "column")
public class Column {
    private String id;
    private String name;
    private String dataType;
    private int length;

    public Column() {
    }

    public Column(String name, String dataType, int length) {
        this.name = name;
        this.dataType = dataType;
        this.length = length;
        this.id = null;
    }

    public Column(String id, String name, String dataType, int length) {
        this.id = id;
        this.name = name;
        this.dataType = dataType;
        this.length = length;
    }

    public String getId() {
        return id;
    }

    @XmlAttribute
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
