package br.gov.sp.fatec.eatsconsumer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import br.gov.sp.fatec.eatsconsumer.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    final String TAG_SCREEN = "TAG-MainActivity";
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG_SCREEN, "onCreate");
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}