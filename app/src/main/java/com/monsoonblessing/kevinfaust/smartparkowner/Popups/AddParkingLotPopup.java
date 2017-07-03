package com.monsoonblessing.kevinfaust.smartparkowner.Popups;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.monsoonblessing.kevinfaust.smartparkowner.firebase.LotObject;
import com.monsoonblessing.kevinfaust.smartparkowner.R;

import net.glxn.qrgen.android.QRCode;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Kevin Faust on 3/28/2017.
 */

public class AddParkingLotPopup extends DialogFragment {

    @BindView(R.id.name_text)
    EditText lotNameTextView;
    @BindView(R.id.max_spots_text)
    TextView maxSpotsTextView;
    @BindView(R.id.hourly_price_text)
    TextView hourlyPriceTextView;

    private DatabaseReference mUserDatabaseRef;
    private DatabaseReference mUserParkingLotsDatabaseRef;
    private FirebaseAuth mAuth;

    private StorageReference mStorageRef;

    private ProgressDialog pd;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.add_parking_lot_popup, null);
        ButterKnife.bind(this, v);

        mAuth = FirebaseAuth.getInstance();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        String user_id = mAuth.getCurrentUser().getUid();
        // user's database
        mUserDatabaseRef = db.child(getString(R.string.UserData)).child(user_id);
        // user parking lots
        mUserParkingLotsDatabaseRef = mUserDatabaseRef.child(getString(R.string.ParkingLots));

        mStorageRef = FirebaseStorage.getInstance().getReference().child(getString(R.string.ParkingLotQRCodes));

        pd = new ProgressDialog(getActivity());

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();
    }

    @OnClick(R.id.create_parking_lot_btn)
    void onCreateParkingLot() {

        final String name = lotNameTextView.getText().toString();
        final String spots = maxSpotsTextView.getText().toString();
        final String price = hourlyPriceTextView.getText().toString();

        // check if all fields are filled out
        if (hasValidFrields(name, spots, price)) {

            pd.setMessage("Setting Up");
            pd.show();

            // add new parking lot to user's parking lot database
            final DatabaseReference newParkingLot = mUserParkingLotsDatabaseRef.push();

            // create qr code containing the owner's id and the parking lot id
            String data = mAuth.getCurrentUser().getUid() + " " + newParkingLot.getKey();
            File QRfile = QRCode.from(data).file();

            final UploadTask uploadTask = mStorageRef.child(newParkingLot.getKey()).putFile(Uri.fromFile(QRfile));
            // upload image to storage
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    // successfull image upload to storage
                    // url of the picture stored on google
                    @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    // create new lot
                    LotObject lot = new LotObject(
                            name,
                            Integer.parseInt(spots),
                            Integer.parseInt(spots),
                            Double.parseDouble(price),
                            downloadUrl.toString()
                    );

                    // add parking lot to database and dismiss popup
                    newParkingLot.setValue(lot);
                    pd.dismiss();
                    dismiss();
                }

                ;
            });
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // problem uploading image
                    Toast.makeText(getActivity(), "Error occured", Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
            });


        }
    }


    public boolean hasValidFrields(String name, String spots, String price) {

        boolean hasValidFields = false;

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(spots) && !TextUtils.isEmpty(price)) {
            hasValidFields = true;
        } else {
            Toast.makeText(getActivity(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
        }
        return hasValidFields;
    }


}
