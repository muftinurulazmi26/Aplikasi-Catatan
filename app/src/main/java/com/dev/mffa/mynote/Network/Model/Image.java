package com.dev.mffa.mynote.Network.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "image")
public class Image {

    @PrimaryKey
    private int idImages;
    private int idNote;
    private String images;

    public Image() {
    }

    public Image(int idImages, int idNote, String images) {
        this.idImages = idImages;
        this.idNote = idNote;
        this.images = images;
    }

    public int getIdImages() {
        return idImages;
    }

    public void setIdImages(int idImages) {
        this.idImages = idImages;
    }

    public int getIdNote() {
        return idNote;
    }

    public void setIdNote(int idNote) {
        this.idNote = idNote;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
}
