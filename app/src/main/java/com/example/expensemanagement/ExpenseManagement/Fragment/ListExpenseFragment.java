package com.example.expensemanagement.ExpenseManagement.Fragment;

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

import com.example.expensemanagement.ExpenseManagement.ViewModel.ListExpenseViewModel;
import com.example.expensemanagement.R;
import com.example.expensemanagement.databinding.FragmentListExpenseBinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import Adapter.ExpenseAdapter;
import Entity.Expense;
import Helper.SQLiteHelper;

public class ListExpenseFragment extends Fragment implements ExpenseAdapter.ListItemListener {

    private ListExpenseViewModel mViewModel;
    private FragmentListExpenseBinding binding;
    private ExpenseAdapter adapter;
    private boolean isSearch = false;
    private final boolean[] menuOpened = {false};
    private final boolean[] isShowDeleteIcons = {false};
    private List<Integer> checkedItemIDs = new ArrayList<>();
    private int totalRecord;
    private List<Integer> totalSearchRecord = new ArrayList<>();
    private boolean isSortDesc = false;
    private boolean isSortData = false;

    public static ListExpenseFragment newInstance() {
        return new ListExpenseFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(ListExpenseViewModel.class);
        binding = FragmentListExpenseBinding.inflate(inflater, container, false);

        int tripID = getArguments().getInt("tripID");

        // Bat su kien cho cac button tren toolbar
        binding.toolbarListExpense.backBtn.setOnClickListener(v -> goBack());
        binding.toolbarListExpense.homeBtn.setOnClickListener(v -> goHome());

        // Bat su kien cho search box
        binding.txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.txtSearch.setIconified(false);
            }
        });
        binding.txtSearch.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchExpenses(s, tripID);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchExpenses(s, tripID);
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
        binding.btnDeleteExpenses.setOnClickListener(v -> deleteExpenses());

        // Bat su kien cho button sort
        binding.iconSort.setImageResource(R.drawable.icon_sorts);
        binding.iconSort.setOnClickListener(v -> sortData());

        binding.toolbarListExpense.toolbarTitle.setText("List expenses");

        RecyclerView recyclerView = binding.listExpense;
        recyclerView.setHasFixedSize(true);
        mViewModel.listExpenses.observe(
                getViewLifecycleOwner(),
                expenses -> {
                    binding.recordNumber.setText("List expenses (" + expenses.size() + ")");
                    adapter = new ExpenseAdapter(expenses, this);
                    if (expenses.size() == 0) {
                        binding.listExpense.setVisibility(View.INVISIBLE);
                        binding.emptyView.setVisibility(View.VISIBLE);
                    } else {
                        for (Expense expense : expenses) {
                            updateCheckedList(expense.getID(), expense.isDeleteCheckboxChecked());
                        }
                        binding.listExpense.setVisibility(View.VISIBLE);
                        binding.emptyView.setVisibility(View.INVISIBLE);
                        binding.listExpense.setAdapter(adapter);
                        binding.listExpense.setLayoutManager(new LinearLayoutManager(getActivity()));
                        binding.checkDeleteAll.setChecked(!isSearch ? checkedItemIDs.size() == totalRecord : totalSearchRecord.size() != 0 && checkedItemIDs.containsAll(totalSearchRecord));
                    }
                }
        );
        SQLiteHelper helper = new SQLiteHelper(getParentFragment().getActivity());
        List<Expense> result = helper.getExpensesByTrip(tripID, isShowDeleteIcons[0], checkedItemIDs);
        totalRecord = result.size();
        mViewModel.listExpenses.postValue(result);
        binding.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuOpened[0] = !menuOpened[0];
                if (menuOpened[0]) {
                    expandMenu();
                } else {
                    minimizeMenu();
                }
            }
        });
        binding.btnAddExpense.setOnClickListener(v -> goToAddExpense(tripID));
        return binding.getRoot();
    }

    // sort data theo thứ tự
    private void sortData() {
        // Gán biến isSort = true (Để lấy data trong hàm search)
        isSortData = true;
        // Khởi tạo List để lưu data sort
        List<Expense> sortedList = new ArrayList<>();
        // Kiểm tra trạng thái sort (DESC hay ASC)
        if (isSortDesc) {
            // Set icon sort (ASC)
            binding.iconSort.setImageResource(R.drawable.ic_baseline_arrow_sort_asc_24);
            // Sắp xếp data (ASC)
            sortedList = mViewModel.listExpenses.getValue().stream().sorted(Comparator.comparing(Expense::getAmount).reversed()).collect(Collectors.toList());
        } else {
            // Set icon sort (DESC)
            binding.iconSort.setImageResource(R.drawable.ic_baseline_arrow_sort_desc_24);
            // Sắp xếp data (DESC)
            sortedList = mViewModel.listExpenses.getValue().stream().sorted(Comparator.comparing(Expense::getAmount)).collect(Collectors.toList());
        }
        // Cập nhật recycler view
        mViewModel.listExpenses.postValue(sortedList);
        adapter.notifyDataSetChanged();
        // ĐỔi trạng thái sort
        isSortDesc = !isSortDesc;
    }

    // cập nhật checkedList khi render recycler view
    private void updateCheckedList(int ID, boolean isChecked) {
        boolean isIdExist = checkedItemIDs.contains(ID);
        if (isIdExist && !isChecked) {
            checkedItemIDs.remove(checkedItemIDs.indexOf(ID));
        } else if (!isIdExist && isChecked) {
            checkedItemIDs.add(ID);
        }
    }

    // Xóa nhiều expenses
    private void deleteExpenses() {
        List<Integer> expenseIDs = new ArrayList<>();
        for (Expense expense : mViewModel.listExpenses.getValue()) {
            if (expense.isDeleteCheckboxChecked()) {
                expenseIDs.add(expense.getID());
            }
        }
        if (expenseIDs.size() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setCancelable(true);
            builder.setTitle("Warning");
            builder.setMessage("Do you want to delete " + expenseIDs.size() + " items?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SQLiteHelper helper = new SQLiteHelper(getContext());
                    boolean isSuccess = helper.deleteExpenses(expenseIDs);
                    if (isSuccess) {
                        Toast.makeText(getContext(), "Delete expenses success", Toast.LENGTH_SHORT).show();
                        List<Expense> temp = mViewModel.listExpenses.getValue();
                        Iterator<Expense> iterator = temp.iterator();
                        while (iterator.hasNext()) {
                            Expense expense = iterator.next();
                            if (expenseIDs.contains(expense.getID())) {
                                iterator.remove();
                            }
                        }
                        mViewModel.listExpenses.postValue(temp);
                        binding.checkDeleteAll.setChecked(false);
                        checkedItemIDs.clear();
                        hideMenuAndDeleteIcons();
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Delete expenses fail", Toast.LENGTH_SHORT).show();
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
        } else {
            Toast.makeText(getContext(), "No expense selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void hideMenuAndDeleteIcons() {
        minimizeMenu();
        hideDeleteIcons();
        updateShowExpenseCheckboxes(false);
        updateAllCheckboxStatus(false);
        binding.checkDeleteAll.setChecked(false);
        isShowDeleteIcons[0] = false;
        menuOpened[0] = false;
    }

    private void showOrHideDeleteIcons() {
        isShowDeleteIcons[0] = !isShowDeleteIcons[0];
        if (isShowDeleteIcons[0]) {
            showDeleteIcons();
        } else {
            hideDeleteIcons();
        }
        updateShowExpenseCheckboxes(isShowDeleteIcons[0]);
    }

    // update show checkbox of all expenses
    private void updateShowExpenseCheckboxes(boolean isShowDeleteIcon) {
        List<Expense> temp = mViewModel.listExpenses.getValue();
        if (temp.size() > 0) {
            for (Expense expense : temp) {
                expense.setShowDeleteCheckbox(isShowDeleteIcon);
            }
            mViewModel.listExpenses.postValue(temp);
            adapter.notifyDataSetChanged();
        }
    }

    // update checkbox status of all expenses
    private void updateAllCheckboxStatus(boolean checkboxValue) {
        List<Expense> temp = mViewModel.listExpenses.getValue();
        if (temp.size() > 0) {
            for (Expense expense : temp) {
                expense.setDeleteCheckboxChecked(checkboxValue);
            }
            mViewModel.listExpenses.postValue(temp);
            adapter.notifyDataSetChanged();
        }
    }

    // hide checkboxes, button cancel and delete
    private void hideDeleteIcons() {
        binding.btnDeleteExpenses.setVisibility(View.GONE);
        binding.btnCancelDelete.setVisibility(View.GONE);
        binding.checkDeleteAll.setVisibility(View.GONE);
    }

    // show checkboxes, button cancel and delete
    private void showDeleteIcons() {
        binding.btnDeleteExpenses.setVisibility(View.VISIBLE);
        binding.btnCancelDelete.setVisibility(View.VISIBLE);
        binding.checkDeleteAll.setVisibility(View.VISIBLE);
    }

    private void goHome() {
        Navigation.findNavController(getView()).navigate(R.id.mainFragment);
    }

    private void goBack() {
        Navigation.findNavController(getView()).navigate(R.id.listTripFragment);
    }

    // Su kien xoay button menu, mo rong menu sang trai va hien thi cac button con
    public void expandMenu() {
        // Khai bao cac su kien
        Animation rotateOpenMenu = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_open_menu);
        Animation expandToLeft = AnimationUtils.loadAnimation(getContext(), R.anim.expand_to_left);
        // Set su kien
        binding.btnMenu.setAnimation(rotateOpenMenu);
        binding.btnAddExpense.setAnimation(expandToLeft);
        binding.btnShowDeleteIcons.setAnimation(expandToLeft);
        // Thay doi trang thai visible cho cac button
        binding.btnAddExpense.setVisibility(View.VISIBLE);
        binding.btnShowDeleteIcons.setVisibility(View.VISIBLE);
    }

    // Su kien xoay button menu, thu nho menu sang phai va an cac button con
    public void minimizeMenu() {
        // Khai bao cac su kien
        Animation rotateCloseMenu = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_close_menu);
        Animation minimizeToRight = AnimationUtils.loadAnimation(getContext(), R.anim.minimize_to_right);
        // Set su kien
        binding.btnMenu.setAnimation(rotateCloseMenu);
        binding.btnAddExpense.setAnimation(minimizeToRight);
        binding.btnShowDeleteIcons.setAnimation(minimizeToRight);
        // Thay doi trang thai visible cho cac button
        binding.btnAddExpense.setVisibility(View.GONE);
        binding.btnShowDeleteIcons.setVisibility(View.GONE);
    }

    private void goToAddExpense(int tripID) {
        Bundle bundle = new Bundle();
        bundle.putInt("ID", 0);
        bundle.putInt("tripID", tripID);
        Navigation.findNavController(getView()).navigate(R.id.addExpenseFragment, bundle);
    }

    // sự kiện item click của expenses
    @Override
    public void onItemClick(int ID, int tripID) {
        Optional<Expense> expense = mViewModel.listExpenses.getValue().stream().filter(a -> a.getID() == ID).findFirst();
        if (expense.isPresent()) {
            Bundle bundle = new Bundle();
            bundle.putInt("ID", ID);
            bundle.putInt("tripID", tripID);
            Navigation.findNavController(getView()).navigate(R.id.viewExpenseFragment, bundle);
        }
    }

    // sự kiện thay đổi status của checkbox từng item
    @Override
    public void onCheckboxStatusChanged(int ID, boolean isChecked) {
        // Lấy expense trong list expense của view model theo ID
        Optional<Expense> expense = mViewModel.listExpenses.getValue().stream().filter(a -> a.getID() == ID).findFirst();
        if (expense.isPresent()) {
            // Kiểm tra nếu ID tồn tại trong list checked (List xóa)
            boolean isIdExist = checkedItemIDs.contains(ID);
            if (isChecked && !isIdExist) {
                // Nếu không tồn tại và status là checked thì add vào list checked (List xóa)
                checkedItemIDs.add(ID);
            } else if (!isChecked && isIdExist) {
                // Nếu tồn tại và status là unchecked thì remove khỏi list checked (List xóa)
                checkedItemIDs.remove(checkedItemIDs.indexOf(ID));
            }
        }
        // Cập nhật trạng thái của check delete all
        // Nếu không search (số bản ghi hiển thị là tất cả (total record)) thì kiểm tra số bản ghi bằng size của list checked (List xóa)
        // Nếu đã search thì số bản ghi đã search được phải > 0 và list checked (List xóa) chứa toàn bộ bản ghi search được.
        binding.checkDeleteAll.setChecked(!isSearch ? checkedItemIDs.size() == totalRecord : totalSearchRecord.size() != 0 && checkedItemIDs.containsAll(totalSearchRecord));
    }

    // search expense
    private void searchExpenses(String input, int tripID) {
        // Khởi tạo đối tượng SQLiteHelper
        SQLiteHelper helper = new SQLiteHelper(getParentFragment().getActivity());
        // Khởi tạo mảng lưu toàn bộ data
        List<Expense> allTrip = new ArrayList<>();
        // Kiểm tra nếu đang sort data desc thì get data theo DESC hoặc ASC
        if (isSortData) {
            allTrip = helper.getExpensesByTrip(tripID, isShowDeleteIcons[0], checkedItemIDs, !isSortDesc);
        } else {
            allTrip = helper.getExpensesByTrip(tripID, isShowDeleteIcons[0], checkedItemIDs);
        }
        // Convert sang viết thường
        String queryText = input.trim().toLowerCase();
        // Khởi tạo mảng để lấy data search
        List<Expense> results = new ArrayList<>();
        totalSearchRecord = new ArrayList<>();
        // Kiểm tra chuỗi search nếu rỗng thì truy vấn database
        if (!queryText.equals("")) {
            // Kiểm tra nếu đang sort data desc thì get data theo DESC hoặc ASC
            if (isSortData) {
                results = helper.searchExpense(queryText, tripID, isShowDeleteIcons[0], checkedItemIDs, !isSortDesc);
            } else {
                results = helper.searchExpense(queryText, tripID, isShowDeleteIcons[0], checkedItemIDs);
            }
            // Lưu ID search để xử lý sau này
            for (Expense expense : results) {
                totalSearchRecord.add(expense.getID());
            }
            // Biến isSearch để xử lý sau này
            isSearch = true;
        } else {
            // Nếu chuỗi rỗng thì gán isSearch = false (Không còn search) và result = all data (Hiển thị toàn bộ data)
            isSearch = false;
            results = allTrip;
        }
        // Update trạng thái của các expense mà ID nằm trong list checked (List xóa)
        for (Expense expense : results) {
            if (checkedItemIDs.contains(expense.getID())) {
                expense.setDeleteCheckboxChecked(true);
            }
        }
        // Cập nhật recycler view
        mViewModel.listExpenses.postValue(results);
        adapter.notifyDataSetChanged();
    }
}