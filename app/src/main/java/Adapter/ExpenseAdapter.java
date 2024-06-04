package Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanagement.R;
import com.example.expensemanagement.databinding.ItemExpenseBinding;

import java.util.List;

import Entity.Expense;
import Helper.SQLiteHelper;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>{
    public List<Expense> expenses;
    private ExpenseAdapter.ListItemListener listItemListener;

    public ExpenseAdapter(List<Expense> expenses, ExpenseAdapter.ListItemListener listItemListener) {
        this.expenses = expenses;
        this.listItemListener = listItemListener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseAdapter.ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseAdapter.ExpenseViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.bindData(expense);
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public interface ListItemListener{
        void onItemClick(int ID, int tripID);
        void onCheckboxStatusChanged(int ID, boolean isChecked);
    }
    public class ExpenseViewHolder extends RecyclerView.ViewHolder{
        private final ItemExpenseBinding binding;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemExpenseBinding.bind(itemView);
        }

        public void bindData(Expense expense){
            binding.expenseTitle.setText("Type:  " + expense.getType());
            binding.expenseDate.setText("Date:  " + expense.getDate());
            binding.expenseTime.setText("Time:  " + expense.getTime());
            binding.expenseAmount.setText("Amount: " + String.valueOf(expense.getAmount()));
            binding.optionMenu.setOnClickListener(v -> showOptionMenu(expense.getID(), expense.getTripID()));
            binding.getRoot().setOnClickListener(a->listItemListener.onItemClick(expense.getID(), expense.getTripID()));
            // dựa trên trạng thái để hiển thị checkbox delete
            if (expense.isShowDeleteCheckbox())
            {
                binding.checkDelete.setVisibility(View.VISIBLE);
            }
            else
            {
                binding.checkDelete.setVisibility(View.GONE);
            }
            // set trạng thái cho checkbox delete
            binding.checkDelete.setChecked(expense.isDeleteCheckboxChecked());
            binding.checkDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    expense.setDeleteCheckboxChecked(binding.checkDelete.isChecked());
                    listItemListener.onCheckboxStatusChanged(expense.getID(), expense.isDeleteCheckboxChecked());
                }
            });
        }

        // bắt sự kiện cho OptionMenu
        private void showOptionMenu(int ID, int tripID)
        {
            PopupMenu popupMenu = new PopupMenu(this.itemView.getContext(), binding.optionMenu);
            popupMenu.inflate(R.menu.expense_option_menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId())
                    {
//                        case R.id.viewMenuBtn:
//                        {
//                            Bundle bundle = new Bundle();
//                            bundle.putInt("ID", ID);
//                            bundle.putInt("tripID", tripID);
//                            Navigation.findNavController(itemView).navigate(R.id.viewExpenseFragment, bundle);
//                            break;
//                        }
                        case R.id.updateMenuBtn:
                        {
                            Bundle bundle = new Bundle();
                            bundle.putInt("ID", ID);
                            bundle.putInt("tripID", tripID);
                            Navigation.findNavController(itemView).navigate(R.id.addExpenseFragment, bundle);
                            //handle update
                            break;
                        }
                        case R.id.deleteMenuBtn:
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                            builder.setCancelable(true);
                            builder.setTitle("Confirm dialog");
                            builder.setMessage("Do you want to delete this expense?\nThis action cannot be recovered");
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SQLiteHelper helper = new SQLiteHelper(itemView.getContext());
                                    boolean isSuccess = helper.deleteExpense(ID);
                                    if (isSuccess)
                                    {
                                        Toast.makeText(itemView.getContext(), "Successfully delete expense", Toast.LENGTH_SHORT).show();
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("tripID", tripID);
                                        Navigation.findNavController(itemView).navigate(R.id.listExpenseFragment, bundle);
                                    }
                                    else
                                    {
                                        Toast.makeText(itemView.getContext(), "Cannot delete expense", Toast.LENGTH_SHORT).show();
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
                            break;
                        }
                    }
                    return false;
                }
            });
            popupMenu.show();
        }
    }
}
