package dk.itu.mmda.bikeshare.HoldersAndAdapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
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

public class RideHolder extends RecyclerView.ViewHolder {

    private TextView mNameView;
    private TextView mAddressView;
    private TextView mPriceView;
    private TextView mTypeView;

    public RideHolder(LayoutInflater layoutInflater, ViewGroup parent) {
        super(layoutInflater.inflate(R.layout.list_item_ride, parent, false));
        mNameView = itemView.findViewById(R.id.list_bike_name);
        mAddressView = itemView.findViewById(R.id.list_bike_address);
        mTypeView = itemView.findViewById(R.id.list_bike_type);
        mPriceView = itemView.findViewById(R.id.list_bike_price);

    }

    public void bind(final Ride ride) {
        mNameView.setText(ride.getBikeName());
        mAddressView.setText(ride.getAddress());
        mPriceView.setText(ride.getPricePerMin() + "kr/min");
        mTypeView.setText(ride.getType());

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {


                final Intent intent = new Intent(itemView.getContext(), SpecificBikeActivity.class);
                Realm.getDefaultInstance().executeTransaction( //By doing this async the app will crash
                        new Realm.Transaction() {
                            @Override
                            public  void execute(Realm bgrealm) {
                                intent.putExtra("Ride", ride);
                            }

                        }
                );
                Activity a = (Activity) itemView.getContext();
                a.startActivityForResult(intent, a.getResources().getInteger(R.integer.notUsedRequest));

                //onclick done
            }
        });

        final LinearLayout itemBG = itemView.findViewById(R.id.listItemBG);
        Realm.getDefaultInstance().executeTransaction( //By doing this async the app will crash
                new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgrealm) {
                        int color;
                        if (ride.isFree()) {
                            color = ContextCompat.getColor(itemView.getContext(), R.color.colorFreeBike);
                        } else {
                            color = ContextCompat.getColor(itemView.getContext(), R.color.colorTakenBike);

                        }
                            itemBG.setBackgroundColor(color);
                            mNameView.setBackgroundColor(color);
                            mAddressView.setBackgroundColor(color);
                            mTypeView.setBackgroundColor(color);
                            mPriceView.setBackgroundColor(color);
                    }
                }
        );
    }



}
