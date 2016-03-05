package hu.ait.android.helen.finalproject;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.greenrobot.event.EventBus;
import hu.ait.android.helen.finalproject.data.MoneyResult;

/**
 * Created by Helen on 5/13/2015.
 */
public class HttpGetTask extends AsyncTask<String, Void, String> {

    public static final String FILTER_RESULT = "FILTER_RESULT";
    public static final String KEY_RESULT = "KEY_RESULT";
    private Context ctx;

    public HttpGetTask(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected String doInBackground(String... params) {
        String result = "";

        HttpURLConnection connection = null;
        InputStream is = null;

        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = connection.getInputStream();

                int ch;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while ((ch = is.read()) != -1) {
                    bos.write(ch);
                }//while

                result = new String(bos.toByteArray());
            }//if
        }//try
        catch(Exception e){
            e.printStackTrace();
        }//catch
        finally{
            if(is != null){
                try{
                    is.close();
                }//try
                catch(IOException e){
                    e.printStackTrace();
                }//catch
            }//if

            if(connection != null){
                connection.disconnect();
            }//if
        }//finally

        return result;
    }//doInBackground

    @Override
    protected void onPostExecute(String result) {
        try{
            Gson gson = new Gson();
            MoneyResult moneyResult = gson.fromJson(result, MoneyResult.class);

            EventBus.getDefault().post(moneyResult);
        }//try
        catch (Exception e){
            e.printStackTrace();
        }//catch
    }//onPostExecute
}//class HttpGetTask
