package com.horizan.horizan;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private LinearLayout registerLayout;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button registerBtn;
    private TextView loginRouteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RegisterActivity.this.getWindow().setBackgroundDrawableResource(R.drawable.gradient);
        RegisterActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();*/
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        registerLayout = (LinearLayout) findViewById(R.id.register_layout);
        firstNameEditText = (EditText) findViewById(R.id.first_name);
        lastNameEditText = (EditText) findViewById(R.id.last_name);
        emailEditText = (EditText) findViewById(R.id.email);
        passwordEditText = (EditText) findViewById(R.id.password);
        registerBtn = (Button) findViewById(R.id.register);
        loginRouteTextView = (TextView) findViewById(R.id.login_route);
        //registerLayout.setBackgroundResource(R.drawable.register_background);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
        loginRouteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        // DEBUG CODE
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        System.out.println("Width: " + width + " Height: " + height);
        //
    }

    private void registerUser() {
        final String firstName = firstNameEditText.getText().toString();
        final String lastName = lastNameEditText.getText().toString();
        final String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "There are one or more empty fields.",
                    Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                String userId = firebaseUser.getUid();
                                User user = new User(firstName, lastName, email);
                                databaseReference.child("users").child(userId).setValue(user);
                                Toast.makeText(RegisterActivity.this, "Registered Successfully",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                            } else {
                                Toast.makeText(RegisterActivity.this, "Failed to Register",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
