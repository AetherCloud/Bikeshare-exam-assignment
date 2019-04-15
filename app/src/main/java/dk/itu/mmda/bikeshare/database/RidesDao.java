package dk.itu.mmda.bikeshare.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;
@Deprecated
@Dao
public interface RidesDao {

    @Insert
    void insert(RidesEntity ride);

    @Update
    void update(RidesEntity ride);

    @Delete
    void delete(RidesEntity ride);

    @Query("DELETE FROM rides_table")
    void deleteAll();

    @Query("SELECT * FROM rides_table " +
            "ORDER BY id")
    LiveData<List<RidesEntity>> getAllRides();

    @Query("SELECT * FROM rides_table " +
            "WHERE name LIKE :lookupName LIMIT 1")
    RidesEntity getRide(String lookupName);

    @Query("SELECT * FROM rides_table " +
            "WHERE `end` = '' AND name like :name")
    RidesEntity getEndableRide(String name);
}
