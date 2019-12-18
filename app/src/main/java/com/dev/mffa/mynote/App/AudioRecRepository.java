package com.dev.mffa.mynote.App;

import com.dev.mffa.mynote.Network.AudioRecDAO;
import com.dev.mffa.mynote.Network.IAudioDataSource;
import com.dev.mffa.mynote.Network.Model.AudioRec;

import java.util.List;

import io.reactivex.Flowable;

public class AudioRecRepository implements IAudioDataSource {
    private IAudioDataSource iAudioDataSource;
    private static AudioRecRepository mInstance;

    public AudioRecRepository(IAudioDataSource iAudioDataSource) {
        this.iAudioDataSource = iAudioDataSource;
    }

    public static AudioRecRepository getmInstance(IAudioDataSource iAudioDataSource) {
        if (mInstance == null){
            mInstance = new AudioRecRepository(iAudioDataSource);
        }
        return mInstance;
    }


    @Override
    public Flowable<List<AudioRec>> getAudioByIdNote(String idNote) {
        return iAudioDataSource.getAudioByIdNote(idNote);
    }

    @Override
    public void insertAudio(AudioRec... audioRecs) {
        iAudioDataSource.insertAudio(audioRecs);
    }

    @Override
    public void deleteAudio(AudioRec audioRec) {
        iAudioDataSource.deleteAudio(audioRec);
    }
}
