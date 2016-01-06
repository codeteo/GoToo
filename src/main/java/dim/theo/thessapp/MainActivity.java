package dim.theo.thessapp;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import java.util.ArrayList;

import dim.theo.thessapp.helpers.Helper;
import dim.theo.thessapp.model.MarkerItem;
import dim.theo.thessapp.presenter.MainPresenterImpl;

import static dim.theo.thessapp.helpers.Helper.distanceFrom;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraChangeListener {

    private Helper helper;
    private GoogleMap mMap;
    public static final String TAG = "MAINACTIVITY";

    private TypedArray markerIcons;
    private String[] names;
    private String[] texts;

    private LatLng mapCenter;

    private ArrayList<LatLng> latLngArrayList = new ArrayList<LatLng>() {{
        add(new LatLng(40.632205, 22.951809));
        add(new LatLng(40.6307, 22.9487));
        add(new LatLng(40.638, 22.946));
        add(new LatLng(40.6333, 22.9459));
        add(new LatLng(40.640026, 22.953464));
        add(new LatLng(40.634974, 22.947864));
        add(new LatLng(40.64279, 22.937489));
        add(new LatLng(40.641792, 22.9523));
        add(new LatLng(40.636808, 22.943736));
        add(new LatLng(40.643213, 22.944237));
        add(new LatLng(40.633333, 22.95));
        add(new LatLng(40.6331, 22.9512));
        add(new LatLng(40.640883, 22.948543));
        add(new LatLng(40.638849, 22.947649));
        add(new LatLng(40.632844, 22.947094));
        add(new LatLng(40.642677, 22.954641));
        add(new LatLng(40.639167, 22.949722));
        add(new LatLng(40.6357, 22.9453));
        add(new LatLng(40.6421, 22.9461));
        add(new LatLng(40.640848, 22.944807));
        add(new LatLng(40.615556, 22.956667));
        add(new LatLng(40.626369, 22.948428));
        add(new LatLng(40.626897, 22.957219));
        add(new LatLng(40.945833, 22.455));
        add(new LatLng(40.635, 22.937));
    }};

    public ArrayList<Marker> markerArrayList = new ArrayList<>(latLngArrayList.size());
    private ArrayList<MarkerItem> markerItemArrayList = new ArrayList<>(latLngArrayList.size());

    private static final LatLng SKG_VIEW = new LatLng(40.6402778, 22.9438889);

    private MainPresenterImpl mainPresenter;

    double visibleWidth;
    double radius1, radius2, radius3, radius4, radius5;

    private BottomSheetLayout bottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomSheet = (BottomSheetLayout) findViewById(R.id.bottomsheet);

        mainPresenter = new MainPresenterImpl();
        helper = new Helper(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        markerIcons = getResources().obtainTypedArray(R.array.array_marker_icons);
        names = getResources().getStringArray(R.array.array_markeritems_names);
        texts = getResources().getStringArray(R.array.array_markeritems_texts);

        populateMarkerItemsArrayList();
    }

    public void populateMarkerItemsArrayList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MarkerItem markerItem = new MarkerItem();
                mainPresenter.populateMarkerItemsArrayList(markerItem, latLngArrayList, names, markerIcons, markerItemArrayList);
            }
        }).start();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnCameraChangeListener(this);

        mapCenter = mMap.getCameraPosition().target;

        addMarkers();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        int i;
        for (i = 0; i < markerArrayList.size(); i++) {
            if (markerArrayList.get(i).getPosition().equals(marker.getPosition())) {
                break;
            }
        }

        bottomSheet.showWithSheetView(LayoutInflater.from(this).inflate(R.layout.bottom_sheet, bottomSheet, false));
        TextView textView = (TextView) bottomSheet.findViewById(R.id.sheet_text);
        textView.setText(Html.fromHtml(texts[i]));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        return false;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        // update mapCenter
        mapCenter = mMap.getCameraPosition().target;

        VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
        LatLng nearLeft = visibleRegion.nearLeft;
        LatLng nearRight = visibleRegion.nearRight;

        visibleWidth = distanceFrom(nearLeft.latitude, nearLeft.longitude, nearRight.latitude, nearRight.longitude);
        radius1 = visibleWidth / 10;
        radius2 = visibleWidth / 8;
        radius3 = visibleWidth / 6;
        radius4 = visibleWidth / 4;
        radius5 = visibleWidth / 2;

        // reCalculate markers distance from Center
        reCalculateDistance();

    }

    private void reCalculateDistance() {
        Location tempLocation = new Location("loc1");
        Location mapCenterLocation = new Location("mapCenter");

        mapCenterLocation.setLatitude(mapCenter.latitude);
        mapCenterLocation.setLongitude(mapCenter.longitude);

        for (LatLng pos : latLngArrayList) {
            tempLocation.setLatitude(pos.latitude);
            tempLocation.setLongitude(pos.longitude);

            if (mapCenterLocation.distanceTo(tempLocation) < radius1) {
                resizeIcon(pos, 8);
            } else if(mapCenterLocation.distanceTo(tempLocation) < radius2) {
                resizeIcon(pos, 7);
            } else if(mapCenterLocation.distanceTo(tempLocation) < radius3) {
                resizeIcon(pos, 6);
            } else if(mapCenterLocation.distanceTo(tempLocation) < radius4) {
                resizeIcon(pos, 5);
            } else {
                resizeIcon(pos, 4);
            }
        }

    }

    private void resizeIcon(LatLng pos, int scaleFactor) {
        int i = removeMarker(pos);
        Bitmap halfsizeBitmap = helper.scaleBitmap(scaleFactor);
        markerArrayList.set(i, mMap.addMarker(new MarkerOptions()
                .position(pos)
                .icon(BitmapDescriptorFactory.fromBitmap(halfsizeBitmap))));
    }

    private int removeMarker(LatLng pos) {
        return mainPresenter.removeMarkers(markerArrayList, pos);
    }

    private void addMarkers() {
        int i = 0;

        for (LatLng pos : latLngArrayList) {
            markerArrayList.add(i, mMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .icon(BitmapDescriptorFactory.fromResource(markerIcons.getResourceId(i, -1)))));
            i++;
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SKG_VIEW, 12));
    }

}
