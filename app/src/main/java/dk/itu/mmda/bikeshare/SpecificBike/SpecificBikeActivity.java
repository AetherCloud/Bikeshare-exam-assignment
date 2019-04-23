package dk.itu.mmda.bikeshare.SpecificBike;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import dk.itu.mmda.bikeshare.R;
import dk.itu.mmda.bikeshare.database.Ride;

public class SpecificBikeActivity extends AppCompatActivity {
    Ride mRide;
//    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_bike);

        mRide = getIntent().getParcelableExtra("Ride");
        setTitle(mRide.getBikeName());


        Bitmap bmp = BitmapFactory.decodeByteArray(mRide.getImage(), 0, mRide.getImage().length);
        ImageView imageView = findViewById(R.id.bikeImage);

        imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, bmp.getWidth(),
                bmp.getHeight(), false));

//        fm = getSupportFragmentManager();
//        Fragment fragment = new NotReservedFragment();
//        fm.beginTransaction()
//                .add(R.id.specificBikeFragment, fragment)
//                .commit();
    }
    public void reserveBike(View view) {

//        Fragment fragment = new ReservedFragment();
//        fm.beginTransaction()
//                .replace(R.id.specificBikeFragment, fragment)
//                .commit();

        //todo Check realm if ride is free first
        final Intent intent = new Intent(this, ReservedBikeActivity.class);
        intent.putExtra("Ride", mRide);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == getResources().getInteger(R.integer.finishParentActivty)){
            finish();
        }
    }
}
