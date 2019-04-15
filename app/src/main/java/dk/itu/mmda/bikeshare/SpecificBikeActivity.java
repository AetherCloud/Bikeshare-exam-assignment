package dk.itu.mmda.bikeshare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import dk.itu.mmda.bikeshare.database.Ride;

public class SpecificBikeActivity extends AppCompatActivity {
    Ride mRide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_bike);

        Intent intent = getIntent();
        mRide = intent.getParcelableExtra("Ride");
        setTitle(mRide.getBikeName());


        Bitmap bmp = BitmapFactory.decodeByteArray(mRide.getImage(), 0, mRide.getImage().length);
        ImageView imageView = findViewById(R.id.bikeImage);

        imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, 300,
                300, false));





    }
}
