package br.gov.sp.fatec.eatsconsumer.activity.restaurant;

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
import br.gov.sp.fatec.eatsconsumer.adapter.RestaurantAdapter;
import br.gov.sp.fatec.eatsconsumer.databinding.ActivityRestaurantBinding;
import br.gov.sp.fatec.eatsconsumer.databinding.ProgressDialogBinding;
import br.gov.sp.fatec.eatsconsumer.models.Restaurant;
import br.gov.sp.fatec.eatsconsumer.repository.RestaurantRepository;
import br.gov.sp.fatec.eatsconsumer.tasks.AsyncTaskExecutor;

public class RestaurantActivity extends AppCompatActivity {
    final String TAG_SCREEN = "TAG-RestaurantActivity";
    private ActivityRestaurantBinding binding;
    private AlertDialog progressDialog;
    private RestaurantRepository restaurantRepository;
    private RestaurantAdapter adapter;
    private List<Restaurant> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG_SCREEN, "onCreate");
        binding = ActivityRestaurantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        restaurantRepository = new RestaurantRepository();

        listData = new ArrayList<>();

        binding.rvData.setLayoutManager(new LinearLayoutManager(this));
        binding.rvData.scrollToPosition(0);
        adapter = new RestaurantAdapter(listData);
        binding.rvData.setAdapter(adapter);

        binding.btBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        fetchData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchData();
    }

    private void fetchData() {
        Log.i(TAG_SCREEN, "fetchData");
        new GetAllData().execute();
    }

    private class GetAllData extends AsyncTaskExecutor<Void, Void, List<Restaurant>> {
        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected List<Restaurant> doInBackground(Void... voids) {
            Task<QuerySnapshot> task = restaurantRepository.getAllRestaurant();
            try {
                QuerySnapshot querySnapshot = Tasks.await(task);
                List<Restaurant> restaurants = new ArrayList<>();
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    Restaurant restaurant = document.toObject(Restaurant.class);
                    if (restaurant != null) {
                        restaurant.setId(document.getId());
                        restaurants.add(restaurant);
                    }
                }
                return restaurants;

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Restaurant> restaurants) {
            hideProgressDialog();
            if (restaurants != null) {
                listData.clear();
                listData.addAll(restaurants);
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