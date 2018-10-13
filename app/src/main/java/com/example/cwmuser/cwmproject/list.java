package com.example.cwmuser.cwmproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cwmuser on 02/06/2016.
 */
public class list extends Activity{

    public Button toMapsbutton;
    public Button addButton;
    public List<String> nameList;
    public List<String> latList;
    public List<String> lngList;
    private ListView listView;
    ArrayAdapter<String> arrayAdapter;
    static final int READ_BLOCK_SIZE = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        toMapsbutton = (Button) findViewById(R.id.toMapsButton);

        toMapsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFiles();
                startActivity(new Intent(list.this, MainActivity.class));
            }
        });

        addButton = (Button) findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(list.this, mapadditem.class),1);
                arrayAdapter.notifyDataSetChanged();
            }
        });

        initFiles();

        listView = (ListView) findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nameList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v,
                                    int position, long id) {
                String question = "Edit Name " + nameList.get(position);
                EditDialog editDialog = EditDialog.newInstance(question, position);
                editDialog.show(getFragmentManager(), "dialog");

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View v,
                                           int position, long id) {
                String question = "Are you sure you want to remove item: " + nameList.get(position);
                ConfirmationDialog confirmationDialog = ConfirmationDialog.newInstance(question, position);
                confirmationDialog.show(getFragmentManager(), "dialog");

                return true;
            }
        });
    }

    public void doEditName(int position, String newName){
        nameList.set(position,newName);
        arrayAdapter.notifyDataSetChanged();
    }

    public void doPositiveClick(int position){
        nameList.remove(position);
        latList.remove(position);
        lngList.remove(position);

        arrayAdapter.notifyDataSetChanged();
    }

    public void doNegativeClick(){
        //do nothing
    }

    public void initFiles(){
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsoluteFile() + "/MyApplication");
        File file1 = new File(directory, "nameList.csv");
        File file2 = new File(directory, "latList.csv");
        File file3 = new File(directory, "lngList.csv");
        FileInputStream fIn = null;

        try {
            fIn = new FileInputStream(file1);
            InputStreamReader isr = new InputStreamReader(fIn);
            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            String s = "";
            int charRead;
            while ((charRead = isr.read(inputBuffer)) > 0) {
                String readString = String.copyValueOf(inputBuffer, 0, charRead);
                s += readString;
                inputBuffer = new char[READ_BLOCK_SIZE];
            }

            String[] retrievedStringArray = s.split(",");
            List<String> list = new ArrayList<String>(Arrays.asList(retrievedStringArray));
            nameList = list;
        } catch (IOException e) {
            e.printStackTrace();

            nameList = new ArrayList<String>();
        }

        try {
            fIn = new FileInputStream(file2);
            InputStreamReader isr = new InputStreamReader(fIn);
            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            String s = "";
            int charRead;
            while ((charRead = isr.read(inputBuffer)) > 0) {
                String readString = String.copyValueOf(inputBuffer, 0, charRead);
                s += readString;
                inputBuffer = new char[READ_BLOCK_SIZE];
            }

            String[] retrievedStringArray = s.split(",");
            List<String> list = new ArrayList<String>(Arrays.asList(retrievedStringArray));
            latList = list;
        } catch (IOException e) {
            e.printStackTrace();

            latList = new ArrayList<String>();
        }

        try {
            fIn = new FileInputStream(file3);
            InputStreamReader isr = new InputStreamReader(fIn);
            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            String s = "";
            int charRead;
            while ((charRead = isr.read(inputBuffer)) > 0) {
                String readString = String.copyValueOf(inputBuffer, 0, charRead);
                s += readString;
                inputBuffer = new char[READ_BLOCK_SIZE];
            }

            String[] retrievedStringArray = s.split(",");
            List<String> list = new ArrayList<String>(Arrays.asList(retrievedStringArray));
            lngList = list;
        } catch (IOException e) {
            e.printStackTrace();

            lngList = new ArrayList<String>();
        }
        if (lngList.get(0) == "") {
            lngList.remove(0);
        }
        if (latList.get(0) == "") {
            latList.remove(0);
        }
        if (nameList.get(0) == "") {
            nameList.remove(0);
        }
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        if( requestCode ==1){
            Log.d("MYAPP", "On requestcode = " + requestCode);
            if(resultCode==RESULT_OK) {
                Log.d("MYAPP", "On resultcode = " + resultCode);
                double newlat = data.getDoubleExtra("Lat",0);
                double newlng = data.getDoubleExtra("Lng",0);
                String newplace = data.getStringExtra("Name");

                Log.d("MYAPP", "data = " + newlat + " " + newlng +" " + newplace);

                nameList.add(newplace);
                latList.add(Double.toString(newlat));
                lngList.add(Double.toString(newlng));


                arrayAdapter.notifyDataSetChanged();
                saveFiles();
            }

        }
    }

    public void saveFiles() {
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsoluteFile() + "/MyApplication");
        directory.mkdirs();
        File file1 = new File(directory, "nameList.csv");
        File file2 = new File(directory, "latList.csv");
        File file3 = new File(directory, "lngList.csv");

        try {
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file1));
            for (int cnt = 0; cnt < nameList.size(); cnt++) {
                osw.write(nameList.get(cnt));
                if (cnt != nameList.size() - 1) {
                    osw.write(",");
                }
            }
            osw.flush();
            osw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file2));
            for (int cnt = 0; cnt < latList.size(); cnt++) {
                osw.write(latList.get(cnt));
                if (cnt != latList.size() - 1) {
                    osw.write(",");
                }
            }
            osw.flush();
            osw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file3));
            for (int cnt = 0; cnt < lngList.size(); cnt++) {
                osw.write(lngList.get(cnt));
                if (cnt != lngList.size() - 1) {
                    osw.write(",");
                }
            }
            osw.flush();
            osw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        saveFiles();
        super.onPause();
    }
}
