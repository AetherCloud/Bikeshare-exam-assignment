package dk.itu.mmda.bikeshare;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import dk.itu.mmda.bikeshare.database.Ride;

public class RideAdapter extends RecyclerView.Adapter<RideHolder> {
//    private List<RidesEntity> mValues;
    private rideAdapterInterface mInterface;
    private List<Ride> mValues;

    public RideAdapter(List<Ride> values, rideAdapterInterface i) {
        mValues = values;
        mInterface = i;
    }

    //By using an interface the holder can can notifyDataSetChanged from the adapter.
    // More methods can be added later if needed
    //Source: https://stackoverflow.com/questions/43433706/best-way-to-notify-recyclerview-adapter-from-viewholder/43434674
    interface rideAdapterInterface {
        void updateData();
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
        holder.bind(ride, mInterface);
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

}
