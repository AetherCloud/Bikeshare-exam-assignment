package dk.itu.mmda.bikeshare.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Deprecated
public class RidesRepository {

    private RidesDao mRidesDao;
    private LiveData<List<RidesEntity>> mAllRides;
    public RidesRepository(Application application){
        RidesDB db = RidesDB.get(application);
        mRidesDao = db.ridesDao();
        mAllRides = mRidesDao.getAllRides();
    }

    public void insert(RidesEntity ride) {
        new InsertAsyncTask(mRidesDao).execute(ride);
    }

    public void update(RidesEntity ride){
        new UpdateAsyncTask(mRidesDao).execute(ride);
    }

    public void delete(RidesEntity ride){
        new DeleteAsyncTask(mRidesDao).execute(ride);
    }

    public void deleteAll(){
        new DeleteAllAsyncTask(mRidesDao).execute();
    }

    public LiveData<List<RidesEntity>> getAllRides() {
        return mAllRides;
    }

    public RidesEntity getRide(String string){
//        new GetRideAsyncTask(mRidesDao).execute(string);
        return mRidesDao.getRide(string);
    }
    public RidesEntity getEndableRide(String name){
        RidesEntity entity = null;
        try {
            entity =  new GetEndableRideAsyncTask(mRidesDao).execute(name).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return entity;
    }

    private static class InsertAsyncTask extends AsyncTask<RidesEntity, Void, Void>{

        private RidesDao mDao;

        public InsertAsyncTask(RidesDao dao){
            mDao = dao;
        }
        @Override
        protected Void doInBackground(final RidesEntity... rides) {
            mDao.insert(rides[0]);
            return null;
        }
    }
    private static class UpdateAsyncTask extends AsyncTask<RidesEntity, Void, Void>{

        private RidesDao mDao;

        public UpdateAsyncTask(RidesDao dao){
            mDao = dao;
        }
        @Override
        protected Void doInBackground(final RidesEntity... rides) {
            mDao.update(rides[0]);
            return null;
        }
    }
    private static class DeleteAsyncTask extends AsyncTask<RidesEntity, Void, Void>{

        private RidesDao mDao;

        public DeleteAsyncTask(RidesDao dao){
            mDao = dao;
        }
        @Override
        protected Void doInBackground(final RidesEntity... rides) {
            mDao.delete(rides[0]);
            return null;
        }
    }
    private static class DeleteAllAsyncTask extends AsyncTask<Void, Void, Void>{

        private RidesDao mDao;

        public DeleteAllAsyncTask(RidesDao dao){
            mDao = dao;
        }
        @Override
        protected Void doInBackground(final Void... voids) {
            mDao.deleteAll();
            return null;
        }
    }
    private static class GetEndableRideAsyncTask extends AsyncTask<String, Void, RidesEntity>{

        private RidesDao mDao;

        public GetEndableRideAsyncTask(RidesDao dao){
            mDao = dao;
        }

        @Override
        protected RidesEntity doInBackground(String... strings) {
            return mDao.getEndableRide(strings[0]);
        }
    }
    /*private static class GetAllRidesAsyncTask extends AsyncTask<Void, Void, LiveData<List<Ride>>>{

        private RidesDao mDao;

        public GetAllRidesAsyncTask(RidesDao dao){
            mDao = dao;
        }
        @Override
        protected LiveData<List<Ride>> doInBackground(Void...voids) {
            return mDao.getAllRides();

        }
    }
    private static class GetRideAsyncTask extends AsyncTask<String, Void, Ride>{

        private RidesDao mDao;

        public GetRideAsyncTask(RidesDao dao){
            mDao = dao;
        }
        @Override
        protected Ride doInBackground(final String... strings) {
            return mDao.getRide(strings[0]);
        }
    }*/
}
