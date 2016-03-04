package com.example.jane.timskaapplication101.files;

/**
 * Created by Jane on 7/27/2015.
 */
public class PDFItem {

    String name;
    String size;
    String path;
    int photoID;

    public PDFItem(String name, String size,String path, int photoID) {
        this.name = name;
        this.size = size;
        this.path = path;
        this.photoID = photoID;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}
