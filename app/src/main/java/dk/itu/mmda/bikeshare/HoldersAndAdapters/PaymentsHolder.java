package dk.itu.mmda.bikeshare.HoldersAndAdapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import dk.itu.mmda.bikeshare.R;
import dk.itu.mmda.bikeshare.SpecificBike.SpecificBikeActivity;
import dk.itu.mmda.bikeshare.Database.Ride;
import io.realm.Realm;

public class PaymentsHolder extends RecyclerView.ViewHolder {

    private TextView mPaymentView;

    public PaymentsHolder(LayoutInflater layoutInflater, ViewGroup parent) {
        super(layoutInflater.inflate(R.layout.list_item_payment, parent, false));
        mPaymentView = itemView.findViewById(R.id.payment_list_price);

    }

    public void bind(final Double payment) {
        mPaymentView.setText(payment+"kr");

    }

}
