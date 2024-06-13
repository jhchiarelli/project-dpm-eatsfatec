package br.gov.sp.fatec.eatsconsumer.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import br.gov.sp.fatec.eatsconsumer.activity.product.ProductFromCategoryActivity;
import br.gov.sp.fatec.eatsconsumer.models.Category;
import br.gov.sp.fatec.eatsconsumer.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.viewholder> {
    List<Category> items;
    Context context;

    public CategoryAdapter(List<Category> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public CategoryAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_vh, parent, false);
        return new viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.viewholder holder, int position) {
        holder.titleTxt.setText(items.get(position).getDescription());

        Glide.with(context)
                .load(items.get(position).getImageUrl())
                .centerCrop()
                .placeholder(R.drawable.ic_image_24)
                .into(holder.pic);

        holder.itemView.setOnClickListener(v -> {
            Log.d("TAG-CategoryAdapter", "onClick Category " + items.get(position).getDescription() + " id: " + items.get(position).getId());
            Intent intent=new Intent(context, ProductFromCategoryActivity.class);
            intent.putExtra("categoryId",items.get(position).getId());
            intent.putExtra("categoryName",items.get(position).getDescription());
            context.startActivity(intent);
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
            titleTxt = itemView.findViewById(R.id.catNameTxt);
            pic = itemView.findViewById(R.id.imgCat);
        }
    }
}
