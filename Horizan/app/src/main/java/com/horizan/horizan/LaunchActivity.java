package com.horizan.horizan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LaunchActivity extends AppCompatActivity {

    //private Button registerBtn;
    //private Button loginBtn;
    //private Button dashboardBtn;
    private TextView welcome;
    private DatabaseReference databaseReference;
    private DatabaseReference usersDatabaseReference;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        welcome = (TextView) findViewById(R.id.welcome);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        usersDatabaseReference = databaseReference.child("users");
        user = new User("", "", "");
        usersDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /*registerBtn = (Button) findViewById(R.id.register);
        loginBtn = (Button) findViewById(R.id.login);
        dashboardBtn = (Button) findViewById(R.id.dashboard);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LaunchActivity.this, RegisterActivity.class));
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
            }
        });
        dashboardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LaunchActivity.this, DashboardActivity.class));
            }
        });*/
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            //FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            //System.out.println(firebaseUser.getDisplayName());
            Animation animation = AnimationUtils.loadAnimation(LaunchActivity.this, R.anim.launch_animation);
            welcome.startAnimation(animation);
            Thread timer = new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        startActivity(new Intent(LaunchActivity.this, DashboardActivity.class));
                        finish();
                    }
                }
            };
            timer.start();
        } else {
            welcome.setText("Let's get started!");
            Animation animation = AnimationUtils.loadAnimation(LaunchActivity.this, R.anim.launch_animation);
            welcome.startAnimation(animation);
            Thread timer = new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        startActivity(new Intent(LaunchActivity.this, RegisterActivity.class));
                        finish();
                    }
                }
            };
            timer.start();
        }
    }

    private void getData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {

        }
    }
}
