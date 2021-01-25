package ohlim.fooda.service;

import javassist.NotFoundException;
import ohlim.fooda.error.exception.InvalidLocationException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

// TODO: component 로 제공했을 때  이점을 생각해보기
@Component
public class LocationUtil {

    public static String getGPSKakaoApiFromLocation(String location){
        String apiKey = "fb79115e278a86dc71d1de6675938a26";
        String apiUrl = "https://dapi.kakao.com/v2/local/search/address.json";
        String jsonString = null;

        try{
            location = URLEncoder.encode(location, "UTF-8");
            String address = apiUrl + "?query=" + location;
            URL url = new URL(address);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("Authorization", "KakaoAK " + apiKey);

            BufferedReader rd = null;
            rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuffer docJson = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null){
                docJson.append(line);
            }
            jsonString = docJson.toString();
            rd.close();

        } catch (UnsupportedEncodingException | MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonString;
    }

    public static List<Double> getLatLonFromJsonString(String locationJson) throws ParseException, NotFoundException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(locationJson);
        JSONArray documentArray = (JSONArray) jsonObject.get("documents");
        JSONObject meta = (JSONObject) jsonObject.get("meta");
        Integer totalCount = Integer.parseInt(meta.get("total_count").toString());

        // 입력받은 location 에 해당하는 좌표가 존재하지 않으면 error
        if(totalCount == 0){
            throw new InvalidLocationException();
        }

        JSONObject document = (JSONObject) documentArray.get(0);
        JSONObject location = (JSONObject) document.get("address");
        Double lat = Double.parseDouble(location.get("x").toString());
        Double lon = Double.parseDouble(location.get("y").toString());

        List<Double> LatLon = new ArrayList<>();
        LatLon.add(lat);
        LatLon.add(lon);
        return LatLon;
    }
}
