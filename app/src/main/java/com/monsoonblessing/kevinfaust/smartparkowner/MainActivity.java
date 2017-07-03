package com.monsoonblessing.kevinfaust.smartparkowner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.monsoonblessing.kevinfaust.smartparkowner.Popups.AddParkingLotPopup;
import com.monsoonblessing.kevinfaust.smartparkowner.firebase.LotObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    @BindView(R.id.tool_bar)
    Toolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    //////////////////////////////For the Navigation Drawer//////////////////////
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    private ActionBarDrawerToggle mDrawerToggle;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mUserDatabaseRef;
    private DatabaseReference mUserParkingLotsDatabaseRef;

    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar); // toolbar set up
        setupNavView(); // nav drawer setup
        mAuth = FirebaseAuth.getInstance();

        // if user signs out, redirect to home page
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //if user logs out, redirect them to login page
                if (mAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MainActivity.this, UserLoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                }
            }
        };


        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        String user_id = mAuth.getCurrentUser().getUid();
        // user's database
        mUserDatabaseRef = db.child(getString(R.string.UserData)).child(user_id);
        // user parking lots
        mUserParkingLotsDatabaseRef = mUserDatabaseRef.child(getString(R.string.ParkingLots));


        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<LotObject, LotViewHolder>(LotObject.class, R.layout.recycler_lot_row, LotViewHolder.class, mUserParkingLotsDatabaseRef) {
            @Override
            public void populateViewHolder(LotViewHolder lotViewHolder, LotObject lot, final int position) {

                lotViewHolder.viewLogBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, VehicleLogActivity.class);
                        final DatabaseReference ref = firebaseRecyclerAdapter.getRef(position);
                        String parkingLotId = ref.getKey();
                        i.putExtra("parkingLotId", parkingLotId);
                        startActivity(i);
                    }
                });


                // setting fields in a recyclerview row

                lotViewHolder.setNameTV(lot.getName());

                int availableSpots = lot.getAvailableSpots();
                int maximumSpots = lot.getMaxSpots();
                // lot availability text with color
                lotViewHolder.setSpotsTV("Availability: " + availableSpots + " / " + maximumSpots);
                if (((float) availableSpots / maximumSpots) >= 0.60) {
                    lotViewHolder.setSpotsTVColor(MainActivity.this, R.color.md_green_200);
                } else if (((float) availableSpots / maximumSpots) >= 0.30) {
                    lotViewHolder.setSpotsTVColor(MainActivity.this, R.color.md_orange_200);
                } else {
                    lotViewHolder.setSpotsTVColor(MainActivity.this, R.color.md_red_500);
                }

                lotViewHolder.setHourlyChargeTV("Hourly charge: $" + String.format("%.2f", lot.getHourlyCharge()));

                lotViewHolder.setQRImage(MainActivity.this, lot.getQrCodeUrl());

                lotViewHolder.setAccuracy("Accuracy: " + String.format("%.2f", lot.getAccuracy()) +
                                            "% (" + lot.getLifetimeScans() + " total reads)");
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, final int position) {
                Log.d(TAG, "You clicked on " + position);
                final DatabaseReference ref = firebaseRecyclerAdapter.getRef(position);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        LotObject lot = dataSnapshot.getValue(LotObject.class);
                        String lot_stringified = new Gson().toJson(lot);
                        Intent intent = new Intent(MainActivity.this, UpdateLotActivity.class);
                        intent.putExtra("LotDatabaseKey", ref.getKey());
                        Log.d(TAG, "Parking lot database key: " + ref.getKey());
                        intent.putExtra("LotData", lot_stringified);
                        Log.d(TAG, "Starting update lot activity");
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }));

    }

    public void addParkingLot() {
        AddParkingLotPopup p = new AddParkingLotPopup();
        p.show(getSupportFragmentManager(), "AddParkingLotPopup");
    }

    /**
     * Authentication state listener
     */

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }


    /***********************************************************************************************
     * MENU
     **********************************************************************************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add:
                addParkingLot();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Navigation View
     */
    private void setupNavView() {
        mNavView.setNavigationItemSelectedListener(this); // "this" because our navigation select listener is this activity (below) (implemented)

        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) { //nav drawer items on click

        int current = item.getItemId();

        switch (current) {
            case R.id.home:
                mDrawerLayout.closeDrawer(GravityCompat.START); //closes the drawer by setting the gravity to "start" (all the way to the left hidden)
                break;
            case R.id.logout:
                mAuth.signOut();
                break;
        }
        return false;
    }

}
