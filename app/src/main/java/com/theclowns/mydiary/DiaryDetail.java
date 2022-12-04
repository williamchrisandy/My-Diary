package com.theclowns.mydiary;

public class DiaryDetail
{
    private int diaryId;
    private String locationId;

    public DiaryDetail(int diaryId, String locationId)
    {
        this.diaryId = diaryId;
        this.locationId = locationId;
    }

    public int getDiaryId()
    {
        return diaryId;
    }

    public void setDiaryId(int diaryId)
    {
        this.diaryId = diaryId;
    }

    public String getLocationId()
    {
        return locationId;
    }

    public void setLocationId(String locationId)
    {
        this.locationId = locationId;
    }
}
