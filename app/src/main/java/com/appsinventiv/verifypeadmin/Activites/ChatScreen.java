package com.appsinventiv.verifypeadmin.Activites;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.verifypeadmin.Adapters.SupportChatAdapter;
import com.appsinventiv.verifypeadmin.Models.SupportChatModel;
import com.appsinventiv.verifypeadmin.Models.User;
import com.appsinventiv.verifypeadmin.R;
import com.appsinventiv.verifypeadmin.Utils.Constants;
import com.appsinventiv.verifypeadmin.Utils.KeyboardUtils;
import com.appsinventiv.verifypeadmin.Utils.NotificationAsync;
import com.appsinventiv.verifypeadmin.Utils.SharedPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatScreen extends AppCompatActivity {
    EditText message;
    ImageView send;
    private DatabaseReference mDatabase;
    String msg = "";
    private List<SupportChatModel> itemList = new ArrayList<>();
    SupportChatAdapter adapter;
    RecyclerView recyclerView;
    private User otherUser;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);

        }
        this.setTitle("Support Chat");
        phone = getIntent().getStringExtra("phone");
        mDatabase = Constants.M_DATABASE;
        adapter = new SupportChatAdapter(this, itemList);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        send = findViewById(R.id.send);
        message = findViewById(R.id.message);
        KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                recyclerView.scrollToPosition(itemList.size() - 1);


            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (message.getText().length() == 0) {
                    message.setError("Cant send empty message");
                } else {
                    sendMessageToDb();
                }
            }
        });
        getOtherUserFromDb();
        getDataFromDb();

    }

    private void getOtherUserFromDb() {
        mDatabase.child("Users").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    otherUser = dataSnapshot.getValue(User.class);
                    ChatScreen.this.setTitle(otherUser.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getDataFromDb() {
        mDatabase.child("AdminSupportChat").child(phone)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            itemList.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                SupportChatModel model = snapshot.getValue(SupportChatModel.class);
                                if (model != null && model.getMessage() != null) {
                                    itemList.add(model);
                                }
                            }
                            adapter.setItemList(itemList);
                            recyclerView.scrollToPosition(itemList.size() - 1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void sendMessageToDb() {
        msg = message.getText().toString();
        message.setText("");
        String key = "" + System.currentTimeMillis();
        SupportChatModel myModel = new SupportChatModel(key,
                msg,
                "admin",
                otherUser.getPhone(),
                otherUser.getName(),
                System.currentTimeMillis());
        mDatabase.child("SupportChat").child(otherUser.getPhone()).child(key).setValue(
                myModel
        );

        mDatabase.child("AdminSupportChat").child(otherUser.getPhone())
                .child(key).setValue(myModel);
        sendNotification();
    }

    private void sendNotification() {
        NotificationAsync notificationAsync = new NotificationAsync(this);
        String NotificationTitle = "New message from: Support";
        String NotificationMessage = msg;
        notificationAsync.execute(
                "ali",
                otherUser.getFcmKey(),
                NotificationTitle,
                NotificationMessage,
                "Admin",
                "msg");
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