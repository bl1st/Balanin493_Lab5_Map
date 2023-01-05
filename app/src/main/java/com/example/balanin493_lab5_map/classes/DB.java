package com.example.balanin493_lab5_map.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

import com.example.balanin493_lab5_map.R;
import com.example.balanin493_lab5_map.model.ExpirationDate;
import com.example.balanin493_lab5_map.model.Position;
import com.example.balanin493_lab5_map.model.Shape;
import com.example.balanin493_lab5_map.model.ShapeColors;
import com.example.balanin493_lab5_map.model.ShapeState;
import com.example.balanin493_lab5_map.model.Tile;
import com.example.balanin493_lab5_map.model.TileDataContainer;
import com.google.rpc.context.AttributeContext;
import com.google.type.DateTime;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class DB extends SQLiteOpenHelper {
    public DB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        expDate = get_expirationDate();
    }
    public ExpirationDate expDate;

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE baseurl (id INTEGER PRIMARY KEY AUTOINCREMENT, baseUrl text not null);";
        db.execSQL(sql);
        sql = "Insert into baseurl (baseUrl) values ('http://tilemap.spbcoit.ru:7000');";
        db.execSQL(sql);

        sql = "CREATE TABLE lastpos (id INTEGER PRIMARY KEY AUTOINCREMENT, level INTEGER not null, offsetX real not null, offsetY real not null);";
        db.execSQL(sql);
        //level field contains POSITION in levels array that i load from the server
        sql = "Insert into lastpos (level, offsetX, offsetY) values (1,0,0);";
        db.execSQL(sql);

        sql = "CREATE TABLE shapes (id INTEGER PRIMARY KEY AUTOINCREMENT, rivers INTEGER not null, coastlines real not null, railroads integer not null, roads integer not null);";
        db.execSQL(sql);
        sql = "Insert into shapes (rivers, railroads, roads,coastlines) values (0,0,0,0);";
        db.execSQL(sql);

        sql = "CREATE TABLE tiles (id INTEGER PRIMARY KEY AUTOINCREMENT, scale INTEGER NOT NULL, x INTEGER NOT NULL, y INTEGER NOT NULL, image TEXT NOT NULL, lat REAL NOT NULL, lon REAL NOT NULL, expirationDate INTEGER NOT NULL)";
        db.execSQL(sql);

        sql = "CREATE TABLE expirationDate (id INTEGER PRIMARY KEY AUTOINCREMENT, day INTEGER NOT NULL, hour INTEGER NOT NULL, minute INTEGER NOT NULL)";
        db.execSQL(sql);

        sql = "INSERT INTO expirationDate (day,hour,minute) VALUES (0,0,5)";
        db.execSQL(sql);

        sql = "CREATE TABLE ShapeColors (id INTEGER PRIMARY KEY AUTOINCREMENT, coastline INTEGER NOT NULL, river INTEGER NOT NULL, railroad INTEGER NOT NULL, road INTEGER NOT NULL)";
        db.execSQL(sql);

        sql = "INSERT INTO ShapeColors (coastline,river,railroad,road) VALUES (0,0,0,0)";
        db.execSQL(sql);

    }

    public ShapeColors get_shapeColor()
    {
        ShapeColors colors = new ShapeColors();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM ShapeColors;";
        Cursor cur = db.rawQuery(sql,null);
        if (cur.moveToFirst())
        {

            colors.coastline = cur.getInt(1);
            colors.river = cur.getInt(2);
            colors.railroad = cur.getInt(3);
            colors.road = cur.getInt(4);

            return colors;
        }
        return null;

    }


    public void set_shapeColors(ShapeColors colors)
    {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "Update ShapeColors set coastline= "+ colors.coastline+", river= "+ colors.river +", railroad= "+ colors.railroad + ", road = " + colors.road + " WHERE id = 1;";
        db.execSQL(sql);

    }


    public ExpirationDate get_expirationDate()
    {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM expirationDate;";
        Cursor cur = db.rawQuery(sql,null);

        if (cur.moveToFirst())
        {
            int days = cur.getInt(1);
            int hours = cur.getInt(2);
            int minutes = cur.getInt(3);

            ExpirationDate date = new ExpirationDate(days, hours, minutes);
            this.expDate = date;

            return date;
        }
        return null;
    }
    public void set_expirationDate(int days, int hours, int minutes)
    {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "Update expirationDate set day= "+ days+", hour= "+ hours +", minute= "+ minutes+" Where id=1;";
        db.execSQL(sql);

        this.expDate = new ExpirationDate(days,hours,minutes);

    }



    public void put_tile(int x, int y, double lat, double lon, int scale, String imgbase64)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Date currentDate = new Date();

                Calendar c = Calendar.getInstance();
                c.setTime(currentDate);

                c.add(Calendar.DATE, expDate.Days); //same with c.add(Calendar.DAY_OF_MONTH, 1);
                c.add(Calendar.HOUR, expDate.Hours);
                c.add(Calendar.MINUTE, expDate.Minutes);

                Date expirationDate = c.getTime();
                Timestamp ts = new Timestamp(expirationDate.getTime());
                String expTime = ts.toString();


                ContentValues cv = new ContentValues();
                cv.put("scale",scale);
                cv.put("x",x);
                cv.put("y",y);
                cv.put("image",imgbase64);
                cv.put("lat",lat);
                cv.put("lon",lon);
                cv.put("expirationDate",expTime);

                SQLiteDatabase db = getWritableDatabase();
                db.insert("tiles",null,cv);
            }
        });

       t.start();

      //  String sql = "INSERT INTO tiles (scale,x,y,image,lat,lon, expirationDate) VALUES ("+ scale+",  "+ x+", "+ y+", '"+ imgbase64+"', "+ lat+", "+ lon+", '"+ expTime+"');";
       // db.execSQL(sql);


    }

    public TileDataContainer get_TileInfo(int scale, int x, int y)
    {
        SQLiteDatabase db =getReadableDatabase();
        String sql = "SELECT image,lat,lon FROM tiles WHERE scale="+scale +" AND x="+x+" AND y=" + y+";";
        Cursor cur = db.rawQuery(sql, null);
        if (cur.moveToFirst())
        {
            String bmp = cur.getString(0);
            double lat = cur.getDouble(1);
            double lon = cur.getFloat(2);

            TileDataContainer container = new TileDataContainer(x,y,scale,lat,lon,bmp);
            return container;
        }
        return null;
    }


    public void clear_tiles()
    {
        Date currentDate = new Date();
        Timestamp ts = new Timestamp(currentDate.getTime());
        String string_ts = ts.toString();

        SQLiteDatabase db = getWritableDatabase();
        String sql = "DELETE FROM tiles WHERE expirationDate<=" + string_ts+ ";";
        db.execSQL(sql);

    }


    public ShapeState get_shapes()
    {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM shapes;";
        Cursor cur = db.rawQuery(sql,null);

        if (cur.moveToFirst())
        {
            ShapeState state = new ShapeState();
            if (cur.getInt(1) == 1) state.rivers = true;
            else state.rivers = false;

            if (cur.getInt(2) == 1) state.coastlines = true;
            else state.coastlines = false;

            if (cur.getInt(3) == 1) state.railroads = true;
            else state.railroads = false;

            if (cur.getInt(4) == 1) state.roads = true;
            else state.roads = false;
            return state;

        }
        return null;
    }

    public void set_shapes(boolean rivers, boolean coastlines, boolean roads, boolean railroads)
    {
        SQLiteDatabase db = getWritableDatabase();

        int riv = rivers == true ? 1 : 0;
        int coast = coastlines == true ? 1 : 0;
        int road = roads == true ? 1 : 0;
        int rail = railroads == true ? 1 : 0;

        String sql = "Update shapes set rivers= "+ riv +", coastlines= "+ coast +", railroads= "+ rail+", roads="+road+" Where id=1;";
        db.execSQL(sql);

    }



    public String get_baseurl()
    {
        String baseurl ="";

        SQLiteDatabase db =getReadableDatabase();
        String sql = "SELECT * FROM baseurl;";
        Cursor cur = db.rawQuery(sql,null);

        if (cur.moveToFirst())
        {
            baseurl = cur.getString(1);
            return baseurl;
        }

        return null;
    }

    public void set_baseurl(String baseurl)
    {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "Update baseurl Set baseUrl = '" + baseurl + "' Where id=1;";
        db.execSQL(sql);
    }



    public Position get_lastpos()
    {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM lastpos;";
        Cursor cur = db.rawQuery(sql,null);

        if (cur.moveToFirst())
        {
            Position pos = new Position();
            pos.level = cur.getInt(1);
            pos.offsetX = cur.getFloat(2);
            pos.offsetY = cur.getFloat(3);
            return pos;

        }
        return null;
    }

    public void set_lastpos(int level, float offsetX, float offsetY)
    {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "Update lastpos Set level = " + level + " , offsetX=" + offsetX + " , offsetY=" + offsetY + " Where id=1;";
        db.execSQL(sql);

    }



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
