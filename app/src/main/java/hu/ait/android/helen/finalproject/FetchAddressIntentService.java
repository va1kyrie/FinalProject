package hu.ait.android.helen.finalproject;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Helen on 5/16/2015.
 */
public class FetchAddressIntentService extends IntentService {

    private static final String TAG = "fetch-intent-service";

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationaddress";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";

    protected ResultReceiver receiver;

    public FetchAddressIntentService() {
        super(TAG);
    }//FetchAddressIntentService

    @Override
    protected void onHandleIntent(Intent intent) {
        receiver = intent.getParcelableExtra(RECEIVER);

        if(receiver == null){
            Log.i(TAG, "No receiver received. Nowhere to send results.");
        }//if

        Location location = intent.getParcelableExtra(LOCATION_DATA_EXTRA);

        if(location == null){
            Log.i(TAG, "No location data provided");
            deliverResultToReceiver(FAILURE_RESULT, "No location data provided");
            return;
        }//if

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses = null;

        try{
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
        }//try
        catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            String errorMessage = "Invalid longitude or latitude used";
            Log.e(TAG, errorMessage + ". " +
                    "Lat = " + location.getLatitude() +
                    ", Long = " + location.getLongitude(),
                    illegalArgumentException);
        }//catch
        catch (IOException e) {
            e.printStackTrace();
        }//catch

        if(addresses == null || addresses.size() == 0){
            Log.e(TAG, "no addresses");
            deliverResultToReceiver(FAILURE_RESULT, "No addresses");
        }//if
        else{
            Address address = addresses.get(0);
            String country = address.getCountryCode();
            deliverResultToReceiver(SUCCESS_RESULT, country);
        }//else

    }//onHandleIntent

    private void deliverResultToReceiver(int resultCode, String s) {
        Bundle bundle = new Bundle();
        bundle.putString(RESULT_DATA_KEY, s);
        receiver.send(resultCode, bundle);
    }//deliverResultToReceiver
}//class FetchAddressIntentService
