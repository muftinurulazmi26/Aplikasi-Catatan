package com.dev.mffa.mynote.App;

import com.dev.mffa.mynote.Network.INoteDataSource;
import com.dev.mffa.mynote.Network.Model.Note;
import com.dev.mffa.mynote.Network.NoteDAO;

import java.util.List;

import io.reactivex.Flowable;

public class NoteDataSource implements INoteDataSource {
    private NoteDAO noteDao;
    private static NoteDataSource mInstance;

    public NoteDataSource(NoteDAO noteDao) {
        this.noteDao = noteDao;
    }

    public static NoteDataSource getmInstance(NoteDAO noteDao) {
        if (mInstance == null){
            mInstance = new NoteDataSource(noteDao);
        }
        return mInstance;
    }


    @Override
    public Flowable<List<Note>> getNoteByChecked(boolean checked) {
        return noteDao.getNoteByChecked(checked);
    }

    @Override
    public Flowable<List<Note>> getNoteByFav(boolean favorite) {
        return noteDao.getNoteByFav(favorite);
    }

    @Override
    public Flowable<List<Note>> getNoteByDate(String date) {
        return noteDao.getNoteByDate(date);
    }

    @Override
    public Flowable<List<Note>> getAllNote() {
        return noteDao.getAllNote();
    }

    @Override
    public void insertNote(Note... notes) {
        noteDao.insertNote(notes);
    }

    @Override
    public void updateNote(Note... notes) {
        noteDao.updateNote(notes);
    }

    @Override
    public void deleteNote(Note note) {
        noteDao.deleteNote(note);
    }
}
