package com.dev.mffa.mynote.App;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.mffa.mynote.Network.Model.AudioRec;
import com.dev.mffa.mynote.R;
import com.dev.mffa.mynote.Utils.CountDownTimer;
import com.dev.mffa.mynote.Utils.ExampleCountDownTimer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ak.sh.ay.musicwave.MusicWave;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.VHAudio> {
    private Context context;
    private List<AudioRec> audioRecList;
    public static MediaPlayer mediaPlayer;
    private Visualizer visualizer;
    public static AudioRec item;
    private String status;
    private boolean isplaying = false;
    private CountDownTimer countDownTimer;

    public AudioAdapter(Context context, List<AudioRec> audioRecList) {
        this.context = context;
        this.audioRecList = audioRecList;
    }

    @NonNull
    @Override
    public VHAudio onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.audio_list,viewGroup,false);

        return new VHAudio(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VHAudio vhAudio, int position) {
        item = audioRecList.get(position);

        vhAudio.timestamp.setText(item.getTimestamp());

        vhAudio.duration.setText(item.getDuration());

        vhAudio.btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isplaying == true && mediaPlayer != null){
                    vhAudio.btn_play.setImageResource(R.drawable.img_play);
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    visualizer.release();
                    countDownTimer.cancel();
                    countDownTimer.onFinish();
                    isplaying = false;

                } else if (isplaying == false){
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(item.getAudios());
                        mediaPlayer.prepare();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setupVisualizer(vhAudio.musicWave,mediaPlayer);
                    visualizer.setEnabled(true);
                    mediaPlayer.start();
                    isplaying = true;

                    countDownTimer = new CountDownTimer(item.getRecordTime(),1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            vhAudio.duration.setText(getHumanTimeText(millisUntilFinished));
                        }

                        @Override
                        public void onFinish() {
                            vhAudio.duration.setText(item.getDuration());
                        }
                    }.start();

                    vhAudio.btn_play.setImageResource(R.drawable.img_stop);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            visualizer.setEnabled(false);
                            vhAudio.btn_play.setImageResource(R.drawable.img_play);
                            isplaying = false;
                        }
                    });
                }
            }
        });

    }

    private String getHumanTimeText(long milisecond) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milisecond),
                TimeUnit.MILLISECONDS.toSeconds(milisecond) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milisecond)));
    }

    @Override
    public int getItemCount() {
        return audioRecList.size();
    }

    public class VHAudio extends RecyclerView.ViewHolder {
        public TextView timestamp,duration;
        public ImageView btn_play;
        public MusicWave musicWave;
        public RelativeLayout viewBackground,viewForeground;

        public VHAudio(@NonNull View itemView) {
            super(itemView);

            timestamp = itemView.findViewById(R.id.timestamp);
            duration = itemView.findViewById(R.id.duration);
            btn_play = itemView.findViewById(R.id.btn_play);
            musicWave = itemView.findViewById(R.id.waveAudio);
            viewBackground = itemView.findViewById(R.id.viewBackground);
            viewForeground = itemView.findViewById(R.id.viewForeground);
        }
    }

    private void setupVisualizer(MusicWave musicWave, MediaPlayer mediaPlayer) {
        visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                musicWave.updateVisualizer(waveform);
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {

            }
        },Visualizer.getMaxCaptureRate() / 2,true,false);
        visualizer.setEnabled(true);
    }

}
