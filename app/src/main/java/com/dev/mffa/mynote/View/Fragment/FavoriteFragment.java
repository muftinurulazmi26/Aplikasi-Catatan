package com.dev.mffa.mynote.View.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dev.mffa.mynote.App.AppDatabase;
import com.dev.mffa.mynote.App.NoteDataSource;
import com.dev.mffa.mynote.App.NoteRepository;
import com.dev.mffa.mynote.App.NotesAdapter;
import com.dev.mffa.mynote.Network.Model.Note;
import com.dev.mffa.mynote.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView textView;
    private NotesAdapter adapter;
    private List<Note> noteList = new ArrayList<>();
    private CompositeDisposable disposable = new CompositeDisposable();
    private NoteRepository noteRepository;

    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        getActivity().setTitle("Favorite");

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        //database room
        AppDatabase appDatabase = AppDatabase.getmInstance(getContext());
        noteRepository = NoteRepository.getmInstance(NoteDataSource.getmInstance(appDatabase.noteDao()));

        recyclerView = view.findViewById(R.id.recycler_view);
        textView = view.findViewById(R.id.txt_empty_notes_view);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        adapter = new NotesAdapter(getContext(),noteList);
        recyclerView.setAdapter(adapter);

        loadByChecked();

        return view;
    }

    private void loadByChecked() {
        disposable.add(noteRepository.getNoteByFav(true)
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
                }));
    }

    private void toggleEmptyNotes() {
        if (noteList.size() > 0) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
