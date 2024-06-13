package br.gov.sp.fatec.eatsconsumer.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import br.gov.sp.fatec.eatsconsumer.R;
import br.gov.sp.fatec.eatsconsumer.databinding.AdapterRestaurantBinding;
import br.gov.sp.fatec.eatsconsumer.models.Restaurant;
import br.gov.sp.fatec.eatsconsumer.repository.RestaurantRepository;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {
    private List<Restaurant> restaurants;

    public RestaurantAdapter(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterRestaurantBinding binding = AdapterRestaurantBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RestaurantViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.bind(restaurant);
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        private final AdapterRestaurantBinding binding;

        public RestaurantViewHolder(@NonNull AdapterRestaurantBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Restaurant restaurant) {
            binding.tvNomeRestaurante.setText(restaurant.getName());
            binding.tvEmail.setText(restaurant.getEmail());
            binding.tvFone.setText(restaurant.getPhone());
            binding.tvEndereco.setText(restaurant.getAddress());
            binding.rbAvaliacao.setRating(restaurant.getRating().floatValue());
            Glide.with(binding.getRoot().getContext())
                    .load(restaurant.getUrlImage())
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_24)
                    .into(binding.ivLogo);
            binding.rbAvaliacao.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
                restaurant.setRating(Double.valueOf(rating));
                runRating(binding.getRoot().getContext(), restaurant);
            });
        }
    }

    private static void runRating(Context ctx, Restaurant restaurant) {
        RestaurantRepository restaurantRepository = new RestaurantRepository();
        final Handler handler = new Handler(Looper.getMainLooper());
        restaurantRepository.updateRestaurant(restaurant,
                aVoid -> {
                    handler.post(() -> {
                        Toast.makeText(ctx, "Avaliação realizada", Toast.LENGTH_SHORT).show();
                    });
                },
                e -> {
                    handler.post(() -> {
                        Toast.makeText(ctx, "Falha na avaliação do restaurante", Toast.LENGTH_SHORT).show();

                    });
                });
    }
}
