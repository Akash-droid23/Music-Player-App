package com.example.onmusic;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);

//        Following code usage is taken from github dexter documentation
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                        Toast.makeText(MainActivity.this, "Runtime permission given", Toast.LENGTH_SHORT).show();
                        ArrayList<File> mySongs = fetchSongs(Environment.getExternalStorageDirectory()); // It will read the external storage
                        String [] items = new String[mySongs.size()]; // It will store names of all songs
                        for (int i=0; i<mySongs.size();i++){
                            items[i] = mySongs.get(i).getName().replace(".mp3", "");
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, items);
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(MainActivity.this, PlaySong.class);
                                String currentSong = listView.getItemAtPosition(position).toString();
                                intent.putExtra("songList", mySongs);
                                intent.putExtra("currentSong", currentSong);
                                intent.putExtra("position", position);
                                startActivity(intent);
                            }
                        });

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(MainActivity.this, "Runtime permission denied", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        //  It will ask permission in runtime (In case of restarting the app and permission was denied last time)
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }
    //  function to return array list of file
        public ArrayList<File> fetchSongs(File file){

            ArrayList arrayList = new ArrayList();
            File [] songs = file.listFiles(); // It will list all the files in the directory
            if(songs != null){
                for (File myFile: songs){
                    if(! myFile.isHidden() && myFile.isDirectory()){
                        arrayList.addAll(fetchSongs(myFile));
                    }
                    else {
                        if (myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith(".")){
                            arrayList.add(myFile);

                        }
                    }

                }

            }
            return arrayList;

        }
    }
