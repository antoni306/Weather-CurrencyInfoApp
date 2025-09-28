/**
 *
 *  @author Kostuj Antoni S30687
 *
 */

package zad1;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;
import java.util.stream.Collectors;

public class Service  {
    private  String country;
    private String city;
    private  String currencyCountry;
    private String rateForCurr;
    public Service(String country){
        this.country=country;
        this.currencyCountry=getCode(country);
    }
    public Service(String country,String city,String rateForCurr){
        this.country=country;
        this.city=city;
        this.rateForCurr=rateForCurr;
        this.currencyCountry=getCode(country);
    }
    public String getRateForCurr(){
        return rateForCurr;
    }
    public String getCountry(){
        return country;
    }
    public String getCity(){
        return city;
    }


    private Object getJSONoBJECT(String path){
        try{
            URL url =new URL(path);
            HttpsURLConnection urlConnection=(HttpsURLConnection) url.openConnection();
            try(InputStream inputStream=urlConnection.getInputStream();
                BufferedReader latReader =new BufferedReader(new InputStreamReader(inputStream))
            ){
                JSONParser parser=new JSONParser();
                String json=latReader.lines().collect(Collectors.joining());
                return  parser.parse(json);
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public String getWeather(String city)  {
        this.city=city;
        String latLongRequest="https://api.openweathermap.org/geo/1.0/direct?q={city name}&limit=1&appid=a99ea957a9c2e9b6bffe3eaffeded875";

        String request="https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid=3f4f67e1075eb29a89d63c060b978bbf";
        latLongRequest=latLongRequest.replaceAll("\\{city name}",city);


        JSONArray jsonArray=(JSONArray) getJSONoBJECT(latLongRequest);
        JSONObject jsonObject=(JSONObject) jsonArray.get(0);
        String lat=jsonObject.get("lat").toString();
        String lon=jsonObject.get("lon").toString();
        request=request.replaceAll("\\{lat}",lat);
        request=request.replaceAll("\\{lon}",lon);


        JSONObject jsonObjTemp=(JSONObject) getJSONoBJECT(request);
        JSONObject main=(JSONObject) jsonObjTemp.get("main");

        return main.toString();
    }
    public String getCode(String country){
        String countryToCurrencyPath="https://restcountries.com/v3.1/name/"+country;
        return ((JSONObject)(((JSONObject) ((JSONArray)getJSONoBJECT(countryToCurrencyPath))
                .get(0))
                .get("currencies"))).keySet().stream().findFirst().get().toString();
    }
    public Double getRateFor(String currency) {
        this.rateForCurr=currency;

        String fromCurrency=getCode(this.country);

        String path="https://v6.exchangerate-api.com/v6/1c673499c279ff2c50f715ea/pair/";
        path=path.concat(fromCurrency+"/"+currency);
        JSONObject jsonObject=(JSONObject) getJSONoBJECT(path);
        return Double.parseDouble(jsonObject.get("conversion_rate").toString());
    }
    public Double getNBPRate() {
        String path="https://api.nbp.pl/api/exchangerates/tables/A/?format=json";
        JSONObject jsonObject=(JSONObject)(((JSONArray) getJSONoBJECT(path)).get(0));
        JSONArray rates=(JSONArray) jsonObject.get("rates");
        boolean found=false;
        int i=0;
        JSONObject obj=new JSONObject();
        String forCurrency=getCode(country);
        if(Objects.equals(forCurrency, "PLN"))
            return 1.0;
        while(!found&& i<rates.size()){
            obj=(JSONObject) rates.get(i);
            if(forCurrency.equals(obj.get("code"))){
                found=true;
            }else
                i++;
        }
        return (Double)obj.get("mid");
    }

}
