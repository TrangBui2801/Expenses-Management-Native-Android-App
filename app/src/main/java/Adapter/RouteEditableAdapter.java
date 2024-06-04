package Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanagement.R;
import com.example.expensemanagement.TripManagement.ViewModel.AddTripViewModel;
import com.example.expensemanagement.databinding.ItemRouteEditableBinding;

import java.util.List;

import Entity.TravelRoute;

public class RouteEditableAdapter extends RecyclerView.Adapter<RouteEditableAdapter.RouteEditableViewHolder>{

    private List<TravelRoute> routes;

    public RouteEditableAdapter(List<TravelRoute> routes) {
        this.routes = routes;
    }

    @NonNull
    @Override
    public RouteEditableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_route_editable, parent, false);
        RouteEditableViewHolder check = new RouteEditableAdapter.RouteEditableViewHolder(view);
        return new RouteEditableAdapter.RouteEditableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteEditableViewHolder holder, int position) {
        TravelRoute route = routes.get(position);
        holder.bindData(route);
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    public class RouteEditableViewHolder extends RecyclerView.ViewHolder {

        private ItemRouteEditableBinding binding;

        public RouteEditableViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemRouteEditableBinding.bind(itemView);
        }

        public void bindData(TravelRoute route) {
            binding.viewRouteDate.setText(route.getTravelDate());
            binding.viewRouteDestination.setText(route.getDestination());
            binding.icDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setCancelable(true);
                    builder.setTitle("Confirm dialog");
                    builder.setMessage("Do you want to delete this route?\nThis action cannot be recovered");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AddTripViewModel.removeRoute(route);
                            Toast.makeText(itemView.getContext(), "Successfully remove route", Toast.LENGTH_SHORT).show();
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
            });
        }
    }
}
