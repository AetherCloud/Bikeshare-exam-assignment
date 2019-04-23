package dk.itu.mmda.bikeshare;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

import dk.itu.mmda.bikeshare.database.Ride;
import dk.itu.mmda.bikeshare.database.RidesEntity;
import dk.itu.mmda.bikeshare.database.RidesVM;
import io.realm.Realm;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {


//    private RidesEntity sRidesDB;
    private RideAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private Realm realm;
//    private RidesVM mRidesVM;

//    public RidesVM getRidesVM(){
//        return mRidesVM;
//    }

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);
//        sRidesDB = RidesEntity.get(getActivity());
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

//        mRidesVM = ViewModelProviders.of(getActivity()).get(RidesVM.class);
        if(mAdapter == null) {
            mAdapter = new RideAdapter(realm.where(Ride.class).equalTo("isFree", true).findAll(), new RideAdapter.rideAdapterInterface(){

                @Override
                public void updateData() {
                    mAdapter.notifyDataSetChanged();
                }
            });

        }
        mRecyclerView.setAdapter(mAdapter);
//        mRidesVM.getAllRides().observe(this, new Observer<List<RidesEntity>>() {
//            @Override
//            public void onChanged(@Nullable List<RidesEntity> rides) {
//                mAdapter.setRides(rides);
//                mAdapter.notifyDataSetChanged();
//            }
//        });
        return view;
    }

    void createDialog(final String title, int layoutId, int editTextWhatId, int editTextWhereId){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setPositiveButton("Ok", null)
                .setNegativeButton("Cancel", null);
        View viewInflated = LayoutInflater.from(getActivity()).inflate(layoutId, (ViewGroup) BikeShareActivity.getBG(), false);
        final EditText whatText = (EditText) viewInflated.findViewById(editTextWhatId);
        final EditText whereText = (EditText) viewInflated.findViewById(editTextWhereId);
        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Do nothing here, get overwritten below
//                The reason being that without the second override, the ok button will always close the dialog
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String tmpWhatText = whatText.getText().toString().trim();
                final String tmpWhereText = whereText.getText().toString().trim();
                if(!tmpWhatText.isEmpty() && !tmpWhereText.isEmpty()) {
                    dialog.dismiss();

                    final Ride newRide = new Ride();
                    newRide.setPrimKey(UUID.randomUUID().toString());
                    newRide.setBikeName(tmpWhatText);
                    newRide.setTimeToCurrent();

                    //Bitmap stuff todo delete later
                    Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.raytracer);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    newRide.setImage(byteArray);

                    if (title == getResources().getString(R.string.AddBikeDialogTitle)) {
                        newRide.setStartRide(tmpWhereText);
                        newRide.setEndRide("");
                    } else {
                        newRide.setStartRide("");
                        newRide.setEndRide(tmpWhereText);
                    }
                        realm.executeTransactionAsync(
                                new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm bgrealm) {
                                        bgrealm.copyToRealm(newRide);

                                    }
                                  },
                                new Realm.Transaction.OnSuccess() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT)
                                                .show();

                                    }
                                },
                                new Realm.Transaction.OnError() {
                                    @Override
                                    public void onError(Throwable error) {
                                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT)
                                                .show();

                                    }
                                });
//                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

}
