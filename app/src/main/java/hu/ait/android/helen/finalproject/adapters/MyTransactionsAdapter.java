package hu.ait.android.helen.finalproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Currency;
import java.util.List;

import hu.ait.android.helen.finalproject.R;
import hu.ait.android.helen.finalproject.data.MyTransaction;

/**
 * Created by Helen on 5/13/2015.
 */
public class MyTransactionsAdapter extends BaseAdapter {

    private Context context;
    private List<MyTransaction> entries;
    private String homeU;

    public MyTransactionsAdapter(Context context, List<MyTransaction> entries,
                                 String homeU){
        this.context = context;
        this.entries = entries;
        this.homeU = homeU;
    }//TransactionAdapter

    public void addTransaction(MyTransaction myTransaction){
        entries.add(myTransaction);
    }//addTransaction

    public void updateTransaction(int index, MyTransaction myTransaction){
        entries.set(index, myTransaction);
    }//update

    public void removeTransaction(int index) { entries.remove(index); }//removeTransaction

    public void removeAll() { entries.clear(); }//removeAll

    @Override
    public int getCount() {
        return entries.size();
    }//getCount

    @Override
    public MyTransaction getItem(int position) {
        return entries.get(position);
    }//getItem

    @Override
    public long getItemId(int position) {
        return position;
    }//getItemId

    static class ViewHolder{
        TextView tvDesc;
        TextView tvDate;
        TextView tvAmtHome;
        TextView tvAmtAway;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            v = inflater.inflate(R.layout.trans_item, null);
            ViewHolder holder = new ViewHolder();
            holder.tvDesc = (TextView) v.findViewById(R.id.tvDesc);
            holder.tvDate = (TextView) v.findViewById(R.id.tvTransDate);
            holder.tvAmtAway = (TextView) v.findViewById(R.id.tvAmtAway);
            holder.tvAmtHome = (TextView) v.findViewById(R.id.tvAmtHome);

            v.setTag(holder);
        }//if

        MyTransaction myTransaction = entries.get(position);
        if(myTransaction != null){
            final ViewHolder holder = (ViewHolder) v.getTag();

            String away;
            String home;

            if(myTransaction.getType() == MyTransaction.EXPENSE){
                holder.tvAmtAway.setTextColor(context.getResources()
                        .getColor(R.color.coral));
                holder.tvAmtHome.setTextColor(context.getResources()
                        .getColor(R.color.coral));
                away = "- " + Currency.getInstance(myTransaction.getUnit()).getSymbol() +
                        myTransaction.getAmountAway();

                home = "- " + Currency.getInstance(homeU).getSymbol()
                        + myTransaction.getAmountHome();
            }//if
            else{
                holder.tvAmtAway.setTextColor(context.getResources()
                        .getColor(R.color.lightbluegrey));
                holder.tvAmtHome.setTextColor(context.getResources()
                        .getColor(R.color.lightbluegrey));
                away = Currency.getInstance(myTransaction.getUnit()).getSymbol() +
                        myTransaction.getAmountAway();

                home = Currency.getInstance(homeU).getSymbol()
                        + myTransaction.getAmountHome();
            }//else
            holder.tvDesc.setText(myTransaction.getDesc());
            holder.tvDate.setText(myTransaction.getDate());

            holder.tvAmtAway.setText(away);
            holder.tvAmtHome.setText(home);
        }//if

        return v;
    }//getView
}//class TransactionAdapter
