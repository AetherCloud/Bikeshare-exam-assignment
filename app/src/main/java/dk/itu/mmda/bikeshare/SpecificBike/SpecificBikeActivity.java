package dk.itu.mmda.bikeshare.SpecificBike;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import dk.itu.mmda.bikeshare.R;
import dk.itu.mmda.bikeshare.database.Ride;

public class SpecificBikeActivity extends AppCompatActivity {
    Ride mRide;
//    FragmentManager fm;
    Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //fullscreen image in
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
//            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            hideSystemUI();
//        GestureDetector gestureDetector = new GestureDetector(this, new MyGestureListener());
        }
        setContentView(R.layout.activity_specific_bike);
        //Gesture
//        View view = findViewById(R.id.specific_bike_land_view)


        mRide = getIntent().getParcelableExtra("Ride");
        setTitle(mRide.getBikeName());

        if(savedInstanceState != null){
            bmp = savedInstanceState.getParcelable("bitmap");
        }
        else{
            bmp = BitmapFactory.decodeByteArray(mRide.getImage(), 0, mRide.getImage().length);
        }
        ImageView imageView = findViewById(R.id.bikeImage);
        imageView.setImageBitmap(bmp);


    }

    public void reserveBike(View view) {

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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(bmp != null) outState.putParcelable("bitmap", bmp);
        super.onSaveInstanceState(outState);
    }

    //https://developer.android.com/training/system-ui/immersive.html
    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                         View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }
    // In the SimpleOnGestureListener subclass you should override
    // onDown and any other gesture that you want to detect.
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            hideSystemUI();
            Log.e("dk.itu.mmda.bikeshare","tap");
            return super.onSingleTapUp(e);
        }
    }
}

