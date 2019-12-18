package com.dev.mffa.mynote.App;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.dev.mffa.mynote.Network.AudioRecDAO;
import com.dev.mffa.mynote.Network.ImageDAO;
import com.dev.mffa.mynote.Network.Model.AudioRec;
import com.dev.mffa.mynote.Network.Model.Image;
import com.dev.mffa.mynote.Network.Model.Note;
import com.dev.mffa.mynote.Network.NoteDAO;

@Database(entities = {Note.class, AudioRec.class, Image.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public static final String DATABASE_NAME ="APP_NOTE";
    public abstract NoteDAO noteDao();
    public abstract AudioRecDAO audioRecDAO();
    public abstract ImageDAO imageDAO();
    private static AppDatabase mInstance;

    public static AppDatabase getmInstance(Context context) {
        if (mInstance == null){
            mInstance = Room.databaseBuilder(context,AppDatabase.class,DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return mInstance;
    }
}
