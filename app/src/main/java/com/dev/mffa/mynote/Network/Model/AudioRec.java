package com.dev.mffa.mynote.Network.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "audio")
public class AudioRec {

    @PrimaryKey
    private int idAudio;
    private int idNote;
    private String audios;
    private String duration;
    private String timestamp;
    private long recordTime;

    public AudioRec(int idAudio, int idNote, String audios, String duration, String timestamp, long recordTime) {
        this.idAudio = idAudio;
        this.idNote = idNote;
        this.audios = audios;
        this.duration = duration;
        this.timestamp = timestamp;
        this.recordTime = recordTime;
    }

    public int getIdAudio() {
        return idAudio;
    }

    public void setIdAudio(int idAudio) {
        this.idAudio = idAudio;
    }

    public int getIdNote() {
        return idNote;
    }

    public void setIdNote(int idNote) {
        this.idNote = idNote;
    }

    public String getAudios() {
        return audios;
    }

    public void setAudios(String audios) {
        this.audios = audios;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public long getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(long recordTime) {
        this.recordTime = recordTime;
    }
}
