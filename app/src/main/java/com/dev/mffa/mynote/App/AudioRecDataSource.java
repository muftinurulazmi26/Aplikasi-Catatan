package com.dev.mffa.mynote.App;

import com.dev.mffa.mynote.Network.AudioRecDAO;
import com.dev.mffa.mynote.Network.IAudioDataSource;
import com.dev.mffa.mynote.Network.Model.AudioRec;

import java.util.List;

import io.reactivex.Flowable;

public class AudioRecDataSource implements IAudioDataSource {
    private AudioRecDAO audioRecDAO;
    private static AudioRecDataSource mInstance;

    public AudioRecDataSource(AudioRecDAO audioRecDAO) {
        this.audioRecDAO = audioRecDAO;
    }

    public static AudioRecDataSource getmInstance(AudioRecDAO audioRecDAO) {
        if (mInstance == null){
            mInstance = new AudioRecDataSource(audioRecDAO);
        }
        return mInstance;
    }


    @Override
    public Flowable<List<AudioRec>> getAudioByIdNote(String idNote) {
        return audioRecDAO.getAudioByIdNote(idNote);
    }

    @Override
    public void insertAudio(AudioRec... audioRecs) {
        audioRecDAO.insertAudio(audioRecs);
    }

    @Override
    public void deleteAudio(AudioRec audioRec) {
        audioRecDAO.deleteAudio(audioRec);
    }
}
