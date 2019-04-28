package dk.itu.mmda.bikeshare;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import dk.itu.mmda.bikeshare.Database.Ride;
import dk.itu.mmda.bikeshare.HoldersAndAdapters.RideAdapter;
import io.realm.Realm;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {


    private RideAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private Realm realm;
    private double lon;
    private double lat;
    private String address = "";
    private View startView;
    private Bitmap currentBitmap;
    private MyLocationManager mLocationManager;

    private Fragment thisFragment;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        thisFragment = this;
        mLocationManager = new MyLocationManager(getActivity());
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);


        mLocationManager.setmLocationCallback(new LocationCallback(){

            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null) return;
                for(Location location : locationResult.getLocations()){
                    lon = location.getLongitude();
                    lat = location.getLatitude();
                    address = mLocationManager.getAddress(lon, lat);
                }
            }
        });
        mLocationManager.setmFusedLocationProviderClient(LocationServices.getFusedLocationProviderClient(getActivity()));


        mRecyclerView = view.findViewById(R.id.fragment_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(mAdapter == null) {
            mAdapter = new RideAdapter(realm.where(Ride.class).equalTo("isFree", true).findAll());
        }
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    void createAddBikeDialog() {

        //permission to use gps
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        requestPermissions(perms, 1011);
        mLocationManager.enableLocation();

        if (mLocationManager.hasPermission(perms[0]) && mLocationManager.hasPermission(perms[1]) && mLocationManager.isGpsEnabled()) {

            mLocationManager.startLocationUpdates();

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setTitle(getResources().getString(R.string.AddBikeDialogTitle))
                    .setPositiveButton("Ok", null)
                    .setNegativeButton("Cancel", null);
            startView = LayoutInflater.from(getActivity()).inflate(R.layout.add_bike_dialog, (ViewGroup) BikeShareActivity.getBG(), false);
            final EditText nameText = startView.findViewById(R.id.addBike_name);
            final EditText typeText = startView.findViewById(R.id.addBike_type);
            builder.setView(startView);

            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                Do nothing here, get overwritten below
//                The reason being that without the second override, the ok button will always close the dialog
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mLocationManager.stopLocationUpdates();
                    dialog.cancel();
                }
            });

            final AlertDialog dialog = builder.create();

            //Stuff to change size of dialog
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.show();
            dialog.getWindow().setAttributes(lp);

            //back button
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    if (i == KeyEvent.KEYCODE_BACK) {
                        //stop gps when back is pressed, same as cancel
                        mLocationManager.stopLocationUpdates();
                        dialog.dismiss();
                    }
                    return true;
                }
            });

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String tmpNameText = nameText.getText().toString().trim();
                    final String tmpTypeText = typeText.getText().toString().trim();
                    if (!tmpNameText.isEmpty() && address != "" && !tmpTypeText.isEmpty() && currentBitmap != null) {
                        dialog.dismiss();

                        final Ride newRide = new Ride();
                        newRide.setPrimKey(UUID.randomUUID().toString());
                        newRide.setBikeName(tmpNameText);
                        newRide.setType(tmpTypeText);
                        newRide.setStartLongitude(lon);
                        newRide.setStartLatitude(lat);
                        newRide.setAddress(address);
                        mLocationManager.stopLocationUpdates();

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        currentBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        newRide.setImage(byteArray);


                        realm.executeTransactionAsync(
                                new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm bgrealm) {
                                        bgrealm.copyToRealm(newRide);

                                    }
                                },
                                new Realm.Transaction.OnSuccess() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT)
                                                .show();

                                    }
                                },
                                new Realm.Transaction.OnError() {
                                    @Override
                                    public void onError(Throwable error) {
                                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT)
                                                .show();

                                    }
                                });
                        mLocationManager.stopLocationUpdates();

                        //By notifying and reloading this fragment the user will see the list update when adding a new bike
                        mAdapter.notifyDataSetChanged(); //todo this does not work with the first item
                        //https://stackoverflow.com/questions/20702333/refresh-fragment-at-reload
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        if (Build.VERSION.SDK_INT >= 26) {
                            ft.setReorderingAllowed(false);
                        }
                        ft.detach(thisFragment).attach(thisFragment).commit();
                    }

                }
            });
        }
    }




    public void setDialogImage(Bitmap bmp){
        ImageView imageView = startView.findViewById(R.id.start_imageview);
        currentBitmap = bmp;
        imageView.setImageBitmap(bmp);

    }

    public RideAdapter getRecyclerviewAdapter(){
        return mAdapter;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
