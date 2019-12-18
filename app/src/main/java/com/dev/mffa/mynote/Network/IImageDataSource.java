package com.dev.mffa.mynote.Network;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.dev.mffa.mynote.Network.Model.Image;

import io.reactivex.Flowable;

public interface IImageDataSource {
    Flowable<Image> getImageByIdNote(String idNote);
    void insertImage (Image...images);
    void deleteImage (Image image);
}
