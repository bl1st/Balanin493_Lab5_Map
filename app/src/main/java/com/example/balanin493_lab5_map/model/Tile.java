package com.example.balanin493_lab5_map.model;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Base64;
import android.widget.Toast;

import com.example.balanin493_lab5_map.MapView;
import com.example.balanin493_lab5_map.classes.DB;
import com.example.balanin493_lab5_map.classes.Request;

import org.json.JSONObject;

public class Tile {
    public Bitmap bmp = null;
    public int scale;
    public int x;
    public int y;
    double lat, lon;



    public Tile(int x, int y, int scale, Activity ctx, MapView mv, DB db)
    {
        //find tile in db, if not exists, load then put in db
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.bmp = null;


        TileDataContainer container = db.get_TileInfo(scale, x, y);
        if (container != null)
        {
           container.FillTileFromContainer(this);
        }
        else {
            Request getTile = new Request() {
                @Override
                public void onSuccess(String res, Context ctx) throws Exception {
                    JSONObject obj = new JSONObject(res);
                    String b64 = obj.getString("data");
                    lat = obj.getDouble("lat");
                    lon = obj.getDouble("lon");

                   db.put_tile(x, y, lat, lon, scale, b64);

                    byte[] jpeg = Base64.decode(b64, Base64.DEFAULT);

                    bmp = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
                    //mv.invalidate();
                }
            };

            getTile.send(ctx, "GET", "/raster/" + scale + "/" + x + "-" + y);
        }


    }



}
