package com.dev.mffa.mynote.Network;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.dev.mffa.mynote.Network.Model.Note;

import java.util.List;

import io.reactivex.Flowable;

public interface INoteDataSource {
    Flowable<List<Note>> getNoteByChecked(boolean checked);
    Flowable<List<Note>> getNoteByFav(boolean favorite);
    Flowable<List<Note>> getNoteByDate(String date);
    Flowable<List<Note>> getAllNote();
    void insertNote(Note...notes);
    void updateNote(Note...notes);
    void deleteNote(Note note);
}
