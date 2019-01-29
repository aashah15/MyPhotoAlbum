package com.beans.coder.myphotoalbum;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ImagesActivity extends AppCompatActivity implements ImageAdapter.onItemClickListener {

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private FirebaseStorage storage;
    private DatabaseReference databaseReference;
    private ValueEventListener dbListener;
    private List<Photo> photos;
    private ArrayList<String> dates;
    private List<Photo> photosList;
    private ProgressBar progressBarCircle;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private final String userID = user.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBarCircle = findViewById(R.id.progress_bar_circle);

        photos = new ArrayList<>();
        dates = new ArrayList<>();
        photosList = new ArrayList<>();
        imageAdapter = new ImageAdapter(ImagesActivity.this,photosList);

        if(getIntent().hasExtra("OrderBy") && getIntent().hasExtra("EqualTo")) {
            String orderBy = getIntent().getStringExtra("OrderBy");
            String equalTo = getIntent().getStringExtra("EqualTo");
            storage = FirebaseStorage.getInstance();
            databaseReference = FirebaseDatabase.getInstance().getReference(userID);
            Query query = FirebaseDatabase.getInstance().getReference().child(userID)
                    .orderByChild(orderBy)
                    .equalTo(equalTo);
            dbListener = query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //photos.clear();
                    // get all of the children at this level
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    // shake hand with each of child (userId)
                    for (DataSnapshot ds : children) {
                        // shake hand with each of child(id)
                        Photo photo = ds.getValue(Photo.class);
                        photo.setName(photo.getName());
                        photo.setDate(photo.getDate());
                        photo.setPlace(photo.getPlace());
                        photo.setEvent(photo.getEvent());
                        photo.setImageUri(photo.getImageUri());
                        photo.setKey(ds.getKey());

                        String date = photo.getDate();
                        photos.add(photo);
                        dates.add(date);
                    }
                    try {
                        dates = sortDates(dates);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    for(int i=dates.size()-1; i>=0; i--){
                        for(int j=0; j<photos.size(); j++){
                            Photo photo = photos.get(j);
                            if(dates.get(i).equals(photo.getDate())){
                                photosList.add(photo);
                            }
                        }
                    }

                    recyclerView.setAdapter(imageAdapter);
                    imageAdapter.setOnItemClickListener(ImagesActivity.this);
                    progressBarCircle.setVisibility(View.INVISIBLE);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ImagesActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBarCircle.setVisibility(View.INVISIBLE);
                }
            });
        }
    }
    // Sorting List by date format
    private ArrayList<String> sortDates(ArrayList<String> dates) throws ParseException {
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        Map<Date, String> dateFormatMap = new TreeMap<>();
        for (String date: dates)
            dateFormatMap.put(f.parse(date),date);
        return new ArrayList<>(dateFormatMap.values());
    }
    @Override
    public void onItemClick(int position) {
        final Photo selectedItem = photos.get(position);
        String selectedEvent = selectedItem.getEvent();
        Toast.makeText(ImagesActivity.this,selectedEvent,Toast.LENGTH_LONG).show();
    }
    @Override
    public void onUpdateClick(int position) {
        final Photo selectedItem = photos.get(position);
        String selectedKey = selectedItem.getKey();
        String selectedName = selectedItem.getName();
        String selectedDate = selectedItem.getDate();
        String selectedPlace = selectedItem.getPlace();
        String selectedEvent = selectedItem.getEvent();
        String selectedImage = selectedItem.getImageUri();

        Intent intent = new Intent(this,UpdateActivity.class);

        intent.putExtra("Key",selectedKey);
        intent.putExtra("Name",selectedName);
        intent.putExtra("date",selectedDate);
        intent.putExtra("Place",selectedPlace);
        intent.putExtra("Event",selectedEvent);
        intent.putExtra("Image",selectedImage);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ImagesActivity.this);
        builder.setTitle("Are you sure to Delete this?");
        builder.setMessage("Deletion is permanent...");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deletePhoto(position);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog ad = builder.create();
        ad.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(dbListener);
    }
    public void deletePhoto(int position){
        final Photo selectedItem = photos.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference imageRef = storage.getReferenceFromUrl(selectedItem.getImageUri());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                databaseReference.child(selectedKey).removeValue();
                photos.remove(selectedItem);
                Toast.makeText(ImagesActivity.this,"Item Deleted!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
