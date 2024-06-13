package br.gov.sp.fatec.eatsconsumer.activity.product;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import br.gov.sp.fatec.eatsconsumer.R;
import br.gov.sp.fatec.eatsconsumer.adapter.ProductAdapter;
import br.gov.sp.fatec.eatsconsumer.databinding.ActivityProductFromCategoryBinding;
import br.gov.sp.fatec.eatsconsumer.databinding.ProgressDialogBinding;
import br.gov.sp.fatec.eatsconsumer.models.Product;
import br.gov.sp.fatec.eatsconsumer.repository.ProductRepository;
import br.gov.sp.fatec.eatsconsumer.tasks.AsyncTaskExecutor;

public class ProductFromCategoryActivity extends AppCompatActivity {

    final String TAG_SCREEN = "TAG-ProductActivity";
    private ActivityProductFromCategoryBinding binding;
    private ProductRepository productRepository;
    private AlertDialog progressDialog;
    private ProductAdapter adapter;
    private List<Product> listData;
    private String productCategoryId;
    private String productCategoryName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG_SCREEN, "onCreate");
        binding = ActivityProductFromCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        productCategoryId = getIntent().getStringExtra("categoryId");
        productCategoryName = getIntent().getStringExtra("categoryName");

        binding.tvTitle.setText(productCategoryName);

        productRepository = new ProductRepository();

        listData = new ArrayList<>();
        binding.rvData.setLayoutManager(new LinearLayoutManager(this));
        binding.rvData.scrollToPosition(0);
        adapter = new ProductAdapter(listData);
        binding.rvData.setAdapter(adapter);

        binding.btBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        fetchData(productCategoryId);

    }

    private void fetchData(String categoryId) {
        Log.i(TAG_SCREEN, "fetchData");
        new GetProductsFromIdData().execute(categoryId);
    }

    private class GetProductsFromIdData extends AsyncTaskExecutor<String, Void, List<Product>> {
        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected List<Product> doInBackground(String... categoryId) {
            String iud = categoryId[0];
            Task<QuerySnapshot> task = productRepository.getProductFromCategory(iud);
            try {
                QuerySnapshot querySnapshot = Tasks.await(task);
                List<Product> data = new ArrayList<>();
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    Product product = document.toObject(Product.class);
                    if (product != null) {
                        product.setId(document.getId());
                        data.add(product);
                    }
                }
                return data;

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Product> data) {
            hideProgressDialog();
            if (data != null) {
                listData.clear();
                listData.addAll(data);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            ProgressDialogBinding dialogBinding = ProgressDialogBinding.inflate(getLayoutInflater());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogBinding.getRoot());
            builder.setCancelable(false);
            progressDialog = builder.create();
        }
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}