package com.theclowns.mydiary;

public class Location
{
    private String locationId;
    private String locationName;
    private double locationLatitude;
    private double locationLongitude;

    public Location(String locationId, String locationName, double locationLatitude, double locationLongitude)
    {
        this.locationId = locationId;
        this.locationName = locationName;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
    }

    public String getLocationId()
    {
        return locationId;
    }

    public void setLocationId(String locationId)
    {
        this.locationId = locationId;
    }

    public String getLocationName()
    {
        return locationName;
    }

    public void setLocationName(String locationName)
    {
        this.locationName = locationName;
    }

    public double getLocationLatitude()
    {
        return locationLatitude;
    }

    public void setLocationLatitude(double locationLatitude)
    {
        this.locationLatitude = locationLatitude;
    }

    public double getLocationLongitude()
    {
        return locationLongitude;
    }

    public void setLocationLongitude(double locationLongitude)
    {
        this.locationLongitude = locationLongitude;
    }
}
