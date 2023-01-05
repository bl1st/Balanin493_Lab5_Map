package com.example.balanin493_lab5_map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balanin493_lab5_map.classes.Request;
import com.example.balanin493_lab5_map.classes.DB;
import com.example.balanin493_lab5_map.model.ShapeStorage;

import org.json.JSONArray;
import org.json.JSONObject;

public class Activity_Map extends AppCompatActivity {

    MapView mv;
    DB db;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem_settings:
                startActivityForResult(new Intent(this,Activity_Settings.class),100);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //493 Balanin
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try
        {   //100 = Settings Activity
            if (requestCode == 100)
            {
                if (resultCode == RESULT_OK)
                {
                    Request.base = db.get_baseurl();
                    mv.shapeStates = db.get_shapes();
                    mv.setPaintColors(db.get_shapeColor());

                    mv.invalidate();

                }
            }
        }
        catch (Exception ex)
        {
            Log.e("TEST","SOMETHING WENT WRONG ON ONACTIVITYRESULT");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mv = findViewById(R.id.mapView);

        db = new DB(this, getResources().getString(R.string.dbName), null,1);

        //Request.base = db.get_baseurl();


    }

    public void ZoomOut(View v)
    {
        if (mv.scale >= mv.levels.length - 1)
        {
            Toast.makeText(this, "Предел приближения",Toast.LENGTH_SHORT).show();
            return;
        }
        mv.tiles.clear();
        mv.shapes.ClearStorage();

        mv.scale++;

        mv.ofs_x *= 2.0f;
        mv.ofs_y *= 2.0f;

        mv.ofs_x -= mv.width / 2.0f;
        mv.ofs_y -= mv.height / 2.0f;


        mv.invalidate();

    }
    public void ZoomIn(View v)
    {
        if (mv.scale == 0)
        {
            Toast.makeText(this, "Предел отдаления",Toast.LENGTH_SHORT).show();
            return;
        }
        mv.tiles.clear();
        mv.shapes.ClearStorage();

        mv.scale--;

        mv.ofs_x += mv.width / 2.0f;
        mv.ofs_y += mv.height / 2.0f;

        mv.ofs_x /= 2.0f;
        mv.ofs_y /= 2.0f;

        mv.invalidate();

    }
}