package dk.itu.mmda.bikeshare.SpecificBike;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import dk.itu.mmda.bikeshare.BikeShareActivity;
import dk.itu.mmda.bikeshare.MyLocationManager;
import dk.itu.mmda.bikeshare.R;
import dk.itu.mmda.bikeshare.database.Account;
import dk.itu.mmda.bikeshare.database.Ride;
import io.realm.Realm;

public class ReservedBikeActivity extends AppCompatActivity {

    Ride mRide;
    MyLocationManager mLocationManager;
    private Double lon;
    private Double lat;
    private String address;
    Activity thisActivity;
    Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserved_bike);

        thisActivity = this;
        mRide = getIntent().getParcelableExtra("Ride");
        setTitle(mRide.getBikeName());

        //todo save preference
        getSharedPreferences("bikeshareSharedPrefs", this.MODE_PRIVATE).edit()
        .putBoolean("isReserving", true)
        .putString("rideId", mRide.getPrimKey())
        .apply();

        mLocationManager = new MyLocationManager(this);

        if(savedInstanceState != null){
            bmp = savedInstanceState.getParcelable("bitmap");
        }
        else{
            bmp = BitmapFactory.decodeByteArray(mRide.getImage(), 0, mRide.getImage().length);
        }
        ImageView imageView = findViewById(R.id.reserved_bike_image);
        imageView.setImageBitmap(bmp);


    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed(); This means that the user cannot press back
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(bmp != null) outState.putParcelable("bitmap", bmp);
        super.onSaveInstanceState(outState);
    }


    public void endRide(View view) {
        //Remove shared prefs when ending ride
        getSharedPreferences("bikeshareSharedPrefs", Context.MODE_PRIVATE).edit().remove("isReserving").remove("rideId").commit();

        setupLocationManager();
        saveLocationAndAddress();


    }

    private void setupLocationManager(){
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
        mLocationManager.setmFusedLocationProviderClient(LocationServices.getFusedLocationProviderClient(this));

    }

    private void saveLocationAndAddress() {

        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        requestPermissions(perms, 1011);
        mLocationManager.enableLocation();

        if (mLocationManager.hasPermission(perms[0]) && mLocationManager.hasPermission(perms[1]) && mLocationManager.isGpsEnabled()) {
            mLocationManager.startLocationUpdates();
            createEndRideDialog();
        }
    }

    private void createEndRideDialog(){

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.AddBikeDialogTitle))
                    .setPositiveButton("Ok", null)
                    .setNegativeButton("Cancel", null);
            View endView = LayoutInflater.from(this).inflate(R.layout.end_ride_dialog, (ViewGroup) findViewById(R.id.reserved_bike_BG), false);
            final TextView nameText = endView.findViewById(R.id.end_ride_dialog_name);
            nameText.setText(mRide.getBikeName());
            builder.setView(endView);

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
//            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//            lp.copyFrom(dialog.getWindow().getAttributes());
//            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.show();
//            dialog.getWindow().setAttributes(lp);

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

                    if (lon != null && lat != null && !address.isEmpty()) {

                        final Ride currRide = Realm.getDefaultInstance().where(Ride.class).equalTo("primKey", mRide.getPrimKey()).findFirst();

                        Date date = new Date();
                        SimpleDateFormat ft =
                                new SimpleDateFormat("hh:mm - dd.MM.yy");

                        String now = ft.format(date);
                        long diff = getTimeDifferenceMinutes(currRide.getStartTime(), now);
                        final double price = diff * currRide.getPricePerMin(); //todo do something else with this
                        Realm.getDefaultInstance().executeTransactionAsync(
                                new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        Ride r = realm.where(Ride.class).equalTo("primKey", mRide.getPrimKey())
                                                .findFirst();
                                        r.setIsFree(true);
                                        r.setAddress(address);
                                        r.setStartLatitude(lat);
                                        r.setStartLongitude(lon);
                                        r.setStartTimeToCurrent();

                                        Account a = realm.where(Account.class).findFirst();
                                        a.setBalance(a.getBalance()-price);

                                    }
                                }
                        );

//                        Realm.getDefaultInstance().executeTransactionAsync( //By doing this async the app will crash
//                                new Realm.Transaction() {
//                                    @Override
//                                    public void execute(Realm bgrealm) {
//                                        Ride currRide = bgrealm.where(Ride.class).equalTo("primKey", mRide.getPrimKey()).findFirst();
//                                        currRide.setEndTimeToCurrent();
//                                        r = currRide;
//                                        long diff = currRide.getStartEndTimeDifference();
//                                        price = diff * currRide.getPricePerMin();
//                                    }
//                                }
//                        );
//                    } //todo i think this needs down
                        Toast.makeText(thisActivity, "The ride was in total " + price + "kr", Toast.LENGTH_LONG)
                                .show();
//                        mRide.setBikeName("ENDED");
                        mLocationManager.stopLocationUpdates();
                        dialog.dismiss();
                        setResult(getResources().getInteger(R.integer.finishParentActivityResult));
                        finish();
//                        realm.executeTransactionAsync(
//                                new Realm.Transaction() {
//                                    @Override
//                                    public void execute(Realm bgrealm) {
//                                        bgrealm.copyToRealm(newRide);
//
//                                    }
//                                },
//                                new Realm.Transaction.OnSuccess() {
//                                    @Override
//                                    public void onSuccess() {
//                                        Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT)
//                                                .show();
//
//                                    }
//                                },
//                                new Realm.Transaction.OnError() {
//                                    @Override
//                                    public void onError(Throwable error) {
//                                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT)
//                                                .show();
//
//                                    }
//                                });

                    }
                }
            });
        }

    public long getTimeDifferenceMinutes(String startTime, String endTime){
        SimpleDateFormat ft =
                new SimpleDateFormat ("hh:mm - dd.MM.yy");
        Date start = null;
        Date end = null;
        Long diffMinutes = null;
        try{
            start = ft.parse(startTime);
            end = ft.parse(endTime);
            long diff = end.getTime() - start.getTime();
            diffMinutes = diff / (60 * 1000) % 60;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diffMinutes;

    }
    }
