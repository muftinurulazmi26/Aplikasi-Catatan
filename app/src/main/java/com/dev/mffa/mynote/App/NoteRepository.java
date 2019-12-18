package com.dev.mffa.mynote.App;

import com.dev.mffa.mynote.Network.INoteDataSource;
import com.dev.mffa.mynote.Network.Model.Note;

import java.util.List;

import io.reactivex.Flowable;

public class NoteRepository implements INoteDataSource {
    private INoteDataSource mLocalnoteDataSource;
    private static NoteRepository mInstance;

    public NoteRepository(INoteDataSource mLocalnoteDataSource) {
        this.mLocalnoteDataSource = mLocalnoteDataSource;
    }

    public static NoteRepository getmInstance(INoteDataSource mLocalnoteDataSource) {
        if (mInstance == null){
            mInstance = new NoteRepository(mLocalnoteDataSource);
        }
        return mInstance;
    }

    @Override
    public Flowable<List<Note>> getNoteByChecked(boolean checked) {
        return mLocalnoteDataSource.getNoteByChecked(checked);
    }

    @Override
    public Flowable<List<Note>> getNoteByFav(boolean favorite) {
        return mLocalnoteDataSource.getNoteByFav(favorite);
    }

    @Override
    public Flowable<List<Note>> getNoteByDate(String date) {
        return mLocalnoteDataSource.getNoteByDate(date);
    }

    @Override
    public Flowable<List<Note>> getAllNote() {
        return mLocalnoteDataSource.getAllNote();
    }

    @Override
    public void insertNote(Note... notes) {
        mLocalnoteDataSource.insertNote(notes);
    }

    @Override
    public void updateNote(Note... notes) {
        mLocalnoteDataSource.updateNote(notes);
    }

    @Override
    public void deleteNote(Note note) {
        mLocalnoteDataSource.deleteNote(note);
    }
}
