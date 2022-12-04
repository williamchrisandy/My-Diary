package com.theclowns.mydiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.Vector;

public class MainActivity extends AppCompatActivity
{
    RecyclerView recyclerViewDiary;
    DiaryAdapter diaryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewDiary = findViewById(R.id.recycler_view_diary);

        diaryAdapter = new DiaryAdapter(this);
        diaryAdapter.setOnItemClickListener(new DiaryAdapter.ClickListener()
        {
            @Override
            public void onItemClick(int diaryId)
            {
                intentToLocationActivity(diaryId);
            }

            @Override
            public void onItemDeletedClick(int diaryId)
            {
                DiaryHeaderDatabase diaryHeaderDatabase = new DiaryHeaderDatabase(MainActivity.this);
                diaryHeaderDatabase.deleteDiaryHeader(diaryId);
            }
        });

        recyclerViewDiary.setAdapter(diaryAdapter);
        recyclerViewDiary.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        loadDiaryData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId() == R.id.add_item)
        {
            DiaryHeaderDatabase diaryHeaderDatabase = new DiaryHeaderDatabase(this);
            diaryHeaderDatabase.insertDiaryHeader(new DiaryHeader(-1, "Untitled", "No Date", ""));

            intentToLocationActivity(-1);
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadDiaryData()
    {
        DiaryHeaderDatabase diaryHeaderDatabase = new DiaryHeaderDatabase(this);
        Vector<DiaryHeader> diaryHeaderVector = diaryHeaderDatabase.getDiaryHeader();
        diaryAdapter.setDiaryHeaderVector(diaryHeaderVector);
    }

    private void intentToLocationActivity(int diaryId)
    {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.KEY_DIARY_ID, diaryId);
        startActivity(intent);
    }
}