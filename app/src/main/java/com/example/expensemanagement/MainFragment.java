package com.example.expensemanagement;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.expensemanagement.databinding.FragmentMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Entity.ApiResponse;
import Entity.Trip;
import Helper.ApiResponseHelper;
import Helper.SQLiteHelper;

public class MainFragment extends Fragment {

    private MainViewModel mViewModel;
    private FragmentMainBinding binding;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        binding = FragmentMainBinding.inflate(inflater, container, false);
//        binding.btnAdd.setOnClickListener(v -> redirect("ADD"));
        binding.btnView.setOnClickListener(v -> redirect("VIEW"));
        binding.btnUpload.setOnClickListener(v -> uploadData());
        return binding.getRoot();
    }

    private void uploadData() {
        // show progress bar
        binding.progressBar.setVisibility(View.VISIBLE);
        // Khoi tao queue de gui request
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        // Lay data
        SQLiteHelper helper = new SQLiteHelper(getContext());
        List<Trip> trips = helper.getTrips(false, new ArrayList<Integer>());
        String url = "https://cw1786.onrender.com/sendPayLoad";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // bat su kien hung response tu api
                ApiResponse apiResponse = ApiResponseHelper.convertResponse(response);
                // an progress bar
                binding.progressBar.setVisibility(View.GONE);
                // Lay upload Response code
                String responseCode = apiResponse.getUploadResponseCode();
                // show dialog
                if (responseCode.equals("SUCCESS")) {
                    Dialog dialog = new Dialog(getContext(), R.style.custom_dialog);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(true);
                    LayoutInflater inflater = requireActivity().getLayoutInflater();
                    View view = inflater.inflate(R.layout.upload_success_dialog, null);
                    TextView userId = view.findViewById(R.id.txtSuccessUserId);
                    TextView numberOfRecords = view.findViewById(R.id.txtSuccessNumber);
                    TextView nameOfTrips = view.findViewById(R.id.txtSuccessName);
                    TextView message = view.findViewById(R.id.txtSuccessMessage);
                    Button closeBtn = view.findViewById(R.id.btnCancelSuccessDialog);
                    userId.setText("User ID: " + apiResponse.getUserId());
                    numberOfRecords.setText("Record number: " + String.valueOf(apiResponse.getNumberOfRecords()));
                    nameOfTrips.setText("Record names: " + apiResponse.getNameOfRecords());
                    message.setText("Message: " + apiResponse.getMessage());
                    closeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setContentView(view);
                    dialog.show();
                } else {
                    Dialog dialog = new Dialog(getContext(), R.style.custom_dialog);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(true);
                    LayoutInflater inflater = requireActivity().getLayoutInflater();
                    View view = inflater.inflate(R.layout.upload_error_dialog, null);
                    TextView message = view.findViewById(R.id.txtErrorMessage);
                    Button closeBtn = view.findViewById(R.id.btnCancelErrorDialog);
                    message.setText("Message: " + apiResponse.getMessage());
                    closeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setContentView(view);
                    dialog.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            // Show error
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error: " + error);
                binding.progressBar.setVisibility(View.GONE);
                Dialog dialog = new Dialog(getContext(), R.style.custom_dialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                LayoutInflater inflater = requireActivity().getLayoutInflater();
                View view = inflater.inflate(R.layout.upload_error_dialog, null);
                TextView message = view.findViewById(R.id.txtErrorMessage);
                Button closeBtn = view.findViewById(R.id.btnCancelErrorDialog);
                message.setText("Message: Error while uploading data");
                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.setContentView(view);
                dialog.show();
            }
        }) {

            // update Content type of body
            @Override
            public String getBodyContentType() {
                Map<String, String> pars = new HashMap<>();
                pars.put("Content-Type", "application/x-www-form-urlencoded");
                return "application/x-www-form-urlencoded";
            }

            @Override
            protected Map<String, String> getParams() {
                // set up body
                Map<String, String> params = new HashMap<String, String>();
                // Khoi tao 1 json array
                JSONArray jsonArray = new JSONArray();
                // Duyet cac trip va cap nhat Json array
                for (Trip trip : trips) {
                    JSONObject temp = new JSONObject();
                    boolean isConvertDataSuccess = false;
                    try {
                        temp.put("type", trip.getType());
                        temp.put("name", trip.getName());
                        temp.put("fromDate", trip.getFromDate());
                        temp.put("toDate", trip.getToDate());
                        temp.put("route", trip.getTravelRoutes());
                        temp.put("transportType", trip.getTransportType());
                        temp.put("transportName", trip.getTransportName());
                        temp.put("total", trip.getTotalExpense());
                        isConvertDataSuccess = true;
                    } catch (JSONException e) {
                        isConvertDataSuccess = false;
                        e.printStackTrace();
                    }
                    if (isConvertDataSuccess) {
                        jsonArray.put(temp);
                    }
                }
                // Khoi tao body cho request
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("userId", "trangbtgch211176");
                    jsonObject.put("detailList", jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Xu ly body
                String payload = jsonObject.toString().replace("\\", "");
                params.put("jsonpayload", payload);
                return params;
            }
        };
        // Gui request
        requestQueue.add(stringRequest);
    }

    private void redirect(String destination) {
        switch (destination) {
            case "ADD": {
                Bundle bundle = new Bundle();
                bundle.putInt("ID", 0);
                Navigation.findNavController(getView()).navigate(R.id.addTripFragment, bundle);
                break;
            }
            case "VIEW": {
                Navigation.findNavController(getView()).navigate(R.id.listTripFragment);
                break;
            }
            default: {
                break;
            }
        }
    }

}