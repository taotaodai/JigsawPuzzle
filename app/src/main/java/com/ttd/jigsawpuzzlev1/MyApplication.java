package com.ttd.jigsawpuzzlev1;

import android.app.Application;

import com.ttd.jigsawpuzzlev1.data.DaoMaster;
import com.ttd.jigsawpuzzlev1.data.DaoSession;

import org.greenrobot.greendao.database.Database;

public class MyApplication extends Application {
    private DaoSession daoSession;


    @Override
    public void onCreate() {
        super.onCreate();
        initDB();
    }

    private void initDB(){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "puzzle-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }
    public DaoSession getDaoSession() {
        return daoSession;
    }

}
