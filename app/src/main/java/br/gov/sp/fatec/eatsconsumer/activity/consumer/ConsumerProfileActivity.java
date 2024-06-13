package br.gov.sp.fatec.eatsconsumer.activity.consumer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.gov.sp.fatec.eatsconsumer.MainActivity;
import br.gov.sp.fatec.eatsconsumer.R;
import br.gov.sp.fatec.eatsconsumer.databinding.ActivityConsumerProfileBinding;
import br.gov.sp.fatec.eatsconsumer.models.Consumer;

public class ConsumerProfileActivity extends AppCompatActivity {
    final String TAG_SCREEN = "TAG-ConsumerProfileActivity";
    private ActivityConsumerProfileBinding binding;
    private FirebaseAuth mAuth;
    String nameRes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG_SCREEN, "onCreate");
        binding = ActivityConsumerProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Consumer consumer = (Consumer)getIntent().getSerializableExtra("objConsumer");

        if (consumer != null) {
            binding.tvNameUser.setText(consumer.getName());
            nameRes = consumer.getName();
        }

        mAuth = FirebaseAuth.getInstance();

        binding.btUserData.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ConsumerDataActivity.class);
            intent.putExtra("objConsumer", consumer);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        binding.btBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        binding.btLogout.setOnClickListener(v -> {
            mAuth.signOut();
            callDashboard();
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG_SCREEN, "Entry onStart");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            binding.tvNameUser.setText(nameRes);
            binding.btLogout.setVisibility(View.VISIBLE);
            Log.i(TAG_SCREEN, "User is logged in");
            Log.i(TAG_SCREEN, "User ID: " + currentUser.getUid());
            Log.i(TAG_SCREEN, "User Email: " + currentUser.getEmail());
        } else {
            Log.i(TAG_SCREEN, "Efetue o Login");
//            binding.btLogout.setVisibility(View.INVISIBLE);
//            binding.btSignIn.setEnabled(true);
//            showSignIn();
        }
    }

    private void callDashboard() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}