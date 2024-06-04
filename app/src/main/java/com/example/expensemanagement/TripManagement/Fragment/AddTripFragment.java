package com.example.expensemanagement.TripManagement.Fragment;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.expensemanagement.R;
import com.example.expensemanagement.TripManagement.ViewModel.AddTripViewModel;
import com.example.expensemanagement.databinding.FragmentAddTripBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Adapter.RouteEditableAdapter;
import Entity.TravelRoute;
import Entity.Trip;
import Helper.SQLiteHelper;

public class AddTripFragment extends Fragment {

    private AddTripViewModel mViewModel;
    private FragmentAddTripBinding binding;
    private final Calendar fromDateCalendar = Calendar.getInstance();
    private final Calendar toDateCalendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private RouteEditableAdapter routeAdapter;
    private List<TravelRoute> travelRoutes;

    public static AddTripFragment newInstance() {
        return new AddTripFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //Check if ID > 0 (or button Update is clicked)
        int ID = getArguments().getInt("ID");

        // gan view model de thao tac trong controller (fragment)
        mViewModel = new ViewModelProvider(requireActivity()).get(AddTripViewModel.class);
        // gan view de thao tac trong controller (fragment)
        binding = FragmentAddTripBinding.inflate(inflater, container, false);

        // Bat su kien cho cac button tren toolbar
        binding.toolbarAddTrip.backBtn.setOnClickListener(v -> cancelEdit(ID));
        binding.toolbarAddTrip.homeBtn.setOnClickListener(v -> goHome());

        // Dropdown
        // Trip type
        // khoi tao 1 list string de hien thi trong dropdown
        List<String> types = new ArrayList<String>();
        // add options
        types.add("Conference");
        types.add("Client Meeting");
        types.add("Business Travel");
        types.add("Travel");
        // trong lap trinh android, khong the dua 1 bien vao trong view ma phai thong qua 1 view khac
        // su dung adapter de convert tu bien sang view
        // khoi tao 1 array adapter voi kieu du lieu truyen vao la string va gan vao view item_spinner
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this.getContext(), R.layout.item_spinner, types);
        // add container view cho cac options
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // set adapter cho dropdown
        binding.sltTripType.setAdapter(typeAdapter);
        // bat su kien cho textview
        binding.sltTripType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // get value tu dropdown
                String selectedItem = types.get(i);
                // kien tra gia tri, neu bang "other" thi show textview "other transport"
//                if (selectedItem.equals("Other")) {
//                    binding.containerOtherTransport.setVisibility(View.VISIBLE);
//                } else {
//                    binding.containerOtherTransport.setVisibility(View.GONE);
//                }
            }
        });

        // Transport
        // khoi tao 1 list string de hien thi trong dropdown
        List<String> items = new ArrayList<String>();
        // add options
        items.add("Personal transport");
        items.add("Public transport");
        // trong lap trinh android, khong the dua 1 bien vao trong view ma phai thong qua 1 view khac
        // su dung adapter de convert tu bien sang view
        // khoi tao 1 array adapter voi kieu du lieu truyen vao la string va gan vao view item_spinner
        ArrayAdapter<String> transportAdapter = new ArrayAdapter<String>(this.getContext(), R.layout.item_spinner, items);
        // add container view cho cac options
        transportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // set adapter cho dropdown
        binding.sltTransport.setAdapter(transportAdapter);
        // bat su kien cho textview
        binding.sltTransport.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // get value tu dropdown
                String selectedItem = items.get(i);
                // kien tra gia tri, neu bang "other" thi show textview "other transport"
//                if (selectedItem.equals("Other")) {
//                    binding.containerOtherTransport.setVisibility(View.VISIBLE);
//                } else {
//                    binding.containerOtherTransport.setVisibility(View.GONE);
//                }
            }
        });

        if (ID > 0) {
            binding.toolbarAddTrip.toolbarTitle.setText("Edit trip");
            // get data tu sqlite voi id truyen tu view khac sang
            SQLiteHelper sqLiteHelper = new SQLiteHelper(this.getContext());
            Trip trip = sqLiteHelper.getTripByID(ID);
            // thao tac voi view thong qua bien "binding"
            binding.txtTripName.setText(trip.getName());
            binding.txtTripDestination.setText(trip.getDestination());
            binding.txtTripFromDate.setText(trip.getFromDate());
            binding.txtTripToDate.setText(trip.getToDate());
            // kiem tra gia tri cua transport trong trip
            binding.sltTripType.setSelection(items.indexOf(trip.getTransportType()));
            binding.sltTransport.setSelection(items.indexOf(trip.getTransportType()));
            binding.txtTransportName.setText(trip.getTransportName());
            binding.txtTripDescription.setText(trip.getDescription());
            travelRoutes = trip.getTravelRoutes();
            binding.btnAddTrip.setText("Update");
            binding.swtRiskAssessment.setChecked(trip.getRiskAssessment().toUpperCase().equals("YES") ? true : false);
        } else {
            binding.toolbarAddTrip.toolbarTitle.setText("Add trip");
            // set gia tri default cho view khi them moi
            int day = fromDateCalendar.get(Calendar.DAY_OF_MONTH);
            int month = fromDateCalendar.get(Calendar.MONTH);
            int year = fromDateCalendar.get(Calendar.YEAR);
            binding.txtTripFromDate.setText(day + "/" + (month + 1) + "/" + year);
            binding.txtTripToDate.setText(day + "/" + (month + 1) + "/" + year);
            travelRoutes = new ArrayList<>();
        }

        //Date picker

        //Check if from date and to date are set up (when update trip), then set the time for calendar
        String tripCurrentFromDate = binding.txtTripFromDate.getText().toString();
        String tripCurrentToDate = binding.txtTripToDate.getText().toString();
        if (ID > 0) {
            if (!tripCurrentFromDate.equals("")) {
                try {
                    // convert chuoi sang date
                    Date date = dateFormat.parse(tripCurrentFromDate);
                    // set date cho calendar
                    fromDateCalendar.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (!tripCurrentToDate.equals("")) {
                try {
                    Date date = dateFormat.parse(tripCurrentToDate);
                    toDateCalendar.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        //Get fromdate data
        int fDay = fromDateCalendar.get(Calendar.DAY_OF_MONTH);
        int fMonth = fromDateCalendar.get(Calendar.MONTH);
        int fYear = fromDateCalendar.get(Calendar.YEAR);

        //Get todate data
        int tDay = toDateCalendar.get(Calendar.DAY_OF_MONTH);
        int tMonth = toDateCalendar.get(Calendar.MONTH);
        int tYear = toDateCalendar.get(Calendar.YEAR);

        // khoi tao 2 datepicker dialog voi su kien khi click vaof button "ok" thi set text cho tung text view
        DatePickerDialog fDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                binding.txtTripFromDate.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);

            }
        }, fYear, fMonth, fDay);

        DatePickerDialog tDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                binding.txtTripToDate.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
            }
        }, tYear, tMonth, tDay);

        // bat su kien cho tung textview (show datepicker dialog)
        binding.txtTripFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fDialog.show();
            }
        });
        binding.txtTripToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tDialog.show();
            }
        });

        // Recycler view for route
        // khoi tao 1 recycler view de thao tac voi view
        RecyclerView listRoutes = binding.listRoute;
        listRoutes.setHasFixedSize(true);
        // quan sat thay doi cua bien "route" ben view model
        mViewModel.routes.observe(
                getViewLifecycleOwner(),
                routes -> {
                    routeAdapter = new RouteEditableAdapter(routes);
                    binding.listRoute.setAdapter(routeAdapter);
                    binding.listRoute.setLayoutManager(new LinearLayoutManager(getContext()));
                    // check size cua bien route ben view model
                    // size = 0 thi show textview "noroute", nguoc lai thi show recycler view
                    if (routes.size() > 0) {
                        binding.listRoute.setVisibility(View.VISIBLE);
                        binding.noRoute.setVisibility(View.GONE);
                    } else {
                        binding.listRoute.setVisibility(View.GONE);
                        binding.noRoute.setVisibility(View.VISIBLE);
                    }
                }
        );
        // gan data vao view model
        mViewModel.routes.postValue(travelRoutes);

        // Add Route Button
        // bat su kien cho button "add route"
        binding.btnAddRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // lay data fromdate va todate tu view cha de set limmit cho datepicker trong dialog "add route"
                String txtFromDate = binding.txtTripFromDate.getText().toString();
                String txtToDate = binding.txtTripToDate.getText().toString();
                // kiem tra 2 bien da dc set data?
                boolean isSetAllDate = true;
                String error = "";
                if (txtFromDate.equals("") || txtFromDate == null) {
                    error += "From date is required to create route.";
                    error += "\n";
                    isSetAllDate = false;
                }
                if (txtToDate.equals("") || txtToDate == null) {
                    error += "To date is required to create route.";
                    error += "\n";
                    isSetAllDate = false;
                }
                if (isSetAllDate) {
                    showDialog(txtFromDate, txtToDate);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.custom_dialog);
                    builder.setCancelable(true);
                    builder.setTitle("Warning");
                    builder.setMessage(error);
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        binding.btnAddTrip.setOnClickListener(v -> addOrUpdate(ID));
        binding.btnCancelAddTrip.setOnClickListener(v -> cancelEdit(ID));
        return binding.getRoot();
    }

    private void cancelEdit(int ID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle("Warning");
        if (ID > 0)
        {
            builder.setMessage("Do you want to cancel update trip?");
        }
        else
        {
            builder.setMessage("Do you want to cancel add new trip?");
        }
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                viewListTrip();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void goHome() {
        Navigation.findNavController(getView()).navigate(R.id.mainFragment);
    }

    private void goBack() {
        Navigation.findNavController(getView()).navigate(R.id.listTripFragment);
    }

    private void showDialog(String fromDate, String toDate) {
        // khoi tao dialog
        final Dialog dialog = new Dialog(getContext(), R.style.custom_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        // set view cho dialog
        dialog.setContentView(R.layout.add_route_dialog);
        // khoi tao calendar cho dialog
        final Calendar dialogCalendar = Calendar.getInstance();
        int day = dialogCalendar.get(Calendar.DAY_OF_MONTH);
        int month = dialogCalendar.get(Calendar.MONTH);
        int year = dialogCalendar.get(Calendar.YEAR);
        // khoi tao cac bien de thao tac voi view trong dialog
        EditText routeDate = dialog.findViewById(R.id.txtRouteDate);
        EditText routeDestination = dialog.findViewById(R.id.txtRouteDestination);
        Button addRouteBtn = dialog.findViewById(R.id.btnAddRouteDialog);
        Button cancelBtn = dialog.findViewById(R.id.btnCancelRouteDialog);
        // khoi tao dialog va bat su kien set text cho textview trong dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                routeDate.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
            }
        }, year, month, day);
        try {
            // set mindate va maxdate cho dialog theo fromdate vaf todate
            datePickerDialog.getDatePicker().setMinDate(dateFormat.parse(fromDate).getTime());
            datePickerDialog.getDatePicker().setMaxDate(dateFormat.parse(toDate).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // bat du kien cho textview "routedate" va show datepicker dialog
        routeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
        // bat su kien cho button add trong dialog
        addRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // validate data
                boolean isValid = true;
                String txtRouteDestination = routeDestination.getText().toString();
                if (txtRouteDestination.equals("") || txtRouteDestination == null) {
                    isValid = false;
                    routeDestination.setError("Destination cannot be blank");
                } else {
                    routeDestination.setError(null);
                }
                String txtRouteDate = routeDate.getText().toString();
                if (txtRouteDate.equals("") || txtRouteDate == null) {
                    isValid = false;
                    routeDate.setError("Date cannot be blank");
                } else {
                    routeDate.setError(null);
                }
                if (isValid) {
                    // neu data da validated thi khoi tao bien TravelRoute de luu data
                    TravelRoute route = new TravelRoute(txtRouteDate, txtRouteDestination);
                    List<TravelRoute> temp = new ArrayList<>();
                    // lay value tu view model (addtripviewmodel)
                    temp = mViewModel.routes.getValue();
                    // add new data vao trong mang
                    temp.add(route);
                    // set lai value cho view model
                    mViewModel.routes.setValue(temp);
                    // thong bao su thay doi data cho recycler view
                    routeAdapter.notifyDataSetChanged();
                    // an dialog
                    dialog.dismiss();
                    // thong bao them moi thanh cong
                    Toast.makeText(getContext(), "Add new route success", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // dong dialog
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        // show dialog
        dialog.show();
    }

    private void addOrUpdate(int ID) {
        // validate data
        boolean isValidate = true;
        String riskAssessment = "";
        // get data tu switch
        boolean isRequireRiskAssessment = binding.swtRiskAssessment.isChecked();
        if (isRequireRiskAssessment) {
            riskAssessment = "Yes";
        } else {
            riskAssessment = "No";
        }

        String tripType = binding.sltTripType.getText().toString();
        if (tripType.equals("") || tripType == null)
        {
            binding.sltTripType.setError("Trip type cannot be blank");
            isValidate = false;
        }
        else
        {
            binding.sltTripType.setError(null);
        }
        //
        String tripName = binding.txtTripName.getText().toString();
        if (tripName.trim().equals("") || tripName == null) {
            binding.txtTripName.setError("Trip name cannot be blank");
            isValidate = false;
        } else {
            binding.txtTripName.setError(null);
        }
        String tripDestination = binding.txtTripDestination.getText().toString();
        if (tripDestination.trim().equals("") || tripDestination == null) {
            binding.txtTripDestination.setError("Destination cannot be blank");
            isValidate = false;
        } else {
            binding.txtTripDestination.setError(null);
        }
        String tripFromDate = binding.txtTripFromDate.getText().toString();
        String tripToDate = binding.txtTripToDate.getText().toString();
        if (tripFromDate.trim().equals("") || tripFromDate == null) {
            binding.txtTripFromDate.setError("Trip start date cannot be blank");
            isValidate = false;
        } else {
            binding.txtTripFromDate.setError(null);
        }
        if (tripToDate.trim().equals("") || tripToDate == null) {
            binding.txtTripFromDate.setError("Trip end date cannot be blank");
            isValidate = false;
        } else {
            binding.txtTripFromDate.setError(null);
        }
        // check data validated?
        if (isValidate) {
            // khoi tao 2 bien kieu Data
            Date fromDate = new Date();
            Date toDate = new Date();
            try {
                // kiem tra fromdate da dung format?
                fromDate = dateFormat.parse(tripFromDate);
            } catch (ParseException e) {
                binding.txtTripFromDate.setError("Trip start date is not valid");
                isValidate = false;
            }
            try {
                toDate = dateFormat.parse(tripToDate);
            } catch (ParseException e) {
                binding.txtTripToDate.setError("Trip end date is not valid");
                isValidate = false;
            }
            // kiem tra fromdate < todate?
            if (isValidate) {
                if (fromDate.compareTo(toDate) > 0) {
                    isValidate = false;
                    binding.txtTripFromDate.setError("Trip start date must be before end date");
                    binding.txtTripToDate.setError("Trip end date must be after start date");
                }
            }
        }
        String transportType = binding.sltTransport.getText().toString();
        if (transportType.trim().equals("") || transportType == null) {
            binding.sltTransport.setError("Transport type cannot be blank");
            isValidate = false;
        } else {
            binding.sltTransport.setError(null);
        }

        String transportName = binding.txtTransportName.getText().toString();
        if (transportName.trim().equals("") || transportName == null) {
            binding.txtTransportName.setError("Transport name cannot be blank");
            isValidate = false;
        } else {
            binding.txtTransportName.setError(null);
        }

        String tripDescription = binding.txtTripDescription.getText().toString();
        // toan bo data da validated?
        if (isValidate) {
            // khoi tao bien Trip
            Trip trip = new Trip(tripType, tripName, tripDestination, tripFromDate, tripToDate, transportType, transportName, mViewModel.routes.getValue(), riskAssessment, tripDescription);
            // khoi tao bien SQLiteHelper
            SQLiteHelper helper = new SQLiteHelper(this.getContext());
            final boolean[] isSuccess = {false};
            if (ID > 0) {
                // id > 0 (update) thi goi ham update() trong SQLiteHelper
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setTitle("Warning");
                builder.setMessage("Do you want to update this trip?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        trip.setID(ID);
                        isSuccess[0] = helper.editTrip(trip);
                        if (isSuccess[0]) {
                            Toast.makeText(getContext(), "Update trip success", Toast.LENGTH_SHORT).show();
                            viewListTrip();
                        } else {
                            Toast.makeText(getContext(), "Update trip fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                // id = 0 (add new) thi goi ham insert() trong SQLiteHelper
                long newID = helper.addTrip(trip);
                isSuccess[0] = newID > -1;
                if (isSuccess[0]) {
                    Toast.makeText(this.getContext(), "Add new trip success", Toast.LENGTH_SHORT).show();
                    viewListTrip();
                } else {
                    Toast.makeText(this.getContext(), "Add new trip fail", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void viewListTrip() {
        Navigation.findNavController(getView()).navigate(R.id.listTripFragment);
    }
}