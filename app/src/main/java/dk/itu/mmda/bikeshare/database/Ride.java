package dk.itu.mmda.bikeshare.database;

import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.RealmObject;

public class Ride extends RealmObject implements Parcelable {
    @PrimaryKey
    private String primKey;
    private String mBikeName;
    private String mStartRide;
    private String mEndRide;
    private String mTime;
    private boolean isFree = true;
    private byte[] image;
    private double longitude;
    private double latitude;


    public Ride () {

    }

    //Parcelable
    //https://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(primKey);
        parcel.writeString(mBikeName);
        parcel.writeString(mStartRide);
        parcel.writeString(mEndRide);
        parcel.writeString(mTime);
        parcel.writeByte((byte) (isFree ? 1 : 0));
        parcel.writeByteArray(image);
        parcel.writeDouble(longitude);
        parcel.writeDouble(latitude);
    }

    protected Ride(Parcel in) {
        primKey = in.readString();
        mBikeName = in.readString();
        mStartRide = in.readString();
        mEndRide = in.readString();
        mTime = in.readString();
        isFree = in.readByte() != 0;
        image = in.createByteArray();
        longitude = in.readDouble();
        latitude = in.readDouble();
    }

    public static final Creator<Ride> CREATOR = new Creator<Ride>() {
        @Override
        public Ride createFromParcel(Parcel in) {
            return new Ride(in);
        }

        @Override
        public Ride[] newArray(int size) {
            return new Ride[size];
        }
    };

    public String getBikeName() {
        return mBikeName;
    }
    public String getStartRide() { return mStartRide; }
    public String getEndRide() { return mEndRide; }
    public String getTime() {
        return mTime;
    }

    public void setBikeName(String bikeName) { mBikeName = bikeName; }
    public void setStartRide (String startRide) { mStartRide = startRide; }
    public void setEndRide(String mEndRide) { this.mEndRide = mEndRide; }
    public void setTimeToCurrent(){
        Date date = new Date();
        SimpleDateFormat ft =
                new SimpleDateFormat ("hh:mm - dd.MM.yy");

        mTime = ft.format(date);

    }

    public String getPrimKey() {
        return primKey;
    }

    public void setPrimKey(String primKey) {
        this.primKey = primKey;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setIsFree(boolean free) {
        isFree = free;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


}

