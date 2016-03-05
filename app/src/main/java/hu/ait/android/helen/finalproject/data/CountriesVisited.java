package hu.ait.android.helen.finalproject.data;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by Helen on 5/17/2015.
 */
public class CountriesVisited extends SugarRecord<CountriesVisited>
        implements Serializable {

    private String country;

    public CountriesVisited(){}//CountriesVisited

    public CountriesVisited(String country){
        this.country = country;
    }//CountriesVisited

    public String getCountry() { return country; }//getCountry

    public void setCountry(String country) {
        this.country = country;
    }//setCountry
    
}//class
