package com.example.expensemanagement.TripManagement.ViewModel;

import android.app.DatePickerDialog;
import android.view.View;
import android.widget.DatePicker;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Entity.TravelRoute;
import Entity.Trip;

public class AddTripViewModel extends ViewModel {
    public MutableLiveData<Trip> trip = new MutableLiveData<>();
    public static MutableLiveData<List<TravelRoute>> routes = new MutableLiveData<>();

    private Calendar calendar = Calendar.getInstance();

    public static void removeRoute(TravelRoute route)
    {
        List<TravelRoute> temp = routes.getValue();
        temp.remove(temp.indexOf(route));
        routes.postValue(temp);
    }

    public static void updateRoute(TravelRoute route, String date, String destination)
    {
        List<TravelRoute> temp = routes.getValue();
        TravelRoute newData = new TravelRoute(date, destination);
        temp.set(temp.indexOf(route), newData);
        routes.postValue(temp);
    }
}