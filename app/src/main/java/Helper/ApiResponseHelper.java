package Helper;

import org.json.JSONException;
import org.json.JSONObject;

import Entity.ApiResponse;

public class ApiResponseHelper {
    public static ApiResponse convertResponse(String input)
    {
        ApiResponse response = new ApiResponse();
        try {
            JSONObject object = new JSONObject(input);
            String responseCode = object.getString("uploadResponseCode");
            response.setUploadResponseCode(responseCode);
            response.setMessage(object.getString("message"));
            if (responseCode.equals("SUCCESS")) {
                response.setUserId(object.getString("userid"));
                response.setNumberOfRecords(object.getInt("number"));
                response.setNameOfRecords(object.getString("names"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }
}
