package Entity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TravelRoute {
    protected String travelDate;
    protected String destination;

    public TravelRoute() {
    }

    public TravelRoute(String travelDate, String destination) {
        this.travelDate = travelDate;
        this.destination = destination;
    }

    public String getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(String travelDate) {
        this.travelDate = travelDate;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    // Hàm xử lý convert sang chuỗi json
    public String convertToJson()
    {
        JSONObject jsonObject = new JSONObject();
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(format.parse(this.travelDate));
            jsonObject.put("Year", calendar.get(Calendar.YEAR));
            jsonObject.put("Month", calendar.get(Calendar.MONTH));
            jsonObject.put("Day", calendar.get(Calendar.DAY_OF_MONTH));
            jsonObject.put("Destination", this.destination);
        } catch (JSONException | ParseException e) {
            return "";
        }
        String check = jsonObject.toString();
        return jsonObject.toString();
    }
}
