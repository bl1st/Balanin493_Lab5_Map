package com.example.balanin493_lab5_map.model;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.balanin493_lab5_map.MapView;
import com.example.balanin493_lab5_map.classes.DB;

public class TileDataContainer
{
    public Bitmap bmp = null;
    public int scale;
    public int x;
    public int y;
    double lat, lon;

    public TileDataContainer(int x, int y, int scale, double lat, double lon, String img) {
        //find tile in db, if not exists, load then put in db
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.bmp = null;
        this.lat = lat;
        this.lon = lon;

        byte[] jpeg = Base64.decode(img, Base64.DEFAULT);
        bmp = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
    }

    public void FillTileFromContainer(Tile t)
    {
        t.x = this.x;
        t.y =this.y;
        t.lat = this.lat;
        t.lon = this.lon;
        t.scale = this.scale;
        t.bmp = this.bmp;

    }
}
