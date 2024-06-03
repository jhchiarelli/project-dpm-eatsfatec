package br.gov.sp.fatec.eatsconsumer.activity.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import br.gov.sp.fatec.eatsconsumer.R;
import br.gov.sp.fatec.eatsconsumer.databinding.ActivitySignUpBinding;
import br.gov.sp.fatec.eatsconsumer.databinding.ProgressDialogBinding;
import br.gov.sp.fatec.eatsconsumer.model.ResAuthModel;
import br.gov.sp.fatec.eatsconsumer.repository.AuthRepository;
import br.gov.sp.fatec.eatsconsumer.tasks.AsyncTaskExecutor;

public class SignUpActivity extends AppCompatActivity {
    final String TAG_SCREEN = "TAG-SignUpActivity";
    private ActivitySignUpBinding binding;
    private AuthRepository authRepository;
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG_SCREEN, "onCreate");
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository();

        binding.btCreate.setOnClickListener(v -> checkData());
        binding.btBack.setOnClickListener(v -> showDashboard());
    }

    private void showDashboard() {
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void checkData() {
        Log.i(TAG_SCREEN, "checkData");
        closeKeyboard();

        String nome = binding.edNome.getText().toString();
        String email = binding.edEmail.getText().toString();
        String senha = binding.edSenha.getText().toString();

        if (!nome.isEmpty()) {
            if (!email.isEmpty()) {
                if (!senha.isEmpty()) {
                    signUp(email, senha);
                } else {
                    binding.edSenha.setError("Informe sua senha.");
                    binding.edSenha.requestFocus();
                    Toast.makeText(this, "Informe sua senha.", Toast.LENGTH_SHORT).show();
                }
            } else {
                binding.edEmail.setError("Informe seu email.");
                binding.edEmail.requestFocus();
                Toast.makeText(this, "Informe seu email.", Toast.LENGTH_SHORT).show();
            }
        } else {
            binding.edNome.setError("Informe seu nome.");
            binding.edNome.requestFocus();
            Toast.makeText(this, "Informe seu nome.", Toast.LENGTH_SHORT).show();
        }
    }

    private void signUp(String email, String senha) {
        Log.i(TAG_SCREEN, "signUp");

        new SignInTask().execute(email, senha);
    }

    public class SignInTask extends AsyncTaskExecutor<String, Void, ResAuthModel> {
        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected ResAuthModel doInBackground(String... params) {
            String email = params[0];
            String password = params[1];

            final ResAuthModel authHolder = new ResAuthModel();

            authRepository.createAuth(email, password, new AuthRepository.AuthCallback() {
                @Override
                public void onSuccess(ResAuthModel res) {
                    authHolder.setUser(res.getUser());
                    authHolder.setMessage(res.getMessage());
                }

                @Override
                public void onFailure(Exception e) {
                    authHolder.setUser(null);
                    authHolder.setMessage(e.getMessage());
                    Log.i(TAG_SCREEN, "Error Create User -> " + e.getMessage());
                }
            });

            // Espera até que o login seja completado ou falhe
            while (authHolder.getMessage() == null) {
                try {
                    Thread.sleep(100); // aguarde um pouco antes de verificar novamente
                } catch (InterruptedException e) {
                    Log.i(TAG_SCREEN, "Error LoginInterruptedException -> " + e.getMessage());
                }
            }

            return authHolder;
        }

        @Override
        protected void onPostExecute(ResAuthModel result) {
            hideProgressDialog();
            if (result.getUser() != null) {
                openSignUpFinish(result.getUser().getUid(), result.getUser().getEmail());
            } else {
                Toast.makeText(SignUpActivity.this, "Erro ao criar login, " + result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void openSignUpFinish(String idUser, String email) {
        Log.i(TAG_SCREEN, "openSignUpFinish");
        Intent intent = new Intent(getApplicationContext(), SignUpFinishActivity.class);
        intent.putExtra("idUser", idUser);
        intent.putExtra("emailConsumer", email);
        intent.putExtra("nomeConsumer", binding.edNome.getText().toString());
        startActivity(intent);
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