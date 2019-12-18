package com.dev.mffa.mynote.Network.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "notes")
public class Note {
    @PrimaryKey
    private int idNote;
    private String note;
    private String timestamp;
    private boolean checked;
    private boolean iconText;
    private boolean iconAudio;
    private boolean iconImage;
    private boolean favorite;
    private String date;

    public Note(int idNote, String note, String timestamp, boolean checked, boolean iconText, boolean iconAudio, boolean iconImage, boolean favorite, String date) {
        this.idNote = idNote;
        this.note = note;
        this.timestamp = timestamp;
        this.checked = checked;
        this.iconText = iconText;
        this.iconAudio = iconAudio;
        this.iconImage = iconImage;
        this.favorite = favorite;
        this.date = date;
    }

    public Note() {
    }

    public int getIdNote() {
        return idNote;
    }

    public void setIdNote(int idNote) {
        this.idNote = idNote;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isIconText() {
        return iconText;
    }

    public void setIconText(boolean iconText) {
        this.iconText = iconText;
    }

    public boolean isIconAudio() {
        return iconAudio;
    }

    public void setIconAudio(boolean iconAudio) {
        this.iconAudio = iconAudio;
    }

    public boolean isIconImage() {
        return iconImage;
    }

    public void setIconImage(boolean iconImage) {
        this.iconImage = iconImage;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
