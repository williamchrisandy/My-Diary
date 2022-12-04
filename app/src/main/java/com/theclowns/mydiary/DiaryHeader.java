package com.theclowns.mydiary;

public class DiaryHeader
{
    private int diaryId;
    private String diaryTitle;
    private String diaryDate;
    private String diaryDescription;

    public DiaryHeader(int diaryId, String diaryTitle, String diaryDate, String diaryDescription)
    {
        this.diaryId = diaryId;
        this.diaryTitle = diaryTitle;
        this.diaryDate = diaryDate;
        this.diaryDescription = diaryDescription;
    }

    public int getDiaryId()
    {
        return diaryId;
    }

    public void setDiaryId(int diaryId)
    {
        this.diaryId = diaryId;
    }

    public String getDiaryTitle()
    {
        return diaryTitle;
    }

    public void setDiaryTitle(String diaryTitle)
    {
        this.diaryTitle = diaryTitle;
    }

    public String getDiaryDate()
    {
        return diaryDate;
    }

    public void setDiaryDate(String diaryDate)
    {
        this.diaryDate = diaryDate;
    }

    public String getDiaryDescription()
    {
        return diaryDescription;
    }

    public void setDiaryDescription(String diaryDescription)
    {
        this.diaryDescription = diaryDescription;
    }
}
