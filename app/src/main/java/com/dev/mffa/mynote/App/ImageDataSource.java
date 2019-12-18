package com.dev.mffa.mynote.App;

import com.dev.mffa.mynote.Network.IImageDataSource;
import com.dev.mffa.mynote.Network.ImageDAO;
import com.dev.mffa.mynote.Network.Model.Image;

import io.reactivex.Flowable;

public class ImageDataSource implements IImageDataSource {
    private ImageDAO imageDAO;
    private static ImageDataSource mInstance;

    public ImageDataSource(ImageDAO imageDAO) {
        this.imageDAO = imageDAO;
    }

    public static ImageDataSource getmInstance(ImageDAO imageDAO) {
        if (mInstance == null){
            mInstance = new ImageDataSource(imageDAO);
        }
        return mInstance;
    }

    @Override
    public Flowable<Image> getImageByIdNote(String idNote) {
        return imageDAO.getImageByIdNote(idNote);
    }

    @Override
    public void insertImage(Image... images) {
        imageDAO.insertImage(images);
    }

    @Override
    public void deleteImage(Image image) {
        imageDAO.deleteImage(image);
    }
}
