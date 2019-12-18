package com.dev.mffa.mynote.Network;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.dev.mffa.mynote.Network.Model.AudioRec;

import java.util.List;

import io.reactivex.Flowable;

public interface IAudioDataSource {
    Flowable<List<AudioRec>> getAudioByIdNote(String idNote);
    void insertAudio(AudioRec...audioRecs);
    void deleteAudio(AudioRec audioRec);
}
