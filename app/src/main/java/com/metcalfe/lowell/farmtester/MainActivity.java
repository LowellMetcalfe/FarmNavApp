package com.metcalfe.lowell.farmtester;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    Button btn;
    GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    public LatLng current;
    public List<Double> latList;
    public List<Double> longList;
    public List<Integer> eastingList;
    public List<Integer> northingList;
    public CoordinateConversion CC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btn = (Button) findViewById(R.id.button);
        latList = new ArrayList<>();
        longList = new ArrayList<>();
        eastingList = new ArrayList<>();
        northingList = new ArrayList<>();
        /*
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                StartConvert();
            }
        });
        */
        //StartConvert();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        LatLng field1 = new LatLng(51.91922550,-0.25225102);
         //mMap.addMarker(new MarkerOptions().position(field1).title("field1"));
         //might need to edit zoom level to view whole field
     /*   Intent receivedIntent = getIntent();
         if (receivedIntent.getExtras() != null){
             double[] LatLongs = receivedIntent.getDoubleArrayExtra("LatLongs");
             for (int i = 0; i < LatLongs.length ; i++) {
                 //mMap.addMarker(new MarkerOptions().position(LatLongs[i]));
             }
         }else{

         }*/
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(field1));
    }

    public void StartConvert(View view) {
        //Intent intent = new Intent(this, ConvertActivity.class);
        //startActivity(intent);
        //should i be finishing activities when not using them anymore??
        //finish();
    }
    public void PlotButton(View view){
        getLocation();
    }
    public void GoButton(View view) {
        String UTM;
        String[] utm;
        CC = new CoordinateConversion();
        for (int i = 0; i < latList.size() ; i++) {
            UTM = CC.latLon2UTM(latList.get(i), longList.get(i));
            //UTM is split up into two variables so it can be better used
            utm = UTM.split(" ");
            //UTM response looks like eg: 30 U 689150 5755659. so we collect the last two values.
            eastingList.add(Integer.parseInt(utm[2]));
            northingList.add(Integer.parseInt(utm[3]));
        }
    }
    @SuppressLint("MissingPermission")
    public void getLocation() {
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Location: ", location.toString());
                current = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(current));
                latList.add(current.latitude);
                longList.add(current.longitude);
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            @Override
            public void onProviderEnabled(String provider) {
            }
            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        if (Build.VERSION.SDK_INT < 23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                // TODO: 01/02/2019 take less readings, calc best value and stop when necessary
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5, locationListener);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //checks if both permissions are granted
        if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                Log.d("","Made it through to ifs");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }
}
