package com.example.balanin493_lab5_map.classes;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Request {

    public static String base = "http://tilemap.spbcoit.ru:7000";


    public void onSuccess(String res, Context ctx) throws Exception {

    }

    public void onFail(Activity ctx) {

    }

    //493 Balanin
    public void send(Activity ctx, String method, String request) {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    URL url = new URL(base + request);

                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod(method);


                    InputStream is = con.getInputStream();
                    BufferedInputStream inp = new BufferedInputStream(is);

                    byte[] buf = new byte[2048];
                    String str = "";

                    while (true) {
                        int len = inp.read(buf);
                        if (len < 0) break;

                        str += new String(buf, 0, len);
                    }
                    con.disconnect();

                    final String res = str;

                    ctx.runOnUiThread(() ->
                    {
                        try {
                            onSuccess(res, ctx);
                        } catch (Exception e) {
                            e.printStackTrace();
                            onFail(ctx);
                        }
                    });

                } catch (ProtocolException e) {
                    e.printStackTrace();
                    onFail(ctx);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    onFail(ctx);
                } catch (IOException e) {
                    e.printStackTrace();
                    onFail(ctx);
                }

                /*
                catch (Exception ex)
               {
                   ctx.runOnUiThread(() ->
                   {
                       onFail(ctx);
                   });
               }
*/

            }
        };
        Thread t = new Thread(r);
        t.start();
    }
}