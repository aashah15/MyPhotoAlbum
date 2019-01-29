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

import java.util.ArrayList;

public class SearchByEventActivity extends AppCompatActivity {

    private ArrayList<String> listByEvent;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private FirebaseAuth auth;
    private String event = "event";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_event);

        listView = findViewById(R.id.search_by_event_list);

        listByEvent = new ArrayList<>();

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listByEvent);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = listByEvent.get(position);
                String field = event;
                Intent intent = new Intent(SearchByEventActivity.this,ImagesActivity.class);
                intent.putExtra("OrderBy",field);
                intent.putExtra("EqualTo",selectedItem);
                startActivity(intent);
            }
        });
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        final String userID = user.getUid();
        Query query = FirebaseDatabase.getInstance().getReference().child(userID)
        .orderByChild("event");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // get all of the children at this level
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                // shake hand with each of child (userId)

                for(DataSnapshot ds: children) {
                    // shake hand with each of child(id)
                    Photo photo = new Photo();
                    String Event = ds.getValue(Photo.class).getEvent();
                    photo.setEvent(Event);
                    String event = photo.getEvent();
                    if(!listByEvent.contains(event)) {
                        listByEvent.add(event);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SearchByEventActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
