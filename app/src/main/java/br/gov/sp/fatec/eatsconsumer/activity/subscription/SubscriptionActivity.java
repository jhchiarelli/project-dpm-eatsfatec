package br.gov.sp.fatec.eatsconsumer.activity.subscription;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import br.gov.sp.fatec.eatsconsumer.R;
import br.gov.sp.fatec.eatsconsumer.databinding.ActivityConsumerDataBinding;
import br.gov.sp.fatec.eatsconsumer.databinding.ActivityConsumerProfileBinding;
import br.gov.sp.fatec.eatsconsumer.databinding.ActivitySubscriptionBinding;
import br.gov.sp.fatec.eatsconsumer.models.Consumer;
import br.gov.sp.fatec.eatsconsumer.repository.ConsumerRepository;

public class SubscriptionActivity extends AppCompatActivity {
    final String TAG_SCREEN = "TAG-SubscriptionActivity";
    private ActivitySubscriptionBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG_SCREEN, "onCreate");
        binding = ActivitySubscriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Consumer consumer = (Consumer)getIntent().getSerializableExtra("objConsumer");

        binding.btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }
}