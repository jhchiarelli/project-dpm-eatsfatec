package br.gov.sp.fatec.eatsconsumer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import br.gov.sp.fatec.eatsconsumer.models.Publicity;
import br.gov.sp.fatec.eatsconsumer.R;

public class PublicityAdapter extends RecyclerView.Adapter<PublicityAdapter.Sliderviewholder> {
    private List<Publicity> sliderItems;
    private ViewPager2 viewPager2;
    private Context context;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sliderItems.addAll(sliderItems);
            notifyDataSetChanged();
        }
    };

    public PublicityAdapter(List<Publicity> sliderItems, ViewPager2 viewPager2) {
        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public PublicityAdapter.Sliderviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new Sliderviewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.publicity_vh, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PublicityAdapter.Sliderviewholder holder, int position) {

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(60));

        Glide.with(context)
                .load(sliderItems.get(position).getImageUrl())
                .apply(requestOptions)
                .into(holder.imageView);

        if (position == sliderItems.size() - 2) {
            viewPager2.post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    public class Sliderviewholder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public Sliderviewholder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
        }
    }
}
