package de.dihco.android.stechuhr;

/**
 * Created by Martin on 08.02.2015.
 * Set of information about a working timespan, most likely gathered from a cursor
 */
public class TimeOverView {
    public long startZeit = 0;
    public long endZeit = 0;
    public long arbeitsZeit = 0;
    public long pausenZeit = 0;
    public long überStunden = 0;
    public long forcedPauseTime = 0;
    public String pauseTimesString = "";
    public int daysWorked = 0;
}
