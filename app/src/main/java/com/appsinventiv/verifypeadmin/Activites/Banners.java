package com.appsinventiv.verifypeadmin.Activites;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.verifypeadmin.Adapters.BannersAdapter;
import com.appsinventiv.verifypeadmin.Models.BannerModel;
import com.appsinventiv.verifypeadmin.Models.NotificationModel;
import com.appsinventiv.verifypeadmin.R;
import com.appsinventiv.verifypeadmin.Utils.CommonUtils;
import com.appsinventiv.verifypeadmin.Utils.CompressImage;
import com.appsinventiv.verifypeadmin.Utils.Constants;
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
import java.util.List;

public class Banners extends AppCompatActivity {

    private static final int REQUEST_CODE_CHOOSE = 23;
    RecyclerView recycler;
    Button upload;
    ImageView pickImage;
    private ArrayList<String> mSelected = new ArrayList<>();
    private String imageUrl;
    EditText details,url;
    ProgressBar progress;
    DatabaseReference mDatabase;
    private String livePicPath;
    private List<BannerModel> bannerList = new ArrayList<>();
    BannersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banners);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
            this.setTitle("Banners");
        }
        mDatabase = Constants.M_DATABASE;
        url = findViewById(R.id.url);
        progress = findViewById(R.id.progress);
        upload = findViewById(R.id.upload);
        details = findViewById(R.id.details);
        recycler = findViewById(R.id.recycler);
        pickImage = findViewById(R.id.pickImage);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (details.getText().length() == 0) {
                    details.setError("Enter Message");
                } else if (url.getText().length() == 0) {
                    url.setError("Enter url");
                } else if (imageUrl == null) {
                    CommonUtils.showToast("Please select image");
                } else {
                    uploadPicture();
                }

            }
        });
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Options options = Options.init()
                        .setRequestCode(REQUEST_CODE_CHOOSE)                                           //Request code for activity results
                        .setCount(1)
                        .setExcludeVideos(true)
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                        ;                                       //Custom Path For media Storage

                Pix.start(Banners.this, options);
            }
        });
        adapter = new BannersAdapter(this, bannerList, new BannersAdapter.BannerAdapterCallbacks() {
            @Override
            public void onDelete(BannerModel model) {
                showAlert(model);
            }
        });
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recycler.setAdapter(adapter);

        getDataFromDB();
    }

    private void showAlert(BannerModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to delete this? ");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDatabase.child("Banners").child(model.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        CommonUtils.showToast("Deleted");
                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void getDataFromDB() {
        mDatabase.child("Banners").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bannerList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    BannerModel model = snapshot1.getValue(BannerModel.class);
                    bannerList.add(model);
                }
                adapter.setItemList(bannerList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && data != null) {
            mSelected = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            CompressImage cimage = new CompressImage(Banners.this);
            imageUrl = cimage.compressImage("" + mSelected.get(0));
            Glide.with(Banners.this).load(mSelected.get(0)).into(pickImage);
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
        String key = "" + System.currentTimeMillis();
        BannerModel model = new BannerModel(key, details.getText().toString(), livePicPath,url.getText().toString());
        mDatabase.child("Banners").child(key).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progress.setVisibility(View.INVISIBLE);
                details.setText("");
                CommonUtils.showToast("Uploaded");
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

}