package com.horizan.horizan;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class SearchActivity extends AppCompatActivity {

    private final String MAPBOX_API_KEY
            = "pk.eyJ1Ijoic3Vuam9uYXRoYW41IiwiYSI6ImNqOWhrbGZlMTM5aW8zM25yd2VpMWozNmgifQ.WkXwJMVvp5uZ8XaCArnJTQ";
    private final String GOOGLE_PLACES_API_KEY = "AIzaSyAH008n41rXGsO2oYtJgZduebNYwN127_I";

    private MapView mapView;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private DatabaseReference databaseReference;
    private DatabaseReference universitiesDatabaseReference;
    private List<String> universities;
    private List<double[]> locations;
    private String placeId = "";

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
                mapboxMap.getMarkerViewManager().setOnMarkerViewClickListener(new MapboxMap.OnMarkerViewClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker, @NonNull View view,
                                                 @NonNull MapboxMap.MarkerViewAdapter adapter) {
                        startActivity(new Intent(SearchActivity.this, UniversityInfoActivity.class));
                        return false;
                    }
                });
                /*mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        startActivity(new Intent(SearchActivity.this, UniversityInfoActivity.class));
                        return false;
                    }
                });*/
            }
        });
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(SearchActivity.this, drawerLayout, R.string.open,
                R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    private String createPlaceSearchUrl(double lat, double lng) {
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location=").append(lat).append(",").append(lng);
        stringBuilder.append("&radius=").append(100);
        stringBuilder.append("&types=").append("university");
        stringBuilder.append("&sensor=true");
        stringBuilder.append("&key=").append(GOOGLE_PLACES_API_KEY);
        return stringBuilder.toString();
    }

    private String createPlaceDetailsUrl(String placeId) {
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        stringBuilder.append("placeid=").append(placeId);
        stringBuilder.append("&key=").append(GOOGLE_PLACES_API_KEY);
        return stringBuilder.toString();
    }

    private class PlaceSearchCallBackTask extends AsyncTask<String, Integer, String> {
        
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection
                        .getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                return stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject data = new JSONObject(result);
                if (data.getString("status").equalsIgnoreCase("OK")) {
                    JSONArray jsonArray = data.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        final JSONObject place = jsonArray.getJSONObject(i);
                        if (!place.isNull("place_id")) {
                            placeId = place.getString("place_id");
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SearchActivity.this.getMenuInflater().inflate(R.menu.toolbar_actions, menu);
        MenuItem item = menu.findItem(R.id.action_search_bar);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                final int index = compareUserSearch(s);
                if (index != -1) {
                    final double[] coordinates = getLocation(index);
                    mapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(final MapboxMap mapboxMap) {
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(coordinates[0], coordinates[1]))
                                    .zoom(13)
                                    .build();
                            mapboxMap.easeCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            final Icon info = drawableToIcon(SearchActivity.this, R.drawable.ic_info_outline_black_24dp);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mapboxMap.addMarker(new MarkerViewOptions()
                                            .position(new LatLng(coordinates[0], coordinates[1]))
                                    );
                                }
                            }, 50);
                            Intent collegeInfo = new Intent("collegeInfo");
                            collegeInfo.putExtra("name", universities.get(index));
                            LocalBroadcastManager.getInstance(SearchActivity.this).sendBroadcast(collegeInfo);
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
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_search_bar: {
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
        // Implement Regex
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

    private Icon drawableToIcon(@NonNull Context context, @DrawableRes int id) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(context.getResources(), id, context.getTheme());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        //DrawableCompat.setTint(vectorDrawable, colorRes);
        vectorDrawable.draw(canvas);
        return IconFactory.getInstance(context).fromBitmap(bitmap);
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
