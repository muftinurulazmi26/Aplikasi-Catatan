package com.dev.mffa.mynote.App;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.mffa.mynote.Network.Model.Note;
import com.dev.mffa.mynote.R;
import com.dev.mffa.mynote.View.DetailNoteActivity;
import com.dev.mffa.mynote.View.Fragment.HomeFragment;
import com.dev.mffa.mynote.View.MainActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import spencerstudios.com.bungeelib.Bungee;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.VHNotes> {
    private Context context;
    private List<Note> noteList;
    private CompositeDisposable disposableComposit = new CompositeDisposable();
    private NoteRepository noteRepository;
    public static Note note;

    public TimelineAdapter(Context context, List<Note> noteList) {
        this.context = context;
        this.noteList = noteList;
    }

    @NonNull
    @Override
    public TimelineAdapter.VHNotes onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.note_list_row_timeline,viewGroup,false);

        //database room
        AppDatabase appDatabase = AppDatabase.getmInstance(context);
        noteRepository = NoteRepository.getmInstance(NoteDataSource.getmInstance(appDatabase.noteDao()));

        return new VHNotes(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineAdapter.VHNotes vhNotes, int position) {
        note = noteList.get(position);

        vhNotes.tv_note.setText(note.getNote());
        // Displaying dot from HTML character code
        vhNotes.tv_dot.setText(Html.fromHtml("&#8226;"));
        // Changing dot color to random color
        vhNotes.tv_dot.setTextColor(getRandomMaterialColor("A700"));
        // Formatting and displaying timestamp
        vhNotes.tv_timestamp.setText(note.getTimestamp());

        //check icon
        if (note.isIconText() == true){
            vhNotes.ic_text.setVisibility(View.VISIBLE);
        } else {
            vhNotes.ic_text.setVisibility(View.GONE);
        }
        if (note.isIconAudio() == true){
            vhNotes.ic_sound.setVisibility(View.VISIBLE);
        } else {
            vhNotes.ic_sound.setVisibility(View.GONE);
        }
        if (note.isIconImage() == true){
            vhNotes.ic_image.setVisibility(View.VISIBLE);
        } else {
            vhNotes.ic_image.setVisibility(View.GONE);
        }
        if (note.isFavorite() == true){
            vhNotes.ic_fav_white.setImageResource(R.drawable.ic_favorite_red_24dp);
            vhNotes.ic_fav_white.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,"Delete favorite",Toast.LENGTH_SHORT).show();
                    noFavorite(noteList.get(position));
                }
            });
        } else {
            vhNotes.ic_fav_white.setImageResource(R.drawable.ic_favorite_border_white_24dp);
            vhNotes.ic_fav_white.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,"Add to favorite",Toast.LENGTH_SHORT).show();
                    thisFavorite(noteList.get(position));
                }
            });
        }

        //checkbox on click
        if (note.isChecked() == true){
            vhNotes.checkBox.setImageResource(R.drawable.ic_check_box_white_24dp);
            vhNotes.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NoCheckedButton(noteList.get(position));
                }
            });
        } else {
            vhNotes.checkBox.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
            vhNotes.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context,"Done",Toast.LENGTH_SHORT).show();
                    CheckedButton(noteList.get(position));
                }
            });
        }


        vhNotes.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailNoteActivity.class);
                intent.putExtra(MainActivity.KEY_ID,noteList.get(position).getIdNote());
                intent.putExtra(HomeFragment.KEY_NOTE,noteList.get(position).getNote());
                intent.putExtra(HomeFragment.KEY_TIMESTAMP,noteList.get(position).getTimestamp());
                intent.putExtra(HomeFragment.KEY_ACTIVITY,MainActivity.VALUE_HOMEFRAGMENT);
                context.startActivity(intent);
                Bungee.shrink(context);
            }
        });
    }

    private void thisFavorite(Note note) {
        note.setFavorite(true);
        insertFav(note);
    }

    private void noFavorite(Note note) {
        note.setFavorite(false);
        updateFav(note);
    }

    private void NoCheckedButton(Note note) {
        note.setChecked(false);
        updateDone(note);
    }

    private void CheckedButton(Note note) {
        note.setChecked(true);
        insertDone(note);
    }

    private void updateDone(Note update) {
        Disposable disposable2 = (Disposable) Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                noteRepository.updateNote(update);
                emitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe();
        disposableComposit.add(disposable2);
    }

    private void insertDone(Note insert) {
        Disposable disposable2 = (Disposable) Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                noteRepository.updateNote(insert);
                emitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe();
        disposableComposit.add(disposable2);
    }

    private void updateFav(Note update) {
        Disposable disposable2 = (Disposable) Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                noteRepository.updateNote(update);
                emitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe();
        disposableComposit.add(disposable2);
    }

    private void insertFav(Note insert) {
        Disposable disposable2 = (Disposable) Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                noteRepository.updateNote(insert);
                emitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe();
        disposableComposit.add(disposable2);
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public class VHNotes extends RecyclerView.ViewHolder {
        public TextView tv_note,tv_dot,tv_timestamp;
        public RelativeLayout cardView;
        public ImageView ic_text,ic_sound,ic_image,ic_fav_white,checkBox;

        public VHNotes(@NonNull View itemView) {
            super(itemView);
            tv_dot = itemView.findViewById(R.id.dot);
            tv_note = itemView.findViewById(R.id.note);
            tv_timestamp = itemView.findViewById(R.id.timestamp);
            cardView = itemView.findViewById(R.id.cardView);
            checkBox = itemView.findViewById(R.id.checkBox);
            ic_image = itemView.findViewById(R.id.ic_image);
            ic_sound = itemView.findViewById(R.id.ic_sound);
            ic_text = itemView.findViewById(R.id.ic_text);
            ic_fav_white = itemView.findViewById(R.id.ic_fav_bor_white);
        }
    }

    /**
     * Chooses random color defined in res/array.xml
     */

    private int getRandomMaterialColor(String typeColor){
        int returnColor = Color.GRAY;
        int arrayId = context.getResources().getIdentifier("mdcolor_"+typeColor,"array",context.getPackageName());

        if (arrayId != 0){
            TypedArray colors = context.getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private String formatDate(String dateStr){
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("d MMM yyyy HH:mm");
            return fmtOut.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
