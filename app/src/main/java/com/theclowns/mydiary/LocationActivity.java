package com.theclowns.mydiary;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.BannerAdSize;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.banner.BannerView;
import com.huawei.hms.maps.CameraUpdate;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapsInitializer;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.SupportMapFragment;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.Marker;
import com.huawei.hms.maps.model.MarkerOptions;
import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.Coordinate;
import com.huawei.hms.site.api.model.DetailSearchRequest;
import com.huawei.hms.site.api.model.DetailSearchResponse;
import com.huawei.hms.site.api.model.QueryAutocompleteRequest;
import com.huawei.hms.site.api.model.QueryAutocompleteResponse;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;

import java.net.URLEncoder;
import java.util.Vector;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_WIFI_STATE;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback
{
    private HuaweiMap hMap;
    private SupportMapFragment mSupportMapFragment;
    private Marker mMarker;
    private SearchService searchService;
    private EditText editTextSearchKeyword;
    private LatLng currentLocation;
    private Vector<MarkerWithSite> markerWithSiteVector;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        MapsInitializer.setApiKey(getString(R.string.api_key));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        editTextSearchKeyword = findViewById(R.id.edit_text_search_keyword);

        markerWithSiteVector = new Vector<>();

        searchService = SearchServiceFactory.create(this, getApiKey());
        mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mSupportMapFragment != null) mSupportMapFragment.getMapAsync(this);
    }

    @RequiresPermission(allOf = {ACCESS_FINE_LOCATION, ACCESS_WIFI_STATE})
    @Override
    public void onMapReady(HuaweiMap map)
    {
        hMap = map;
        hMap.setMyLocationEnabled(true);
        hMap.setOnMyLocationButtonClickListener(new HuaweiMap.OnMyLocationButtonClickListener()
        {
            @Override
            public boolean onMyLocationButtonClick()
            {
                currentLocation = hMap.getCameraPosition().target;
                return false;
            }
        });
        hMap.getUiSettings().setMyLocationButtonEnabled(true);

        Intent intent = getIntent();
        int diaryId = intent.getIntExtra(DetailActivity.KEY_DIARY_ID, -1);

        if (diaryId != -1)
        {
            hMap.setOnInfoWindowClickListener(new HuaweiMap.OnInfoWindowClickListener()
            {
                @Override
                public void onInfoWindowClick(Marker marker)
                {
                    for (MarkerWithSite markerWithSite : markerWithSiteVector)
                    {
                        Marker siteMarker = markerWithSite.getMarker();
                        if (marker.getId().equals(siteMarker.getId()))
                        {
                            Site site = markerWithSite.getSite();

                            String locationId = site.getSiteId();

                            LocationDatabase locationDatabase = new LocationDatabase(LocationActivity.this);
                            if (!locationDatabase.isLocationExistsInDatabase(locationId))
                                locationDatabase.insertLocation(new Location(locationId, site.getName(), site.getLocation().getLat(), site.getLocation().getLng()));

                            DiaryDetailDatabase diaryDetailDatabase = new DiaryDetailDatabase(LocationActivity.this);
                            if(diaryDetailDatabase.isLocationInTheDiary(diaryId, locationId)) Toast.makeText(LocationActivity.this, "This location has been added to the diary.", Toast.LENGTH_LONG).show();
                            else
                            {
                                diaryDetailDatabase.insertDiaryDetail(new DiaryDetail(diaryId, locationId));
                                Toast.makeText(LocationActivity.this, "Location added.", Toast.LENGTH_LONG).show();
                                finish();
                            }
                            break;
                        }
                    }
                }
            });

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(-6.202, 106.7813), 10f);
            hMap.animateCamera(cameraUpdate);
        }
        else
        {
            editTextSearchKeyword.setVisibility(View.GONE);
            Button buttonSearch = findViewById(R.id.button_search);
            buttonSearch.setVisibility(View.GONE);

            DetailSearchRequest request = new DetailSearchRequest();
            request.setSiteId(intent.getStringExtra(DetailActivity.KEY_LOCATION_ID));
            request.setChildren(false);

            SearchResultListener<DetailSearchResponse> resultListener = new SearchResultListener<DetailSearchResponse>()
            {
                @Override
                public void onSearchResult(DetailSearchResponse result)
                {
                    Site site;
                    if (result == null || (site = result.getSite()) == null) return;

                    hMap.clear();
                    if (mMarker != null) mMarker.remove();
                    Coordinate coordinate = site.getLocation();
                    LatLng location = new LatLng(coordinate.getLat(), coordinate.getLng());
                    MarkerOptions options = new MarkerOptions()
                            .position(location)
                            .title(site.getName())
                            .snippet(site.getFormatAddress() + ".");
                    mMarker = hMap.addMarker(options);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 10f);
                    hMap.animateCamera(cameraUpdate);
                }

                @Override
                public void onSearchError(SearchStatus status)
                {
                    Toast.makeText(LocationActivity.this, "Error: " + status.getErrorCode() + " " + status.getErrorMessage(), Toast.LENGTH_LONG).show();
                }
            };
            searchService.detailSearch(request, resultListener);
        }

    }

    public void findAddressByKeyword(View view)
    {
        QueryAutocompleteRequest request = new QueryAutocompleteRequest();
        String query = editTextSearchKeyword.getText().toString();
        if (query.length() <= 0)
        {
            Toast.makeText(this, "Query cannot be empty.", Toast.LENGTH_LONG).show();
            return;
        }

        request.setQuery(query);
        if (currentLocation != null)
        {
            request.setLocation(new Coordinate(currentLocation.latitude, currentLocation.longitude));
            request.setRadius(10000);
        }
        searchService.queryAutocomplete(request, new SearchResultListener<QueryAutocompleteResponse>()
                {
                    @Override
                    public void onSearchResult(QueryAutocompleteResponse results)
                    {
                        if (results == null) return;
                        Site[] sites = results.getSites();
                        if (sites == null || sites.length == 0) return;

                        Toast.makeText(LocationActivity.this, "Search completed.", Toast.LENGTH_LONG).show();

                        double minDistance = Double.MAX_VALUE;
                        LatLng markerLocation = new LatLng(-6.202, 106.7813);

                        hMap.clear();

                        if (!markerWithSiteVector.isEmpty())
                        {
                            for (MarkerWithSite markerWithSite : markerWithSiteVector)
                            {
                                Marker marker = markerWithSite.getMarker();
                                if (marker != null) marker.remove();
                            }
                            markerWithSiteVector.clear();
                        }

                        for (Site site : sites)
                        {
                            MarkerWithSite markerWithSite = new MarkerWithSite(site);

                            Coordinate coordinate = site.getLocation();
                            LatLng location = new LatLng(coordinate.getLat(), coordinate.getLng());
                            if(site.getDistance() < minDistance)
                            {
                                minDistance = site.getDistance();
                                markerLocation= location;
                            }

                            MarkerOptions options = new MarkerOptions()
                                    .position(location)
                                    .title(site.getName())
                                    .snippet(site.getFormatAddress() + ". Click to add this location to the diary.");

                            markerWithSite.setMarker(hMap.addMarker(options));
                            markerWithSiteVector.add(markerWithSite);

                        }
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(markerLocation, 10f);
                        hMap.animateCamera(cameraUpdate);
                    }

                    @Override
                    public void onSearchError(SearchStatus status)
                    {
                        Toast.makeText(LocationActivity.this, "Error: " + status.getErrorCode() + " " + status.getErrorMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private String getApiKey()
    {
        String apiKey = getString(R.string.api_key);

        try
        {
            return URLEncoder.encode(apiKey, "utf-8");
        } catch (Exception e)
        {
            return null;
        }
    }
}