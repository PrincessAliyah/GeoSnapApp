package org.kashmirworldfoundation.WildlifeGeoSnap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.ListFragment;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jetbrains.annotations.NotNull;
import org.kashmirworldfoundation.WildlifeGeoSnap.Fragment.AddFragment;
import org.kashmirworldfoundation.WildlifeGeoSnap.Fragment.MapFragment;
import org.kashmirworldfoundation.WildlifeGeoSnap.Fragment.PreyFragment;
import org.kashmirworldfoundation.WildlifeGeoSnap.Fragment.ProfileFragment;
import org.kashmirworldfoundation.WildlifeGeoSnap.Fragment.StationAsyncTask;

import java.io.IOException;
import java.io.InputStream;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private AddFragment addFragment;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;
    private static final String TAG = "MainActivity";
    public Double latitudeD;
    private Double longitudeD;
    private Double altitudeD;

    private String latitude;
    private String longitude;
    private String altitude;


    private static final String ELEVATION_API_KEY="AIzaSyBh-rFSAH9QqPtUrLXcT5Z0c2ZQiJUWkTc";

    private DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        addFragment = new AddFragment();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);

        toggle.syncState();

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_profile);
        }
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){

            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                break;
            case R.id.nav_add:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AddFragment()).commit();
                break;
            case R.id.nav_map:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MapFragment()).commit();
                break;
            case R.id.nav_prey:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PreyFragment()).commit();
                break;
            case R.id.nav_list:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new org.kashmirworldfoundation.WildlifeGeoSnap.Fragment.ListFragment()).commit();
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed(){
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else{
            super.onBackPressed();
        }
    }


    private void determineLocation() {

        if (checkPermission()) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {

                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                latitudeD=location.getLatitude();
                                longitudeD=location.getLongitude();
                                latitude=String.valueOf(latitudeD);
                                longitude=String.valueOf(longitudeD);
                                altitudeD= getElevationFromGoogleMaps(longitudeD,latitudeD);
                                altitude=String.valueOf(altitudeD);
                                setArgument();
                            }
                        }
                    });
        }

    }

    private double getElevationFromGoogleMaps(double longitude, double latitude) {
        double result = Double.NaN;
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        String url = "https://maps.googleapis.com/maps/api/elevation/"
                + "xml?locations=" + String.valueOf(latitude)
                + "," + String.valueOf(longitude)
                + "&key="
                + ELEVATION_API_KEY;
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = httpClient.execute(httpGet, localContext);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                int r = -1;
                StringBuffer respStr = new StringBuffer();
                while ((r = instream.read()) != -1)
                    respStr.append((char) r);
                String tagOpen = "<elevation>";
                String tagClose = "</elevation>";
                if (respStr.indexOf(tagOpen) != -1) {
                    int start = respStr.indexOf(tagOpen) + tagOpen.length();
                    int end = respStr.indexOf(tagClose);
                    String value = respStr.substring(start, end);
                    //result = (double)(Double.parseDouble(value)*3.2808399); // convert from meters to feet
                    result=(double)Double.parseDouble(value);
                }
                instream.close();
            }
        } catch (ClientProtocolException e) {}
        catch (IOException e) {}

        return result;
    }


    //send the argument to add fragment
    private void setArgument(){
        Bundle bundle = new Bundle();
        bundle.putString("latitude", latitude);
        bundle.putString("longitude", longitude);
        bundle.putString("altitude", altitude);
        addFragment.setArguments(bundle);
        addFragment.refresh();
    }

    //ask user to get the location premission and check
    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, LOCATION_REQUEST);
            return false;
        }
        return true;
    }





    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        determineLocation();
                    } else {
                        Toast.makeText(this, "Location Permission not Granted", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}
