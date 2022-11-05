package com.example.balanin493_lab5_map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class MapView extends SurfaceView {

    Context ctx;

    int width;
    int height;

    //  int current_level_index = 0;

    Paint p;

    int scale = 0;
//нужно скачать вот это с сервера
    int[] levels = {16, 8, 4, 2, 1};
    int[] xtiles = {54, 108, 216, 432, 864};
    int[] ytiles = {27, 54, 108, 216, 432};

    int tilew = 100;
    int tileh = 100;

  //  var queue = [];

   Image[] tile = null;

    //boolean pending = true;

    float ofs_x = 0;
    float ofs_y = 0;

    float last_x = 0;
    float last_y = 0;

    boolean btn = false;


    public MapView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.ctx = context;
        this.p = new Paint();
        p.setColor(Color.BLACK);
        p.setStyle(Paint.Style.STROKE); //рисует только окантовку
        //В последнюю очередь
        scale = 0;
        setWillNotDraw(false); //enables onDraw (default - disabled)
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
       // if (!pending) return;

        width = canvas.getWidth();
        height = canvas.getHeight();
        canvas.drawColor(Color.rgb(255,255,255));

        int screen_x0 = 0;
        int screen_y0 = 0;
        int screen_x1 = canvas.getWidth()-1;
        int screen_y1 = canvas.getHeight() -1;

        int xt = xtiles[scale];
        int yt = ytiles[scale];
        int lv = levels[scale];

        for (int y = 0; y < yt; y++)
        {
            for (int x = 0; x < xt; x++)
            {
                //tile box
                //get tile position
                float x0 = x * tilew+ (int)(ofs_x);
                float y0 = y * tileh + (int)(ofs_y);
                float x1 = x0 + tilew;
                float y1 = y0 + tileh;

                if (!rect_rect(x0, y0, x1, y1, screen_x0, screen_y0, screen_x1, screen_y1)) continue;
                canvas.drawRect(x0,y0,x1,y1,p);
               // Image image = lookup(lv, x, y);
              //  if (image == null) query(lv, x, y);
               // else
                //    ctx.drawImage(image, x0, y0);
            }
        }

       // download();
       // pending = false;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    { //493 balanin
        float x = event.getX();
        float y = event.getY();
        int action = event.getAction();
        switch (action)
        {
            case MotionEvent.ACTION_MOVE:

                float dx = x - last_x;
                float dy = y - last_y;

                invalidate();

               // if (btn) //==true
               // {
                    ofs_x += x - last_x;
                    ofs_y += y - last_y;

                 //   pending = true;
               // }

                last_x = x;
                last_y = y;

                return true;

            case MotionEvent.ACTION_DOWN:
                last_x = event.getX();
                last_y = event.getY();
                //btn = true;
                return true;
            case MotionEvent.ACTION_UP:
                btn = false;
                return true;

        }
        return false;
       // return super.onTouchEvent(event);
    }

    //check if intersects
    public boolean rect_rect(double ax0,double ay0,double ax1,double ay1,double bx0,double by0,double bx1,double by1)
    {
        if (ax1 < bx0) return false;
        if (ax0 > bx1) return false;
        if (ay1 < by0) return false;
        if (ay0 > by1) return false;
        return true; //intersects anyway
    }
    public Image lookup(int level,float x,float y)
    {
        for (int i = 0; i < tile.length; i++)
        {
         //   Tile t = tile[i];
          //  if (t.level == level && t.x == x && t.y == y) return t.image;
        }

        return null;
    }

}
