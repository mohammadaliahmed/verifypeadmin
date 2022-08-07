package com.appsinventiv.verifypeadmin.Activites;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.appsinventiv.verifypeadmin.R;
import com.appsinventiv.verifypeadmin.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {


    CardView support;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        support=findViewById(R.id.support);
        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ChatListScreen.class));
            }
        });
        updateFcmKey();


    }
    private void updateFcmKey() {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    String token = task.getResult();
                    SharedPrefs.setFcmKey(token);
                    mDatabase = FirebaseDatabase.getInstance("https://verifipe-default-rtdb.firebaseio.com/").getReference();

                    mDatabase.child("Admin").child("fcmKey").setValue(token);

                }
            });

    }


}