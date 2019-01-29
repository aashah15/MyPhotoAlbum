package com.beans.coder.myphotoalbum;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SearchByActivity extends AppCompatActivity {

    private Button btnSearchByName;
    private Button btnSearchByDate;
    private Button btnSearchByPlace;
    private Button btnSearchByEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by);

        btnSearchByName = findViewById(R.id.btn_search_by_name);
        btnSearchByDate = findViewById(R.id.btn_search_by_date);
        btnSearchByPlace = findViewById(R.id.btn_search_by_place);
        btnSearchByEvent = findViewById(R.id.btn_search_by_event);

        btnSearchByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchByActivity.this,SearchByNameActivity.class);
                startActivity(intent);
            }
        });

        btnSearchByDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchByActivity.this,SearchByDateActivity.class);
                startActivity(intent);
            }
        });

        btnSearchByPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchByActivity.this,SearchByPlaceActivity.class);
                startActivity(intent);
            }
        });

        btnSearchByEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchByActivity.this,SearchByEventActivity.class);
                startActivity(intent);
            }
        });
    }
}
