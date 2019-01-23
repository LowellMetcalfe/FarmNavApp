package com.metcalfe.lowell.farmtester;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ConvertActivity extends AppCompatActivity {

    private static CoordinateConversion CC;
    private Button btn;
    private Button btnFinishClick;
    private EditText edt1;
    private EditText edt2;
    private TextView txt1;
    //TODO: make lists for latlong and easting/northing, to take arrays of both the data
    public  List<Double[]> LatLongList;
    public  List<int[]> EastNorthList;
    public  List<Integer> northings;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert);
        edt1 = findViewById(R.id.edt1);
        edt2 = findViewById(R.id.edt2);
        txt1 = findViewById(R.id.txt3);
        btn = findViewById(R.id.button);
        LatLongList = new ArrayList<Double[]>();
        EastNorthList = new ArrayList<int[]>();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn1Clicked();
            }
        });
        /*btnFinishClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 21/01/2019 find a way to go back on the finished button
                Intent intent = new Intent(ConvertActivity.this, MainActivity.class);
                intent.putExtra("LatLongs",LatLongList.toArray());
                startActivity(intent);
            }
        });*/
    }
    public void btn1Clicked(){
        // TODO: 21/01/2019  implement grab gps data call instead of user input
        Convert(Double.parseDouble(edt1.getText().toString()),Double.parseDouble(edt2.getText().toString()));
    }
    public void FinishClicked(){


    }
    public void Convert(double latitude, double longitude) {
        Double[] LatLong = new Double[]{latitude,longitude};
        LatLongList.add(LatLong);
        CC = new CoordinateConversion();
        //UTM is the easting by northing value
        String UTM = CC.latLon2UTM(latitude, longitude);
        //UTM is split up into two variables so it can be better used
        String[] utm = UTM.split(" ");
        int easting = Integer.parseInt(utm[2]);
        int northing = Integer.parseInt(utm[3]);
        int[] EastNorth = new int[]{easting, northing};
        EastNorthList.add(EastNorth);
        System.out.println("Easting: " + easting);
        System.out.println("Northing: " + northing);
        ShowVertices();
        Back2LatLong(UTM);
    }
    public void ShowVertices(){
        String verticesList ="";
        for (int i = 0; i < EastNorthList.size() ; i++) {
            //adds onto a string to be shown containing:
            //a numbered list of easting and northing values of the vertices.
            verticesList = verticesList + (i+1 + ". Eastings: " + EastNorthList.get(i)[0] + "          Northings: " + EastNorthList.get(i)[1]  + "\n");
        }
        txt1.setText(verticesList);
    }
    public static double[] Back2LatLong(String UTM) {
        double[] LatLong = CC.utm2LatLon(UTM);
        return LatLong;
    }
}
