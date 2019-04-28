package dk.itu.mmda.bikeshare;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MyLocationManager {

    Activity mActivity;
    private boolean gps_enabled = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;

    public MyLocationManager(Activity activity){
        mActivity = activity;
    }

    //source: https://stackoverflow.com/questions/10311834/how-to-check-if-location-services-are-enabled
    public void enableLocation(){
        LocationManager lm = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}


        if(!gps_enabled) {
            // notify user
            new AlertDialog.Builder(mActivity)
                    .setMessage(R.string.locationNotEnabled)
                    .setPositiveButton(R.string.locationEnableNow, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            mActivity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton(android.R.string.cancel ,null)
                    .show();
        }
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
//        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
    }
    public void stopLocationUpdates() {
        mFusedLocationProviderClient
                .removeLocationUpdates(mLocationCallback);
    }

    public String getAddress(double longitude, double latitude) {
        Geocoder geocoder = new Geocoder(mActivity, Locale.getDefault());
        StringBuilder stringBuilder = new StringBuilder();
        try {
            List<Address> addresses =
                    geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                stringBuilder.append(address.getAddressLine(0)).append("\n");
                stringBuilder.append(address.getLocality()).append("\n");
                stringBuilder.append(address.getPostalCode()).append("\n");
                stringBuilder.append(address.getCountryName());
            } else
                return "No address found";
        } catch (IOException ex) { }
        return stringBuilder.toString();
    }

    public boolean isGpsEnabled(){
        return gps_enabled;
    }

    public void setmLocationCallback(LocationCallback mLocationCallback) {
        this.mLocationCallback = mLocationCallback;
    }

    public void setmFusedLocationProviderClient(FusedLocationProviderClient mFusedLocationProviderClient) {
        this.mFusedLocationProviderClient = mFusedLocationProviderClient;
    }

    public boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return Objects.requireNonNull(mActivity)
                    .checkSelfPermission(permission) ==
                    PackageManager.PERMISSION_GRANTED;
        return true;
    }
}
