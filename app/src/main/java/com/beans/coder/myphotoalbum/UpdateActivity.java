package com.beans.coder.myphotoalbum;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UpdateActivity extends AppCompatActivity {

    private EditText newName;
    private EditText newDate;
    private EditText newPlace;
    private EditText newEvent;
    private ImageView updateImageView;
    private Button nextTen;
    private Button prev;
    private Button next;
    private Button update;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private final String userID = user.getUid();
    private String imageUri;
    private String key;
    private List<Photo> photoList;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        newName = findViewById(R.id.new_name);
        newDate = findViewById(R.id.new_date);
        newPlace = findViewById(R.id.new_place);
        newEvent = findViewById(R.id.new_event);
        updateImageView = findViewById(R.id.update_image_view);
        nextTen = findViewById(R.id.btn_next_ten);
        prev = findViewById(R.id.btn_prev);
        next = findViewById(R.id.btn_next);
        update = findViewById(R.id.button_update);

        photoList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference().child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot ds : children) {
                    // shake hand with each of child(id)
                    Photo photo = ds.getValue(Photo.class);
                    photo.setId(ds.getKey());
                    photo.setName(photo.getName());
                    photo.setDate(photo.getDate());
                    photo.setPlace(photo.getPlace());
                    photo.setEvent(photo.getEvent());
                    photo.setImageUri(photo.getImageUri());

                    photoList.add(photo);
                }
                if(getIntent().hasExtra("Key") && getIntent().hasExtra("Name") && getIntent().hasExtra("Dphoto") &&
                        getIntent().hasExtra("Place") && getIntent().hasExtra("Event") && getIntent().hasExtra("Image")){
                    key = getIntent().getStringExtra("Key");
                    String name = getIntent().getStringExtra("Name");
                    String date = getIntent().getStringExtra("Dphoto");
                    String place = getIntent().getStringExtra("Place");
                    String event = getIntent().getStringExtra("Event");
                    imageUri = getIntent().getStringExtra("Image");

                    newName.setText(name);
                    newDate.setText(date);
                    newPlace.setText(place);
                    newEvent.setText(event);
                    Picasso.get().load(imageUri).into(updateImageView);
                }else {
                    Photo photo = photoList.get(i);
                    showPhoto(photo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
        nextTen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextTen();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prev();
            }
        });
    }
    public void showPhoto(Photo photo) {
        String name = photo.getName();
        String date = photo.getDate();
        String place = photo.getPlace();
        String event = photo.getEvent();
        imageUri = photo.getImageUri();
        key = photo.getId();

        newName.setText(name);
        newDate.setText(date);
        newPlace.setText(place);
        newEvent.setText(event);
        Picasso.get().load(imageUri).into(updateImageView);
    }
    public void update(){
        if(imageUri == null || newName.getText().toString().isEmpty() || newDate.getText().toString().isEmpty() ||
                newPlace.getText().toString().isEmpty() || newEvent.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please Fill Empty Fields", Toast.LENGTH_LONG).show();
            return;
        }
            Photo photo = new Photo();

            photo.setName(newName.getText().toString().trim());
            photo.setDate(newDate.getText().toString().trim());
            photo.setPlace(newPlace.getText().toString().trim());
            photo.setEvent(newEvent.getText().toString().trim());
            photo.setImageUri(imageUri);

            databaseReference.child(key).setValue(photo);

            Toast.makeText(this,"Updated Successfully",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(UpdateActivity.this,PhotoAlbumActivity.class);
            startActivity(intent);
    }
    public void nextTen(){
        i = i + 9;
        if(i>photoList.size()) {
            i = photoList.size() - 1;
            System.out.println("i = "+i);
            Toast toast = Toast.makeText(this,"Last Photo of List",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        Photo photo = photoList.get(i);
        showPhoto(photo);
    }
    public void next(){
       i = i + 1;
        if(i>photoList.size()-1) {
            i = photoList.size() - 1;
            Toast toast = Toast.makeText(this,"Last Photo of List",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        Photo photo = photoList.get(i);
        showPhoto(photo);
    }
    public void prev(){
        i = i - 1;
        if(i<0) {
            i = 0;
            Toast toast = Toast.makeText(this,"First Photo of List",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        Photo photo = photoList.get(i);
        showPhoto(photo);
    }
}
