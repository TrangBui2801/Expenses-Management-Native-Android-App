package com.example.expensemanagement.ExpenseManagement.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import Entity.Expense;

public class ListExpenseViewModel extends ViewModel {
    public MutableLiveData<List<Expense>> listExpenses = new MutableLiveData<List<Expense>>();
}