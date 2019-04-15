package dk.itu.mmda.bikeshare.database;
import android.arch.persistence.room.ColumnInfo;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
@Deprecated
@Entity(tableName = "rides_table")
public class RidesEntity    {

    public RidesEntity(){

    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @NonNull
    @ColumnInfo(name = "name")
    private String mBikeName;

    @NonNull
    @ColumnInfo(name = "start")
    private String mBikeStart;

    @NonNull
    @ColumnInfo(name = "end")
    private String mBikeEnd;

    @NonNull
    @ColumnInfo(name = "time")
    private String mTime;

    public RidesEntity(String name, String start, String end) {
        mBikeName = name;
        mBikeStart = start;
        mBikeEnd = end;
        Date date = new Date();
        SimpleDateFormat ft =
                new SimpleDateFormat ("hh:mm - dd.MM.yy");

        mTime = ft.format(date);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBikeName() {
        return mBikeName;
    }

    public void setBikeName(String bikeName) {
        mBikeName = bikeName;
    }

    @NonNull
    public String getBikeStart() {
        return mBikeStart;
    }

    public void setBikeStart(@NonNull String mBikeStart) {
        this.mBikeStart = mBikeStart;
    }

    @NonNull
    public String getBikeEnd() {
        return mBikeEnd;
    }

    public void setBikeEnd(@NonNull String mBikeEnd) {
        this.mBikeEnd = mBikeEnd;
    }

    @NonNull
    public String getTime() {
        return mTime;
    }

    public void setTime(@NonNull String mTime) {
        this.mTime = mTime;
    }


//    public void setmTime(@NonNull String mTime) {

//    }
}

/*
@Deprecated
public class RidesDB {
    private static RidesDB sRidesDB;
    private ArrayList<Ride> mAllRides;

    private RidesDB(Context context) {
        // Add some rides for testing purposes
        mAllRides = new ArrayList<>();
        mAllRides.add(new Ride("Chuck Norris bike", "ITU", "Fields"));
        mAllRides.add(new Ride("Chuck Norris bike", "Fields", "Kongens Nytorv"));
        mAllRides.add(new Ride("Bruce Lee bike", "Kobenhavns Lufthavn", "Kobenhavns Hovedbanegard"));
    }

    public static RidesDB get(Context context) {
        if (sRidesDB == null)
            sRidesDB = new RidesDB(context);
        return sRidesDB;
    }

    public List<Ride> getRidesDB() {
        return mAllRides;
    }

    public void addRide(String what, String where) {
        mAllRides.add(new Ride(what, where, ""));
    }

    public void endRide(String what, String where) {
        mAllRides.add(new Ride(what, "", where));
    }
}
*/