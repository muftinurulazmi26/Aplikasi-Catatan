package com.dev.mffa.mynote.Network;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.dev.mffa.mynote.Network.Model.Image;

import io.reactivex.Flowable;

@Dao
public interface ImageDAO {
    @Query("SELECT * FROM image WHERE idNote=:idNote")
    Flowable<Image> getImageByIdNote(String idNote);

    @Insert
    void insertImage (Image...images);

    @Delete
    void deleteImage (Image image);
}
