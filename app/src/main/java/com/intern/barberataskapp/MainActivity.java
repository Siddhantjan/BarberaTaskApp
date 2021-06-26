package com.intern.barberataskapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ImageButton addUser;
    private RecyclerView productsRv;
    private RelativeLayout addUserLayout,mailLayout;
    private Button addUserBtn;

    private ArrayList<ModalUser> userList;
    private AdapterUser adapterUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addUser = findViewById(R.id.addProfile);
        productsRv = findViewById(R.id.productsRv);
        addUserLayout = findViewById(R.id.addUserLayout);
        mailLayout = findViewById(R.id.mailLayout);
        addUserBtn = findViewById(R.id.addUserBtn);

        loadAllUser();
        addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AddUserActivity.class));
            }
        });
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AddUserActivity.class));
            }
        });


    }

    private void loadAllUser() {
        userList = new ArrayList<>();
        //get all user
        DatabaseReference reference =  FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            userList.clear();
                            for (DataSnapshot ds: snapshot.getChildren()){
                                ModalUser model = ds.getValue(ModalUser.class);
                                userList.add(model);
                            }
                            adapterUser = new AdapterUser(MainActivity.this, userList);
                            productsRv.setAdapter(adapterUser);
                        }
                        else {
                            addUserLayout.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}