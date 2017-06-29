package com.monsoonblessing.kevinfaust.smartparkowner;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Kevin Faust on 3/28/2017.
 */

public class VehicleLogViewHolder extends RecyclerView.ViewHolder {

    private TextView licenseTV;
    private TextView inTimeTV;
    private TextView outTimeTV;
    private TextView accuracyTV;


    public VehicleLogViewHolder(View itemView) {
        super(itemView);
        licenseTV = (TextView) itemView.findViewById(R.id.plate_number);
        inTimeTV = (TextView) itemView.findViewById(R.id.in_time);
        outTimeTV = (TextView) itemView.findViewById(R.id.out_time);
        accuracyTV = (TextView) itemView.findViewById(R.id.accuracy);
    }

    public void setLicense(String s) {
        licenseTV.setText(s);
    }

    public void setInTime(String s) {
        inTimeTV.setText(s);
    }

    public void setOutTime(String s) {
        outTimeTV.setText(s);
    }

    public void setAccuracy(String s) {
        accuracyTV.setText(s);
    }


}
