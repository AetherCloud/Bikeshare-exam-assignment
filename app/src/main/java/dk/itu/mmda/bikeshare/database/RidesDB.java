package dk.itu.mmda.bikeshare.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
@Deprecated
@Database(entities = {RidesEntity.class}, version = 1, exportSchema = false)
public abstract class RidesDB extends RoomDatabase {

    public abstract RidesDao ridesDao();
    private static volatile RidesDB sRidesDB;

    public static RidesDB get(final Context context){
        if(sRidesDB == null)
            synchronized (RidesDB.class) {
                if (sRidesDB == null)
                    sRidesDB =
                            Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    RidesDB.class, "rides_database")
                                    .build();
            }
        return sRidesDB;
    }
}

