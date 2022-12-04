package com.theclowns.mydiary;

import com.huawei.hms.maps.model.Marker;
import com.huawei.hms.site.api.model.Site;

public class MarkerWithSite
{
    private Marker marker;
    private Site site;

    public MarkerWithSite(Site site)
    {
        this.site = site;
    }

    public Marker getMarker()
    {
        return marker;
    }

    public void setMarker(Marker marker)
    {
        this.marker = marker;
    }

    public Site getSite()
    {
        return site;
    }

    public void setSite(Site site)
    {
        this.site = site;
    }
}
