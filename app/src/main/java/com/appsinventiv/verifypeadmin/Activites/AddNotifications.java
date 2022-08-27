package com.appsinventiv.verifypeadmin.Activites;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;

import com.appsinventiv.verifypeadmin.Models.NotificationModel;
import com.appsinventiv.verifypeadmin.Models.User;
import com.appsinventiv.verifypeadmin.R;
import com.appsinventiv.verifypeadmin.Utils.CommonUtils;
import com.appsinventiv.verifypeadmin.Utils.CompressImage;
import com.appsinventiv.verifypeadmin.Utils.Constants;
import com.appsinventiv.verifypeadmin.Utils.NotificationAsync;
import com.appsinventiv.verifypeadmin.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

public class AddNotifications extends AppCompatActivity {


    private static final int REQUEST_CODE_CHOOSE = 23;
    ImageView image;
    Button pickImage, submit;
    EditText url, title, description;
    private ArrayList<String> mSelected = new ArrayList<>();
    private String imageUrl;
    private String livePicPath;
    private DatabaseReference mDatabase;
    ProgressBar progress;
    private ArraySet<String> fcmKeyList = new ArraySet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notifications);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
            this.setTitle("Add notification");
        }
        mDatabase = Constants.M_DATABASE;
        image = findViewById(R.id.image);
        pickImage = findViewById(R.id.pickImage);
        progress = findViewById(R.id.progress);
        submit = findViewById(R.id.submit);
        description = findViewById(R.id.description);
        url = findViewById(R.id.url);
        title = findViewById(R.id.title);

        getAllUsersFromDB();
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Options options = Options.init()
                        .setRequestCode(REQUEST_CODE_CHOOSE)                                           //Request code for activity results
                        .setCount(1)
                        .setExcludeVideos(true)
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                        ;                                       //Custom Path For media Storage

                Pix.start(AddNotifications.this, options);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageUrl == null) {
                    CommonUtils.showToast("Please select image");
                } else if (url.getText().length() == 0) {
                    url.setError("Enter url");
                } else if (title.getText().length() == 0) {
                    title.setError("Enter Title");
                } else if (description.getText().length() == 0) {
                    description.setError("Enter description");
                } else {
                    uploadPicture();
                }


            }
        });


    }

    private void getAllUsersFromDB() {
        mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    User user = snapshot1.getValue(User.class);
                    if (user != null && user.getFcmKey() != null && user.getFcmKey().length() > 0) {
                        fcmKeyList.add(user.getFcmKey());

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && data != null) {
            mSelected = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            CompressImage cimage = new CompressImage(AddNotifications.this);
            imageUrl = cimage.compressImage("" + mSelected.get(0));
            Glide.with(AddNotifications.this).load(mSelected.get(0)).into(image);
        }
    }

    private void uploadPicture() {
        progress.setVisibility(View.VISIBLE);
        try {
            String imgName = Long.toHexString(Double.doubleToLongBits(Math.random()));

            Uri file = Uri.fromFile(new File(imageUrl));

            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

            final StorageReference riversRef = mStorageRef.child("Photos").child(imgName);

            riversRef.putFile(file)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get a URL to the uploaded content

                        riversRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        livePicPath = "" + uri;
                                        saveData();

                                    }
                                });


                            }
                        });


                    })
                    .addOnFailureListener(exception -> {
                        // Handle unsuccessful uploads
                        // ...
//                        mDatabase.child("Errors").child("picUploadError").child(mDatabase.push().getKey()).setValue(exception.getMessage());
//
//                        CommonUtils.showToast("There was some error uploading pic");


                    });
        } catch (Exception e) {
//            mDatabase.child("Errors").child("mainError").child(mDatabase.push().getKey()).setValue(e.getMessage());
        }


    }

    private void saveData() {
        String id = System.currentTimeMillis() + "";
        NotificationModel model = new NotificationModel(
                id,
                url.getText().toString(),
                title.getText().toString(),
                description.getText().toString(),
                livePicPath,
                System.currentTimeMillis()
        );
        mDatabase.child("NotificationsFromAdmin").child(id).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                CommonUtils.showToast("Notification Added");
                CommonUtils.showToast("Sending Notifications to all users");
                sendNotifications();
//                finish();
            }
        });
    }

    private void sendNotifications() {
        progress.setVisibility(View.GONE);
        for (String fcmKey : fcmKeyList) {
            NotificationAsync notificationAsync = new NotificationAsync(this);
            String NotificationTitle = title.getText().toString();
            String NotificationMessage = description.getText().toString();
            notificationAsync.execute(
                    "ali",
                    fcmKey,
                    NotificationTitle,
                    NotificationMessage,
                    "admin",
                    "notification");
        }
    }


}