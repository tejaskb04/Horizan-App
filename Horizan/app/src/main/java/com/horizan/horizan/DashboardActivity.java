package com.horizan.horizan;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DashboardActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_dashboard: {
                    return true;
                }
                case R.id.navigation_search: {
                    startActivity(new Intent(DashboardActivity.this, SearchActivity.class));
                    return true;
                }
                case R.id.navigation_account: {
                    startActivity(new Intent(DashboardActivity.this, SettingsActivity.class));
                    return true;
                }
            }
            return false;
        }
    };
    private ListView collegeDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        collegeDashboard = (ListView) findViewById(R.id.college_dashboard);
        String[] dummyData = new String[] {"dummy_1", "dummy_2", "dummy_3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(DashboardActivity.this, R.layout.dashboard_view,
                dummyData);
        collegeDashboard.setAdapter(adapter);
    }
}
