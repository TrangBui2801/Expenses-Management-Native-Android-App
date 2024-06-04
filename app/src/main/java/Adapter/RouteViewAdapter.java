package Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanagement.R;
import com.example.expensemanagement.databinding.ItemRouteEditableBinding;
import com.example.expensemanagement.databinding.ItemRouteViewBinding;

import java.util.List;

import Entity.TravelRoute;

public class RouteViewAdapter extends RecyclerView.Adapter<RouteViewAdapter.RouteViewHolder> {

    private List<TravelRoute> routes;

    public RouteViewAdapter(List<TravelRoute> routes) {
        this.routes = routes;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_route_view, parent, false);
        return new RouteViewAdapter.RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        TravelRoute route = routes.get(position);
        holder.bindData(route);
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    public class RouteViewHolder extends RecyclerView.ViewHolder {

        private ItemRouteViewBinding binding;

        public RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemRouteViewBinding.bind(itemView);
        }

        public void bindData(TravelRoute route){
            binding.viewRouteDate.setText(route.getTravelDate());
            binding.viewRouteDestination.setText(route.getDestination());
        }
    }
}
