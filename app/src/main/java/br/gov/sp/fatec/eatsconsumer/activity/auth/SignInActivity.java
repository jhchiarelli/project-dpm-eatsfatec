package br.gov.sp.fatec.eatsconsumer.activity.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import br.gov.sp.fatec.eatsconsumer.MainActivity;
import br.gov.sp.fatec.eatsconsumer.R;
import br.gov.sp.fatec.eatsconsumer.databinding.ActivitySignInBinding;
import br.gov.sp.fatec.eatsconsumer.databinding.ProgressDialogBinding;
import br.gov.sp.fatec.eatsconsumer.model.ResAuthModel;
import br.gov.sp.fatec.eatsconsumer.repository.AuthRepository;
import br.gov.sp.fatec.eatsconsumer.tasks.AsyncTaskExecutor;

public class SignInActivity extends AppCompatActivity {
    final String TAG_SCREEN = "TAG-SignInActivity";
    private ActivitySignInBinding binding;
    private AuthRepository authRepository;
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG_SCREEN, "onCreate");
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository();

        binding.btSignIn.setOnClickListener(v -> checkData());
        binding.tvCadastro.setOnClickListener(v -> showNewUser());
    }

    private void showDashboard() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void checkData() {
        Log.i(TAG_SCREEN, "checkData");
        closeKeyboard();

        String email = binding.edEmail.getText().toString();
        String senha = binding.edSenha.getText().toString();


        if (!email.isEmpty()) {
            if (!senha.isEmpty()) {
                signIn(email, senha);
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
    }

    private void signIn(String email, String senha) {
        Log.i(TAG_SCREEN, "signIn");

        new LoginTask().execute(email, senha);
    }

    public class LoginTask extends AsyncTaskExecutor<String, Void, ResAuthModel> {
        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected ResAuthModel doInBackground(String... params) {
            String email = params[0];
            String password = params[1];

            final ResAuthModel authHolder = new ResAuthModel();

            authRepository.auth(email, password, new AuthRepository.AuthCallback() {
                @Override
                public void onSuccess(ResAuthModel res) {
                    authHolder.setUser(res.getUser());
                    authHolder.setMessage(res.getMessage());
                }

                @Override
                public void onFailure(Exception e) {
                    authHolder.setUser(null);
                    authHolder.setMessage(e.getMessage());
                    Log.i(TAG_SCREEN, "Error Login -> " + e.getMessage());
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
                showDashboard();
            } else {
                Toast.makeText(SignInActivity.this, "Erro ao fazer login, " + result.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void showNewUser() {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}