package com.metcalfe.lowell.farmtester;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    Button btn;
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btn = (Button) findViewById(R.id.button);

        Intent receivedIntent = getIntent();
        Bundle bundle = receivedIntent.getExtras();

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
         mMap.addMarker(new MarkerOptions().position(field1).title("field1"));
         //might need to edit zoom level to view whole field
/*         if (receivedIntent.getExtras() != null){
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
        //Intent intent = new Intent(".CONVERT");
        //Intent intent = new Intent(this, ConvertActivity.class);
        Intent intent = new Intent(this, ConvertActivity.class);
        intent.putExtra("Test","TEST MEME");
        startActivity(intent);
        //should i be finishing activities when not using them anymore??
        //finish();
    }
}
