package hu.ait.android.helen.finalproject;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.Currency;
import java.util.List;
import java.util.Locale;

import hu.ait.android.helen.finalproject.adapters.FragAdapter;
import hu.ait.android.helen.finalproject.adapters.MyTransactionsAdapter;
import hu.ait.android.helen.finalproject.data.CountriesVisited;
import hu.ait.android.helen.finalproject.data.MyTransaction;
import hu.ait.android.helen.finalproject.fragments.SettingsPage;


public class MainActivity extends ActionBarActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String URL_BASE =
            "http://api.fixer.io/latest?base=";

    public static final String URL_SYMBOLS = "&sysmbols=";
    private static final String TAG = "travel-main-activity";

    private MyTransactionsAdapter adapter;
    private SharedPreferences sp;

    protected GoogleApiClient client;
    protected Location lastLocation;
    private AddressResultReceiver receiver;
    private List<CountriesVisited> countries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpEverything();

        sp = getSharedPreferences(SettingsPage.PREF_SETTINGS, Context.MODE_PRIVATE);

        if(isLocationEnabled(this)) {
            buildGoogleApiClient();
            receiver = new AddressResultReceiver(new Handler());
        }//if

        String homeCountry = Currency.getInstance(Locale.getDefault()).getCurrencyCode();
        String homeUnit = sp.getString(SettingsPage.KEY_HOME_UNIT, homeCountry);

        List<MyTransaction> myTransactions = MyTransaction.listAll(MyTransaction.class);
        adapter = new MyTransactionsAdapter(this, myTransactions, homeUnit);

        countries = CountriesVisited.listAll(CountriesVisited.class);

    }//onCreate

    private boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }//catch

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }//else

    }//isLocationEnabled

    private void setUpEverything() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new FragAdapter(getSupportFragmentManager()));
        pager.setCurrentItem(1);
        pager.setBackgroundColor(getResources().getColor(R.color.tab_grey));

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);
        tabs.setBackgroundColor(getResources().getColor(R.color.tab_grey));
        tabs.setTextColor(getResources().getColor(R.color.coral));
        tabs.setForegroundGravity(Gravity.CENTER_HORIZONTAL);

        TextView titles = (TextView) findViewById(R.id.psts_tab_title);
        titles.setBackgroundColor(getResources().getColor(R.color.tab_grey));
    }//setUptEverything

    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(client);
        if(lastLocation != null){
            startIntentService();
        }//if
    }//onConnected

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        client.connect();
    }//onConnectionSuspended

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }//onConnectionFailed

    protected void startIntentService(){
        Intent i = new Intent(this, FetchAddressIntentService.class);
        i.putExtra(FetchAddressIntentService.RECEIVER, receiver);
        i.putExtra(FetchAddressIntentService.LOCATION_DATA_EXTRA, lastLocation);
        startService(i);
    }//startIntentService

    private class AddressResultReceiver extends ResultReceiver{
        public AddressResultReceiver(Handler handler){
            super(handler);
        }//AddressResultReceiver

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData){
            if(resultCode == FetchAddressIntentService.SUCCESS_RESULT){
                String awayCountry = resultData.getString(FetchAddressIntentService.RESULT_DATA_KEY);
                String awayCurrency = Currency.getInstance(new
                        Locale(Locale.getDefault().getLanguage(), awayCountry)).getCurrencyCode();
                String awayCurrName = Currency.getInstance(awayCurrency).getDisplayName();

                CountriesVisited newCountry = new CountriesVisited(awayCountry);
                newCountry.save();

                SharedPreferences.Editor editor = sp.edit();
                editor.putString(SettingsPage.KEY_AWAY_UNIT, awayCurrency);
                editor.putString(SettingsPage.KEY_AWAY_TYPE, awayCurrName);
                editor.commit();
            }//if
        }//onReceiveResult

    }//class AddressResultReceiver


    @Override
    protected void onStart() {
        super.onStart();
        if(isLocationEnabled(this)) {
            client.connect();
        }
    }//onStart

    @Override
    protected void onStop() {
        super.onStop();
        if(client.isConnected()){
            client.disconnect();
        }//if
    }//onStop
}//class MainActivity
