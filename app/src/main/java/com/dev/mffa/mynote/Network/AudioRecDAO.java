package com.dev.mffa.mynote.Network;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.dev.mffa.mynote.Network.Model.AudioRec;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface AudioRecDAO {
    @Query("SELECT * FROM audio WHERE idNote=:idNote")
    Flowable<List<AudioRec>> getAudioByIdNote(String idNote);

    @Insert
    void insertAudio(AudioRec...audioRecs);

    @Delete
    void deleteAudio(AudioRec audioRec);
}
