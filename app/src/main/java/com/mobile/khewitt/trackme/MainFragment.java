package com.mobile.khewitt.trackme;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
/*------------------------------------------------------------------------//
* Kyle Hewitt       Mobile Design       GPS Distance Calculation
*
* Fragment for GPS calculation. This fragment gets readings and calculates
*  distance as well as gives the user the ability to change update frequancy.
//------------------------------------------------------------------------*/
public class MainFragment extends Fragment {

    TextView mDistanceDisplay,mAccuracyDisplay,mSetFreqDisplayText, mFastFreqSelectText;
    //Various text displays for output
    Button mStart, mStop,mPickTime,mSetSelectedFreq;
    //Start, stop, open set freq screen, and set time buttons
    ImageButton mSlow,mMed,mFast;
    //Images the user can select in place of picking time in the set freq screen
    LocationListener locationListener;
    //This is the listener for getting location objects from the GPS
    int mTime = 5000;
    //default time between updates. Can be changed by user at runtime
    NumberPicker mSecondPicker;
    //selection wheel to let user set update freq at runtime
    ImageView mConnectionStatus, mImageView;
    //display icons so that user knows what is going on between the time start is pressed and when
        //calculations are being done
    double mDistance;
    //holds distance over the iteration
    double mPreviousLongitude, mPreviousLatitude;
    //holds previous long and lat readings for calculating distance
    boolean mIsFirstReading = true;
    //This marks the first reading in an iteration. False every other time
    boolean singleToast = true;
    float[] distance = new float[1];
    //float array for distanceBetween method to fill
    float mAccuracy;
    //Accurcy reading


//-----------------------------------------------------------------------------------------------//
//--------------------------------Empty Constructor----------------------------------------------//
    public MainFragment() {

    }

//-----------------------------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------//
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//Initialization
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mStart = (Button)view.findViewById(R.id.start_button);
        mStop = (Button)view.findViewById(R.id.stop_button);
        mSetSelectedFreq = (Button)view.findViewById(R.id.set_new_time);
        mSlow = (ImageButton)view.findViewById(R.id.slow_speed);
        mMed = (ImageButton)view.findViewById(R.id.med_speed);
        mFast = (ImageButton)view.findViewById(R.id.high_speed);
        mSlow.setVisibility(View.GONE);//Only exsists when user selects Set GPS Freq
        mMed.setVisibility(View.GONE);//Only exsists when user selects Set GPS Freq
        mFast.setVisibility(View.GONE);//Only exsists when user selects Set GPS Freq
        mFastFreqSelectText = (TextView)view.findViewById(R.id.quickselect_text);
        mFastFreqSelectText.setVisibility(View.GONE);//Only exsists when user selects Set GPS Freq
        mSetFreqDisplayText = (TextView)view.findViewById(R.id.sec_picker_display);
        mSetFreqDisplayText.setVisibility(View.GONE);//Only exsists when user selects Set GPS Freq
        mConnectionStatus = (ImageView)view.findViewById(R.id.connection_status);
        mDistanceDisplay = (TextView)view.findViewById(R.id.textView);
        mConnectionStatus.setClickable(true);
        mImageView = (ImageView)view.findViewById(R.id.imageView);
        mSecondPicker = (NumberPicker)view.findViewById(R.id.secondsPicker);
        mSecondPicker.setMaxValue(120);//user can select up to 120 seconds between updates
        mSecondPicker.setValue(5);//defaults selection to 5 seconds
        mSecondPicker.setWrapSelectorWheel(false);//no wheel wrap
        mSecondPicker.setVisibility(View.GONE);//Only exsists when user selects Set GPS Freq
        mSetSelectedFreq.setVisibility(View.GONE);//Only exsists when user selects Set GPS Freq
        mStart.setClickable(true);
        mStop.setClickable(false);
        mPickTime = (Button)view.findViewById(R.id.freq_button);
        mAccuracyDisplay = (TextView)view.findViewById(R.id.acc_display);
        final LocationManager locationManager = (LocationManager) getActivity()
                .getSystemService(Context.LOCATION_SERVICE);





//Button Functions
    //LocationListener
      mStart.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              onStartButtonPressed();
              locationListener = new LocationListener() {
                  @Override
            //--This Method is called every time the GPS updates-----------------//
            //--A location object is passed in containing all the GPS readings--//
                  public void onLocationChanged(Location location) {
                      getAccuracy(location);
                      calcDistance(location);
                  }

                  @Override
                  public void onStatusChanged(String provider, int status, Bundle extras){/*not used*/}
                  @Override
                  public void onProviderEnabled(String provider){/*not used*/}
                  @Override
                  public void onProviderDisabled(String provider){/*not used*/}
              };
              locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                      mTime, 0, locationListener);
          }
      });
    //stop button
      mStop.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              stopPressed();
              locationManager.removeUpdates(locationListener);
          }
      });
    //explination of icons
        mConnectionStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeToast("Connection Status",true);
            }
        });
    //Brings up widgets and buttons to allow user to change the update freq
        mPickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageView.setVisibility(View.INVISIBLE);
                mStart.setVisibility(View.INVISIBLE);
                mStop.setVisibility(View.INVISIBLE);
                mPickTime.setVisibility(View.INVISIBLE);
                mDistanceDisplay.setVisibility(View.INVISIBLE);
                mSecondPicker.setVisibility(View.VISIBLE);
                mSetSelectedFreq.setVisibility(View.VISIBLE);
                mSetFreqDisplayText.setVisibility(View.VISIBLE);
                mSlow.setVisibility(View.VISIBLE);
                mMed.setVisibility(View.VISIBLE);
                mFast.setVisibility(View.VISIBLE);
                mFastFreqSelectText.setVisibility(View.VISIBLE);
            }
        });
    //Action that sets users selections and returns to the main view
        mSetSelectedFreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFreq((mSecondPicker.getValue()*1000));
                if(mTime<10000){
                    mImageView.setImageResource(R.drawable.standing);
                }else if(mTime<15000){
                    mImageView.setImageResource(R.drawable.walking);
                }else{
                    mImageView.setImageResource(R.drawable.running);
                }
            }
        });
    //quick selection button for update freq
        mSlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFreq(5000);
                mSecondPicker.setValue(5);
                mImageView.setImageResource(R.drawable.standing);

            }
        });
    //quick selection button for update freq
        mMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFreq(10000);
                mSecondPicker.setValue(10);
                mImageView.setImageResource(R.drawable.walking);
            }
        });
    //quick selection button for update freq
        mFast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFreq(15000);
                mSecondPicker.setValue(15);
                mImageView.setImageResource(R.drawable.running);
            }
        });
        return view;
    }//OnCreateView
//-------------------------starts the gps and tracking-------------------------------------------//

    public void onStartButtonPressed(){
        mDistance = 0;
        mConnectionStatus.setImageResource(R.drawable.connecting);
        mStop.setClickable(true);
        mStart.setClickable(false);
        makeToast("Start", false);
    }

//--------------get the accuracy and set a color display to reflect quality of connection--------//

    public void getAccuracy(Location location){
        if(singleToast) {//So that the connected toast only shows the first time the method is called
                            //and not on every update
            makeToast("Connected", true);
            singleToast = false;
        }
        mConnectionStatus.setImageResource(R.drawable.connected);
        mAccuracy = location.getAccuracy();
        mAccuracyDisplay.setText("" + mAccuracy + "m");
    //Color codes accuracy display
        mAccuracyDisplay.setAlpha(1f);
        if (mAccuracy <= 5) {
            mAccuracyDisplay.setBackgroundColor(Color.GREEN);
        } else if (mAccuracy <= 25) {
            mAccuracyDisplay.setBackgroundColor(Color.YELLOW);
        } else {
            mAccuracyDisplay.setBackgroundColor(Color.RED);
        }

    }//getAccuracy

//------------calculates the distance between reading and adds it into a summation----------------//

    public void calcDistance(Location location){
        if (mIsFirstReading) {//handles the first reading when there is no previous reading to pass
            mPreviousLatitude = location.getLatitude();
            mPreviousLongitude = location.getLongitude();
        }
        if (mAccuracy<= MainActivity.getAccuracyBuffer()) {//acuracy buffer prevents highly inacurate readings
        Location.distanceBetween(mPreviousLatitude, mPreviousLongitude
                , location.getLatitude(), location.getLongitude(), distance);
            if(distance[0]>.5) {//prevents calculating distance when device is still
                mDistance += distance[0];
            }
        mDistanceDisplay.setText(String.format("Distance: %.2f meters", mDistance));
        mPreviousLongitude = location.getLongitude();
        mPreviousLatitude = location.getLatitude();
        mIsFirstReading = false;
        }else{
            makeToast("Waiting for better accuracy", false);
        }
    }//calcDistance

//-------------------Handles the process of stopping the tracking--------------------------------//

    public void stopPressed(){
        mIsFirstReading = true;
        singleToast = true;
        mStart.setClickable(true);
        mStop.setClickable(false);
        mAccuracyDisplay.setAlpha(0);
        mConnectionStatus.setImageResource(R.drawable.no_signal);
        mDistance = 0;
        makeToast("Stop",false);
    }

//-----------------------------------Makes Toasts------------------------------------------------//

    public void makeToast(String message, boolean isLong){
        if(isLong) {
            Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

//-------------------------Stops tracking when application is onPause-----------------------//

    @Override
    public void onPause() {
        super.onPause();
        LocationManager locationManager = (LocationManager) getActivity()
                .getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(locationListener);
    }


//-------------------------Sets a user defined update Freq-------------------------------------//

    public void setFreq(int time){
        mTime = time;
        makeToast("GPS freq set to "+mTime+"Miliseconds", true);
        mImageView.setVisibility(View.VISIBLE);
        mStart.setVisibility(View.VISIBLE);
        mStop.setVisibility(View.VISIBLE);
        mDistanceDisplay.setVisibility(View.VISIBLE);
        mPickTime.setVisibility(View.VISIBLE);
        mSecondPicker.setVisibility(View.GONE);
        mSetSelectedFreq.setVisibility(View.GONE);
        mSetFreqDisplayText.setVisibility(View.GONE);
        mSlow.setVisibility(View.GONE);
        mMed.setVisibility(View.GONE);
        mFast.setVisibility(View.GONE);
        mFastFreqSelectText.setVisibility(View.GONE);

    }//setFreq


//-----------------------------------------------------------------------------------------------//
    @Override
    public void onResume() {
        super.onResume();
    }
}
