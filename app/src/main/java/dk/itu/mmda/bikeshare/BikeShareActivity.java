package dk.itu.mmda.bikeshare;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

import dk.itu.mmda.bikeshare.database.RidesEntity;
import dk.itu.mmda.bikeshare.database.RidesVM;
import io.realm.Realm;
import io.realm.exceptions.RealmMigrationNeededException;

public class BikeShareActivity extends AppCompatActivity {

    private static LinearLayout BG;
    private Button mAddRide;
    private Button mEndRide;
    private FragmentManager fm;
//    private RidesEntity sRidesDB;
//    private RideAdapter mAdapter;
    private CheckBox mCheckBox;
    private ListFragment mListFragment;

//    private boolean showList;
    public static LinearLayout getBG(){
        return BG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_share);

        //Realm
        Realm.init(this);

        //checkbox and fragment
        mCheckBox = (CheckBox) findViewById(R.id.showListCheckbox);
        fm = getSupportFragmentManager();
        mListFragment = new ListFragment();

        if(mCheckBox.isChecked()) {
            Fragment fragment = mListFragment;
            fm.beginTransaction()
                    .add(R.id.fragment, fragment)
                    .commit();
        }
        else{
            Fragment fragment = new HideListFragment();
            fm.beginTransaction()
                    .add(R.id.fragment, fragment)
                    .commit();
        }

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
               if(isChecked) {
                   Fragment fragment = mListFragment;
                   fm.beginTransaction()
                           .replace(R.id.fragment, fragment)
                           .commit();
               }
               else{
                   Fragment fragment = new HideListFragment();
                   fm.beginTransaction()
                           .replace(R.id.fragment, fragment)
                           .commit();
               }
           }
        });

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
        mAddRide = (Button) findViewById(R.id.add_button);
        mEndRide = (Button) findViewById(R.id.end_button);
        // View products click event
        mAddRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListFragment.createDialog(
                        getResources().getString(R.string.StartDialogTitle),
                        R.layout.start_dialog,
                        R.id.start_what,
                        R.id.start_where
                );
            }
        });
        mEndRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListFragment.createDialog(
                        getResources().getString(R.string.EndDialogTitle),
                        R.layout.end_dialog,
                        R.id.end_what,
                        R.id.end_where
                );
            }
        });
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
                mListFragment.createDialog(
                        getResources().getString(R.string.StartDialogTitle),
                        R.layout.start_dialog,
                        R.id.start_what,
                        R.id.start_where
                );
                return true;
            case R.id.menu_end:
                mListFragment.createDialog(
                        getResources().getString(R.string.EndDialogTitle),
                        R.layout.end_dialog,
                        R.id.end_what,
                        R.id.end_where
                );
                return true;
            case R.id.menu_deleteAll:
                Realm.getDefaultInstance().executeTransaction( //By doing this async the app will crash
                        new Realm.Transaction() {
                            @Override
                            public  void execute(Realm bgrealm) {
                                bgrealm.deleteAll();
                            }});

//                mListFragment.getRidesVM().deleteAllRides();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

}
