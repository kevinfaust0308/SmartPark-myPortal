package com.monsoonblessing.kevinfaust.smartparkowner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VehicleLogActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private FirebaseAuth mAuth;

    private DatabaseReference currentParkingLotRef;

    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_log);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        Intent i = getIntent();
        String lotID = i.getStringExtra("parkingLotId");

        // database reference to the current parking lot
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        String user_id = mAuth.getCurrentUser().getUid();
        // user's database
        DatabaseReference userDatabaseRef = db.child(getString(R.string.UserData)).child(user_id);
        currentParkingLotRef = userDatabaseRef.child(getString(R.string.ParkingLots)).child(lotID).child("VehicleLog");


        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Vehicle, VehicleLogViewHolder>(Vehicle.class, R.layout.recycler_vehicle_log_row, VehicleLogViewHolder.class, currentParkingLotRef) {
            @Override
            protected void populateViewHolder(VehicleLogViewHolder viewHolder, Vehicle vehicle, int position) {

                viewHolder.setLicense(vehicle.getPlateNumber());
                viewHolder.setAccuracy(String.format("%.2f", vehicle.getOcrAccuracy()));

                viewHolder.setAccuracy("Accuracy: " + String.format("%.2f", vehicle.getOcrAccuracy()) +
                        "%");

                viewHolder.setInTime(String.valueOf(vehicle.getTimeIn()));

                if (vehicle.getTimeOut() == null) {
                    viewHolder.setOutTime("Still inside lot");
                } else {
                    viewHolder.setOutTime(String.valueOf(vehicle.getTimeOut()));
                }
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}
