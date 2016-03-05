package hu.ait.android.helen.finalproject.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by Helen on 5/15/2015.
 */
public class PickADate extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public static final String TAG = "PickADate";

    public interface PickADateInterface{
        public void onPickADateResult(int year, int month, int day);
    }

    private PickADateInterface pickADateInterface;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try{
            pickADateInterface = (PickADateInterface) activity;
        }//try
        catch (ClassCastException e){
            throw new ClassCastException("Calling class " +
                    "must implement PickADateInterface");
        }//catch
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(getActivity(), this, year, month, day);

        return datePickerDialog;
    }//onCreateDialog

    public void onDateSet(DatePicker view, int year, int month, int day){
        pickADateInterface.onPickADateResult(year, month, day);
    }//onDateSet
}//PickADate
