package dk.itu.mmda.bikeshare;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import dk.itu.mmda.bikeshare.database.RidesEntity;

public class RideHolder extends RecyclerView.ViewHolder {

    private TextView mBikeNameView;
    private TextView mBikeStartView;
    private TextView mBikeEndView;
    private TextView mBikeTimeView;

    public RideHolder(LayoutInflater layoutInflater, ViewGroup parent) {
        super(layoutInflater.inflate(R.layout.list_item_ride, parent, false));
        mBikeNameView = itemView.findViewById(R.id.what_bike_ride);
        mBikeStartView = itemView.findViewById(R.id.start_ride);
        mBikeEndView = itemView.findViewById(R.id.end_ride);
        mBikeTimeView = itemView.findViewById(R.id.ride_time);
    }

    public void bind(RidesEntity ride) {
        mBikeNameView.setText(ride.getBikeName());
        mBikeStartView.setText(ride.getBikeStart());
        mBikeEndView.setText(ride.getBikeEnd());
        mBikeTimeView.setText(ride.getTime());
    }
}
