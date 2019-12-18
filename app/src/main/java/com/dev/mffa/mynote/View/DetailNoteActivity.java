package com.dev.mffa.mynote.View;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anshul.gooey.GooeyMenu;
import com.dev.mffa.mynote.App.AppDatabase;
import com.dev.mffa.mynote.App.AudioAdapter;
import com.dev.mffa.mynote.App.AudioRecDataSource;
import com.dev.mffa.mynote.App.AudioRecRepository;
import com.dev.mffa.mynote.App.Const;
import com.dev.mffa.mynote.App.ImageDataSource;
import com.dev.mffa.mynote.App.ImageRepository;
import com.dev.mffa.mynote.App.NoteDataSource;
import com.dev.mffa.mynote.App.NoteRepository;
import com.dev.mffa.mynote.App.NotesAdapter;
import com.dev.mffa.mynote.Network.Model.AudioRec;
import com.dev.mffa.mynote.Network.Model.Image;
import com.dev.mffa.mynote.Network.Model.Note;
import com.dev.mffa.mynote.R;
import com.dev.mffa.mynote.Utils.RecyclerItemTouchHelper;
import com.dev.mffa.mynote.View.Fragment.HomeFragment;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkTextView;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

public class DetailNoteActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private static final String TAG = DetailNoteActivity.class.getSimpleName();
    private AutoLinkTextView at_note;
    private EditText edt_note;
    private ImageView img_scanner,btn_dlt_img;
    private TextView timestamp,length_karakter;
    private LinearLayout ll_timestamps;
    private RelativeLayout rl_edt_notes;
    private CompositeDisposable disposable = new CompositeDisposable();
    private NoteRepository noteRepository;
    private static AudioRecRepository audioRecRepository;
    private static ImageRepository imageRepository;
    private RecordView recordView;
    private RecordButton recordButton;
    private static String pathAudio;
    private String time;
    private String eNote;
    private String mTimestamp;
    private String keyActivity;
    private static String pathImage;
    private String sep;
    private String newFolder;
    private String timestamps;
    private String extStorageDirectory;
    private boolean mChecked,mFavorite;
    private int idM;
    private MediaRecorder mediaRecorder;
    private RecyclerView recyclerView;
    private AudioAdapter adapter;
    private List<AudioRec> audioRecList = new ArrayList<>();
    private Animation animFadeIn,animFadeOut;
    private Random random = new Random();
    private LinearLayout coordinatorLayout;
    private boolean insertAudio = false;
    private boolean insertCharacter = false;
    private boolean insertImage = false;
    private String sizeChar;
    private boolean istartRecord = false;
    private GooeyMenu gooeyMenu;
    private Bitmap bitmap = null;
    private Uri imageUri;
    private static File fileAudio, fileImage;
    private DateFormat dateFormat;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_note);

        Intent intent = getIntent();
        idM = intent.getIntExtra(MainActivity.KEY_ID,0);
        eNote = intent.getStringExtra(HomeFragment.KEY_NOTE);
        mTimestamp = intent.getStringExtra(HomeFragment.KEY_TIMESTAMP);
        mChecked = intent.getBooleanExtra(HomeFragment.KEY_CHECHKED,false);
        mFavorite = intent.getBooleanExtra(HomeFragment.KEY_FAVORITE,false);
        keyActivity = intent.getStringExtra(HomeFragment.KEY_ACTIVITY);

        //database room
        AppDatabase appDatabase = AppDatabase.getmInstance(this);
        noteRepository = NoteRepository.getmInstance(NoteDataSource.getmInstance(appDatabase.noteDao()));
        audioRecRepository = AudioRecRepository.getmInstance(AudioRecDataSource.getmInstance(appDatabase.audioRecDAO()));
        imageRepository = ImageRepository.getmInstance(ImageDataSource.getmInstance(appDatabase.imageDAO()));

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        length_karakter = findViewById(R.id.length_karakter);
        img_scanner = findViewById(R.id.img_scanner);
        btn_dlt_img = findViewById(R.id.btn_dlt_img);
        timestamp = findViewById(R.id.timestamp);
        ll_timestamps = findViewById(R.id.ll_timestamp);
        rl_edt_notes = findViewById(R.id.rl_edt_note);
        recyclerView = findViewById(R.id.rec_audio);
        recordView = findViewById(R.id.record_view);
        recordButton = findViewById(R.id.record_button);
        gooeyMenu = findViewById(R.id.gooey_menu);
        edt_note = findViewById(R.id.edt_note);
        at_note = findViewById(R.id.at_note);

        //init
        recordButton.setRecordView(recordView);

        animFadeIn = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        animFadeOut = AnimationUtils.loadAnimation(this,R.anim.fade_out);

        dateFormat = new SimpleDateFormat("d-M-yyyy-HH-mm");
        timestamps = dateFormat.format(new Date());

        sep = File.separator; // Use this instead of hardcoding the "/"
        newFolder = "HiNote";
        extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File myNewFolder = new File(extStorageDirectory + sep + newFolder);
        myNewFolder.mkdir();

        if (keyActivity.equals(MainActivity.VALUE_SHOW_ALL)){
            ll_timestamps.setVisibility(View.GONE);
            at_note.setVisibility(View.GONE);
            edt_note.setVisibility(View.VISIBLE);
            edt_note.setText(eNote);
            gooeyMenu.startAnimation(animFadeIn);
            gooeyMenu.setOnMenuListener(new GooeyMenu.GooeyMenuInterface() {
                @Override
                public void menuOpen() {
                }

                @Override
                public void menuClose() {
                }

                @Override
                public void menuItemClicked(int gooeyMenuId) {
                    switch (gooeyMenuId){
                        case 1:
                            CropImage.activity().start(DetailNoteActivity.this);
                            break;
                        case 2:
                            if (imageUri != null){
                                if (edt_note.getText().toString().length() > 0){
                                    savebitmap(bitmap,sep,newFolder,idM,imageRepository,disposable);
                                    createNoteWithAudio(edt_note.getText().toString(),idM,mChecked);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(),"Enter Note!",Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (edt_note.getText().toString().length() > 0){
                                    createNoteWithAudio(edt_note.getText().toString(),idM,mChecked);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(),"Enter Note!",Toast.LENGTH_SHORT).show();
                                }
                            }
                            break;
                        case 3:
                            gooeyMenu.startAnimation(animFadeOut);
                            recordButton.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            });
        }
        else if (keyActivity.equals(MainActivity.VALUE_UPDATE_NOTES)){
            gooeyMenu.setVisibility(View.GONE);
            ll_timestamps.setVisibility(View.VISIBLE);
            edt_note.setVisibility(View.VISIBLE);
            edt_note.setText(eNote);
            recordButton.setVisibility(View.GONE);
            gooeyMenu.setVisibility(View.VISIBLE);
            at_note.setVisibility(View.INVISIBLE);
            gooeyMenu.setOnMenuListener(new GooeyMenu.GooeyMenuInterface() {
                @Override
                public void menuOpen() {
                }

                @Override
                public void menuClose() {
                }

                @Override
                public void menuItemClicked(int gooeyMenuId) {
                    switch (gooeyMenuId){
                        case 1:
                            CropImage.activity().start(DetailNoteActivity.this);
                            break;
                        case 2:
                            if (imageUri != null){
                                savebitmap(bitmap,sep,newFolder,idM,imageRepository,disposable);
                                Const.note.setNote(edt_note.getText().toString());
                                Const.note.setIconAudio(insertAudio);
                                Const.note.setIconImage(insertImage);
                                updateNoteWithAudio(Const.note);
                                finish();
                            } else {
                                if (insertImage == false){
                                    Const.note.setNote(edt_note.getText().toString());
                                    Const.note.setIconAudio(insertAudio);
                                    Const.note.setIconImage(false);
                                    updateNoteWithAudio(Const.note);
                                    finish();
                                } else {
                                    Const.note.setNote(edt_note.getText().toString());
                                    Const.note.setIconAudio(insertAudio);
                                    Const.note.setIconImage(insertImage);
                                    updateNoteWithAudio(Const.note);
                                    finish();
                                }
                            }
                            break;
                        case 3:
                            gooeyMenu.startAnimation(animFadeOut);
                            recordButton.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            });

            if (Const.note.isIconImage() == false){
                btn_dlt_img.setVisibility(View.GONE);
            } else {
                btn_dlt_img.setVisibility(View.VISIBLE);
                btn_dlt_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn_dlt_img.setVisibility(View.GONE);
                        deleteImageInUpdate(Const.image);
                    }
                });
            }
        }
        else if (keyActivity.equals(MainActivity.VALUE_HOMEFRAGMENT)){
            gooeyMenu.setVisibility(View.GONE);
            ll_timestamps.setVisibility(View.VISIBLE);
            at_note.setVisibility(View.VISIBLE);
            edt_note.setVisibility(View.GONE);
            recordButton.setVisibility(View.GONE);
            at_note.setText(eNote);
        }

        timestamp.setText(mTimestamp);
        sizeChar = eNote.replaceAll(" ","");
        length_karakter.setText(String.valueOf(sizeChar.length()+" Character"));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new AudioAdapter(getApplicationContext(),audioRecList);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        //Cancel Bounds is when the Slide To Cancel text gets before the timer . default is 8
        recordView.setCancelBounds(8);

        recordView.setSmallMicColor(Color.parseColor("#c2185b"));

        //prevent recording under one Second
        recordView.setLessThanSecondAllowed(true);

        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                try {
                    String unique = Integer.toString(Math.abs(random.nextInt()));
                    int aUnique = Integer.parseInt(unique);
                    pathAudio = Environment.getExternalStorageDirectory().toString()
                            + sep + newFolder + sep + "HiWav-"+timestamps+aUnique+".wav";
                    mediaRecorder = new MediaRecorder();
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                    mediaRecorder.setOutputFile(pathAudio);
                    mediaRecorder.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaRecorder.start();
                istartRecord = true;
            }

            @Override
            public void onCancel() {
                gooeyMenu.startAnimation(animFadeIn);
                recordButton.setVisibility(View.GONE);
                fileAudio = new File(pathAudio);
                fileAudio.delete();
            }

            @Override
            public void onFinish(long recordTime) {
                gooeyMenu.startAnimation(animFadeIn);
                recordButton.setVisibility(View.INVISIBLE);
                time = getHumanTimeText(recordTime);
                if (istartRecord == true){
                    mediaRecorder.stop();
                    //mediaRecorder.reset();
                    mediaRecorder.release();
                    mediaRecorder = null;
                }
                insertAudioRecord(pathAudio, idM, recordTime);
            }

            @Override
            public void onLessThanSecond() {

            }
        });

        at_note.addAutoLinkMode(
                AutoLinkMode.MODE_PHONE,
                AutoLinkMode.MODE_EMAIL,
                AutoLinkMode.MODE_HASHTAG,
                AutoLinkMode.MODE_MENTION,
                AutoLinkMode.MODE_URL
        );
        at_note.setUrlModeColor(ContextCompat.getColor(this,R.color.blue_800));
        at_note.setEmailModeColor(ContextCompat.getColor(this,R.color.red_800));
        at_note.setMentionModeColor(ContextCompat.getColor(this,R.color.cyan_700));
        at_note.setPhoneModeColor(ContextCompat.getColor(this,R.color.green_800));
        at_note.setMentionModeColor(ContextCompat.getColor(this,R.color.yellow_800));

        loadAudioById();
        loadImageById();
    }

    private File savebitmap(Bitmap bitmap, String sep, String newFolder, int idNote, ImageRepository imageRepository, CompositeDisposable disposable) {
        Random random = new Random();
        String unique = Integer.toString(Math.abs(random.nextInt()));
        DateFormat dateFormat1 = new SimpleDateFormat("d-M-yyyy-HH-mm");
        String timestamps = dateFormat1.format(new Date());
        int aUnique = Integer.parseInt(unique);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        pathImage = Environment.getExternalStorageDirectory().toString()
                + sep + newFolder + sep + "HiJPG-"+timestamps+aUnique+".jpg";
        File f = new File(pathImage);
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        insertImage = true;
        String finalPathImage = pathImage;
        Disposable disposable2 = (Disposable) Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                String id = Integer.toString(Math.abs(random.nextInt()));
                int idImage = Integer.parseInt(id);
                Image item = new Image(idImage, idNote, finalPathImage);
                Const.image = item;
                imageRepository.insertImage(item);
                emitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe();
        disposable.add(disposable2);

        return f;
    }

    private void loadImageById() {
        disposable.add(imageRepository.getImageByIdNote(String.valueOf(idM))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Image>() {
                    @Override
                    public void accept(Image image) throws Exception {
                        File imgFile = new  File(image.getImages());

                        if(imgFile.exists()){

                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                            img_scanner.setImageBitmap(myBitmap);

                        }
                    }
                }));
    }

    public static void deleteImage(Image image){
        CompositeDisposable disposable = new CompositeDisposable();
        Disposable disposable2 = (Disposable) Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                imageRepository.deleteImage(image);
                fileImage = new File(pathImage);
                fileImage.delete();
                emitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe();
        disposable.add(disposable2);
    }

    private void deleteImageInUpdate(Image image){
        insertImage = false;
        Disposable disposable2 = (Disposable) Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                imageRepository.deleteImage(image);
                fileImage = new File(Const.image.getImages());
                fileImage.delete();
                emitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        img_scanner.setImageResource(android.R.color.transparent);
                    }
                });
        disposable.add(disposable2);
    }

    public static void deleteAllAudio(AudioRec audioRec){
        CompositeDisposable disposable = new CompositeDisposable();
        Disposable disposable2 = (Disposable) Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                audioRecRepository.deleteAudio(audioRec);
                fileAudio = new File(pathAudio);
                fileAudio.delete();
                emitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe();
        disposable.add(disposable2);
    }

    private void deleteRecord(AudioRec audioRec) {
        Disposable disposable2 = (Disposable) Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                audioRecRepository.deleteAudio(audioRec);
                fileAudio = new File(pathAudio);
                fileAudio.delete();
                emitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        loadAudioById();
                    }
                });
        disposable.add(disposable2);
        if (audioRecList.size() == 0){
            insertAudio = false;
        }
    }

    private void updateNoteWithAudio(Note eNote) {
        Disposable disposable2 = (Disposable) Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                noteRepository.updateNote(eNote);
                emitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe();
        disposable.add(disposable2);
    }

    private void createNoteWithAudio(String note, int iId, boolean mChecked) {
        Disposable disposable2 = (Disposable) Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                DateFormat dateFormat1 = new SimpleDateFormat("d MMM yyyy HH:mm");
                String timestamps = dateFormat1.format(new Date());
                char tglAwal = timestamps.charAt(0);
                char tglAkhir = timestamps.charAt(1);
                String tglfull = String.valueOf(String.format("%s%s",tglAwal,tglAkhir));
                insertCharacter = true;
                Note item = new Note(idM,note,timestamps,mChecked,insertCharacter,insertAudio,insertImage,mFavorite,tglfull);
                noteRepository.insertNote(item);
                emitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe();

        disposable.add(disposable2);
    }

    private void loadAudioById() {
        disposable.add(audioRecRepository.getAudioByIdNote(String.valueOf(idM))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<List<AudioRec>>() {
            @Override
            public void accept(List<AudioRec> audioRecs) throws Exception {
                audioRecList.clear();
                audioRecList.addAll(audioRecs);
                adapter.notifyDataSetChanged();
            }
        }));
    }

    private String getHumanTimeText(long milisecond) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milisecond),
                TimeUnit.MILLISECONDS.toSeconds(milisecond) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milisecond)));
    }

    private void insertAudioRecord(String pathAudio, int idNote, long recordTime) {
        insertAudio = true;
        Disposable disposable2 = (Disposable) Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                DateFormat dateFormat1 = new SimpleDateFormat("d MMM yyyy HH:mm");
                String timestamps = dateFormat1.format(new Date());
                String id = Integer.toString(Math.abs(random.nextInt()));
                int idAudio = Integer.parseInt(id);
                AudioRec item = new AudioRec(idAudio, idNote, pathAudio, time, timestamps, recordTime);
                Const.audioRec = item;
                audioRecRepository.insertAudio(item);
                emitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        loadAudioById();
                    }
                });
        disposable.add(disposable2);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof AudioAdapter.VHAudio) {

            // remove the item from recycler view
            deleteRecord(audioRecList.get(viewHolder.getAdapterPosition()));
            //Const.audioRec = audioRecList.get(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Audio delete!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    restoreAudio(AudioAdapter.item);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private void restoreAudio(AudioRec audioRec) {
        Disposable disposable2 = (Disposable) Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                audioRecRepository.insertAudio(audioRec);
                emitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        loadAudioById();
                    }
                });
        disposable.add(disposable2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                img_scanner.setImageURI(imageUri);
                btn_dlt_img.setVisibility(View.VISIBLE);
                btn_dlt_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (imageUri != null){
                            img_scanner.setImageResource(android.R.color.transparent);
                            btn_dlt_img.setVisibility(View.GONE);
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calc,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.calculator:
                int value = 0;
                new CalculatorDialog(this) {
                    @Override
                    public void onResult(String result) {

                        NumberFormat nf = NumberFormat.getInstance();
                        double number = 0;
                        try {
                            number =nf.parse(result).doubleValue();
                            //et_price.setText(number+ "");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }.setValue(value).showDIalog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (keyActivity.equals(MainActivity.VALUE_SHOW_ALL)){
            if (AudioAdapter.mediaPlayer != null){
                AudioAdapter.mediaPlayer.stop();
            }
        } else if (keyActivity.equals(MainActivity.VALUE_UPDATE_NOTES)){
            if (AudioAdapter.mediaPlayer != null){
                AudioAdapter.mediaPlayer.stop();
            }
            Const.note.setIconAudio(insertAudio);
            updateNoteWithAudio(Const.note);
        } else if (keyActivity.equals(MainActivity.VALUE_HOMEFRAGMENT)){
            if (AudioAdapter.mediaPlayer != null){
                AudioAdapter.mediaPlayer.stop();
            }
        }
        Bungee.fade(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (AudioAdapter.mediaPlayer != null) {
            AudioAdapter.mediaPlayer.release();
            AudioAdapter.mediaPlayer = null;
        }
        disposable.clear();
    }
}
