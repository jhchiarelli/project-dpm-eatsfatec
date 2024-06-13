package br.gov.sp.fatec.eatsconsumer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import br.gov.sp.fatec.eatsconsumer.activity.auth.SignInActivity;
import br.gov.sp.fatec.eatsconsumer.activity.auth.SignUpActivity;
import br.gov.sp.fatec.eatsconsumer.activity.consumer.ConsumerProfileActivity;
import br.gov.sp.fatec.eatsconsumer.activity.restaurant.RestaurantActivity;
import br.gov.sp.fatec.eatsconsumer.adapter.CategoryAdapter;
import br.gov.sp.fatec.eatsconsumer.adapter.PublicityAdapter;
import br.gov.sp.fatec.eatsconsumer.adapter.RestaurantDashboardAdapter;
import br.gov.sp.fatec.eatsconsumer.databinding.ActivityMainBinding;
import br.gov.sp.fatec.eatsconsumer.models.Category;
import br.gov.sp.fatec.eatsconsumer.models.Consumer;
import br.gov.sp.fatec.eatsconsumer.models.Publicity;
import br.gov.sp.fatec.eatsconsumer.models.Restaurant;
import br.gov.sp.fatec.eatsconsumer.repository.CategoryRepository;
import br.gov.sp.fatec.eatsconsumer.repository.ConsumerRepository;
import br.gov.sp.fatec.eatsconsumer.repository.PublicityRepository;
import br.gov.sp.fatec.eatsconsumer.repository.RestaurantRepository;
import br.gov.sp.fatec.eatsconsumer.tasks.AsyncTaskExecutor;

public class MainActivity extends AppCompatActivity {
    final String TAG_SCREEN = "TAG-MainActivity";
    private ActivityMainBinding binding;
    private PublicityRepository publicityRepository;
    private CategoryRepository categoryRepository;
    private RestaurantRepository restaurantRepository;
    private ConsumerRepository consumerRepository;
    //Firebase Auth
    private FirebaseAuth mAuth;
    private Consumer objConsumer;
    String nameUserRes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG_SCREEN, "onCreate");
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        consumerRepository = new ConsumerRepository();
        categoryRepository = new CategoryRepository();
        publicityRepository = new PublicityRepository();
        restaurantRepository = new RestaurantRepository();

        initCategory();
        initPublicity();
        initRestaurant();

        binding.btRestaurant.setOnClickListener(v -> {
            Log.d(TAG_SCREEN, "btRestaurant.setOnClickListener");
            Intent intent = new Intent(getApplicationContext(), RestaurantActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navHome:
                        // Ação para o item "Início"
                        Toast.makeText(MainActivity.this, "Início selecionado", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.navSearch:
                        // Ação para o item "Busca"
                        Toast.makeText(MainActivity.this, "Busca selecionada", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.navOrders:
                        // Ação para o item "Pedidos"
                        Toast.makeText(MainActivity.this, "Pedidos selecionados", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.navProfile:
                        // Ação para o item "Perfil"
                        // Toast.makeText(MainActivity.this, "Perfil selecionado", Toast.LENGTH_SHORT).show();
                        callProfileUser();
                        return true;
                }
                return false;
            }
        });
    }

    private void callProfileUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            if (objConsumer != null) {
                Intent intent = new Intent(getApplicationContext(), ConsumerProfileActivity.class);
                intent.putExtra("objConsumer", objConsumer);
                intent.putExtra("nameConsumer", nameUserRes);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else {
                Log.d(TAG_SCREEN, "Object Consumer is null");
            }
        } else {
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG_SCREEN, "Entry onStart");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            nameUserRes = "";
            binding.tvWelcome.setText("Olá, identificando!");
            binding.btAdressUser.setText("Procurando endereço...");
            new GetUserTask().execute(currentUser.getUid());
//            binding.btSignIn.setEnabled(false);
//            binding.btLogout.setVisibility(View.VISIBLE);
            Log.i(TAG_SCREEN, "User is logged in");
            Log.i(TAG_SCREEN, "User ID: " + currentUser.getUid());
            Log.i(TAG_SCREEN, "User Email: " + currentUser.getEmail());
        } else {
            Log.i(TAG_SCREEN, "Efetue o Login");
            nameUserRes = "";
            binding.tvWelcome.setText("Olá, efetue o login!");
            binding.btAdressUser.setText("Sua localização");
            binding.tvWelcome.setOnClickListener(v -> showSignIn());
//            binding.btLogout.setVisibility(View.INVISIBLE);
//            binding.btSignIn.setEnabled(true);
//            showSignIn();
        }
    }

    private class GetUserTask extends AsyncTaskExecutor<String, Void, Consumer> {
        @Override
        protected void onPreExecute() {
            showProgressBar(true);
        }

        @Override
        protected Consumer doInBackground(String... strings) {
            String userId = strings[0];
            Log.d(TAG_SCREEN, "Buscando idUser: " + userId);
            Task<QuerySnapshot> task = consumerRepository.getConsumerLogged(userId);
            try {
                QuerySnapshot querySnapshot = Tasks.await(task);
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    Consumer user = document.toObject(Consumer.class);
                    Log.d(TAG_SCREEN, "User: " + user.getName());
                    if (user != null) {
                        user.setId(document.getId());
                        return user;
                    }
                }
//                    if (!task.getResult().isEmpty()) {
//                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
//                        if (document.exists()) {
//                            Consumer user = document.toObject(Consumer.class);
//                            user.setId(document.getId());
//                            return document.toObject(Consumer.class);
//                        }
//                    }

                return null;
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Consumer user) {
            showProgressBar(false);
            if (user != null) {
                Log.i(TAG_SCREEN, "User is logged in as: " + user.getName());
                objConsumer = user;
                binding.tvWelcome.setText("Olá, " + user.getName());
                nameUserRes = user.getName();
                if (!user.getAddress().isEmpty()) {
                    binding.btAdressUser.setText(user.getAddress());
                } else {
                    binding.btAdressUser.setText("Adicione seu endereço");
                }
            } else {
                Log.i(TAG_SCREEN, "User is failed login");
            }
        }
    }

    private void logOut() {
        mAuth.signOut();
        binding.tvWelcome.setText("Efetue o login!");
//        binding.btLogout.setVisibility(View.INVISIBLE);
//        binding.btSignIn.setEnabled(true);
        showSignIn();
    }

    private void showSignIn() {
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void initCategory() {
        Log.i(TAG_SCREEN, "fetchData initCategory");
        new GetcategoryData().execute();
    }

    private void addCategory(List<Category> items) {
        binding.rvCategories.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvCategories.setAdapter(new CategoryAdapter(items));
    }

    private class GetcategoryData extends AsyncTaskExecutor<Void, Void, List<Category>> {
        @Override
        protected void onPreExecute() {
            showProgressBar(true);
        }

        @Override
        protected List<Category> doInBackground(Void... voids) {
            Task<QuerySnapshot> task = categoryRepository.getAll();
            try {
                QuerySnapshot querySnapshot = Tasks.await(task);
                List<Category> data = new ArrayList<>();
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    Category category = document.toObject(Category.class);
                    if (category != null) {
                        category.setId(document.getId());
                        data.add(category);
                    }
                }
                return data;

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Category> data) {
            showProgressBar(false);
            if (data != null) {
                addCategory(data);
            }
        }
    }

    private void initRestaurant() {
        Log.i(TAG_SCREEN, "fetchData initrestaurant");
        new GetRestaurantData().execute();
    }

    private class GetRestaurantData extends AsyncTaskExecutor<Void, Void, List<Restaurant>> {
        @Override
        protected void onPreExecute() {
            showProgressBar(true);
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
            showProgressBar(false);
            if (restaurants != null) {
                addRestaurant(restaurants);
            }
        }
    }

    private void addRestaurant(List<Restaurant> items) {
        binding.rvRestaurants.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
        binding.rvRestaurants.setAdapter(new RestaurantDashboardAdapter(items));
    }

    private void showProgressBar(boolean show) {
        binding.progressBarCategory.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void initPublicity() {
        Log.i(TAG_SCREEN, "fetchData initPublicity");
        new GetPublityData().execute();
    }

    private void addPublicity(List<Publicity> items) {
        binding.vpPublicity.setAdapter(new PublicityAdapter(items, binding.vpPublicity));
        binding.vpPublicity.setClipChildren(false);
        binding.vpPublicity.setClipToPadding(false);
        binding.vpPublicity.setOffscreenPageLimit(3);
        binding.vpPublicity.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));

        binding.vpPublicity.setPageTransformer(compositePageTransformer);
    }

    private class GetPublityData extends AsyncTaskExecutor<Void, Void, List<Publicity>> {
        @Override
        protected void onPreExecute() {
            showProgressBar(true);
        }

        @Override
        protected List<Publicity> doInBackground(Void... voids) {
            Task<QuerySnapshot> task = publicityRepository.getAll();
            try {
                QuerySnapshot querySnapshot = Tasks.await(task);
                List<Publicity> data = new ArrayList<>();
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    Publicity publicity = document.toObject(Publicity.class);
                    if (publicity != null) {
                        publicity.setId(document.getId());
                        data.add(publicity);
                    }
                }
                return data;

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Publicity> data) {
            showProgressBar(false);
            if (data != null) {
                addPublicity(data);
            }
        }
    }

}