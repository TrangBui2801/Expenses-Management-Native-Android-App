package com.example.expensemanagement.TripManagement.ViewModel;

import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Optional;

import Entity.TravelRoute;
import Entity.Trip;

public class ViewTripViewModel extends ViewModel {
    public MutableLiveData<Trip> trip = new MutableLiveData<Trip>();
    public MutableLiveData<List<TravelRoute>> routes = new MutableLiveData<>();
}