package com.beans.coder.myphotoalbum;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PhotoAlbumActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Button btnChooseImage;
    private Button btnUpload;
    private Button btnShow;
    private Button btUpdate;
    private EditText fileName;
    private EditText date;
    private EditText place;
    private EditText event;
    private ImageView imageView;
    private ProgressBar progressBar;

    private Uri imageUri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private StorageTask uploadTask;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private final String userID = user.getUid();

    private List<Photo> photoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_album);

        btnChooseImage = findViewById(R.id.btn_choose_image);
        btnUpload = findViewById(R.id.btn_upload);
        btnShow = findViewById(R.id.btn_show);
        btUpdate = findViewById(R.id.bt_update);
        fileName = findViewById(R.id.edit_text_file_name);
        date = findViewById(R.id.edit_text_file_date);
        place = findViewById(R.id.edit_text_file_place);
        event = findViewById(R.id.edit_text_file_event);
        imageView = findViewById(R.id.image_view);
        progressBar = findViewById(R.id.progress_bar);
        auth = FirebaseAuth.getInstance();

        photoList = new ArrayList<>();

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uploadTask != null && uploadTask.isInProgress()){
                    Toast.makeText(PhotoAlbumActivity.this,"Upload in Progress...",Toast.LENGTH_SHORT).show();
                }else {
                    uploadeFile();
                }
            }
        });
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchByActivity();
            }
        });
        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpdateActivity();
            }
        });
    }
    public void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageView);
        }
    }
    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
    public void  uploadeFile(){
        if(imageUri == null || fileName.getText().toString().isEmpty() || date.getText().toString().isEmpty() ||
                place.getText().toString().isEmpty() || event.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please Fill Empty Fields", Toast.LENGTH_LONG).show();
            return;
        }
                final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." +
                        getFileExtension(imageUri));
                uploadTask = fileReference.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setProgress(0);
                                    }
                                }, 400);
                                Toast.makeText(PhotoAlbumActivity.this, "Upload Photo Successful", Toast.LENGTH_LONG).show();

                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!urlTask.isSuccessful()) ;
                                Uri downloadUrl = urlTask.getResult();

                                String id = databaseReference.push().getKey();
                                Photo photo = new Photo(id, fileName.getText().toString().trim(),
                                        date.getText().toString().trim(), place.getText().toString().trim(),
                                        event.getText().toString().trim(),
                                        downloadUrl.toString());

                                databaseReference.child(userID).child(id).setValue(photo);
                                clearFields();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(PhotoAlbumActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = 100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                                progressBar.setProgress((int) progress);
                            }
                        });
    }

    public void openSearchByActivity(){
        Intent intent = new Intent(this,SearchByActivity.class);
        startActivity(intent);
    }

    public void openUpdateActivity() {
        Intent intent = new Intent(this,UpdateActivity.class);
        startActivity(intent);
    }
    public void clearFields(){
        fileName.setText("");
        date.setText("");
        place.setText("");
        event.setText("");
        imageView.setImageBitmap(null);
    }
}
