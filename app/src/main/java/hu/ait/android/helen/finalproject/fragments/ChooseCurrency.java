package hu.ait.android.helen.finalproject.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.alertdialogpro.AlertDialogPro;

import hu.ait.android.helen.finalproject.R;

/**
 * Created by Helen on 5/13/2015.
 */
public class ChooseCurrency extends DialogFragment
        implements DialogInterface.OnClickListener {

    public static String[] units = {
            "GBP",
            "HUF",
            "SGD",
            "USD",
            "EUR"
    };

    private String[] money = {
            "British Pound Sterling",
            "Hungarian Forint",
            "Singapore Dollar",
            "US Dollar",
            "Euro"
    };

    public static final String TAG = "ChooseCurrency";
    public static int HOME = 0;
    public static int AWAY = 0;

    public interface  CurrencyInterface{
        public void onCurrencyFragmentResult(String moneyName, String moneyUnit);
    }//interface CurrencyInterface

    private CurrencyInterface currencyInterface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            currencyInterface = (CurrencyInterface) getTargetFragment();
        }//try
        catch(ClassCastException e){
            throw new ClassCastException("Calling fragment implement CurrencyInterface");
        }//catch
    }//onCreate

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialogPro.Builder builder = new AlertDialogPro.Builder(getActivity());
        builder.setTitle(getActivity().getString(R.string.available));
        builder.setItems(money, this);

        return builder.create();
    }//onCreateDialog

    @Override
    public void onClick(DialogInterface dialog, int which) {
            currencyInterface.onCurrencyFragmentResult(money[which], units[which]);
    }//onClick

}//class ChooseCurrency
