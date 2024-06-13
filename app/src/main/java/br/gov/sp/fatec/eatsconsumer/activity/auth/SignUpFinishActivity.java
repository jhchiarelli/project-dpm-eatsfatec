package br.gov.sp.fatec.eatsconsumer.activity.auth;

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

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import br.gov.sp.fatec.eatsconsumer.MainActivity;
import br.gov.sp.fatec.eatsconsumer.R;
import br.gov.sp.fatec.eatsconsumer.databinding.ActivitySignUpFinishBinding;
import br.gov.sp.fatec.eatsconsumer.databinding.ProgressDialogBinding;
import br.gov.sp.fatec.eatsconsumer.models.Consumer;
import br.gov.sp.fatec.eatsconsumer.repository.ConsumerRepository;
import br.gov.sp.fatec.eatsconsumer.tasks.AsyncTaskExecutor;
import br.gov.sp.fatec.eatsconsumer.utils.PhoneNumberTextWatcher;

public class SignUpFinishActivity extends AppCompatActivity {
    final String TAG_SCREEN = "TAG-SignUpFinishActivity";
    private ActivitySignUpFinishBinding binding;
    private ConsumerRepository consumerRepository;
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG_SCREEN, "onCreate");
        binding = ActivitySignUpFinishBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        consumerRepository = new ConsumerRepository();

        String idRes = getIntent().getStringExtra("idUser");
        String nomeRes = getIntent().getStringExtra("nomeConsumer");
        String emailRes = getIntent().getStringExtra("emailConsumer");

        binding.edFone.addTextChangedListener(new PhoneNumberTextWatcher(binding.edFone));

        binding.btFinish.setOnClickListener(v -> checkData(idRes, nomeRes, emailRes));
    }

    private void checkData(String idUser, String nome, String email) {
        Log.i(TAG_SCREEN, "checkData");
        closeKeyboard();

        String fone = binding.edFone.getText().toString();
        String endereco = binding.edEndereco.getText().toString();
        String urlImage = binding.edImageUrl.getText().toString();

        if (!fone.isEmpty()) {
            if (!endereco.isEmpty()) {
                addConsumer(idUser, nome, email, fone, endereco, urlImage);
            } else {
                binding.edEndereco.setError("Informe seu endereço.");
                binding.edEndereco.requestFocus();
                Toast.makeText(this, "Informe seu endereço.", Toast.LENGTH_SHORT).show();
            }
        } else {
            binding.edFone.setError("Informe seu nome.");
            binding.edFone.requestFocus();
            Toast.makeText(this, "Informe seu número de telefone.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addConsumer(String idUser, String nome, String email, String fone, String endereco, String urlImage) {
        Log.i(TAG_SCREEN, "addConsumer");
        Consumer user = new Consumer(nome, email, fone, endereco,  "consumer", urlImage, true, idUser);
        new AddUserConsumerTask().execute(user);
    }

    private void showDashboard() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private class AddUserConsumerTask extends AsyncTaskExecutor<Consumer, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected Boolean doInBackground(Consumer... users) {
            Consumer user = users[0];
            final boolean[] result = {false};
            final boolean[] completed = {false};
            final Handler handler = new Handler(Looper.getMainLooper());

            consumerRepository.addUser(user,
                    documentReference -> {
                        handler.post(() -> {
                            if (documentReference != null && documentReference.getId() != null) {
//                                Toast.makeText(SignUpFinishActivity.this, "User added with ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                                result[0] = true;
                            } else {
                                Toast.makeText(SignUpFinishActivity.this, "Error adding user", Toast.LENGTH_SHORT).show();
                            }
                            completed[0] = true;
                        });
                    },
                    e -> {
                        handler.post(() -> {
                            Toast.makeText(SignUpFinishActivity.this, "Error adding user", Toast.LENGTH_SHORT).show();
                            completed[0] = true;
                        });
                    }
            );

            // Aguarde até que a operação seja concluída
            while (!completed[0]) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            return result[0];
            /*userRepository.addUser(user,
                    documentReference -> runOnUiThread(() -> {
                        while (documentReference.getId() == null) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        String generatedId = documentReference.getId();
                        Toast.makeText(SignUpFinishActivity.this, "User added with ID: " + generatedId, Toast.LENGTH_SHORT).show();
                    }),e -> runOnUiThread(() -> {
                        hideProgressDialog();
                        Toast.makeText(SignUpFinishActivity.this, "Error adding user", Toast.LENGTH_SHORT).show();
                    }));
            return true;*/
        }

        @Override
        protected void onPostExecute(Boolean result) {
            hideProgressDialog();
            if (result) {
                showDashboard();
                Toast.makeText(SignUpFinishActivity.this, "User successfully added.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SignUpFinishActivity.this, "Failed to add user.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetUserTask extends AsyncTaskExecutor<String, Void, Consumer> {
        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected Consumer doInBackground(String... strings) {
            String userId = strings[0];
            Task<DocumentSnapshot> task = consumerRepository.getConsumerUser(userId);
            while (!task.isComplete()) {
                // Aguardando ser completado
            }
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    return document.toObject(Consumer.class);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Consumer user) {
            hideProgressDialog();
            if (user != null) {
                Toast.makeText(SignUpFinishActivity.this, "User: " + user.getName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SignUpFinishActivity.this, "No such user", Toast.LENGTH_SHORT).show();
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