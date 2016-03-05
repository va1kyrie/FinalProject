package hu.ait.android.helen.finalproject.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import hu.ait.android.helen.finalproject.CreateNewEntry;
import hu.ait.android.helen.finalproject.R;
import hu.ait.android.helen.finalproject.adapters.MyTransactionsAdapter;
import hu.ait.android.helen.finalproject.data.MyTransaction;

/**
 * Created by Helen on 5/12/2015.
 */
public class MainPage extends Fragment {

    public static final int REQUEST_NEW_ENTRY_CODE = 100;
    public static final int REQUEST_EDIT_ENTRY_CODE = 101;
    public static final int CONTEXT_ACTION_DELETE = 10;
    public static final int CONTEXT_ACTION_EDIT = 11;

    private ListView listView;
    private MyTransactionsAdapter adapter;

    private SharedPreferences sp;
    private TextView tvSum;
    private String homeU;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.main_page, container, false);

        List<MyTransaction> summary = MyTransaction.listAll(MyTransaction.class);

        sp = getActivity().getSharedPreferences(SettingsPage.PREF_SETTINGS,
                Context.MODE_PRIVATE);
        homeU = sp.getString(SettingsPage.KEY_HOME_UNIT, "EUR");

        listView = (ListView) rootView.findViewById(R.id.sumList);
        adapter = new MyTransactionsAdapter(getActivity(), summary, homeU);
        listView.setAdapter(adapter);

        tvSum = (TextView) rootView.findViewById(R.id.tvSum);
        findBalance(rootView);

        FloatingActionButton fab = (FloatingActionButton)
                rootView.findViewById(R.id.addButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateNewEntry();
            }
        });

        FloatingActionButton map = (FloatingActionButton) rootView.findViewById(R.id.mapButton);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMap();
            }
        });

        registerForContextMenu(listView);

        return rootView;
    }//onCreateView

    private void findBalance(View rootView) {
        double bal = 0;
        String homeS = Currency.getInstance(homeU).getSymbol();
        for(int i = 0; i < adapter.getCount(); i++){
            if(adapter.getItem(i).getType() == MyTransaction.EXPENSE){
                bal -= adapter.getItem(i).getAmountHome();
            }//if
            else{
                bal += adapter.getItem(i).getAmountHome();
            }//else
        }//for

        BigDecimal cur = BigDecimal.valueOf(bal).setScale(2, BigDecimal.ROUND_HALF_DOWN).abs();

        if(bal < 0){
            tvSum.setTextColor(rootView.getResources().getColor(R.color.coral));
            tvSum.setText("- " + homeS + cur);
        }//if
        else{
            tvSum.setTextColor(rootView.getResources().getColor(R.color.tab_grey));
            tvSum.setText(homeS + cur);
        }//else
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Menu");
        menu.add(0, CONTEXT_ACTION_DELETE, 0, "Delete");
        menu.add(0, CONTEXT_ACTION_EDIT, 0, "Edit");
    }//onCreateContextMenu

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getItemId() == CONTEXT_ACTION_DELETE){
            AdapterView.AdapterContextMenuInfo info =
                    (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            MyTransaction transaction = (MyTransaction) adapter.getItem(info.position);
            transaction.delete();

            adapter.removeTransaction(info.position);
            adapter.notifyDataSetChanged();
        }//if
        if(item.getItemId() == CONTEXT_ACTION_EDIT){
            AdapterView.AdapterContextMenuInfo info =
                    (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            MyTransaction selectedTransaction = (MyTransaction)
                    adapter.getItem(info.position);
            Intent i = new Intent();
            i.setClass(getActivity(), CreateNewEntry.class);
            i.putExtra(CreateNewEntry.KEY_EDIT_ENTRY, selectedTransaction);
            i.putExtra(CreateNewEntry.KEY_EDIT_ID, info.position);
            startActivityForResult(i, REQUEST_EDIT_ENTRY_CODE);
        }//if
        else{
            return false;
        }//if

        return true;
    }

    public void showCreateNewEntry(){
        Intent i = new Intent();
        i.setClass(getActivity(), CreateNewEntry.class);
        startActivityForResult(i, REQUEST_NEW_ENTRY_CODE);
    }//showCreateNewEntry

    private void showMap() {
        Intent i = new Intent();
        i.setClass(getActivity(), MapPage.class);
        startActivity(i);
    }//showMap

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(resultCode){
            case Activity.RESULT_OK:
                if(requestCode == REQUEST_NEW_ENTRY_CODE){
                    MyTransaction transaction= (MyTransaction)
                            data.getSerializableExtra(CreateNewEntry.KEY_ENTRY);
                    transaction.save();

                    adapter.addTransaction(transaction);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), "New transaction added!",
                            Toast.LENGTH_LONG).show();
                }//if
                else if(requestCode == REQUEST_EDIT_ENTRY_CODE){
                    int index = data.getIntExtra(CreateNewEntry.KEY_EDIT_ID, -1);
                    if(index != -1){
                        MyTransaction transaction = (MyTransaction)
                                data.getSerializableExtra(CreateNewEntry.KEY_ENTRY);
                        transaction.setId(adapter.getItem(index).getId());
                        transaction.save();

                        adapter.updateTransaction(index, (MyTransaction)
                                data.getSerializableExtra(CreateNewEntry.KEY_ENTRY));
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(),
                                "Transaction updated!", Toast.LENGTH_LONG).show();
                    }//if
                }//else if
                findBalance(rootView);
                break;
            case Activity.RESULT_CANCELED:
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
                break;
        }//switch
    }//onActivityResult

}//class MainPage
