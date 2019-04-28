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
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import dk.itu.mmda.bikeshare.SpecificBike.ReservedBikeActivity;
import dk.itu.mmda.bikeshare.database.Account;
import dk.itu.mmda.bikeshare.database.Ride;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

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
        //Realm
//        Realm.init(this);
        if(Realm.getDefaultInstance().where(Account.class).count() != 1){ //todo make a way to actually create account
            createStandardAccount();
        }
        Log.e("dk.itu.mmda.bikeshare","before: "+Realm.getDefaultInstance().where(Account.class).findFirst().getBalance());
        if(Realm.getDefaultInstance().where(Account.class).findFirst().getBalance() <= 50){ //todo make a way to actually create account
            increaseAccountBalance();
        }
        Log.e("dk.itu.mmda.bikeshare","after: "+Realm.getDefaultInstance().where(Account.class).findFirst().getBalance());


        //checkbox and fragment
//        mCheckBox = (CheckBox) findViewById(R.id.showListCheckbox);
        fm = getSupportFragmentManager();
        mListFragment = new ListFragment();
        fm.beginTransaction()
                    .add(R.id.listFragment, mListFragment)
                    .commit();

        mCamera = new MyCamera(this);


//        if(mCheckBox.isChecked()) {
//            Fragment fragment = mListFragment;
//            fm.beginTransaction()
//                    .add(R.id.listFragment, fragment)
//                    .commit();
//        }
//        else{
//            Fragment fragment = new HideListFragment();
//            fm.beginTransaction()
//                    .add(R.id.listFragment, fragment)
//                    .commit();
//        }

//        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//           @Override
//           public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
//               if(isChecked) {
//                   Fragment fragment = mListFragment;
//                   fm.beginTransaction()
//                           .replace(R.id.listFragment, fragment)
//                           .commit();
//               }
//               else{
//                   Fragment fragment = new HideListFragment();
//                   fm.beginTransaction()
//                           .replace(R.id.listFragment, fragment)
//                           .commit();
//               }
//           }
//        });

        // version (made for fun)
        BG = (LinearLayout) findViewById(R.id.background);
        BG.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(BikeShareActivity.this, "Version: " + getResources().getString(R.string.version), Toast.LENGTH_SHORT)
                        .show();
                return true;
            }
        });
        // Database
//        sRidesDB = RidesEntity.get(this);
//        List<Ride> values = sRidesDB.getRidesDB();


        // Buttons
//        mAddRide = (Button) findViewById(R.id.add_button);
//        mEndRide = (Button) findViewById(R.id.end_button);
//        // View products click event
//        mAddRide.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mListFragment.createDialog(
//                        getResources().getString(R.string.StartDialogTitle),
//                        R.layout.add_bike_dialog,
//                        R.id.start_what,
//                        R.id.start_where
//                );
//            }
//        });
//        mEndRide.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mListFragment.createDialog(
//                        getResources().getString(R.string.EndDialogTitle),
//                        R.layout.delete_bike_dialog,
//                        R.id.end_what,
//                        R.id.end_where
//                );
//            }
//        });

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
            Realm.getDefaultInstance().executeTransaction( //By doing this async the app will crash
                    new Realm.Transaction() {
                        @Override
                        public  void execute(Realm bgrealm) {
                            Ride foundRide = bgrealm.where(Ride.class).equalTo("primKey", rideId).findFirst();
                            intent.putExtra("Ride", foundRide);
                        }});

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

//        //Stuff to change size of dialog
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(dialog.getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
//        dialog.getWindow().setAttributes(lp);

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
