package com.mobile.khewitt.trackme;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
/*------------------------------------------------------------------------//
* Kyle Hewitt       Mobile Design       GPS Distance Calculation
*
* Main Activity for GPS distance calculation. Used to inflate fragment
*  and handle action menu selections.
//------------------------------------------------------------------------*/

public class MainActivity extends Activity {


    private static int mAccuracyBuffer = 10;//This is used as the value for the accuracy buffer
    private boolean mBufferActive = true;//The buffer is set by default

//------------------------------------------------------------------------//
//------------------------------------------------------------------------//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainFragment fragment = new MainFragment();
        getFragmentManager().beginTransaction().add(R.id.fragment, fragment);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
    }

//------------------------------------------------------------------------//
//------------------------------------------------------------------------//

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

//------------------------------------------------------------------------//
//------------------------------------------------------------------------//

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.set_accuracy_buffer) {
//if buffer is active clicking action item will set it as inactive
            if(mBufferActive) {
                item.setTitle("Accuracy Buffer(Off)");
                mAccuracyBuffer = Integer.MIN_VALUE;
                mBufferActive = false;
            }else{
//if buffer is inactive, clicking the action item will set it active
                item.setTitle("Accuracy Buffer(On)");
                mAccuracyBuffer = 12;
                mBufferActive = true;
            }
            return true;//it was handled
        }

        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected

//------------------------------------------------------------------------//
//----------------Returns the accuracy buffer variable--------------------//

    public static int getAccuracyBuffer(){
        return mAccuracyBuffer;
    }
}
