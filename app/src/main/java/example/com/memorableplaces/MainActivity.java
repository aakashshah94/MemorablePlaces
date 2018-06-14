package example.com.memorableplaces;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> places = new ArrayList<>();
    static ArrayList<LatLng> placesLocations = new ArrayList<>();

    static  ArrayAdapter<String> arrayAdapter;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(getApplicationContext());
        setContentView(R.layout.activity_main);
        ListView listView = findViewById(R.id.listView);
        Gson gson= new Gson();
        places = gson.fromJson(sessionManager.getKeyPlacesName(),new TypeToken<ArrayList<String>>() {
        }.getType());
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                intent.putExtra("AddOrView","Add");
                startActivity(intent);
            }
        });
        placesLocations = gson.fromJson(sessionManager.getKeyLatLong(),new TypeToken<ArrayList<LatLng>>(){}.getType());
        if(places==null)
        {
            places = new ArrayList<>();
            placesLocations = new ArrayList<>();
        }
        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,places);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                    intent.putExtra("AddOrView","View");
                    intent.putExtra("position",position);
                    startActivity(intent);

            }
        });
    }
}
