package dk.itu.mmda.bikeshare;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import dk.itu.mmda.bikeshare.database.RidesEntity;

public class RideAdapter extends RecyclerView.Adapter<RideHolder> {
    private List<RidesEntity> mValues;

    public RideAdapter(List<RidesEntity> values) {
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
        RidesEntity ride = mValues.get(position);

        holder.bind(ride);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setRides(List<RidesEntity> newRides){
        mValues = newRides;
    }
}
