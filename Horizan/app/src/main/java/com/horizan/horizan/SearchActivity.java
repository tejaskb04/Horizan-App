package com.horizan.horizan;

import android.app.Application;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private final String MAPBOX_API_KEY
            = "pk.eyJ1Ijoic3Vuam9uYXRoYW41IiwiYSI6ImNqOWhrbGZlMTM5aW8zM25yd2VpMWozNmgifQ.WkXwJMVvp5uZ8XaCArnJTQ";

    private MapView mapView;
    private Toolbar toolbar;
    private DatabaseReference databaseReference;
    private DatabaseReference universitiesDatabaseReference;
    private List<String> universities;
    private List<double[]> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getSupportActionBar().hide();
        Mapbox.getInstance(SearchActivity.this, MAPBOX_API_KEY);
        setContentView(R.layout.activity_search);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxMap.getUiSettings().setTiltGesturesEnabled(false);
                mapboxMap.setMyLocationEnabled(true);
                Location userLocation = mapboxMap.getMyLocation();
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()))
                        .zoom(13)
                        .bearing(180)
                        .tilt(30)
                        .build();
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000);
            }
        });
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        universitiesDatabaseReference = databaseReference.child("universities");
        universities = new ArrayList<String>();
        locations = new ArrayList<double[]>();
        universitiesDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Implement Logic
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SearchActivity.this.getMenuInflater().inflate(R.menu.toolbar_actions, menu);
        MenuItem item = menu.findItem(R.id.action_search_bar);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                int index = compareUserSearch(s);
                if (index != -1) {
                    final double[] coordinates = getLocation(index);
                    mapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(MapboxMap mapboxMap) {
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(coordinates[0], coordinates[1]))
                                    .zoom(13)
                                    .build();
                            mapboxMap.easeCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                    });
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // Implement Logic
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search_bar: {
                // Implement Logic
                return true;
            }
            case R.id.action_my_location: {
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(MapboxMap mapboxMap) {
                        Location userLocation = mapboxMap.getMyLocation();
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()))
                                .zoom(13)
                                .build();
                        mapboxMap.easeCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                });
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            String title = (String) ds.child("title").getValue();
            String l  = (String) ds.child("latitude").getValue();
            String lo = (String) ds.child("longitude").getValue();
            double latitude = Double.parseDouble(l);
            double longitude = Double.parseDouble(lo);
            universities.add(title);
            locations.add(new double[] {latitude, longitude});
        }
    }

    private int compareUserSearch(String query) {
        for (int i = 0; i < universities.size(); i++) {
            if (universities.get(i).equalsIgnoreCase(query)) {
                return i;
            }
        }
        return -1;
    }

    private double[] getLocation(int index) {
        return locations.get(index);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
