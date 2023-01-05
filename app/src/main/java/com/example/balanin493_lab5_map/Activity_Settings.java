package com.example.balanin493_lab5_map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.balanin493_lab5_map.classes.DB;
import com.example.balanin493_lab5_map.model.ExpirationDate;
import com.example.balanin493_lab5_map.model.ShapeColors;
import com.example.balanin493_lab5_map.model.ShapeState;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.jaredrummler.android.colorpicker.ColorShape;

public class Activity_Settings extends AppCompatActivity implements ColorPickerDialogListener {

   EditText etUrl, etDays, etHours, etMinutes;
   CheckBox railroads,rivers,roads,coastlines;
   Button btn_coastlines, btn_rivers, btn_railroads, btn_roads;

    DB db;
    ShapeColors shapeColors;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.setTitle("Настройки");

        etUrl = findViewById(R.id.etBaseURL);
        etDays = findViewById(R.id.etDays);
        etMinutes = findViewById(R.id.etMinutes);
        etHours = findViewById(R.id.etHours);

        railroads = findViewById(R.id.checkRailroads);
        rivers = findViewById(R.id.checkRivers);
        roads = findViewById(R.id.checkCarRoads);
        coastlines = findViewById(R.id.checkCoastLines);

        btn_coastlines = findViewById(R.id.btn_coastlines);
        btn_railroads = findViewById(R.id.btn_railroads);
        btn_rivers = findViewById(R.id.btn_rivers);
        btn_roads = findViewById(R.id.btn_roads);



        db = new DB(this, getResources().getString(R.string.dbName), null,1);
        String baseurl = db.get_baseurl();
        shapeColors = db.get_shapeColor();

        btn_coastlines.setBackgroundColor(shapeColors.coastline);
        btn_rivers.setBackgroundColor(shapeColors.river);
        btn_railroads.setBackgroundColor(shapeColors.railroad);
        btn_roads.setBackgroundColor(shapeColors.road);


        etUrl.setText(baseurl);

        ShapeState state = db.get_shapes();
        railroads.setChecked(state.railroads);
        coastlines.setChecked(state.coastlines);
        roads.setChecked(state.roads);
        rivers.setChecked(state.rivers);

        ExpirationDate et = db.expDate;
        etDays.setText(String.valueOf(et.Days));
        etMinutes.setText(String.valueOf(et.Minutes));
        etHours.setText(String.valueOf(et.Hours));


    }


    public void onButtonSave_Click(View v)
    {
        db.set_baseurl(etUrl.getText().toString());
        db.set_shapes(rivers.isChecked(), coastlines.isChecked(), roads.isChecked(), railroads.isChecked());

        int days = Integer.parseInt(etDays.getText().toString());
        int hours = Integer.parseInt(etHours.getText().toString());
        int minutes = Integer.parseInt(etMinutes.getText().toString());

        db.set_expirationDate(days,hours,minutes);

        db.set_shapeColors(shapeColors);




        db.close();

        setResult(RESULT_OK);
        finish();
    }

    public void onButtonCancel_Click(View v)
    {
        db.close();
        setResult(RESULT_CANCELED);
        finish();
    }



    public void onButtonColors_Click(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_coastlines:
                createColorPickerDialog(0);
                break;
            case R.id.btn_rivers:
                createColorPickerDialog(1);
                break;
            case R.id.btn_railroads:
                createColorPickerDialog(2);
                break;
            case R.id.btn_roads:
                createColorPickerDialog(3);

                break;
        }

    }

    public void createColorPickerDialog(int id)
    {
        ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowCustom(true)
                .setAllowPresets(true)
                .setColorShape(ColorShape.CIRCLE)
                .setDialogId(id)
                .show(this);
    }

    @Override
    public void onColorSelected(int dialogId, int color)
    {
        switch (dialogId)
        {
            case 0:
                btn_coastlines.setBackgroundColor(color);
                shapeColors.coastline = color;
                break;
            case 1:
                btn_rivers.setBackgroundColor(color);
                shapeColors.river = color;
                break;
            case 2:
                btn_railroads.setBackgroundColor(color);
                shapeColors.railroad = color;
                break;
            case 3:
                btn_roads.setBackgroundColor(color);
                shapeColors.road = color;
                break;
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {
        Toast.makeText(this, "Dialog dismissed", Toast.LENGTH_SHORT).show();
    }


}