package com.example.expensemanagement.ExpenseManagement.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import Entity.Expense;

public class ViewExpenseViewModel extends ViewModel {
    public MutableLiveData<Expense> expense = new MutableLiveData<Expense>();
}