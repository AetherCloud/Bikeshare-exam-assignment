package dk.itu.mmda.bikeshare.HoldersAndAdapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import dk.itu.mmda.bikeshare.Database.Ride;

public class PaymentsAdapter extends RecyclerView.Adapter<PaymentsHolder> {
    private List<Double> mValues;

    public PaymentsAdapter(List<Double> values) {
        mValues = values;
    }

    @NonNull
    @Override
    public PaymentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        return new PaymentsHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentsHolder holder, int position) {
        Double payment = mValues.get(position);
        holder.bind(payment);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

}
