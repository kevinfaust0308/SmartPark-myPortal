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
import com.monsoonblessing.kevinfaust.smartparkowner.firebase.VehicleObject;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VehicleLogActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private FirebaseAuth mAuth;


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
        DatabaseReference vehicleLogRef = userDatabaseRef.child(getString(R.string.ParkingLots)).child(lotID).child("VehicleLog");


        recyclerView.setHasFixedSize(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<VehicleObject, VehicleLogViewHolder>(VehicleObject.class, R.layout.recycler_vehicle_log_row, VehicleLogViewHolder.class, vehicleLogRef) {
            @Override
            protected void populateViewHolder(VehicleLogViewHolder viewHolder, VehicleObject vehicle, int position) {

                viewHolder.setLicense(vehicle.getPlateNumber());
                viewHolder.setTotTimeTV(vehicle.getTimeIn(), vehicle.getTimeOut());
                viewHolder.setAccuracy(String.format("Accuracy: %.2f%%", vehicle.getOcrAccuracy()));
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}
