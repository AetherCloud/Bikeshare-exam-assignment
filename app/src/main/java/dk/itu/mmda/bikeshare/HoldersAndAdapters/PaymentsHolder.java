package dk.itu.mmda.bikeshare.HoldersAndAdapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import dk.itu.mmda.bikeshare.R;

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
