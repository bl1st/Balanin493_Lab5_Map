package com.example.balanin493_lab5_map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.widget.ArrayAdapter;

import com.example.balanin493_lab5_map.classes.DB;
import com.example.balanin493_lab5_map.classes.DBGarbageCollector;
import com.example.balanin493_lab5_map.classes.Request;
import com.example.balanin493_lab5_map.model.Position;
import com.example.balanin493_lab5_map.model.Shape;
import com.example.balanin493_lab5_map.model.ShapeColors;
import com.example.balanin493_lab5_map.model.ShapeState;
import com.example.balanin493_lab5_map.model.ShapeStorage;
import com.example.balanin493_lab5_map.model.Tile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MapView extends SurfaceView {

    public DBGarbageCollector gc;
    public Activity ctx;

    int width;
    int height;
    DB db;

    ArrayList<Tile> tiles = new ArrayList<Tile>();

    //Arrays for rivers, roads, etc
    public ShapeStorage shapes;
    //Booleans for drawing/not rivers, roads, etc
    public ShapeState shapeStates;


    Paint p;
    Paint paint_Coastline;
    Paint paint_River;
    Paint paint_Railroad;
    Paint paint_Road;

    int scale = 0;

    int[] levels;
    int[] xtiles;
    int[] ytiles;
    float[] resolutions;

    int tilew = 100;
    int tileh = 100;

    float ofs_x;
    float ofs_y;

    float last_x = 0;
    float last_y = 0;

    float lat0, lon0;
    float lat1, lon1;


    public void update_viewport()
    {
        lat0 = -ofs_x * resolutions[scale] - 180.0f;
        lon0 = 90.0f + ofs_y * resolutions[scale];
        lat1 = lat0 + width * resolutions[scale];
        lon1 = lon0 - height * resolutions[scale];
    }

    public Tile getTile(int x, int y, int scale)
    {
        for (int i = 0; i < tiles.size(); i++)
        {
            Tile t = tiles.get(i);
            if (t.x == x && t.y == y && t.scale == scale)
                return t;

        }

        Tile nt = new Tile(x, y, scale, ctx, this, db);
        tiles.add(nt);

        return nt;
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(true);
        this.ctx = (Activity) context;
        this.p = new Paint();
        p.setStyle(Paint.Style.STROKE); //рисует только окантовку
        p.setStrokeWidth(2);

        this.db = new DB(ctx, getResources().getString(R.string.dbName), null, 1);

        //Storage for coastlines, rivers, etc
        shapes = new ShapeStorage();
        //Shapes states | if true -> draw shape
        shapeStates = db.get_shapes();

        ShapeColors colors = db.get_shapeColor();

        paint_Coastline = new Paint();
        paint_Coastline.setColor(colors.coastline);
        paint_Coastline.setStyle(Paint.Style.STROKE);
        paint_Coastline.setStrokeWidth(2);

        paint_River = new Paint();
        paint_River.setColor(colors.river);
        paint_River.setStyle(Paint.Style.STROKE);
        paint_River.setStrokeWidth(2);

        paint_Railroad = new Paint();
        paint_Railroad.setColor(colors.railroad);
        paint_Railroad.setStyle(Paint.Style.STROKE);
        paint_Railroad.setStrokeWidth(2);

        paint_Road = new Paint();
        paint_Road.setColor(colors.road);
        paint_Road.setStyle(Paint.Style.STROKE);
        paint_Road.setStrokeWidth(2);

        //Filling xtiles, ytiles, levels, resolutions arrays
        Request r = new Request() {
            public void onSuccess(String res, Context ctx) throws Exception {
                JSONArray arr = new JSONArray(res);

                levels = new int[arr.length()];
                xtiles = new int[arr.length()];
                ytiles = new int[arr.length()];
                resolutions = new float[arr.length()];

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    levels[i] = obj.getInt("level");
                    xtiles[i] = obj.getInt("xtiles");
                    ytiles[i] = obj.getInt("ytiles");
                    resolutions[i] = (float) obj.getDouble("resolution");
                }

                //get last position on map from db
                set_laspos(db.get_lastpos());
                //Initialize task - dbClearer
                setDBClearer();
                //Draw picture
                setWillNotDraw(false);
                invalidate();

            }
        };
        r.send(ctx, "GET", "/raster");

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //canvas.getWidth and height
        width = getWidth();
        height = getHeight();
        canvas.drawColor(Color.rgb(255, 255, 255));

        int screen_x0 = 0;
        int screen_y0 = 0;
        int screen_x1 = width - 1;
        int screen_y1 = height - 1;

        int xt = xtiles[scale];
        int yt = ytiles[scale];
        int lv = levels[scale];

        for (int y = 0; y < yt; y++) {
            for (int x = 0; x < xt; x++) {
                //tile box
                //get tile position
                float x0 = x * tilew + (int) (ofs_x);
                float y0 = y * tileh + (int) (ofs_y);
                float x1 = x0 + tilew;
                float y1 = y0 + tileh;

                if (!rect_rect(x0, y0, x1, y1, screen_x0, screen_y0, screen_x1, screen_y1))
                    continue;

                Tile t = getTile(x, y, lv);

                if (t.bmp != null)
                    canvas.drawBitmap(t.bmp, x0, y0, p);
            }
        }

        invalidate();

        if (shapeStates.coastlines)
            DrawShape(canvas, shapes.coastline, paint_Coastline);

        if (shapeStates.rivers)
            DrawShape(canvas, shapes.river, paint_River);

        if (shapeStates.railroads)
            DrawShape(canvas, shapes.railroad, paint_Railroad);

        if (shapeStates.roads)
            DrawShape(canvas, shapes.road, paint_Road);

        // DrawShapes(canvas);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) { //493 balanin
        float x = event.getX();
        float y = event.getY();

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_MOVE:

                ofs_x += x - last_x;
                ofs_y += y - last_y;

                invalidate();

                last_x = x;
                last_y = y;
                return true;

            case MotionEvent.ACTION_DOWN:
                shapes.ClearStorage();

                invalidate();

                last_x = event.getX();
                last_y = event.getY();

                return true;

            case MotionEvent.ACTION_UP:
                update_viewport();
                shapes.ClearStorage();

                db.set_lastpos(scale, ofs_x, ofs_y);


                if (shapeStates.coastlines)
                    LoadShape(shapes.coastline, "coastline");

                if (shapeStates.rivers)
                    LoadShape(shapes.river, "river");

                if (shapeStates.railroads)
                    LoadShape(shapes.railroad, "railroad");

                if (shapeStates.roads)
                    LoadShape(shapes.road, "road");

                invalidate();

                return true;
        }

        return false;
        // return super.onTouchEvent(event);
    }

    //check if intersects
    public boolean rect_rect(double ax0, double ay0, double ax1, double ay1, double bx0, double by0, double bx1, double by1) {
        if (ax1 < bx0) return false;
        if (ax0 > bx1) return false;
        if (ay1 < by0) return false;
        if (ay0 > by1) return false;
        return true; //intersects anyway
    }




    public void set_laspos(Position pos) {
        this.scale = pos.level;
        this.ofs_x = pos.offsetX;
        this.ofs_y = pos.offsetY;
    }

    public void setDBClearer() {
        gc = new DBGarbageCollector(ctx, this.db, this);
        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
        exec.scheduleAtFixedRate(gc.ClearTiles(), 0, 1, TimeUnit.MINUTES);
    }

    public float map(double x, double x0, double x1, float a, float b) {
        double t = (x - x0) / (x1 - x0);
        return (float) (a + (b - a) * t);
    }


    public void DrawShape(Canvas canvas, ArrayList<ArrayList<Shape>> shapes, Paint p) {
        for (int i = 0; i < shapes.size(); i++) {
            ArrayList<Shape> shape = shapes.get(i);

            float px0 = map(shape.get(0).x, lat0, lat1, 0, width);
            float py0 = map(shape.get(0).y, lon0, lon1, 0, height);

            for (int pi = 1; pi < shape.size(); pi++) {
                float px1 = map(shape.get(pi).x, lat0, lat1, 0, width);
                float py1 = map(shape.get(pi).y, lon0, lon1, 0, height);

                canvas.drawLine(px0, py0, px1, py1, p);

                px0 = px1;
                py0 = py1;
            }
        }

        invalidate();
    }







    public void LoadShape(ArrayList<ArrayList<Shape>> fillShape, String shapeName)
    {
        Request r = new Request()
        {
            public void onSuccess(String res, Context ctx) throws Exception
            {
                JSONArray array = new JSONArray(res);
                for (int i = 0; i < array.length(); i++) {
                    JSONArray array2 = array.getJSONArray(i);
                    ArrayList<Shape> list = new ArrayList<Shape>();

                    for (int j = 0; j < array2.length(); j++) {

                        JSONObject obj = array2.getJSONObject(j);
                        Shape s = new Shape();
                        s.x = obj.getDouble("x");
                        s.y = obj.getDouble("y");
                        list.add(s);
                    }
                    fillShape.add(list);
                }
            }
        };
        r.send((Activity) ctx, "GET", "/"+shapeName +"/"+ levels[scale] + "?lat0=" + lat0 + "&lon0=" + lon0 + "&lat1=" + lat1 + "&lon1=" + lon1);

    }



    public void setPaintColors(ShapeColors colors)
    {
        paint_Coastline.setColor(colors.coastline);
        paint_River.setColor(colors.river);
        paint_Railroad.setColor(colors.railroad);
        paint_Road.setColor(colors.road);
    }

}
