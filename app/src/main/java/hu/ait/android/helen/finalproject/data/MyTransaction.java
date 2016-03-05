package hu.ait.android.helen.finalproject.data;

import com.orm.SugarRecord;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Helen on 5/13/2015.
 */
public class MyTransaction extends SugarRecord<MyTransaction> implements Serializable {

    private String unit;
    private double amountAway;
    private double amountHome;
    private int type;
    private String date;
    private String desc;

    public static final int EXPENSE = 10;
    public static final int INCOME = 100;

    public MyTransaction() {}//Transaction

    public MyTransaction(String unit, double amountAway,
                         double amountHome, int type, String date, String desc){
        this.unit = unit;
        this.amountAway = amountAway;
        this.type = type;
        this.date = date;
        this.desc = desc;

        this.amountHome = amountHome;
    }//Transaction

    public String getUnit() { return unit; }//getUnit

    public double getAmountAway() { return amountAway; }//getAmountAway

    public double getAmountHome() { return amountHome; }//getAmountHome

    public int getType() { return type; }//getType

    public String getDate() { return date; }//getDate

    public String getDesc() { return desc; }//getDesc

    public void setUnit(String unit) { this.unit = unit; }//setUnit

    public void setAmountAway(double amountAway) {
        this.amountAway = amountAway;
    }//setAmountAway

    public void setAmountHome(double amountHome){
        this.amountHome = amountHome;
    }//setAmountHome

    public void setType(int type) { this.type = type; }//setType

    public void setDate(String date) { this.date = date; }//setDate

    public void setDesc(String desc) { this.desc = desc; }//setDesc

}//class Transaction
