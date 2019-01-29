package com.beans.coder.myphotoalbum;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class SearchByDateActivity extends AppCompatActivity {

    private ArrayList<String> listByDate;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private FirebaseAuth auth;
    private String date = "date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_date);

        listView = findViewById(R.id.search_by_date_list);

        listByDate = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,listByDate);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = listByDate.get(position);
                String field = date;
                Intent intent = new Intent(SearchByDateActivity.this, ImagesActivity.class);
                intent.putExtra("OrderBy", field);
                intent.putExtra("EqualTo", selectedItem);
                startActivity(intent);
            }
        });
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        final String userID = user.getUid();
        Query query = FirebaseDatabase.getInstance().getReference().child(userID)
                .orderByChild("date");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // get all of the children at this level
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                // shake hand with each of child (userId)

                for(DataSnapshot ds: children) {
                    // shake hand with each of child(id)
                    Photo photo = new Photo();
                    photo.setDate(ds.getValue(Photo.class).getDate());
                    String date = photo.getDate();
                    // To eliminate duplicates
                    if(!listByDate.contains(date)) {
                        listByDate.add(date);
                    }
                }
                try {
                    listByDate = sortDates(listByDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // reversing order of list to gate dates in descending order
                Collections.reverse(listByDate);
                // inserting data in adapter with new sorted list
                adapter.clear();
                for(int i=0; i<listByDate.size(); i++) {
                    String date = listByDate.get(i);
                    System.out.println(date);
                    adapter.insert(date, i);
                }
                listView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SearchByDateActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Sorting List by date format
    private ArrayList<String> sortDates(ArrayList<String> dates) throws ParseException {
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        Map<Date, String> dateFormatMap = new TreeMap<>();
        for (String date: dates)
            dateFormatMap.put(f.parse(date), date);
        return new ArrayList<>(dateFormatMap.values());
    }

}
