package com.monsoonblessing.kevinfaust.smartparkowner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Kevin Faust on 3/28/2017.
 */

public class LotViewHolder extends RecyclerView.ViewHolder {

    private TextView nameTV;
    private TextView spotsTV;
    private TextView maxTimeTV;
    private TextView hourlyChargeTV;
    private TextView accuracyTV;
    private ImageView QRImage;
    public Button viewLogBtn;

    public LotViewHolder(View itemView) {
        super(itemView);
        nameTV = (TextView) itemView.findViewById(R.id.name);
        spotsTV = (TextView) itemView.findViewById(R.id.spots);
        maxTimeTV = (TextView) itemView.findViewById(R.id.max_time);
        hourlyChargeTV = (TextView) itemView.findViewById(R.id.hourly_charge);
        accuracyTV = (TextView) itemView.findViewById(R.id.accuracy);
        QRImage = (ImageView) itemView.findViewById(R.id.qr_code_image);
        viewLogBtn = (Button) itemView.findViewById(R.id.view_log_button);
    }

    public void setNameTV(String name) {
        nameTV.setText(name);
    }

    public void setSpotsTV(String spots) {
        spotsTV.setText(spots);
    }

    public void setSpotsTVColor(Context ctx, int color) {
        spotsTV.setTextColor(ContextCompat.getColor(ctx, color));
    }

    public void setMaxTimeTV(String time) {
        maxTimeTV.setText(time);
    }

    public void setHourlyChargeTV(String charge) {
        hourlyChargeTV.setText(charge);
    }

    public void setQRImage(Context ctx, String url) {
        Picasso.with(ctx).load(url).into(QRImage);
    }

    public void setAccuracy(String accuracy) {
        accuracyTV.setText(accuracy);
    }

}