package com.metcalfe.lowell.FarmRouteNav;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    Button btn;
    public static GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    public LatLng current;
    public List<Double> latList;
    public List<Double> longList;
    public List<Integer> eastingList;
    public List<Integer> northingList;
    public List<LatLng> latitudeLongitudes = new ArrayList<>();
    public String[] UTMs;

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
        latitudeLongitudes = new ArrayList<>();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        //sets the map to work in hybrid mode
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //default display location is a field at the coordinates, used for testing
        LatLng field1 = new LatLng(51.919198333333334, -0.25225);
        //sets the zoom level to show the whole field
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        //TODO: make the camera focus to the local position on boot
        //makes the displayed map centre over the default location
        mMap.moveCamera(CameraUpdateFactory.newLatLng(field1));
        getLocation();
        //listens for when the user hold clicks on the map
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //creates a marker at the latLng position parsed from the click listner
                MarkerOptions options = new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude));
                //displays the marker
                mMap.addMarker(options);
                //adds the clicked position to the list of vertices
                latitudeLongitudes.add(latLng);
            }
        });
    }

    public void PlotButton(View view) {
        // TODO: 02/02/2019 logic to check if the position has already been logged, current same as previous
        mMap.addMarker(new MarkerOptions().position(current));
        latitudeLongitudes.add(current);
    }

    public void GoButton(View view) {
        ConvertAllLatLongs(latitudeLongitudes);
        PathFinder PF = new PathFinder();
        PF.FindRoute(UTMs, 25);
    }

    public static void PlotLine(LatLng[] linePositions) {
        //TODO remove any previous lines
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.add(linePositions).color(Color.RED);
        Polyline route = mMap.addPolyline(polylineOptions);
    }

    /**
     * changes the coordinates from a list of points to an array of UTM values
     * @param latitudeLongitudes the list of vertex coordinates
     */
    public void ConvertAllLatLongs(List<LatLng> latitudeLongitudes) {
        //once collected all of the values, turn it into an array
        UTMs = new String[latitudeLongitudes.size()];
        CoordinateConversion CC = new CoordinateConversion();
        for (int i = 0; i < latitudeLongitudes.size(); i++) {
            UTMs[i] = CC.latLon2UTM(latitudeLongitudes.get(i).latitude, latitudeLongitudes.get(i).longitude);
        }
        /*
        *section was used to test the back and forth conversion of geographical data
        double [][] Back2LatLng = new double[latitudeLongitudes.size()][];
        double LatandLong;
        for (int i = 0; i < latitudeLongitudes.size(); i++) {
            Back2LatLng[i] = CC.utm2LatLon(UTMs[i]);
        }*/
    }

    @SuppressLint("MissingPermission")
    public void getLocation() {
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Location: ", location.toString());
                current = new LatLng(location.getLatitude(), location.getLongitude());
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
        if (Build.VERSION.SDK_INT < 23) {//if its running older software
            //dont need permission
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            //checks if the application has permission to access fine and coarse location
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //if it doesnt have permission, ask for permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //checks if both permissions are granted
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//if the permission request is positive
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d("", "Made it through to ifs");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }
}
