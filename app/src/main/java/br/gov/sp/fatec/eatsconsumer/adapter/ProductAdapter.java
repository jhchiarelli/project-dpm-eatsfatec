package br.gov.sp.fatec.eatsconsumer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import br.gov.sp.fatec.eatsconsumer.R;

import br.gov.sp.fatec.eatsconsumer.activity.product.ProductDetailActivity;
import br.gov.sp.fatec.eatsconsumer.databinding.ProductAdapterBinding;
import br.gov.sp.fatec.eatsconsumer.models.Product;
import br.gov.sp.fatec.eatsconsumer.utils.DataFormatter;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products;

    public ProductAdapter(List<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProductAdapterBinding binding = ProductAdapterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductViewHolder holder, int position) {
        Product category = products.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ProductAdapterBinding binding;

        public ProductViewHolder(@NonNull ProductAdapterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Product product) {
            binding.tvProduct.setText(product.getName());
            binding.tvPrice.setText(DataFormatter.formatCurrency(product.getPrice()));

            Glide.with(binding.getRoot().getContext())
                    .load(product.getImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_24)
                    .into(binding.ivProduct);

            binding.cvProduct.setOnClickListener(v -> {
                showProductForm(binding.getRoot().getContext(), product);
            });
        }
    }

    private static void showProductForm(Context ctx, Product product) {
        Log.d("TAG-ProductAdapter", "showForm: " + product.getName());
        Intent intent = new Intent(ctx, ProductDetailActivity.class);
        intent.putExtra("objProduct", product);
        ctx.startActivity(intent);
        Context context = ctx;
        if(context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
}


