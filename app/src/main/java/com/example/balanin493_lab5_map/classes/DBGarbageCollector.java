package com.example.balanin493_lab5_map.classes;

import android.app.Activity;

import com.example.balanin493_lab5_map.MapView;
import com.example.balanin493_lab5_map.R;

public class DBGarbageCollector {

    DB db;
    MapView mv;

    public  DBGarbageCollector(Activity ctx, DB db, MapView mv)
    {
        this.db = db;
        this.mv = mv;
    }


    public Runnable ClearTiles()
    {
        Runnable r =new Runnable() {
            @Override
            public void run() {
                db.clear_tiles();
            }
        };
        return r;

    }

}
