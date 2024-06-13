package br.gov.sp.fatec.eatsconsumer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import br.gov.sp.fatec.eatsconsumer.models.Restaurant;
import br.gov.sp.fatec.eatsconsumer.R;

import java.util.List;

public class RestaurantDashboardAdapter extends RecyclerView.Adapter<RestaurantDashboardAdapter.viewholder> {
    List<Restaurant> items;
    Context context;

    public RestaurantDashboardAdapter(List<Restaurant> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RestaurantDashboardAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_vh, parent, false);
        return new viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantDashboardAdapter.viewholder holder, int position) {
        holder.titleTxt.setText(items.get(position).getName());

        Glide.with(context)
                .load(items.get(position).getUrlImage())
                .centerCrop()
                .placeholder(R.drawable.ic_image_24)
                .into(holder.pic);

        holder.itemView.setOnClickListener(v -> {
            Log.d("TAG-RestaurantDashboardAdapter", "onClick RestaurantDashboard " + items.get(position).getName() + " id: " + items.get(position).getId());
//            Intent intent=new Intent(context, ListRestaurantDashboardFoodActivity.class);
//            intent.putExtra("RestaurantDashboardId",items.get(position).getId());
//            intent.putExtra("RestaurantDashboardName",items.get(position).getDescription());
//            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView titleTxt;
        ImageView pic;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.tvRestaurantName);
            pic = itemView.findViewById(R.id.imgRestaurant);
        }
    }
}
