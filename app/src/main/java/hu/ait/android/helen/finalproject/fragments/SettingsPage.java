package hu.ait.android.helen.finalproject.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Currency;
import java.util.List;
import java.util.Locale;

import hu.ait.android.helen.finalproject.MainActivity;
import hu.ait.android.helen.finalproject.R;
import hu.ait.android.helen.finalproject.adapters.FragAdapter;
import hu.ait.android.helen.finalproject.data.CountriesVisited;

/**
 * Created by Helen on 5/12/2015.
 */
public class SettingsPage extends Fragment implements ChooseCurrency.CurrencyInterface {

    public static final String TAG = "SettingsPage";

    private Button curHome;
    private Button curAway;
    private Button btnSave;


    private String homeU = "USD";
    private String awayU = "EUR";

    private String homeN = "US Dollar";
    private String awayN = "Euro";

    SharedPreferences sp;

    public static final String PREF_SETTINGS = "PREF_SETTINGS";
    public static final String KEY_HOME_TYPE = "KEY_MONEY_TYPE";
    public static final String KEY_HOME_UNIT = "KEY_HOME_UNIT";
    public static final String KEY_AWAY_TYPE = "KEY_AWAY_TYPE";
    public static final String KEY_AWAY_UNIT = "KEY_AWAY_UNIT";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.settings_frag, container, false);

        sp = getActivity().getSharedPreferences(PREF_SETTINGS,
                Context.MODE_PRIVATE);

        String defaultU = Currency.getInstance(Locale.getDefault()).getCurrencyCode();
        String defaultT = Currency.getInstance(defaultU).getDisplayName();
        curHome = (Button) rootView.findViewById(R.id.curHome);
        curHome.setText(sp.getString(KEY_HOME_UNIT, defaultU) + "    "
                + sp.getString(KEY_HOME_TYPE, defaultT));
        curHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseCurrency.HOME = 1;
                showCurrencies();
            }//onClick
        });

        curAway = (Button) rootView.findViewById(R.id.curAway);
        curAway.setText(sp.getString(KEY_AWAY_UNIT, "EUR") + "    "
                + sp.getString(KEY_AWAY_TYPE, "Euro"));
        curAway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseCurrency.AWAY = 1;
                showCurrencies();
            }
        });

        btnSave= (Button) rootView.findViewById(R.id.btnSettings);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = sp.edit();
                editor.putString(KEY_HOME_TYPE, homeN);
                editor.putString(KEY_HOME_UNIT, homeU);
                editor.putString(KEY_AWAY_TYPE, awayN);
                editor.putString(KEY_AWAY_UNIT, awayU);

                editor.commit();
            }
        });



        return rootView;
    }//onCreateView

    private void showCurrencies() {
        FragmentManager fm = getChildFragmentManager();
        ChooseCurrency chooseCurrency = new ChooseCurrency();
        chooseCurrency.setTargetFragment(this, 0);
        chooseCurrency.setRetainInstance(true);
        chooseCurrency.show(fm, ChooseCurrency.TAG);
    }//showCurrencies

    @Override
    public void onCurrencyFragmentResult(String moneyName, String moneyUnit){
        if(ChooseCurrency.HOME == 1){
            curHome.setText(moneyUnit + "    " + moneyName);
            homeN = moneyName;
            homeU = moneyUnit;
        }//if
        else if(ChooseCurrency.AWAY == 1){
            curAway.setText(moneyUnit + "    " + moneyName);
            awayN = moneyName;
            awayU = moneyUnit;
        }//else if
        ChooseCurrency.HOME = 0;
        ChooseCurrency.AWAY = 0;
    }//onOptionsFragmentResult

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_HOME_TYPE, homeN);
        editor.putString(KEY_HOME_UNIT, homeU);
        editor.putString(KEY_AWAY_TYPE, awayN);
        editor.putString(KEY_AWAY_UNIT, awayU);

        editor.commit();
    }//onPause

    @Override
    public void onResume() {
        super.onResume();

        homeN = sp.getString(KEY_HOME_TYPE, "US Dollar");
        homeU = sp.getString(KEY_HOME_UNIT, "USD");

        awayN = sp.getString(KEY_AWAY_TYPE, "Euro");
        awayU = sp.getString(KEY_AWAY_UNIT, "EUR");
    }//onResume

}//class SettingsPage
