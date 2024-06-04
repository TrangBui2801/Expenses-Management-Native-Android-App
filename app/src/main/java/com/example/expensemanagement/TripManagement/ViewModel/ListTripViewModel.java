package com.example.expensemanagement.TripManagement.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.ArrayList;

import Entity.Trip;

public class ListTripViewModel extends ViewModel {
    public MutableLiveData<List<Trip>> listTrips = new MutableLiveData<List<Trip>>();
}