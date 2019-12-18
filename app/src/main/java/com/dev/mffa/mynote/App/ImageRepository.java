package com.dev.mffa.mynote.App;

import com.dev.mffa.mynote.Network.IImageDataSource;
import com.dev.mffa.mynote.Network.Model.Image;

import io.reactivex.Flowable;

public class ImageRepository implements IImageDataSource {
    private IImageDataSource iImageDataSource;
    private static ImageRepository mInstance;

    public ImageRepository(IImageDataSource iImageDataSource) {
        this.iImageDataSource = iImageDataSource;
    }

    public static ImageRepository getmInstance(IImageDataSource iImageDataSource) {
        if (mInstance == null){
            mInstance = new ImageRepository(iImageDataSource);
        }
        return mInstance;
    }

    @Override
    public Flowable<Image> getImageByIdNote(String idNote) {
        return iImageDataSource.getImageByIdNote(idNote);
    }

    @Override
    public void insertImage(Image... images) {
        iImageDataSource.insertImage(images);
    }

    @Override
    public void deleteImage(Image image) {
        iImageDataSource.deleteImage(image);
    }
}
