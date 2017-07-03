package com.monsoonblessing.kevinfaust.smartparkowner;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Kevin Faust on 3/28/2017.
 */

public class VehicleLogViewHolder extends RecyclerView.ViewHolder {

    private TextView licenseTV;
    private TextView dateTimeTV;
    private TextView accuracyTV;


    public VehicleLogViewHolder(View itemView) {
        super(itemView);
        licenseTV = (TextView) itemView.findViewById(R.id.plate_number);
        dateTimeTV = (TextView) itemView.findViewById(R.id.datetime);
        accuracyTV = (TextView) itemView.findViewById(R.id.accuracy);
    }


    public void setLicense(String s) {
        licenseTV.setText(s);
    }


    public void setTotTimeTV(Long timeIn, Long timeOut) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        String dateString = formatter.format(new Date(timeIn));

        dateTimeTV.setText(String.format("%s   |   %.2f min", dateString, (float) (timeOut-timeIn)/60000));
    }


    public void setAccuracy(String s) {
        accuracyTV.setText(s);
    }


}
