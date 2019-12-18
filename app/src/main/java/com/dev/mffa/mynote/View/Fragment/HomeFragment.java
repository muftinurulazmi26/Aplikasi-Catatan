package com.dev.mffa.mynote.View.Fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.mffa.mynote.App.AppDatabase;
import com.dev.mffa.mynote.App.AudioAdapter;
import com.dev.mffa.mynote.App.Const;
import com.dev.mffa.mynote.App.NoteDataSource;
import com.dev.mffa.mynote.App.NoteRepository;
import com.dev.mffa.mynote.App.NotesAdapter;
import com.dev.mffa.mynote.Lib.DbHelper;
import com.dev.mffa.mynote.Network.ApiClient;
import com.dev.mffa.mynote.Network.ApiService;
import com.dev.mffa.mynote.Network.Model.AudioRec;
import com.dev.mffa.mynote.Network.Model.Note;
import com.dev.mffa.mynote.Network.Model.User;
import com.dev.mffa.mynote.R;
import com.dev.mffa.mynote.Utils.PrefUtils;
import com.dev.mffa.mynote.Utils.RecyclerTouchListener;
import com.dev.mffa.mynote.View.DetailNoteActivity;
import com.dev.mffa.mynote.View.MainActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import spencerstudios.com.bungeelib.Bungee;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static String KEY_NOTE ="note_key";
    public static String KEY_TIMESTAMP ="timestamp_key";
    public static String KEY_ACTIVITY ="key_homefragment";
    public static String KEY_CHECHKED ="key_checked";
    public static String KEY_FAVORITE ="key_favorite";
    //public static String KEY_SHOW_ALL ="key_show_all";
    private CompositeDisposable disposableComposit = new CompositeDisposable();
    private NotesAdapter adapter;
    private List<Note> noteList;
    private RecyclerView recyclerView;
    private TextView textView;
    private DbHelper dbHelper = new DbHelper(getContext());
    private NoteRepository noteRepository;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        getActivity().setTitle("Hi Note");

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        //database room
        AppDatabase appDatabase = AppDatabase.getmInstance(getContext());
        noteRepository = NoteRepository.getmInstance(NoteDataSource.getmInstance(appDatabase.noteDao()));

        //dbHelper = new DbHelper(getApplicationContext());

        recyclerView = view.findViewById(R.id.recycler_view);
        textView = view.findViewById(R.id.txt_empty_notes_view);

        // white background notification bar
        //whiteNotificationBar(fab);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        noteList = new ArrayList<>();
        adapter = new NotesAdapter(getContext(),noteList);
        recyclerView.setAdapter(adapter);

        /*
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                showActionDialog(position);
            }
        }));

        loadAllNote();

        return view;
    }

    private void loadAllNote() {
        disposableComposit.add(noteRepository.getAllNote()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<List<Note>>() {
            @Override
            public void accept(List<Note> notes) throws Exception {
                noteList.clear();
                noteList.addAll(notes);
                adapter.notifyDataSetChanged();

                toggleEmptyNotes();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(getContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }));
    }

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose Option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    showNoteDialog(true, noteList.get(position), position);
                }
                else if (which == 1){
                    deleteNote(noteList.get(position),position);
                }
            }
        });
        builder.show();
    }

    private void toggleEmptyNotes() {
        if (noteList.size() > 0) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
        }
    }

    private void deleteNote(final Note note, final int position) {
        Disposable disposable2 = (Disposable) Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                noteRepository.deleteNote(note);
                emitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        loadAllNote();
                    }
                });
        disposableComposit.add(disposable2);

        if (Const.audioRec != null){
            DetailNoteActivity.deleteAllAudio(Const.audioRec);
        }
        if (Const.image != null){
            DetailNoteActivity.deleteImage(Const.image);
        }
    }

    private void showNoteDialog(final boolean shouldUpdate, final Note note, final int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.note_dialog,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);

        final EditText editText = view.findViewById(R.id.note);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title): getString(R.string.lbl_edit_note_title));

        if (shouldUpdate && note != null){
            editText.setText(note.getNote());
        }
        builder.setCancelable(false)
                .setPositiveButton(shouldUpdate ? "Update" : "Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNeutralButton("Show All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(),DetailNoteActivity.class);
                        intent.putExtra(MainActivity.KEY_ID,note.getIdNote());
                        intent.putExtra(KEY_NOTE,editText.getText().toString());
                        intent.putExtra(KEY_CHECHKED,note.isChecked());
                        intent.putExtra(KEY_CHECHKED,note.isFavorite());
                        intent.putExtra(HomeFragment.KEY_TIMESTAMP,note.getTimestamp());
                        Const.note = noteList.get(position);
                        intent.putExtra(HomeFragment.KEY_ACTIVITY,MainActivity.VALUE_UPDATE_NOTES);
                        getActivity().startActivity(intent);
                        Bungee.zoom(getActivity());
                    }
                });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(editText.getText().toString())){
                    Toast.makeText(getContext(), "Enter note!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating note

                if (shouldUpdate && note != null){
                    // update note by it's id
                    note.setNote(editText.getText().toString());
                    updateNote(note, position);
                } else {
                    // create new note
                    //createNote(editText.getText().toString());
                }
            }
        });
    }

    private void updateNote(final Note note, final int position) {
        Disposable disposable2 = (Disposable) Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                noteRepository.updateNote(note);
                emitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        loadAllNote();
                    }
                });
        disposableComposit.add(disposable2);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposableComposit.clear();
    }

}
