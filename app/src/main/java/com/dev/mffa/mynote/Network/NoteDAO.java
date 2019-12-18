package com.dev.mffa.mynote.Network;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.dev.mffa.mynote.Network.Model.Note;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface NoteDAO {
    @Query("SELECT * FROM notes WHERE checked=:checked")
    Flowable<List<Note>> getNoteByChecked(boolean checked);

    @Query("SELECT * FROM notes WHERE favorite=:favorite")
    Flowable<List<Note>> getNoteByFav(boolean favorite);

    @Query("SELECT * FROM notes WHERE date=:date")
    Flowable<List<Note>> getNoteByDate(String date);

    @Query("SELECT * FROM notes")
    Flowable<List<Note>> getAllNote();

    @Insert
    void insertNote(Note...notes);

    @Update
    void updateNote(Note...notes);

    @Delete
    void deleteNote(Note note);
}
