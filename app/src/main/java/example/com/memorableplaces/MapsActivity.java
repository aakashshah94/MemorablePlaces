package example.com.memorableplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(getApplicationContext());
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    public void centerMapOnLocation(Location location,String title)
    {
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        if(!title.equals("Current Location"))
        {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title(title));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();
        if(intent.getStringExtra("AddOrView").equals("Add")){

            mMap.setOnMapLongClickListener(this);
            locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    centerMapOnLocation(location,"Current Location");
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
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centerMapOnLocation(lastKnownLocation,"Current Location");

            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }
        else
        {
            mMap.clear();
            int position = getIntent().getIntExtra("position",0);
            LatLng thisLocation = MainActivity.placesLocations.get(position);
            String name = MainActivity.places.get(position);
            mMap.addMarker(new MarkerOptions().position(thisLocation).title(name));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(thisLocation,10));


        }

        // Add a marker in Sydney and move the camera
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centerMapOnLocation(lastKnownLocation,"Current Location");

            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.US);
        String address = "";

        try{
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if(addresses !=null && addresses.size()>1)
            {
                Address temp = addresses.get(0);
                if(temp.getThoroughfare() != null)
                {
                    if(temp.getSubThoroughfare()!=null)
                        address += temp.getSubThoroughfare();
                }
                address += " "+temp.getThoroughfare();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if(address.equals(""))
        {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm MM-dd-yyyy",Locale.US);
            address = sdf.format(new Date());
        }

        MainActivity.places.add(address);
        MainActivity.placesLocations.add(latLng);
        Gson gson = new Gson();

        sessionManager.storeLocations(gson.toJson(MainActivity.places),gson.toJson(MainActivity.placesLocations));


        MainActivity.arrayAdapter.notifyDataSetChanged();
        Toast.makeText(this,"Location saved",Toast.LENGTH_SHORT).show();
        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
    }
}
