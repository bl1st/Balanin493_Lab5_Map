package com.example.balanin493_lab5_map.model;

public class ExpirationDate {
    public int Hours;
    public int Minutes;
    public int Days;


    public ExpirationDate(int days, int hours, int minutes)
    {
        this.Days = days;
        this.Hours = hours;
        this.Minutes = minutes;
    }
}
