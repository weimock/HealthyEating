package com.example.healthyeating.healthyeating.Boundary;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;

import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthyeating.healthyeating.Controller.SingletonManager;
import com.example.healthyeating.healthyeating.Entity.Location;
import com.example.healthyeating.healthyeating.R;

import com.google.android.gms.maps.model.Marker;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.example.healthyeating.healthyeating.Controller.LocationsManager;
import java.util.ArrayList;
import android.view.View;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements SearchAndSlide.OnSearchSubmitListener,OnMapReadyCallback,LocationDetailsFragment.OnFragmentInteractionListener,SearchAndSlide.OnFragmentInteractionListener {
    private ActionBar toolbar;
    private BottomNavigationView mBottomNavigation;
    private FrameLayout mMainFrame;
    TextView btmTextView;
    Button btn_save;
    Button btn_close;
    private FavouriteFragment favouriteFragment;
    private EateriesFragment eateriesFragment;
    private CaterersFragment caterersFragment;
    private HCSProductsFragment hcsProductsFragment;
    private LocationDetailsFragment ldf;
    SearchAndSlide searchSlide;
    SearchView searchView ;
    static int height = 0;
    int width = 0;
    LocationsManager lm;

    GoogleMap mMap;
    String value;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //   Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        ArrayList<Location> loc = new ArrayList<>();
        if (value != null) {

            Location clickLoc = lm.getLocation(Integer.parseInt(value));
            loc.add(clickLoc);
            loadMapWithMarkers(loc);

            String address = clickLoc.getAddress();
            String name = clickLoc.getName();
            String floor_number = clickLoc.getFloor();
            String unit_number = clickLoc.getUnit();

            String display = "\r\n\tName : "+name+"\r\n"
                    + "\tAddress : "+address+"\r\n"
                    +"\tFloor No. : "+floor_number+"\r\n"
                    +"\tUnit No. : "+unit_number+"\r\n";

            //Set infomration box to be visible
            toggleInformationBox(true);
            btmTextView.setText(display);

        }
        else {
            loc = lm.getListOfLocation("Eateries");
            loadMapWithMarkers(loc);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(1.3521, 103.8198 ), 11.0f));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get LocationManager Instance
        lm = SingletonManager.getLocationManagerInstance();

        value = getIntent().getStringExtra("key");
        getIntent().removeExtra("key");

        if(googleServiceAvailable()){
            Toast.makeText(this,"Perfect",Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_main);

            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            supportMapFragment.getMapAsync(this);
        }

        mMainFrame = (FrameLayout) findViewById(R.id.main_frame);

        mBottomNavigation = (BottomNavigationView) findViewById((R.id.navigation));
        mBottomNavigation.setLabelVisibilityMode(1);

        favouriteFragment = new FavouriteFragment();
        eateriesFragment = new EateriesFragment();
        caterersFragment = new CaterersFragment();
        hcsProductsFragment = new HCSProductsFragment();
        searchSlide = new SearchAndSlide();
        ldf = new LocationDetailsFragment();

        mBottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_eateries:
                        return loadFragment(searchSlide);
                    case R.id.navigation_favourite:
                        return loadFragment(favouriteFragment);
                    case R.id.navigation_caterers:
                        return loadFragment(searchSlide);
                    case R.id.navigation_HCSProduct:
                        return loadFragment(hcsProductsFragment);
                    default:
                        return loadFragment(searchSlide);
                    //return false;
                }
            }
        });

        mBottomNavigation.getMenu().getItem(1).setChecked(true);

        init();
        loadFragment(searchSlide);

        //When click close btn, set the information to be invisible
        btn_close = findViewById(R.id.button_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                toggleInformationBox(false);
                loadMapWithMarkers(lm.getListOfLocation("Eateries"));
            }
        });

    }

    private void init(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        //Find element
        btmTextView = (TextView)findViewById(R.id.btm_textView);
        btn_save = (Button)findViewById(R.id.button_save);
        btn_close = (Button)findViewById(R.id.button_close);
        Log.d("HEIGHT",""+height);
        //Set coordinate
        btmTextView.setY(height*0.04180602006688963210702341137124f);
        btn_save.setY(((float)height-(height*0.271739f)));
        btn_close.setY(((float)height-(height*0.271739f)));

        //Hide information box
        toggleInformationBox(false);

    }



    private void toggleInformationBox(boolean toggle){

        float value = toggle? 1.0f:0.0f;
        btmTextView.setAlpha(value);
        btn_save.setAlpha(value);
        btn_close.setAlpha(value);
    }

    private void loadMapWithMarkers(ArrayList<Location> loc){
        mMap.clear();
        Log.d("SearchBar","Cleared");
        for(int i = 0 ; i<loc.size();i++){
            LatLng ll = new LatLng(loc.get(i).getLatitude(), loc.get(i).getLongitude());
            mMap.addMarker(new MarkerOptions().position(ll)
                    .snippet(""+loc.get(i).getId()));

        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(MainActivity.this, "Clicked on LocationID "+marker.getSnippet(), Toast.LENGTH_LONG).show();

                Location clickLoc = lm.getLocation(Integer.parseInt(marker.getSnippet()));

                String address = clickLoc.getAddress();
                String name = clickLoc.getName();
                String floor_number = clickLoc.getFloor();
                String unit_number = clickLoc.getUnit();

                String display = "\r\n\tName : "+name+"\r\n"
                        + "\tAddress : "+address+"\r\n"
                        +"\tFloor No. : "+floor_number+"\r\n"
                        +"\tUnit No. : "+unit_number+"\r\n";

                //Set infomration box to be visible
                toggleInformationBox(true);

                btmTextView.setText(display);

                return true;
            }
        });

    }



    private boolean loadFragment(Fragment fragment){
        if(fragment !=null){
            getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,fragment).commit();
            return true;
        }
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        //respond to menu item selection
        switch (item.getItemId()) {
            case R.id.navigation_settings:
                //mBottomNavigation.setSelected(false);
                startActivity(new Intent(MainActivity.this, settings.class));
                return true;
            //startActivity(new Intent(this, About.class));
            default:
                startActivity(new Intent(MainActivity.this, settings.class));
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean googleServiceAvailable()
    {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if(isAvailable== ConnectionResult.SUCCESS){
            return true;
        }
        else if(api.isUserResolvableError(isAvailable)){
            Dialog dialog = api.getErrorDialog(this, isAvailable,0);
            dialog.show();
        }
        else{
            Toast.makeText(this,"Cant connect to play services", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    public void searchSubmit(String query) {
        Log.d("SearchBar","This called");
        loadMapWithMarkers(lm.searchLocations("Eateries",query));

        Toast.makeText(MainActivity.this, "Clicked "+ query, Toast.LENGTH_LONG).show();
    }
}
