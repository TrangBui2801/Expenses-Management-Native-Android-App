package com.example.expensemanagement.ExpenseManagement.Fragment;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.expensemanagement.ExpenseManagement.ViewModel.ViewExpenseViewModel;
import com.example.expensemanagement.R;
import com.example.expensemanagement.databinding.FragmentViewExpenseBinding;

import Helper.SQLiteHelper;

public class ViewExpenseFragment extends Fragment {

    private ViewExpenseViewModel mViewModel;
    private FragmentViewExpenseBinding binding;

    public static ViewExpenseFragment newInstance() {
        return new ViewExpenseFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(ViewExpenseViewModel.class);
        binding = FragmentViewExpenseBinding.inflate(inflater, container, false);

        int ID = getArguments().getInt("ID");
        int tripID = getArguments().getInt("tripID");

        binding.toolbarViewExpense.toolbarTitle.setText("Expense detail");
        // Bat su kien cho cac button tren toolbar
        binding.toolbarViewExpense.backBtn.setOnClickListener(v -> goBack(tripID));
        binding.toolbarViewExpense.homeBtn.setOnClickListener(v -> goHome());

        mViewModel.expense.observe(
                getViewLifecycleOwner(),
                expense -> {
                    binding.viewExpenseType.setText(expense.getType());
                    binding.viewExpenseAmount.setText(String.valueOf(expense.getAmount()));
                    binding.viewExpenseDate.setText(expense.getDate());
                    binding.viewExpenseTime.setText(expense.getTime());
                    binding.viewExpenseComment.setText(expense.getComment());
                    Bitmap bitmap = getBitmapByPath(expense.getImageUrl());
                    if (bitmap != null) {
                        binding.viewExpenseImage.setImageBitmap(bitmap);
                    } else {
                        binding.viewExpenseImage.setImageDrawable(getResources().getDrawable(R.drawable.no_image_available));
                    }
                    binding.viewExpenseLocation.setText(expense.getLocation());
                }
        );
        SQLiteHelper sqlHelper = new SQLiteHelper(this.getContext());
        mViewModel.expense.postValue(sqlHelper.getExpenseByID(ID));
        binding.btnEditExpense.setOnClickListener(v -> goToUpdate(ID, tripID));
        binding.btnDeleteExpense.setOnClickListener(v -> deleteExpense(ID, tripID));
        return binding.getRoot();
    }

    private void goBack(int tripID) {
        Bundle bundle = new Bundle();
        bundle.putInt("tripID", tripID);
        Navigation.findNavController(getView()).navigate(R.id.listExpenseFragment, bundle);
    }

    private void goHome() {
        Navigation.findNavController(getView()).navigate(R.id.mainFragment);
    }

    private void goToUpdate(int ID, int tripID) {
        Bundle bundle = new Bundle();
        bundle.putInt("ID", ID);
        bundle.putInt("tripID", tripID);
        Navigation.findNavController(getView()).navigate(R.id.addExpenseFragment, bundle);
    }

    private void deleteExpense(int ID, int tripID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle("Confirm message");
        builder.setMessage("Do you want to delete this expense?\nThis action cannot be recovered");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SQLiteHelper sqlHelper = new SQLiteHelper(getContext());
                boolean isSuccess = sqlHelper.deleteExpense(ID);
                if (isSuccess) {
                    Toast.makeText(getContext(), "Delete expense success", Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putInt("tripID", tripID);
                    Navigation.findNavController(getView()).navigate(R.id.listExpenseFragment, bundle);
                } else {
                    Toast.makeText(getContext(), "Delete expense fail", Toast.LENGTH_SHORT).show();
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
    }

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
}