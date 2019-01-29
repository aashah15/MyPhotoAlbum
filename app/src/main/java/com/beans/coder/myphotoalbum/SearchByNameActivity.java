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

public class SearchByNameActivity extends AppCompatActivity {

    private ArrayList<String> listByName = new ArrayList<>();
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private FirebaseAuth auth;
    private String name = "name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_name);

        listView = findViewById(R.id.search_by_name_list);

        listByName = new ArrayList<>();

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listByName);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = listByName.get(position);
                String field = name;
                Intent intent = new Intent(SearchByNameActivity.this,ImagesActivity.class);
                intent.putExtra("OrderBy",field);
                intent.putExtra("EqualTo",selectedItem);
                startActivity(intent);
            }
        });
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        final String userID = user.getUid();
        Query query = FirebaseDatabase.getInstance().getReference().child(userID)
                .orderByChild("name");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // get all of the children at this level
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                // shake hand with each of child (userId)

                for(DataSnapshot ds: children) {
                    // shake hand with each of child(id)
                    Photo photo = ds.getValue(Photo.class);
                    photo.setName(photo.getName());
                    String name = photo.getName();
                    if(!listByName.contains(name)) {
                        listByName.add(name);
                    }
                }

                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SearchByNameActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
