package com.example.safezone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomePage extends AppCompatActivity {

    private static final String TAG ="homepage" ;

    String s1[], s2[];
    ListView locationsList;
    Button mark, order;
    TextView  userName, userAge, userStatus;
    Intent intent;
    DatabaseReference getLocationReference;
    RecyclerView locationListRecyclerView;
    locationListAdapter adapter;
    String id;
    cLocation updateLocationInfo;
    NavigationView navigationView;
    private ArrayList<locationList> ListOfLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        ListOfLocations = new ArrayList<>();
      //  displayLocations();

     //   locationsList =  findViewById(R.id.itemlist);
    //    navigationView = findViewById(R.id.bottomNavigationView);


        order = findViewById(R.id.order);
        //addLocation = findViewById(R.id.addLocation);
        userName = findViewById(R.id.user_name);
        userAge = findViewById(R.id.user_age);
        userStatus = findViewById(R.id.status);
        mark = findViewById(R.id.mark);


        userName.setText(getIntent().getExtras().getString("fName") + " " + getIntent().getExtras().getString("lName"));
        userAge.setText(getIntent().getExtras().getString("Age"));
        userStatus.setText(getIntent().getExtras().getString("Status"));
        id= getIntent().getExtras().getString("userID");
      //  addLocation.setEnabled(checkIfPositive());

        mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (userStatus.getText().toString()) {
                    case "negative": {
                        alertTest();
                        break;
                    }
                }
            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(HomePage.this, pharmacy_search_activity.class);
                intent.putExtra("ID",id);
                startActivity(intent);
            }
        });

    }







    private void alertTest(){
        AlertDialog.Builder alert = new AlertDialog.Builder(HomePage.this);
        alert.setTitle("Changing Status to Positive");
        alert.setMessage("If you change your status to positive you won't be able to add any location and change your status to negative again till 14 days");
        alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // markAsPositive();
                userStatus.setText("Positive");
                mark.setText("Mark as Negative");
                Toast.makeText(HomePage.this, "Status Changed to Positive",Toast.LENGTH_LONG).show();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.create().show();
    }
    private boolean checkIfPositive(){
        if(userStatus.getText().toString().equals("Positive")){
            return false;
        }else
            return true;
    }
/*

  @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
     NavigationView.OnNavigationItemSelectedListener navListener = new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.HomePage:{
                        intent = new Intent(HomePage.this , HomePage.class);
                        startActivity(intent);
                        break;
                    }
                    case R.id.mapsActivity:{
                        intent = new Intent(HomePage.this , MapsActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case R.id.pharmacy_search_activity:{
                        intent = new Intent(HomePage.this , pharmacy_search_activity.class);
                        startActivity(intent);
                        break;
                    }
                    case R.id.displayPlaces:{
                        intent = new Intent(HomePage.this , displayPlaces.class);
                        startActivity(intent);
                        break;
                    }
                    default:{
                        break;
                    }
                }

                return true;
            }
        };

            }
        })
    public void markAsPositive(){
        getLocationReference = FirebaseDatabase.getInstance().getReference("User").child(getIntent().getExtras().getString("userID"));
        getLocationReference.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                if(snapshot.exists()){
                    getLocationReference.child("status").setValue("Positive");
                    long DBCounter = (long) snapshot.child("locationCounter").getValue();
                    for(int i = 1 ; i <= DBCounter;i++) {
                        String LocationName_num = snapshot.child("locationName_"+i).getValue().toString();
                        increasePositiveCounter(LocationName_num);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void increasePositiveCounter(String locationName){
        getLocationReference = FirebaseDatabase.getInstance().getReference("Locations").child(locationName);
        getLocationReference.addListenerForSingleValueEvent(new ValueEventListener(){
            long DBPositiveCounter = 0;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                double lat = (double) snapshot.child("Latitude").getValue();
                double lon = (double)snapshot.child("Longitude").getValue();
                DBPositiveCounter = (long) snapshot.child("PositiveCounter").getValue();
                if(snapshot.exists()){
                    if (DBPositiveCounter == 0){
                        updateLocationInfo = new cLocation(locationName , lat , lon , 1, id ,1);
                        getLocationReference = FirebaseDatabase.getInstance().getReference("Locations");
                        getLocationReference.child(locationName).setValue(updateLocationInfo);
                    }else{
                        DBPositiveCounter += 1;
                        updateLocationInfo = new cLocation(locationName , lat , lon , 1, id ,DBPositiveCounter);
                        getLocationReference = FirebaseDatabase.getInstance().getReference("Locations");
                        getLocationReference.child(locationName).setValue(updateLocationInfo);
                    }
                }else{
                    Toast.makeText(HomePage.this,  "No Location",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void displayLocations(){
        getLocationReference = FirebaseDatabase.getInstance().getReference("User").child(getIntent().getExtras().getString("userID"));
        getLocationReference.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                if(snapshot.exists()){
                    Log.d(TAG, "data exists");
                    long DBCounter = (long) snapshot.child("locationCounter").getValue();
                    if(DBCounter == 0){
                        ListOfLocations.add(new locationList("No Locations to show"
                                ,"Negative Visitors Counter: 0"
                                , "Positive Visitors Counter: 0"));
                        adapter.notifyDataSetChanged();
                    }else{
                        for(int i = 1 ; i <= DBCounter;i++) {
                            String LocationName = snapshot.child("locationName_"+i).getValue().toString();
                            getPositiveCounter(LocationName);
                            adapter.notifyDataSetChanged();
                        }

                    }
                }else{
                    Log.d(TAG, "no locations (else)");
                    ListOfLocations.add(new locationList("No Locations to show"
                            ,"Negative Visitors Counter: 0"
                            , "Positive Visitors Counter: 0"));
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void getPositiveCounter(String locationName){
        Log.d(TAG, "connecting to firebase 02");
        getLocationReference = FirebaseDatabase.getInstance().getReference("Locations").child(locationName);
        getLocationReference.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                if(snapshot.exists()){
                    Log.d(TAG, "geting location info");
                    String DBVisitorsCounter = snapshot.child("Counter").getValue().toString();
                    String DBPositiveVisitorsCounter = snapshot.child("PositiveCounter").getValue().toString();
                    Log.d(TAG, "initialize the list ");
                    ListOfLocations.add(new locationList("Location Address: "+locationName
                            ,"Negative Visitors Counter: "+DBVisitorsCounter
                            ,"Positive Visitors Counter: "+DBPositiveVisitorsCounter));
                    adapter.notifyDataSetChanged();
                }
                else{
                    Log.d(TAG, "no data exists 02");
                    ListOfLocations.add(new locationList("No Locations to show"
                            ,"Negative Visitors Counter: 0"
                            ,"Positive Visitors Counter: 0"));
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

*/
    /*
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(HomePage.this, MapsActivity.class);
                intent.putExtra("ID",id);
                startActivity(intent);
            }
        });*/
}

