package dk.itu.mmda.bikeshare.HoldersAndAdapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import dk.itu.mmda.bikeshare.Database.Ride;

public class RideAdapter extends RecyclerView.Adapter<RideHolder> {
    private List<Ride> mValues;

    public RideAdapter(List<Ride> values) {
        mValues = values;
    }


    @NonNull
    @Override
    public RideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        return new RideHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull RideHolder holder, int position) {
        Ride ride = mValues.get(position);
        holder.bind(ride);
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

}
