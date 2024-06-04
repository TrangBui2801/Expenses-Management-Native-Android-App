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
import com.example.expensemanagement.databinding.ItemTripBinding;

import java.util.List;

import Entity.Trip;
import Helper.SQLiteHelper;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {
    public List<Trip> trips;
    private ListItemListener listItemListener;

    public TripAdapter(List<Trip> trips, ListItemListener listItemListener) {
        this.trips = trips;
        this.listItemListener = listItemListener;
    }

    @NonNull
    @Override
    // set view cho items trong recycler view
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // gan view cho item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip, parent, false);
        // return view cua trip
        return new TripViewHolder(view);
    }

    @Override
    // bind view cho tung trip item
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        // get trip theo position
        Trip trip = trips.get(position);
        // bind view theo trip
        holder.bindData(trip);
    }

    @Override
    // lay size cua list trip de bind data
    public int getItemCount() {
        return trips.size();
    }

    public interface ListItemListener{
        void onItemClick(int ID);
        void onCheckBoxStatusChanged(int ID, boolean status);
    }
    public class TripViewHolder extends RecyclerView.ViewHolder{
        private final ItemTripBinding binding;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            // bind view cho trip item
            binding = ItemTripBinding.bind(itemView);
        }
        // set data vao trong tung view cua trip items
        public void bindData(Trip trip){
            switch (trip.getType().toLowerCase())
            {
                case "client meeting":
                {
                    binding.iconButton.setImageResource(R.drawable.ic_client_meeting);
                    break;
                }
                case "conference":
                {
                    binding.iconButton.setImageResource(R.drawable.ic_conference);
                    break;
                }
                case "business travel":
                {
                    binding.iconButton.setImageResource(R.drawable.ic_business_travel);
                    break;
                }
                default:
                {
                    binding.iconButton.setImageResource(R.drawable.ic_travel);
                    break;
                }
            }
            binding.itemName.setText("Name:  " + trip.getName());
            binding.itemFromDate.setText("From:  " + trip.getFromDate());
            binding.itemToDate.setText("To:     " + trip.getToDate());
            binding.itemTotal.setText("Total:  " + trip.getTotalExpense());
            binding.optionMenu.setOnClickListener(v -> showOptionMenu(trip.getID()));
            if (trip.isShowDeleteCheckbox())
            {
                binding.checkDelete.setVisibility(View.VISIBLE);
            }
            else
            {
                binding.checkDelete.setVisibility(View.GONE);
            }
            binding.checkDelete.setChecked(trip.isDeleteCheckboxChecked());
            binding.checkDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean status = binding.checkDelete.isChecked();
                    trip.setDeleteCheckboxChecked(status);
                    listItemListener.onCheckBoxStatusChanged(trip.getID(), status);
                }
            });
            // bat su kien khi click vao trip item
            // onItemClick() cua interface ListItemListener dc implement o trong class ListTripFragment
            binding.getRoot().setOnClickListener(a->listItemListener.onItemClick(trip.getID()));
        }
        // show option menu
        private void showOptionMenu(int ID)
        {
            // khoi tao popup menu
            PopupMenu popupMenu = new PopupMenu(this.itemView.getContext(), binding.optionMenu);
            // gan view option menu vao popup
            popupMenu.inflate(R.menu.trip_option_menu);
            // bat su kien cho tung option menu
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                // bat su kien click item trong option menu
                public boolean onMenuItemClick(MenuItem menuItem) {
                    // get id cua menuItem
                    switch (menuItem.getItemId())
                    {
//                        case R.id.viewMenuBtn:
//                        {
//                            Bundle bundle = new Bundle();
//                            bundle.putInt("ID", ID);
//                            Navigation.findNavController(itemView).navigate(R.id.viewTripFragment, bundle);
//                            break;
//                        }
                        // Neu click vao button viewExpenseMenuButton trong menu
                        case R.id.viewExpensesMenuBtn:
                        {
                            Bundle bundle = new Bundle();
                            bundle.putInt("tripID", ID);
                            Navigation.findNavController(itemView).navigate(R.id.listExpenseFragment, bundle);
                            break;
                        }
                        // Neu click vao button updateMenuButton trong menu
                        case R.id.updateMenuBtn:
                        {
                            Bundle bundle = new Bundle();
                            bundle.putInt("ID", ID);
                            Navigation.findNavController(itemView).navigate(R.id.addTripFragment, bundle);
                            //handle update
                            break;
                        }
                        // Neu click vao button deleteMenuButton trong menu
                        case R.id.deleteMenuBtn:
                        {
                            // Confirm tu user
                            // Khoi tao builder cho alert dialog
                            // Ham khoi tao yeu cau tham so la context va duoc lay thong qua ham getContext cua itemView trong recyclerView
                            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                            // Bat su kien click ngoai dialog thi close dialog
                            builder.setCancelable(true);
                            // Set title cho dialog
                            builder.setTitle("Confirm dialog");
                            // Set noi dung cho dialog
                            builder.setMessage("Do you want to delete this trip with all expenses?\nThis action cannot be recovered");
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SQLiteHelper helper = new SQLiteHelper(itemView.getContext());
                                    boolean isSuccess = helper.deleteTrip(ID);
                                    if (isSuccess)
                                    {
                                        Toast.makeText(itemView.getContext(), "Successfully delete trip", Toast.LENGTH_SHORT).show();
                                        Navigation.findNavController(itemView).navigate(R.id.listTripFragment);
                                    }
                                    else
                                    {
                                        Toast.makeText(itemView.getContext(), "Cannot delete trip", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            // Khoi tao dialog tu builder
                            AlertDialog dialog = builder.create();
                            // show dialog khi an vao button delete trong optionMenu
                            dialog.show();
                            break;
                        }
                    }
                    return false;
                }
            });
            // show popup menu
            popupMenu.show();
        }
    }
}
