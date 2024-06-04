package Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Entity.TravelRoute;

public class StringHelper {
    public static String convertListRouteToJSONString(List<TravelRoute> routeList)
    {
        JSONArray jsonArray = new JSONArray();
        for (TravelRoute route: routeList)
        {
            jsonArray.put(route.convertToJson());
        }
        return jsonArray.toString();
    }

    public static List<TravelRoute> convertJSONStringToListRoute(String input)
    {
        // khoi tap mang travelroute de luu data tu json
        List<TravelRoute> travelRoutes = new ArrayList<TravelRoute>();
        try {
            // khoi tao mang json va convert tu chuoi json nhap vao
            JSONArray array = new JSONArray(input);
            for (int i = 0; i < array.length(); i++)
            {
                // lay chuoi tu mang cac chuoi json
                // bien tmp se co kieu {"name": "demo", "date":  "28/01/2000"}
                String tmp = array.getString(i);
                // tao mot json object tu bien tmp
                JSONObject object = new JSONObject(tmp);
                // tao bien kieu TravelRoute de luu data
                TravelRoute temp = new TravelRoute();
                // lay bien theo key
                int day = object.getInt("Day");
                int month = object.getInt("Month");
                int year = object.getInt("Year");
                temp.setTravelDate(day + "/" + month + "/" + year);
                temp.setDestination(object.getString("Destination"));
                // add bien TravelRoute vao list
                travelRoutes.add(temp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return travelRoutes;
    }
}
