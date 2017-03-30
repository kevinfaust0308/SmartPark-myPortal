package com.monsoonblessing.kevinfaust.smartparkowner;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdateLotActivity extends AppCompatActivity {

    @BindView(R.id.name_text)
    EditText lotNameEditText;
    @BindView(R.id.max_spots_text)
    TextView maxSpotsEditText;
    @BindView(R.id.max_time_text)
    TextView maxTimeEditText;
    @BindView(R.id.hourly_price_text)
    TextView hourlyPriceEditText;

    private DatabaseReference mUserDatabaseRef;
    private DatabaseReference mCurrUserParkingLotDatabaseRef;
    private FirebaseAuth mAuth;

    private String mLotDatabaseKey;

    private ProgressDialog pd;

    private Lot lot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_lot);
        ButterKnife.bind(this);

        lot = new Gson().fromJson(getIntent().getStringExtra("LotData"), Lot.class);
        mLotDatabaseKey = getIntent().getStringExtra("LotDatabaseKey");

        // update screen with pre-filled lot data
        lotNameEditText.setText(lot.getName());
        maxSpotsEditText.setText(String.valueOf(lot.getMaxSpots()));
        maxTimeEditText.setText(String.valueOf(lot.getMaxTime()));
        hourlyPriceEditText.setText(String.valueOf(lot.getHourlyCharge()));

        mAuth = FirebaseAuth.getInstance();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        String user_id = mAuth.getCurrentUser().getUid();
        // user's database
        mUserDatabaseRef = db.child(getString(R.string.UserData)).child(user_id);
        // currently selected user's parking lot
        mCurrUserParkingLotDatabaseRef = mUserDatabaseRef
                .child(getString(R.string.ParkingLots))
                .child(mLotDatabaseKey);

        pd = new ProgressDialog(this);
    }


    @OnClick(R.id.update_parking_lot_btn)
    void onUpdateParkingLot() {

        String name = lotNameEditText.getText().toString();
        String spots = maxSpotsEditText.getText().toString();
        String time = maxTimeEditText.getText().toString();
        String price = hourlyPriceEditText.getText().toString();

        // check if all fields are filled out
        if (hasValidFrields(name, spots, time, price)) {

            pd.setMessage("Saving");
            pd.show();

            mCurrUserParkingLotDatabaseRef.child("name").setValue(name);
            mCurrUserParkingLotDatabaseRef.child("maxSpots").setValue(Integer.parseInt(spots));

            // adjust available spots
            int updated_spots = lot.getAvailableSpots() + (Integer.parseInt(spots) - lot.getMaxSpots());
            mCurrUserParkingLotDatabaseRef.child("availableSpots").setValue(updated_spots);

            mCurrUserParkingLotDatabaseRef.child("hourlyCharge").setValue(Double.parseDouble(price));
            mCurrUserParkingLotDatabaseRef.child("maxTime").setValue(Integer.parseInt(time));

            pd.dismiss();
            finish();

        }
    }


    public boolean hasValidFrields(String name, String spots, String time, String price) {

        boolean hasValidFields = false;

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(spots) && !TextUtils.isEmpty(time) && !TextUtils.isEmpty(price)) {
            hasValidFields = true;
        } else {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
        }
        return hasValidFields;
    }


}
