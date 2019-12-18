package com.dev.mffa.mynote.View.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.applikeysolutions.cosmocalendar.selection.OnDaySelectedListener;
import com.applikeysolutions.cosmocalendar.selection.SingleSelectionManager;
import com.applikeysolutions.cosmocalendar.view.CalendarView;
import com.dev.mffa.mynote.App.AppDatabase;
import com.dev.mffa.mynote.App.NoteDataSource;
import com.dev.mffa.mynote.App.NoteRepository;
import com.dev.mffa.mynote.App.TimelineAdapter;
import com.dev.mffa.mynote.Network.Model.Note;
import com.dev.mffa.mynote.R;
import com.dev.mffa.mynote.Utils.MyDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {
    private CalendarView calendarView;
    private TextView title_toolbar,txt_empty_notes_view;
    private RecyclerView recyclerView;
    private CompositeDisposable disposable = new CompositeDisposable();
    private TimelineAdapter adapter;
    private List<Note> noteList = new ArrayList<>();
    private NoteRepository noteRepository;
    private static String date;
    String currentDate;

    public CalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        //database room
        AppDatabase appDatabase = AppDatabase.getmInstance(getContext());
        noteRepository = NoteRepository.getmInstance(NoteDataSource.getmInstance(appDatabase.noteDao()));

        txt_empty_notes_view = view.findViewById(R.id.txt_empty_notes_view);
        recyclerView = view.findViewById(R.id.rec_timeline);
        calendarView = view.findViewById(R.id.calendar_view);
        calendarView.setCalendarOrientation(OrientationHelper.HORIZONTAL);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL, 16));
        adapter = new TimelineAdapter(getContext(),noteList);
        recyclerView.setAdapter(adapter);

        calendarView.setSelectionManager(new SingleSelectionManager(new OnDaySelectedListener() {
            @Override
            public void onDaySelected() {
                currentDate = String.valueOf(calendarView.getSelectedDays().get(0).getCalendar().getTime());
                char tglAwal = currentDate.charAt(8);
                char tglAkhir = currentDate.charAt(9);
                String tglfull = String.valueOf(String.format("%s%s",tglAwal,tglAkhir));
                loadBySelectedDay(tglfull);
            }
        }));

        return view;
    }

    private void loadBySelectedDay(String tgl){
        disposable.add(noteRepository.getNoteByDate(tgl)
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
            txt_empty_notes_view.setVisibility(View.GONE);
        } else {
            txt_empty_notes_view.setVisibility(View.VISIBLE);
        }
    }
}
