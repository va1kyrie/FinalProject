package hu.ait.android.helen.finalproject;


import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.Calendar;
import de.greenrobot.event.EventBus;
import hu.ait.android.helen.finalproject.data.MoneyResult;
import hu.ait.android.helen.finalproject.data.MyTransaction;
import hu.ait.android.helen.finalproject.fragments.ChooseCurrency;
import hu.ait.android.helen.finalproject.fragments.PickADate;
import hu.ait.android.helen.finalproject.fragments.SettingsPage;

/**
 * Created by Helen on 5/13/2015.
 */
public class CreateNewEntry extends FragmentActivity
        implements ChooseCurrency.CurrencyInterface, PickADate.PickADateInterface {
    public static final String TAG = "CreateNewEntry";

    public static final String KEY_EDIT_ENTRY = "KEY_EDIT_ENTRY";
    public static final String KEY_EDIT_ID = "KEY_EDIT_ID";
    public static final String KEY_ENTRY = "KEY_ENTRY";

    private boolean inEditMode = false;

    private EditText etDesc;
    private Button btnDate;
    private EditText etAmount;
    private TextView btnUnit;
    private Switch swType;

    private int entryToEditId = 0;
    private MyTransaction transactionToEdit = null;

    private String awayU;
    private String awayN;
    private String homeU;

    private BigDecimal homeVal;
    private int TYPE = 10;

    SharedPreferences sp;

    private int yearM;
    private int monthM;
    private int dayM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences(SettingsPage.PREF_SETTINGS, Context.MODE_PRIVATE);
        awayU = sp.getString(SettingsPage.KEY_AWAY_UNIT, "EUR");
        homeU = sp.getString(SettingsPage.KEY_HOME_UNIT, "USD");

        setContentView(R.layout.new_entry);
        etDesc = (EditText) findViewById(R.id.etDesc);

        Calendar c = Calendar.getInstance();
        yearM = c.get(Calendar.YEAR);
        monthM = c.get(Calendar.MONTH)+1;
        dayM = c.get(Calendar.DAY_OF_MONTH);

        btnDate = (Button) findViewById(R.id.btnDate);
        btnDate.setText(yearM + "-" + monthM + "-" + dayM);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                PickADate pickADate = new PickADate();
                pickADate.setRetainInstance(true);
                pickADate.show(fm, PickADate.TAG);
            }
        });

        etAmount = (EditText) findViewById(R.id.etAmount);
        etAmount.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(5,2)});
        btnUnit = (TextView) findViewById(R.id.btnUnit);
        btnUnit.setText(awayU);

        swType = (Switch) findViewById(R.id.swType);

        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcHome();
            }//onClick
        });

        if (getIntent().getExtras() != null &&
                getIntent().getExtras().containsKey(KEY_EDIT_ENTRY)) {
            inEditMode = true;

            transactionToEdit = (MyTransaction) getIntent().getSerializableExtra(KEY_EDIT_ENTRY);
            entryToEditId = getIntent().getIntExtra(KEY_EDIT_ID, -1);

            etDesc.setText(transactionToEdit.getDesc());
            btnDate.setText(transactionToEdit.getDate());
            etAmount.setText("" + transactionToEdit.getAmountAway());
            if(transactionToEdit.getType() == MyTransaction.EXPENSE){
                swType.setChecked(true);
            }//if
            else{
                swType.setChecked(false);
            }//else
        }//if

    }//onCreate

    @Override
    public void onPickADateResult(int year, int month, int day) {
        yearM = year;
        monthM = month+1;
        dayM = day;

        btnDate.setText(yearM + "-" + monthM + "-" + dayM);
    }//onPickADateResult

    private void calcHome() {
        String query;
        if (inEditMode) {
            query = MainActivity.URL_BASE + homeU + MainActivity.URL_SYMBOLS + awayU;
        }//if
        else {
            query = MainActivity.URL_BASE + homeU + MainActivity.URL_SYMBOLS + awayU;
        }//else
        new HttpGetTask(getApplicationContext()).execute(query);
    }//calcHome

    public void onEvent(MoneyResult result){
        double rate = getRate(result);
        if(inEditMode) {
            homeVal = BigDecimal.valueOf(transactionToEdit.getAmountAway() / rate)
                    .setScale(2, BigDecimal.ROUND_HALF_DOWN);
        }//if
        else{
            homeVal = BigDecimal.valueOf(Double.valueOf(etAmount.getText().toString()) / rate)
                    .setScale(2, BigDecimal.ROUND_HALF_DOWN);

//                    BigDecimal.valueOf(Double.valueOf(etAmount.getText().toString())).
//                    divide(rate).setScale(2, BigDecimal.ROUND_CEILING);
        }//else

        saveEverything();
    }//onEvent

    public void saveEverything(){
        if (swType.isChecked()) {
            TYPE = MyTransaction.EXPENSE;
        }//if
        else if (!swType.isChecked()) {
            TYPE = MyTransaction.INCOME;
        }//else

        if (inEditMode) {
            updateEntry();
        }//if
        else {
            saveEntry();
        }//else
        finish();
    }

    private double getRate(MoneyResult result) {
        double rate = 1;
        if(awayU.equals("HUF")) {
            rate = result.getRates().getHUF();
        }//if
        else if(awayU.equals("GBP")){
            rate = result.getRates().getGBP();
        }//else if
        else if(awayU.equals("USD")){
            rate = result.getRates().getUSD();
        }//else if
        else if(awayU.equals("EUR")){
            rate = result.getRates().getEUR();
        }//else if
        else if (awayU.equals("SGD")){
            rate = result.getRates().getSGD();
        }
        return rate;
    }//getRate

    private void saveEntry() {
        Intent intentResult = new Intent();
        intentResult.putExtra(KEY_ENTRY,
                new MyTransaction(awayU, Double.valueOf(
                        etAmount.getText().toString()),
                        homeVal.doubleValue(), TYPE, btnDate.getText().toString(),
                        etDesc.getText().toString()));
        setResult(RESULT_OK, intentResult);
    }

    private void updateEntry() {
        transactionToEdit.setDesc(etDesc.getText().toString());
        transactionToEdit.setDate(btnDate.getText().toString());
        transactionToEdit.setUnit(awayU);
        transactionToEdit.setAmountAway(Double.valueOf(etAmount.getText().toString()));

        transactionToEdit.setAmountHome(homeVal.doubleValue());

        if(swType.isChecked()){
            transactionToEdit.setType(MyTransaction.EXPENSE);
        }//if
        else{
            transactionToEdit.setType(MyTransaction.INCOME);
        }//else

        Intent intentResult = new Intent();
        intentResult.putExtra(KEY_ENTRY, transactionToEdit);
        intentResult.putExtra(KEY_EDIT_ID, entryToEditId);
        setResult(RESULT_OK, intentResult);
    }//updateEntry

    public void chooseCurrency() {
        ChooseCurrency.AWAY = 1;
        FragmentManager fm = getSupportFragmentManager();
        ChooseCurrency chooseCurrency = new ChooseCurrency();
        chooseCurrency.setRetainInstance(true);
        chooseCurrency.show(fm, chooseCurrency.TAG);
    }//chooseCurrency

    @Override
    public void onCurrencyFragmentResult(String moneyName, String moneyUnit) {
        awayN = moneyName;
        awayU = moneyUnit;

        btnUnit.setText(awayU);

        ChooseCurrency.AWAY = 0;
    }//onOptionsFragmentResult

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SettingsPage.KEY_AWAY_TYPE, awayN);
        editor.putString(SettingsPage.KEY_AWAY_UNIT, awayU);

        editor.commit();

        EventBus.getDefault().unregister(this);
    }//onPause

    @Override
    protected void onResume() {
        super.onResume();

        awayU = sp.getString(SettingsPage.KEY_AWAY_UNIT, "EUR");
        awayN = sp.getString(SettingsPage.KEY_AWAY_TYPE, "Euro");
        homeU = sp.getString(SettingsPage.KEY_HOME_UNIT, "USD");

        EventBus.getDefault().register(this);
    }
}//class CreateNewEntry
