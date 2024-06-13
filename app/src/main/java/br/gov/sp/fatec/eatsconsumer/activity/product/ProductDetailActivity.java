package br.gov.sp.fatec.eatsconsumer.activity.product;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;

import br.gov.sp.fatec.eatsconsumer.R;
import br.gov.sp.fatec.eatsconsumer.databinding.ActivityProductDetailBinding;
import br.gov.sp.fatec.eatsconsumer.databinding.ActivityProductFromCategoryBinding;
import br.gov.sp.fatec.eatsconsumer.models.Product;
import br.gov.sp.fatec.eatsconsumer.repository.ProductRepository;
import br.gov.sp.fatec.eatsconsumer.utils.DataFormatter;

public class ProductDetailActivity extends AppCompatActivity {
    final String TAG_SCREEN = "TAG-ProductActivity";
    private ActivityProductDetailBinding binding;
    private ProductRepository productRepository;
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG_SCREEN, "onCreate");
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        productRepository = new ProductRepository();

        Product product = (Product)getIntent().getSerializableExtra("objProduct");
        if (product != null) {
            binding.tvName.setText(product.getName());
            binding.tvDescricao.setText(product.getDescription());
            binding.btAddProduct.setText("Adicionar " + DataFormatter.formatCurrency(product.getPrice()));
            Glide.with(this).load(product.getImageUrl()).into(binding.ivProduct);
        }

        binding.btBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }
}