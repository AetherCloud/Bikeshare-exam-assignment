package dk.itu.mmda.bikeshare;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.util.UUID;

import dk.itu.mmda.bikeshare.SpecificBike.ReservedBikeActivity;
import dk.itu.mmda.bikeshare.Database.Account;
import dk.itu.mmda.bikeshare.Database.Ride;
import io.realm.Realm;

public class BikeShareActivity extends AppCompatActivity {

    private static LinearLayout BG;
    private FragmentManager fm;
    private ListFragment mListFragment;
    private MyCamera mCamera;

//    private boolean showList;
    public static LinearLayout getBG(){
        return BG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_share);
        if(Realm.getDefaultInstance().where(Account.class).count() != 1){ //todo make a way to actually create account
            createStandardAccount();
        }
        if(Realm.getDefaultInstance().where(Account.class).findFirst().getBalance() <= 50){ //todo make a way to actually create account
            increaseAccountBalance();
        }


        fm = getSupportFragmentManager();
        mListFragment = new ListFragment();
        fm.beginTransaction()
                    .add(R.id.listFragment, mListFragment)
                    .commit();

        mCamera = new MyCamera(this);


        // version (made for fun)
        BG = findViewById(R.id.background);
        BG.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(BikeShareActivity.this, "Version: " + getResources().getString(R.string.version), Toast.LENGTH_SHORT)
                        .show();
                return true;
            }
        });

        restartReservedRide();
    }


    private void createStandardAccount() {
        final Account account = new Account();
        account.setAccountId(UUID.randomUUID().toString());
        account.setName("Random user");
        Realm.getDefaultInstance().executeTransaction(
                new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealm(account);

                    }
                });
    }
    private void increaseAccountBalance() { //todo make a better way to add more money to account
        Realm.getDefaultInstance().executeTransactionAsync(
                new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Account a = realm.where(Account.class).findFirst();
                        a.setBalance(a.getBalance()+200);
                    }
                }
        );
    }

    /**
     * uses shared preferences to restart ReservedBikeActivity in onCreate
     */
    private void restartReservedRide(){
        boolean isReserving = getSharedPreferences("bikeshareSharedPrefs", Context.MODE_PRIVATE).getBoolean("isReserving", false);
        final String rideId = getSharedPreferences("bikeshareSharedPrefs", Context.MODE_PRIVATE).getString("rideId", "");

        if(isReserving && (rideId != "")) {
            final Intent intent = new Intent(this, ReservedBikeActivity.class);
            Ride foundRide = Realm.getDefaultInstance().where(Ride.class).equalTo("primKey", rideId).findFirst();
            intent.putExtra("Ride", foundRide);
            startActivityForResult(intent, getResources().getInteger(R.integer.notUsedRequest));
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu_layout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_add:
                mListFragment.createAddBikeDialog();
                return true;
            case R.id.menu_account:
                createAccountDialog();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void createAccountDialog() {
        final Account account = Realm.getDefaultInstance().where(Account.class).findFirst();

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(account.getName())
                .setMessage("Current balance: " + account.getBalance()+"kr")
                .setPositiveButton("Close", null);
        final AlertDialog dialog = builder.create();

        dialog.show();

    }

    public void TakePicture(View view) {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, 0);

        Intent i = mCamera.dispatchTakePictureIntent(getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        startActivityForResult(i, getResources().getInteger(R.integer.cameraRequest));
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Bitmap bmp = (Bitmap) data.getExtras().get("data");
//        mListFragment.setDialogImage(bmp);
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        int i = 0;
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == getResources().getInteger(R.integer.cameraRequest)) { //camera
            try {
                if (resultCode == RESULT_OK) {
                    File file = new File(mCamera.getmCurrentPhotoPath());
                    Bitmap bitmap = MediaStore.Images.Media
                            .getBitmap(this.getContentResolver(), Uri.fromFile(file));
                    if (bitmap != null) {
                        bitmap = getResizedBitmap(bitmap, getResources().getInteger(R.integer.maxBitmapSize));
                        mListFragment.setDialogImage(bitmap);
                    }
                }

            } catch (Exception error) {
                error.printStackTrace();
            }
        }
        else if(resultCode == getResources().getInteger(R.integer.refreshListResult)){ //restarted ReservedBike
            mListFragment.getRecyclerviewAdapter().notifyDataSetChanged();
            //https://stackoverflow.com/questions/20702333/refresh-fragment-at-reload
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (Build.VERSION.SDK_INT >= 26) {
                ft.setReorderingAllowed(false);
            }
            ft.detach(mListFragment).attach(mListFragment).commit();
        }
    }
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

}
