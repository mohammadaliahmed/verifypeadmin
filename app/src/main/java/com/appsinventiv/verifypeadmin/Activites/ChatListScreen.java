package com.appsinventiv.verifypeadmin.Activites;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.verifypeadmin.Adapters.ChatListAdapter;
import com.appsinventiv.verifypeadmin.Models.ChatModel;
import com.appsinventiv.verifypeadmin.Models.SupportChatModel;
import com.appsinventiv.verifypeadmin.R;
import com.appsinventiv.verifypeadmin.Utils.SharedPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ChatListScreen extends AppCompatActivity {

    HashMap<String, SupportChatModel> itemMap = new HashMap<>();
    RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    ChatListAdapter adapter;
    private List<SupportChatModel> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);

        }
        this.setTitle("Chats");
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new ChatListAdapter(this, itemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        getDataFromDB();

    }

    private void getDataFromDB() {
        mDatabase = FirebaseDatabase.getInstance("https://verifipe-default-rtdb.firebaseio.com/").getReference();
        mDatabase.child("AdminSupportChat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot keys : dataSnapshot.getChildren()) {
                        for (DataSnapshot msgs : keys.getChildren()) {
                            SupportChatModel model = msgs.getValue(SupportChatModel.class);
                            itemMap.put(keys.getKey(), model);

                        }
                    }
                    itemList = new ArrayList<>(itemMap.values());
                    Collections.sort(itemList, new Comparator<SupportChatModel>() {
                        @Override
                        public int compare(SupportChatModel listData, SupportChatModel t1) {
                            Long ob1 = listData.getTime();
                            Long ob2 = t1.getTime();
                            return ob2.compareTo(ob1);

                        }
                    });
                    adapter.setItemList(itemList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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