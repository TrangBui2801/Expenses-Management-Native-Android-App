package com.example.expensemanagement.TripManagement.Fragment;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.expensemanagement.R;
import com.example.expensemanagement.TripManagement.ViewModel.ViewTripViewModel;
import com.example.expensemanagement.databinding.FragmentViewTripBinding;

import java.util.ArrayList;
import java.util.List;

import Adapter.RouteViewAdapter;
import Entity.TravelRoute;
import Entity.Trip;
import Helper.SQLiteHelper;

public class ViewTripFragment extends Fragment {

    private ViewTripViewModel mViewModel;
    private FragmentViewTripBinding binding;
    private RouteViewAdapter routeAdapter;
    private List<TravelRoute> travelRoutes = new ArrayList<>();

    public static ViewTripFragment newInstance() {
        return new ViewTripFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // gan model de thao tac voi controller
        mViewModel = new ViewModelProvider(this).get(ViewTripViewModel.class);
        // gan view de thao tac trong controller
        binding = FragmentViewTripBinding.inflate(inflater, container, false);

        // lay cac tham so duoc truyen tu view khac sang (ID truyen tu listview)
        int ID = getArguments().getInt("ID");

        // Set title cho toolbar
        binding.toolbarViewTrip.toolbarTitle.setText("Trip detail");
        // Bat su kien cho cac button tren toolbar
        binding.toolbarViewTrip.backBtn.setOnClickListener(v -> goBack());
        binding.toolbarViewTrip.homeBtn.setOnClickListener(v -> goHome());

        // quan sat su thay doi cua data trong model
        mViewModel.trip.observe(
                getViewLifecycleOwner(),
                trip -> {
                    binding.viewTripType.setText(trip.getType());
                    binding.viewTripName.setText(trip.getName());
                    binding.viewTripDestination.setText(trip.getDestination());
                    binding.viewTripFromDate.setText(trip.getFromDate());
                    binding.viewTripToDate.setText(trip.getToDate());
                    binding.viewTripTransportType.setText(trip.getTransportType());
                    binding.viewTripTransportName.setText(trip.getTransportName());
                    binding.viewRiskAssessment.setText(trip.getRiskAssessment());
                    binding.viewTripDescription.setText(trip.getDescription());
                    binding.viewTotal.setText(String.valueOf(trip.getTotalExpense()));
                    travelRoutes = trip.getTravelRoutes();
                }
        );
        // khoi tao doi tuong de thao tac voi sqlite
        SQLiteHelper helper = new SQLiteHelper(this.getContext());
        Trip result = helper.getTripByID(ID);
        // thay doi "trip" trong view model de cap nhat view
        mViewModel.trip.postValue(result);
        // lay mang cac TravelRoute de in vao recycler view
        travelRoutes = result.getTravelRoutes();

        // Recycler view for route
        RecyclerView listRoutes = binding.listRoute;
        listRoutes.setHasFixedSize(true);
        listRoutes.addItemDecoration(new DividerItemDecoration(
                getContext(),
                (new LinearLayoutManager(getContext()).getOrientation())
        ));
        // quan sat su thay doi cua "route" trong view model de cap nhat view
        mViewModel.routes.observe(
                getViewLifecycleOwner(),
                routes -> {
                    routeAdapter = new RouteViewAdapter(routes);
                    binding.listRoute.setAdapter(routeAdapter);
                    binding.listRoute.setLayoutManager(new LinearLayoutManager(getContext()));
                    if (routes.size() > 0)
                    {
                        binding.listRoute.setVisibility(View.VISIBLE);
                        binding.noRoute.setVisibility(View.GONE);
                    }
                    else
                    {
                        binding.listRoute.setVisibility(View.GONE);
                        binding.noRoute.setVisibility(View.VISIBLE);
                    }
                }
        );
        // gan data vao view model de cap nhat view
        mViewModel.routes.postValue(result.getTravelRoutes());
        // bat su kien cho cac button trong view
        binding.btnEditTrip.setOnClickListener(v -> goToUpdate(ID));
        binding.btnViewAllExpense.setOnClickListener(v -> viewListExpense(ID));
        binding.btnDeleteTrip.setOnClickListener(v -> deleteTrip(ID));
        return binding.getRoot();
    }

    private void deleteTrip(int ID)
    {
        // su dung alert dialog de hien thi pop-up
        // khoi tao bien builder de build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle("Confirm dialog");
        builder.setMessage("Do you want to delete this trip with all expenses?\nThis action cannot be recovered.");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SQLiteHelper helper = new SQLiteHelper(getContext());
                boolean isSuccess = helper.deleteTrip(ID);
                if (isSuccess)
                {
                    Toast.makeText(getContext(), "Successfully delete trip", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(getView()).navigate(R.id.listTripFragment);
                }
                else
                {
                    Toast.makeText(getContext(), "Cannot delete trip", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        // khoi tao dialog dua tren buider
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void viewListExpense(int ID)
    {
        Bundle bundle = new Bundle();
        bundle.putInt("tripID", ID);
        Navigation.findNavController(getView()).navigate(R.id.listExpenseFragment, bundle);
    }

    private void goBack() {
        Navigation.findNavController(getView()).navigate(R.id.listTripFragment);
    }

    private void goHome() {
        Navigation.findNavController(getView()).navigate(R.id.mainFragment);
    }

    private void goToUpdate(int ID)
    {
        Bundle bundle = new Bundle();
        bundle.putInt("ID", ID);
        Navigation.findNavController(getView()).navigate(R.id.addTripFragment, bundle);
    }
}