package dk.itu.mmda.bikeshare;

import android.app.Application;

import io.realm.Realm;

public class BikeShareApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
//        Realm.deleteRealm(Realm.getDefaultConfiguration()); //In case realm gets messed up
    }
}
