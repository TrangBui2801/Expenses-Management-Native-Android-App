package com.example.expensemanagement.TripManagement.Fragment;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.expensemanagement.R;
import com.example.expensemanagement.TripManagement.ViewModel.ListTripViewModel;
import com.example.expensemanagement.databinding.FragmentListTripBinding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import Adapter.TripAdapter;
import Entity.Trip;
import Helper.SQLiteHelper;

public class ListTripFragment extends Fragment implements TripAdapter.ListItemListener{

    private ListTripViewModel mViewModel;
    private FragmentListTripBinding binding;
    private TripAdapter adapter;
    private final boolean[] menuOpened = {false};
    private final boolean[] isShowDeleteIcons = {false};
    private boolean isSearch;
    private List<Integer> checkedItemIDs = new ArrayList<>();
    private int totalRecord;
    private List<Integer> totalSearchRecord = new ArrayList<>();

    public static ListTripFragment newInstance() {
        return new ListTripFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // gan view model de thao tac trong controller (fragment)
        mViewModel = new ViewModelProvider(this).get(ListTripViewModel.class);
        // gan view de thao tac trong controller (fragment)
        binding = FragmentListTripBinding.inflate(inflater, container, false);

        // Set title cho toolbar
        binding.toolbarListTrip.toolbarTitle.setText("List trips");
        // Set su kien cho cac button tren toolbar
        binding.toolbarListTrip.backBtn.setOnClickListener(v -> goBack());
        binding.toolbarListTrip.homeBtn.setOnClickListener(v -> goHome());

        // Set su kien khi search
        binding.txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.txtSearch.setIconified(false);
            }
        });
        binding.txtSearch.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchTrip(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchTrip(s);
                return true;
            }
        });

        // Show cac icon checkbox, Delete all va cancel khi an vao menu con
        binding.btnShowDeleteIcons.setOnClickListener(v -> showOrHideDeleteIcons());
        // Bat su kien check all item trong recycler view
        binding.checkDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checkboxValue = binding.checkDeleteAll.isChecked();
                updateAllCheckboxStatus(checkboxValue);
            }
        });
        // Bat su kien an nut cancel delete
        binding.btnCancelDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideMenuAndDeleteIcons();
                checkedItemIDs.clear();
            }
        });
        // Bat su kien an nut delete
        binding.btnDeleteTrips.setOnClickListener(v -> deleteTrips());
        // khoi tao recycler view de thao tac voi recycler view trong view
        RecyclerView recyclerView = binding.listItem;
        recyclerView.setHasFixedSize(true);
        // quan sat su thay doi cua bien trong view model
        mViewModel.listTrips.observe(
                getViewLifecycleOwner(),
                trips -> {
                    binding.recordNumber.setText("List trips (" + trips.size() + ")");
                    // khoi tao va set adapter cho recycler view
                    adapter = new TripAdapter(trips, this);
                    if (trips.size() == 0)
                    {
                        binding.listItem.setVisibility(View.INVISIBLE);
                        binding.emptyView.setVisibility(View.VISIBLE);
                    }
                    // show recycler view
                    else
                    {
                        for (Trip trip: trips)
                        {
                            updateCheckedList(trip.getID(), trip.isDeleteCheckboxChecked());
                        }
                        binding.listItem.setVisibility(View.VISIBLE);
                        binding.emptyView.setVisibility(View.INVISIBLE);
                        binding.listItem.setAdapter(adapter);
                        binding.listItem.setLayoutManager(new LinearLayoutManager(getActivity()));
                        binding.checkDeleteAll.setChecked(!isSearch ? checkedItemIDs.size() == totalRecord : totalSearchRecord.size() != 0 && checkedItemIDs.containsAll(totalSearchRecord));
                    }
                }
        );
        // get data tu sqlite
        SQLiteHelper helper = new SQLiteHelper(getParentFragment().getActivity());
        // set data vao view model
        List<Trip> result = helper.getTrips(isShowDeleteIcons[0], checkedItemIDs);
        totalRecord = result.size();
        mViewModel.listTrips.postValue(result);
        // Menu button event
        // Bắt sự kiện cho Menu button
        binding.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuOpened[0] = !menuOpened[0];
                if (menuOpened[0])
                {
                    expandMenu();
                }
                else
                {
                    minimizeMenu();
                }
            }
        });
        binding.btnAddTrip.setOnClickListener(v -> goToAddNew());
        return binding.getRoot();
    }

    // update list checked item (list xóa)
    private void updateCheckedList(int id, boolean deleteCheckboxChecked) {
        // Kiểm tra ID tồn tại trong list checked item (list xóa)
        boolean isIdExist = checkedItemIDs.contains(id);
        // Nếu ID tồn tại và trạng thái của nó = false thì xóa khỏi list checked item (list xóa)
        if (isIdExist && !deleteCheckboxChecked)
        {
            checkedItemIDs.remove(checkedItemIDs.indexOf(id));
        }
        // Nếu ID chưa tồn tại và trạng thái của nó = true thì thêm vào list checked item (list xóa)
        else if (!isIdExist && deleteCheckboxChecked)
        {
            checkedItemIDs.add(id);
        }
    }

    private void hideMenuAndDeleteIcons()
    {
        minimizeMenu();
        hideDeleteIcons();
        updateShowTripCheckboxes(false);
        updateAllCheckboxStatus(false);
        binding.checkDeleteAll.setChecked(false);
        isShowDeleteIcons[0] = false;
        menuOpened[0] = false;
    }

    // Xóa nhiều trip
    private void deleteTrips() {
        List<Integer> tripIDs = new ArrayList<>();
        for (Trip trip: mViewModel.listTrips.getValue())
        {
            if (trip.isDeleteCheckboxChecked())
            {
                tripIDs.add(trip.getID());
            }
        }
        if (tripIDs.size() > 0)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setCancelable(true);
            builder.setTitle("Warning");
            builder.setMessage("Do you want to delete " + tripIDs.size() + " items?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SQLiteHelper helper = new SQLiteHelper(getContext());
                    boolean isSuccess = helper.deleteTrips(tripIDs);
                    if (isSuccess)
                    {
                        Toast.makeText(getContext(), "Delete trips success", Toast.LENGTH_SHORT).show();
                        List<Trip> temp = mViewModel.listTrips.getValue();
                        Iterator<Trip> iterator = temp.iterator();
                        while (iterator.hasNext()) {
                            Trip trip = iterator.next();
                            if (tripIDs.contains(trip.getID())) {
                                iterator.remove();
                            }
                        }
                        mViewModel.listTrips.postValue(temp);
                        checkedItemIDs.clear();
                        hideMenuAndDeleteIcons();
                        binding.checkDeleteAll.setChecked(false);
                        adapter.notifyDataSetChanged();
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Delete trips fail", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else
        {
            Toast.makeText(getContext(), "No trip selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void showOrHideDeleteIcons() {
        isShowDeleteIcons[0] = ! isShowDeleteIcons[0];
        if (isShowDeleteIcons[0])
        {
            showDeleteIcons();
        }
        else
        {
            hideDeleteIcons();
        }
        updateShowTripCheckboxes(isShowDeleteIcons[0]);
    }

    private void showDeleteIcons()
    {
        binding.btnDeleteTrips.setVisibility(View.VISIBLE);
        binding.btnCancelDelete.setVisibility(View.VISIBLE);
        binding.checkDeleteAll.setVisibility(View.VISIBLE);
    }

    private void hideDeleteIcons()
    {
        binding.btnDeleteTrips.setVisibility(View.GONE);
        binding.btnCancelDelete.setVisibility(View.GONE);
        binding.checkDeleteAll.setVisibility(View.GONE);
    }

    // Update trạng thái show check box
    private void updateShowTripCheckboxes(boolean isShowDeleteIcon) {
        List<Trip> temp = mViewModel.listTrips.getValue();
        if (temp.size() > 0)
        {
            for (Trip trip: temp)
            {
                trip.setShowDeleteCheckbox(isShowDeleteIcon);
            }
            mViewModel.listTrips.postValue(temp);
            adapter.notifyDataSetChanged();
        }
    }

    // update trạng thái của tất cả checkbox khi checkbox check all đổi trạng thái
    private void updateAllCheckboxStatus(boolean checkboxValue)
    {
        List<Trip> temp = mViewModel.listTrips.getValue();
        if (temp.size() > 0)
        {
            for (Trip trip: temp)
            {
                trip.setDeleteCheckboxChecked(checkboxValue);
            }
            mViewModel.listTrips.postValue(temp);
            adapter.notifyDataSetChanged();
        }
    }

    // Su kien xoay button menu, mo rong menu sang trai va hien thi cac button con
    public void expandMenu()
    {
        // Khai bao cac su kien
        Animation rotateOpenMenu = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_open_menu);
        Animation expandToLeft = AnimationUtils.loadAnimation(getContext(), R.anim.expand_to_left);
        // Set su kien
        binding.btnMenu.setAnimation(rotateOpenMenu);
        binding.btnAddTrip.setAnimation(expandToLeft);
        binding.btnShowDeleteIcons.setAnimation(expandToLeft);
        // Thay doi trang thai visible cho cac button
        binding.btnAddTrip.setVisibility(View.VISIBLE);
        binding.btnShowDeleteIcons.setVisibility(View.VISIBLE);
    }

    // Su kien xoay button menu, thu nho menu sang phai va an cac button con
    public void minimizeMenu()
    {
        // Khai bao cac su kien
        Animation rotateCloseMenu = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_close_menu);
        Animation minimizeToRight = AnimationUtils.loadAnimation(getContext(), R.anim.minimize_to_right);
        // Set su kien
        binding.btnMenu.setAnimation(rotateCloseMenu);
        binding.btnAddTrip.setAnimation(minimizeToRight);
        binding.btnShowDeleteIcons.setAnimation(minimizeToRight);
        // Thay doi trang thai visible cho cac button
        binding.btnAddTrip.setVisibility(View.GONE);
        binding.btnShowDeleteIcons.setVisibility(View.GONE);
    }

    private void goBack()
    {
        Navigation.findNavController(getView()).navigate(R.id.mainFragment);
    }

    private void goHome()
    {
        Navigation.findNavController(getView()).navigate(R.id.mainFragment);
    }

    // Sự kiện click từng item
    @Override
    public void onItemClick(int ID) {
        // khoi tao bien optional (nullable) va tim trong list trip trong view model
        Optional<Trip> trip = mViewModel.listTrips.getValue().stream().filter(a -> a.getID() == ID).findFirst();
        // kiem tra trip co ton tai thi navigate sang view
        if (trip.isPresent()) {
            Bundle bundle = new Bundle();
            bundle.putInt("ID", ID);
            Navigation.findNavController(getView()).navigate(R.id.viewTripFragment, bundle);
        }
    }

    // Sự kiện thay đổi trạng thái checkbox từng item
    @Override
    public void onCheckBoxStatusChanged(int ID, boolean status) {
        // khoi tao bien optional (nullable) va tim trong list trip trong view model
        Optional<Trip> trip = mViewModel.listTrips.getValue().stream().filter(a -> a.getID() == ID).findFirst();
        // kiem tra trip co ton tai thi navigate sang view
        if (trip.isPresent()) {
            // Kiểm tra nếu ID tồn tại trong list checked item (list xóa)
            boolean isItemChecked = checkedItemIDs.contains(ID);
            // Nếu không tồn tại và trạng thái của nó là true thì add vào list checked item (list xóa)
            if (status && !isItemChecked)
            {
                checkedItemIDs.add(ID);
            }
            // Nếu tồn tại và trạng thài của nó là false thì xóa khỏi list checked item (list xóa)
            else if (!status && isItemChecked)
            {
                checkedItemIDs.remove(checkedItemIDs.indexOf(ID));
            }
        }
        binding.checkDeleteAll.setChecked(!isSearch ? checkedItemIDs.size() == totalRecord : totalSearchRecord.size() != 0 && checkedItemIDs.containsAll(totalSearchRecord));
    }


    public void goToAddNew()
    {
        Bundle bundle = new Bundle();
        bundle.putInt("ID", 0);
        Navigation.findNavController(getView()).navigate(R.id.addTripFragment, bundle);
    }

    private void searchTrip(String input)
    {
        // khoi tao SQLiteHelper
        SQLiteHelper helper = new SQLiteHelper(getParentFragment().getActivity());
        // khoi tao 1 list tam de luu data
        List<Trip> allTrip = helper.getTrips(isShowDeleteIcons[0], checkedItemIDs);
        // xoa ki tu rong dau va cuoi, chuyen ve dang chu thuong
        String queryText = input.trim().toLowerCase();
        List<Trip> results = new ArrayList<>();
        // chuoi != rong thi goi ham searchTrip()
        totalSearchRecord = new ArrayList<>();
        if (!queryText.equals(""))
        {
            results = helper.searchTrip(queryText, isShowDeleteIcons[0], checkedItemIDs);
            for (Trip trip: results)
            {
                totalSearchRecord.add(trip.getID());
            }
            isSearch = true;
        }
        else
        {
            // list trip get duoc ban dau
            results = allTrip;
            isSearch = false;
        }
        for (Trip trip: results)
        {
            if (checkedItemIDs.contains(trip.getID()))
            {
                trip.setDeleteCheckboxChecked(true);
            }
        }
        // gan data vao bien listTrips trong view model
        mViewModel.listTrips.postValue(results);
        //Thong bao su thay doi data cho recycler view
        adapter.notifyDataSetChanged();
    }
}