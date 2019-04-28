package dk.itu.mmda.bikeshare.database;

import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.RealmObject;

public class Ride extends RealmObject implements Parcelable {
    @PrimaryKey
    private String primKey;
    private String bikeName;
    private String startTime;
    private String endTime;
    private boolean isFree = true;
    private byte[] image;
    private double startLongitude;
    private double startLatitude;
    private double endLongitude;
    private double endLatitude;
    private double pricePerMin = 0.5; //Default price. A way to change this could be implemented
    private String type;
    private String address;



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
        parcel.writeString(bikeName);
        parcel.writeString(startTime);
        parcel.writeString(endTime);
        parcel.writeByte((byte) (isFree ? 1 : 0));
        parcel.writeByteArray(image);
        parcel.writeDouble(startLongitude);
        parcel.writeDouble(startLatitude);
        parcel.writeDouble(endLongitude);
        parcel.writeDouble(endLatitude);
        parcel.writeDouble(pricePerMin);
        parcel.writeString(type);
        parcel.writeString(address);
    }

    protected Ride(Parcel in) {
        primKey = in.readString();
        bikeName = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        isFree = in.readByte() != 0;
        image = in.createByteArray();
        startLongitude = in.readDouble();
        startLatitude = in.readDouble();
        endLongitude = in.readDouble();
        endLatitude = in.readDouble();
        pricePerMin = in.readDouble();
        type = in.readString();
        address = in.readString();
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
        return bikeName;
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



    public double getPricePerMin() {
        return pricePerMin;
    }

    public void setPricePerMin(double pricePerMin) {
        this.pricePerMin = pricePerMin;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setBikeName(String bikeName) {
        this.bikeName = bikeName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTimeToCurrent(){
        Date date = new Date();
        SimpleDateFormat ft =
                new SimpleDateFormat ("hh:mm - dd.MM.yy");

        startTime = ft.format(date);

    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTimeToCurrent() {
        Date date = new Date();
        SimpleDateFormat ft =
                new SimpleDateFormat ("hh:mm - dd.MM.yy");

        endTime = ft.format(date);
    }

    public double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public double getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(double endLongitude) {
        this.endLongitude = endLongitude;
    }

    public double getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(double endLatitude) {
        this.endLatitude = endLatitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}

