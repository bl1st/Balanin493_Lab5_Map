package com.example.balanin493_lab5_map;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class Activity_Map extends AppCompatActivity {

    MapView mv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mv = findViewById(R.id.mapView);

        mv.invalidate();
    }


    public void ZoomOut(View v)
    {
        if (mv.scale == 0) return;
        mv.scale --;

        mv.ofs_x += mv.width  / 2.0f;
        mv.ofs_x += mv.height / 2.0f;

        mv.ofs_x /=2.0f;
        mv.ofs_y /=2.0f;

        mv.invalidate();

    }
    public void ZoomIn(View v)
    {
        if (mv.scale > mv.levels.length - 1) return;
        mv.scale ++;

        mv.ofs_x *=2;
        mv.ofs_y *=2;

        mv.ofs_x += mv.width / 2.0f;
        mv.ofs_x += mv.height / 2.0f;

        mv.invalidate();

    }
}