package br.gov.sp.fatec.eatsconsumer.activity.consumer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import br.gov.sp.fatec.eatsconsumer.MainActivity;
import br.gov.sp.fatec.eatsconsumer.R;
import br.gov.sp.fatec.eatsconsumer.databinding.ActivityConsumerDataBinding;
import br.gov.sp.fatec.eatsconsumer.databinding.ProgressDialogBinding;
import br.gov.sp.fatec.eatsconsumer.models.Consumer;
import br.gov.sp.fatec.eatsconsumer.repository.ConsumerRepository;
import br.gov.sp.fatec.eatsconsumer.tasks.AsyncTaskExecutor;
import br.gov.sp.fatec.eatsconsumer.utils.PhoneNumberTextWatcher;

public class ConsumerDataActivity extends AppCompatActivity {
    final String TAG_SCREEN = "TAG-ConsumerDataActivity";
    private ActivityConsumerDataBinding binding;
    private AlertDialog progressDialog;
    private ConsumerRepository consumerRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG_SCREEN, "onCreate");
        binding = ActivityConsumerDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

         Consumer consumer = (Consumer)getIntent().getSerializableExtra("objConsumer");
         consumerRepository = new ConsumerRepository();

         if (consumer != null) {
             binding.tvEndereco.setText(consumer.getAddress());
             binding.tvFone.setText(consumer.getPhone());
             binding.tvImageUrl.setText(consumer.getUrlImage());
         }

        binding.tvFone.addTextChangedListener(new PhoneNumberTextWatcher(binding.tvFone));

        binding.btUpdateData.setOnClickListener(v -> checkData(consumer));

         binding.btBack.setOnClickListener(v -> {
             finish();
             overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
         });

    }

    private void checkData(Consumer consumer) {
        Log.i(TAG_SCREEN, "checkData");
        closeKeyboard();

        String fone = binding.tvFone.getText().toString();
        String endereco = binding.tvEndereco.getText().toString();
        String urlImage = binding.tvImageUrl.getText().toString();

        if (!endereco.isEmpty()) {
            if (!fone.isEmpty()) {
                updateConsumer(consumer.getId(), consumer.getName(), fone, endereco, urlImage);
            } else {
                binding.tvFone.setError("Informe seu Fone.");
                binding.tvFone.requestFocus();
                Toast.makeText(this, "Informe seu Fone.", Toast.LENGTH_SHORT).show();
            }
        } else {
            binding.tvEndereco.setError("Informe seu endereço.");
            binding.tvEndereco.requestFocus();
            Toast.makeText(this, "Informe seu endereço.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateConsumer(String id, String nome, String fone, String endereco, String urlImage) {
        Log.i(TAG_SCREEN, "addRestaurant");
        Consumer consumer = new Consumer(nome, fone, endereco, urlImage, true);
        consumer.setId(id);
        new UpdateConsumerTask().execute(consumer);
    }

    private class UpdateConsumerTask extends AsyncTaskExecutor<Consumer, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected Boolean doInBackground(Consumer... restaurants) {
            Consumer consumer = restaurants[0];
            final boolean[] result = {false};
            final boolean[] completed = {false};
            final Handler handler = new Handler(Looper.getMainLooper());

            consumerRepository.updateRestaurant(consumer,
                    aVoid -> {
                        handler.post(() -> {
                            result[0] = true;
                            completed[0] = true;
                        });
                    },
                    e -> {
                        handler.post(() -> {
                            Toast.makeText(ConsumerDataActivity.this, "Error update Consumer", Toast.LENGTH_SHORT).show();
                            completed[0] = true;
                        });
                    });

            // Aguarde até que a operação seja concluída
            while (!completed[0]) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            return result[0];
        }
        @Override
        protected void onPostExecute(Boolean result) {
            hideProgressDialog();
            if (result) {
                Toast.makeText(ConsumerDataActivity.this, "Usuário atualizado com sucesso", Toast.LENGTH_SHORT).show();
                callDashboard();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else {
                Toast.makeText(ConsumerDataActivity.this, "Erro ao atualizar Usuário", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void callDashboard() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

    private void closeKeyboard() {
        // Obtem o servico de entrada
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // Verifica se o teclado esta aberto
        if (imm != null && getCurrentFocus() != null) {
            // Fecha o teclado
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}