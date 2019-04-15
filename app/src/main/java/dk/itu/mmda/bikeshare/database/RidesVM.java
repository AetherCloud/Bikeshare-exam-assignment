package dk.itu.mmda.bikeshare.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

@Deprecated
public class RidesVM extends AndroidViewModel {
    private RidesRepository mRidesRepository;
    private LiveData<List<RidesEntity>> mAllRides;

    public RidesVM(@NonNull Application application) {
        super(application);
        mRidesRepository = new RidesRepository(application);
        mAllRides = mRidesRepository.getAllRides();
    }

    public void insert(RidesEntity ride){
        mRidesRepository.insert(ride);
    }
    public void update(RidesEntity ride){
        mRidesRepository.update(ride);
    }
    public void delete(RidesEntity ride){
        mRidesRepository.delete(ride);
    }
    public void deleteAllRides(){
        mRidesRepository.deleteAll();
    }
    public LiveData<List<RidesEntity>> getAllRides(){
        return mAllRides;
    }
    public RidesEntity getRide(String s){
        return mRidesRepository.getRide(s);
    }
    public RidesEntity getEndableRide(String name){return mRidesRepository.getEndableRide(name);}

}
