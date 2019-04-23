package dk.itu.mmda.bikeshare.SpecificBike;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import dk.itu.mmda.bikeshare.BikeShareActivity;
import dk.itu.mmda.bikeshare.R;
import dk.itu.mmda.bikeshare.database.Ride;

public class ReservedBikeActivity extends AppCompatActivity {

    Ride mRide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserved_bike);
        mRide = getIntent().getParcelableExtra("Ride");

        //todo save preference
        SharedPreferences.Editor editor = getPreferences(this.MODE_PRIVATE).edit();
        editor.putBoolean("isReserving", true);
        editor.putString("rideId", mRide.getPrimKey());
        editor.apply();
        //https://learnit.itu.dk/pluginfile.php/239171/mod_resource/content/0/Slides%20%2306.pdf

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed(); This means that the user cannot press back
    }

    public void endRide(View view) {
        setResult(getResources().getInteger(R.integer.finishParentActivty));
        finish();
    }
}
