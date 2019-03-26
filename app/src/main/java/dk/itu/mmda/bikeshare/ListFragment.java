package dk.itu.mmda.bikeshare;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
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

import java.util.List;

import dk.itu.mmda.bikeshare.database.RidesEntity;
import dk.itu.mmda.bikeshare.database.RidesVM;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {


//    private RidesEntity sRidesDB;
    private RideAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RidesVM mRidesVM;

    public RidesVM getRidesVM(){
        return mRidesVM;
    }

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);
//        sRidesDB = RidesEntity.get(getActivity());
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mRidesVM = ViewModelProviders.of(getActivity()).get(RidesVM.class);
        mAdapter = new RideAdapter(mRidesVM.getAllRides().getValue());
        mRecyclerView.setAdapter(mAdapter);
        mRidesVM.getAllRides().observe(this, new Observer<List<RidesEntity>>() {
            @Override
            public void onChanged(@Nullable List<RidesEntity> rides) {
                mAdapter.setRides(rides);
                mAdapter.notifyDataSetChanged();
            }
        });
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
                String tmpWhatText = whatText.getText().toString().trim();
                String tmpWhereText = whereText.getText().toString().trim();
                if(!tmpWhatText.isEmpty() && !tmpWhereText.isEmpty()) {
                    dialog.dismiss();
                    if (title == getResources().getString(R.string.StartDialogTitle)) {
//                        sRidesDB.addRide(tmpWhatText, tmpWhereText);
//                        mRidesVM.insert(new Ride(tmpWhatText, tmpWhereText, ""));
                        mRidesVM.insert(new RidesEntity(tmpWhatText, tmpWhereText, ""));
                    } else {
//                        mRidesVM.insert(new Ride(tmpWhatText, "", tmpWhereText));
//                        mRidesVM.getRide(tmpWhatText).setEndRide(tmpWhereText);
                        RidesEntity entity = mRidesVM.getEndableRide(tmpWhatText);
                        if(entity == null) return; //crashes if entity is not found (entity is null)

                        entity.setBikeEnd(tmpWhereText);
                        mRidesVM.update(entity);
                    }
                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT)
                            .show();
//                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

}
