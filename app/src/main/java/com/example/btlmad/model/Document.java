package com.example.btlmad.model;

public class Document {
    private int id;
    private String name;
    private int typeId;
    private String downloadLink;
    private double sizeKb; // stored in KB

    public Document() {}

    public Document(int id, String name, int typeId, String downloadLink, double sizeKb) {
        this.id = id;
        this.name = name;
        this.typeId = typeId;
        this.downloadLink = downloadLink;
        this.sizeKb = sizeKb;
    }

    public Document(String name, int typeId, String downloadLink, double sizeKb) {
        this.name = name;
        this.typeId = typeId;
        this.downloadLink = downloadLink;
        this.sizeKb = sizeKb;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getTypeId() { return typeId; }
    public void setTypeId(int typeId) { this.typeId = typeId; }

    public String getDownloadLink() { return downloadLink; }
    public void setDownloadLink(String downloadLink) { this.downloadLink = downloadLink; }

    public double getSizeKb() { return sizeKb; }
    public void setSizeKb(double sizeKb) { this.sizeKb = sizeKb; }

    /** Returns human-readable size string */
    public String getFormattedSize() {
        if (sizeKb >= 1024) {
            return String.format("%.2f MB", sizeKb / 1024.0);
        }
        return String.format("%.0f KB", sizeKb);
    }
}
