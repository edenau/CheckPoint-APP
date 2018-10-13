package com.example.cwmuser.cwmproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by cwmuser on 03/06/2016.
 */
public class mapadditem extends FragmentActivity{

    private GoogleMap map;
    private TextView mTapTextView;
    EditText itemName;
    private Button add = null;
    private Button cancel = null;
    private Intent data = new Intent();
    private LatLng lastpoint;
    boolean checkpoint;
    private String newname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapadditem);

        add = (Button) findViewById(R.id.addCheckPoint);
        cancel = (Button) findViewById(R.id.cancelAction);
        itemName = (EditText) findViewById(R.id.itemEdit);
        mTapTextView = (TextView) findViewById(R.id.textView1);
        checkpoint = false;

        SupportMapFragment smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        map = smf.getMap();
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                lastpoint = point;
                mTapTextView.setText("(Latitude,Longtitude)=(" + point.latitude+","+point.longitude+")");
                checkpoint = true;
            }
        });

        //map.setMyLocationEnabled(true);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MYAPP", "OnClick ");

                if (checkpoint==false) {
                    Log.d("MYAPP", "Somethig's wrong");
                    Toast.makeText(getApplicationContext(),"Please Click On the Map",Toast.LENGTH_LONG).show();
                    return;
                } else {
                    if (itemName.getText().toString().isEmpty()){
                      newname = "New Place" ;
                    }
                    else {
                        newname = itemName.getText().toString();
                    }
                    Log.d("MYAPP", "All fine");
                    data.putExtra("Name", newname);
                    data.putExtra("Lat", lastpoint.latitude);
                    data.putExtra("Lng", lastpoint.longitude);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MYAPP", "Cancelled operation");
                setResult(RESULT_CANCELED);
                finish();
            }
        });

    }

}
