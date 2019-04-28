package dk.itu.mmda.bikeshare.SpecificBike;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import dk.itu.mmda.bikeshare.HoldersAndAdapters.PaymentsAdapter;
import dk.itu.mmda.bikeshare.HoldersAndAdapters.RideAdapter;
import dk.itu.mmda.bikeshare.R;
import dk.itu.mmda.bikeshare.Database.Ride;
import io.realm.Realm;

public class SpecificBikeActivity extends AppCompatActivity {
    Ride mRide;
//    FragmentManager fm;
    Bitmap bmp;
    PaymentsAdapter paymentsAdapter;
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //fullscreen image in
        if(isLandscape()){
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


        //adapter
        mRecyclerView = (RecyclerView) findViewById(R.id.payments_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        if(paymentsAdapter == null) {
            paymentsAdapter =
                    new PaymentsAdapter(Realm.getDefaultInstance()
                            .where(Ride.class).equalTo("primKey", mRide.getPrimKey())
                            .findFirst().getPayments());
        }
        mRecyclerView.setAdapter(paymentsAdapter);
    }

    public void reserveBike(View view) {

        //todo Check realm if ride is free first
        final Ride realmRide = Realm.getDefaultInstance().where(Ride.class).equalTo("primKey", mRide.getPrimKey()).findFirst();
        if(realmRide.isFree()){
            Realm.getDefaultInstance().executeTransaction(
                    new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Ride r = realm.where(Ride.class).equalTo("primKey", mRide.getPrimKey()).findFirst();
                            r.setIsFree(false);
                            r.setStartTimeToCurrent();

                        }
                    }
            );
            final Intent intent = new Intent(this, ReservedBikeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            intent.putExtra("Ride", mRide);
            startActivityForResult(intent, getResources().getInteger(R.integer.notUsedRequest));
            setResult(getResources().getInteger(R.integer.refreshListResult));
            finish();
        }
        else{
            Toast.makeText(this, "Bike has been rented, too bad", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == getResources().getInteger(R.integer.finishParentActivityResult)){
            setResult(getResources().getInteger(R.integer.refreshListResult));
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
        if (hasFocus && isLandscape()) {
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

    private boolean isLandscape(){
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
}

