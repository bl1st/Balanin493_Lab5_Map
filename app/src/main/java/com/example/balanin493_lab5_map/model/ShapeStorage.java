package com.example.balanin493_lab5_map.model;

import java.util.ArrayList;

public class ShapeStorage {
    public ArrayList<ArrayList<Shape>> river = new ArrayList<ArrayList<Shape>>();
    public ArrayList<ArrayList<Shape>> coastline = new ArrayList<ArrayList<Shape>>();
    public ArrayList<ArrayList<Shape>> road = new ArrayList<ArrayList<Shape>>();
    public ArrayList<ArrayList<Shape>> railroad = new ArrayList<ArrayList<Shape>>();

    public void ClearStorage()
    {
        river.clear();
        coastline.clear();
        road.clear();
        railroad.clear();
    }
}
