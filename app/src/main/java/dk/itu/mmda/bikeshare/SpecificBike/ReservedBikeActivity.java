package dk.itu.mmda.bikeshare.SpecificBike;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import dk.itu.mmda.bikeshare.BikeShareActivity;
import dk.itu.mmda.bikeshare.R;

public class ReservedBikeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserved_bike);
        //todo save preference
        SharedPreferences.Editor editor = getPreferences(this.MODE_PRIVATE).edit();

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
