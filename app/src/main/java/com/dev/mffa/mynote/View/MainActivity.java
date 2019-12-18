package com.dev.mffa.mynote.View;

import android.Manifest;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.mffa.mynote.App.AppDatabase;
import com.dev.mffa.mynote.App.NoteDataSource;
import com.dev.mffa.mynote.App.NoteRepository;
import com.dev.mffa.mynote.App.NotesAdapter;
import com.dev.mffa.mynote.Network.ApiClient;
import com.dev.mffa.mynote.Network.ApiService;
import com.dev.mffa.mynote.Network.Model.Note;
import com.dev.mffa.mynote.Network.NoteDAO;
import com.dev.mffa.mynote.R;
import com.dev.mffa.mynote.View.Fragment.CalendarFragment;
import com.dev.mffa.mynote.View.Fragment.FavoriteFragment;
import com.dev.mffa.mynote.View.Fragment.HomeFragment;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.pedro.library.AutoPermissions;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ir.esfandune.calculatorlibe.CalculatorDialog;
import spencerstudios.com.bungeelib.Bungee;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static String KEY_NOTE ="note_key";
    public static String KEY_ID ="id_key";
    public static String VALUE_HOMEFRAGMENT ="value_homefragment";
    public static String VALUE_UPDATE_NOTES ="value_update_notes";
    public static String VALUE_SHOW_ALL ="value_show_all";
    private ApiService apiService;
    private CompositeDisposable disposable = new CompositeDisposable();
    private NotesAdapter adapter;
    private List<Note> noteList = new ArrayList<>();
    private SpaceNavigationView spaceNavigationView;
    private NoteRepository noteRepository;
    private Random idNotes = new Random();
    private static final AtomicInteger count = new AtomicInteger(0);
    private int noteID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_main);

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.INTERNET,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                            Toast.makeText(getApplicationContext(),"Please enable Permission this application",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
                .onSameThread()
                .check();

        //database room
        AppDatabase appDatabase = AppDatabase.getmInstance(this);
        noteRepository = NoteRepository.getmInstance(NoteDataSource.getmInstance(appDatabase.noteDao()));

        apiService = ApiClient.getRetrofit(getApplicationContext()).create(ApiService.class);
        adapter = new NotesAdapter(getApplicationContext(),noteList);

        spaceNavigationView = findViewById(R.id.spaceNV);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("HOME",R.drawable.ic_home_black_24dp));
        spaceNavigationView.addSpaceItem(new SpaceItem("FAVORITE",R.drawable.ic_favorite_black_24dp));

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                showNoteDialog(false,null,-1);
                noteID = count.incrementAndGet();
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                switch (itemIndex){
                    case 0:
                        Fragment homeFragment = new HomeFragment();
                        FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction1.replace(R.id.containFrame,homeFragment);
                        //fragmentTransaction1.addToBackStack(null);
                        fragmentTransaction1.commit();
                        break;
                    case 1:
                        Fragment favFragment = new FavoriteFragment();
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.containFrame,favFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                //Toast.makeText(MainActivity.this, itemIndex + " Selected " + itemName, Toast.LENGTH_SHORT).show();
            }
        });

        loadFragment(new HomeFragment());

    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.containFrame,fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void showNoteDialog(final boolean shouldUpdate, final Note note, final int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View view = layoutInflater.inflate(R.layout.note_dialog,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                        int noteIdToShowAl = count.incrementAndGet();
                        Intent intent = new Intent(MainActivity.this,DetailNoteActivity.class);
                        intent.putExtra(KEY_ID,noteIdToShowAl);
                        intent.putExtra(KEY_NOTE,editText.getText().toString());
                        intent.putExtra(HomeFragment.KEY_ACTIVITY,MainActivity.VALUE_SHOW_ALL);
                        startActivity(intent);
                        Bungee.zoom(MainActivity.this);
                    }
                });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(editText.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Enter note!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating note

                if (shouldUpdate && note != null){
                    // update note by it's id
                    //updateNote(note.getId(), editText.getText().toString(), position);
                } else {
                    // create new note
                    createNote(editText.getText().toString());
                }
            }
        });
    }

    private void createNote(String note) {
        Disposable disposable2 = (Disposable) Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                DateFormat dateFormat1 = new SimpleDateFormat("d MMM yyyy HH:mm");
                String timestamps = dateFormat1.format(new Date());
                char tglAwal = timestamps.charAt(0);
                char tglAkhir = timestamps.charAt(1);
                String tglfull = String.valueOf(String.format("%s%s",tglAwal,tglAkhir));
                Note item = new Note(noteID,note,timestamps,false,true,false,false,false,tglfull);
                noteRepository.insertNote(item);
                emitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("MESSAGE",throwable.getMessage());
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        loadAllNote();
                    }
                });
        disposable.add(disposable2);
    }

    private void loadAllNote() {
        disposable.add(noteRepository.getAllNote()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Note>>() {
                    @Override
                    public void accept(List<Note> notes) throws Exception {
                        noteList.clear();
                        noteList.addAll(notes);
                        adapter.notifyDataSetChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                Fragment homeFragment = new CalendarFragment();
                FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                fragmentTransaction1.replace(R.id.containFrame,homeFragment);
                fragmentTransaction1.addToBackStack(null);
                fragmentTransaction1.commit();
                break;
            case R.id.about:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                LayoutInflater layoutInflater = getLayoutInflater();
                View view = layoutInflater.inflate(R.layout.layout_about,null);
                alert.setView(view);

                TextView btn_about = view.findViewById(R.id.btn_about);
                TextView btn_donate = view.findViewById(R.id.btn_donate);
                TextView tv_about = view.findViewById(R.id.tv_about);

                tv_about.setMovementMethod(new ScrollingMovementMethod());

                btn_about.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tv_about.setText(getResources().getText(R.string.about));
                    }
                });

                btn_donate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tv_about.setText(getResources().getText(R.string.donate));
                    }
                });

                alert.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        spaceNavigationView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
