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

public class LaunchActivity extends AppCompatActivity {

    //private Button registerBtn;
    //private Button loginBtn;
    //private Button dashboardBtn;
    private TextView welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        welcome = (TextView) findViewById(R.id.welcome);
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
        }
    }
}
