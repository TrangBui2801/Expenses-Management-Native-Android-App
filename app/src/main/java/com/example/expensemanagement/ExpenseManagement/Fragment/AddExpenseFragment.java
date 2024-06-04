package com.example.expensemanagement.ExpenseManagement.Fragment;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.expensemanagement.ExpenseManagement.ViewModel.AddExpenseViewModel;
import com.example.expensemanagement.R;
import com.example.expensemanagement.databinding.FragmentAddExpenseBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Entity.Expense;
import Entity.Trip;
import Helper.SQLiteHelper;

public class AddExpenseFragment extends Fragment {

    private AddExpenseViewModel mViewModel;
    private FragmentAddExpenseBinding binding;

    final Calendar timePickerCalendar = Calendar.getInstance();
    final Calendar datePickerCalendar = Calendar.getInstance();
    private static final String dateFormatString = "dd/MM/yyyy";
    private static final String timeFormatString = "HH:mm";

    FusedLocationProviderClient mFusedLocationClient;
    private String expenseLocation;

    private String imageUrl = "";
    private String takenImageUrl = "";

    public static AddExpenseFragment newInstance() {
        return new AddExpenseFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        int ID = getArguments().getInt("ID");
        int tripID = getArguments().getInt("tripID");

        // get location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        getLastLocation();

        mViewModel = new ViewModelProvider(this).get(AddExpenseViewModel.class);
        binding = FragmentAddExpenseBinding.inflate(inflater, container, false);

        // Bat su kien cho cac button tren toolbar
        binding.toolbarAddExpense.backBtn.setOnClickListener(v -> cancelEdit(ID, tripID));
        binding.toolbarAddExpense.homeBtn.setOnClickListener(v -> goHome());


        //Code for dropdown
        SQLiteHelper sqLiteHelper = new SQLiteHelper(this.getContext());
        Trip trip = sqLiteHelper.getTripByID(tripID);
        List<String> items = new ArrayList<String>();
        items.add("Travel");
        items.add("Food");
        items.add("Other");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), R.layout.item_spinner, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.sltExpenseType.setAdapter(adapter);
        binding.sltExpenseType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedType = items.get(i);
                if (selectedType.equals("Other")) {
                    binding.containerOtherType.setVisibility(View.VISIBLE);
                } else {
                    binding.containerOtherType.setVisibility(View.GONE);
                }
            }
        });
        // cập nhật view dựa trên id
        if (ID != 0) {
            binding.toolbarAddExpense.toolbarTitle.setText("Edit expense");
            Expense expense = sqLiteHelper.getExpenseByID(ID);
            if (expense.getType().equals("Travel") || expense.getType().equals("Food")) {
                binding.sltExpenseType.setText(adapter.getItem(items.indexOf(expense.getType())), false);
                binding.containerOtherType.setVisibility(View.GONE);
            } else {
                binding.sltExpenseType.setText(adapter.getItem(items.indexOf("Other")), false);
                binding.containerOtherType.setVisibility(View.VISIBLE);
                binding.txtOtherType.setText(expense.getType());
            }
            binding.txtExpenseAmount.setText(String.valueOf(expense.getAmount()));
            binding.txtExpenseDate.setText(expense.getDate());
            binding.txtExpenseComment.setText(expense.getComment());
            binding.txtExpenseTime.setText(expense.getTime());
            binding.btnAddExpense.setText("Update");
            Bitmap bitmap = getBitmapByPath(expense.getImageUrl());
            if (bitmap != null) {
                binding.viewExpenseImage.setImageBitmap(bitmap);
                imageUrl = expense.getImageUrl();
            } else {
                binding.viewExpenseImage.setImageDrawable(getResources().getDrawable(R.drawable.no_image_available));
            }
        } else {
            binding.toolbarAddExpense.toolbarTitle.setText("Add expense");
            binding.containerOtherType.setVisibility(View.GONE);
            binding.viewExpenseImage.setImageDrawable(getResources().getDrawable(R.drawable.no_image_available));
        }
        String expenseCurrentTime = binding.txtExpenseTime.getText().toString();
        //Date picker
        int year = timePickerCalendar.get(Calendar.YEAR);
        int month = timePickerCalendar.get(Calendar.MONTH);
        int day = timePickerCalendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                binding.txtExpenseDate.setText(d + "/" + (m + 1) + "/" + y);
            }
        }, year, month, day);
        try {
            SimpleDateFormat df = new SimpleDateFormat(dateFormatString);
            datePickerDialog.getDatePicker().setMinDate(df.parse(trip.getFromDate()).getTime());
            datePickerDialog.getDatePicker().setMaxDate((df.parse(trip.getToDate()).getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        binding.txtExpenseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
        // Time picker
        if (ID > 0 && !expenseCurrentTime.equals("")) {
            SimpleDateFormat format = new SimpleDateFormat(timeFormatString);
            try {
                Date date = format.parse(expenseCurrentTime);
                timePickerCalendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        int hour = timePickerCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = timePickerCalendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                binding.txtExpenseTime.setText(hour + ":" + minute);
            }
        }, hour, minute, true);
        binding.txtExpenseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog.show();
            }
        });
        binding.btnTakePicture.setOnClickListener(v -> requestCameraPermission());
        binding.btnAddExpense.setOnClickListener(v -> addOrUpdate(ID, tripID));
        binding.btnCancelAddExpense.setOnClickListener(v -> cancelEdit(ID, tripID));
        return binding.getRoot();
    }

    // thao tác vs db
    private void addOrUpdate(int ID, int tripID) {
        boolean isValidate = true;
        String type = binding.sltExpenseType.getText().toString();
        if (type == null || type.equals("")) {
            binding.sltExpenseType.setError("Type cannot be blank");
            isValidate = false;
        } else {
            binding.sltExpenseType.setError(null);
            if (type.equals("Other")) {
                type = binding.txtOtherType.getText().toString();
                if (type == null || type.equals("")) {
                    binding.txtOtherType.setError("Type cannot be blank");
                    isValidate = false;
                } else {
                    binding.txtOtherType.setError(null);
                }
            }
        }
        long baseAmount = 0;
        String txtAmount = binding.txtExpenseAmount.getText().toString();
        if (String.valueOf(txtAmount) == null || String.valueOf(txtAmount).equals("")) {
            binding.txtExpenseAmount.setError("Amount cannot be blank");
            isValidate = false;
        } else {
            baseAmount = Long.parseLong(txtAmount);
            binding.txtExpenseAmount.setError(null);
        }
        String date = binding.txtExpenseDate.getText().toString();
        if (date == null || date.equals("")) {
            binding.txtExpenseDate.setError("Date cannot be blank");
            isValidate = false;
        } else {
            binding.txtExpenseDate.setError(null);
        }
        String time = binding.txtExpenseTime.getText().toString();
        if (time == null || time.equals("")) {
            binding.txtExpenseTime.setError("Time cannot be blank");
            isValidate = false;
        } else {
            binding.txtExpenseTime.setError(null);
        }
        String comment = binding.txtExpenseComment.getText().toString();
        if (isValidate) {
            Expense expense = new Expense(tripID, type, baseAmount, date, time, comment, imageUrl, expenseLocation);
            SQLiteHelper sqLiteHelper = new SQLiteHelper(this.getContext());
            final boolean[] isSuccess = {false};
            if (ID > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setTitle("Warning");
                builder.setMessage("Do you want to update this expense?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        expense.setID(ID);
                        isSuccess[0] = sqLiteHelper.editExpense(expense);
                        if (isSuccess[0]) {
                            Toast.makeText(getContext(), "Update expense success", Toast.LENGTH_SHORT).show();
                            viewListExpense(tripID);
                        } else {
                            Toast.makeText(getContext(), "Update expense fail", Toast.LENGTH_SHORT).show();
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
                long newID = sqLiteHelper.addExpense(expense);
                isSuccess[0] = newID > -1;
                if (isSuccess[0]) {
                    Toast.makeText(this.getContext(), "Add new expense success", Toast.LENGTH_SHORT).show();
                    viewListExpense(tripID);
                } else {
                    Toast.makeText(this.getContext(), "Add expense fail", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void viewListExpense(int tripID) {
        Bundle bundle = new Bundle();
        bundle.putInt("tripID", tripID);
        Navigation.findNavController(getView()).navigate(R.id.listExpenseFragment, bundle);
    }

    private void goHome() {
        Navigation.findNavController(getView()).navigate(R.id.mainFragment);
    }

    private void cancelEdit(int ID, int tripID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle("Warning");
        if (ID > 0) {
            builder.setMessage("Do you want to cancel update expense?");
        } else {
            builder.setMessage("Do you want to cancel add new expense?");
        }
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                viewListExpense(tripID);
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

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            expenseLocation = "Latitude: " + String.valueOf(location.getLatitude()) + "\nLongitude: " + String.valueOf(location.getLongitude());
                        }
                    }
                });
            } else {
                Toast.makeText(getContext(), "Please turn on your location service", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, 2801);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

    // Check and get camera permission
    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 2800);
        } else {
            openDeviceCamera();
        }
    }

    // Get result of permission when user click on request permission dialog
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 2800) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openDeviceCamera();
            } else {
                Toast.makeText(getContext(), "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 2801) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    // Open camera on device
    private void openDeviceCamera() {
        // Khởi tạo 1 intent (activity mới)
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Gán extra output là URL lưu ảnh
        camera.putExtra(MediaStore.EXTRA_OUTPUT, generateImageUri());
        // Chuyển sang activity mới và lấy result của activity khi activity kết thúc và chuyển lại
        startActivityForResult(camera, 2800);
    }

    // Khởi tạo URI ảnh
    public Uri generateImageUri() {
        // Store image in dcim (thẻ nhớ)
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "image" + new Date().getTime() + ".png");
        Uri imgUri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", file);
        imageUrl = file.getAbsolutePath();
        return imgUri;
    }

    // Convert ảnh (bitmap) từ đường dẫn lưu trong db
    public Bitmap getBitmapByPath(String path) {
        try {
            // Decode image size
            BitmapFactory.Options bitMapFactoryOptions = new BitmapFactory.Options();
            bitMapFactoryOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, bitMapFactoryOptions);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;
            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (bitMapFactoryOptions.outWidth / scale / 2 >= REQUIRED_SIZE && bitMapFactoryOptions.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;
            // Decode with inSampleSize
            BitmapFactory.Options bitMapFactoryOptions2 = new BitmapFactory.Options();
            bitMapFactoryOptions2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, bitMapFactoryOptions2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy data từ intent (Khi activity chụp ảnh kết thúc)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 2800 && resultCode == getActivity().RESULT_OK) {
            takenImageUrl = imageUrl;
            Bitmap image = getBitmapByPath(takenImageUrl);
            binding.viewExpenseImage.setImageBitmap(image);
        }
    }
}