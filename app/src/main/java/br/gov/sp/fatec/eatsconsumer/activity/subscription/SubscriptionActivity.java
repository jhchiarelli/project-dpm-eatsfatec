package br.gov.sp.fatec.eatsconsumer.activity.subscription;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.ExecutionException;

import br.gov.sp.fatec.eatsconsumer.MainActivity;
import br.gov.sp.fatec.eatsconsumer.R;
import br.gov.sp.fatec.eatsconsumer.activity.auth.SignUpFinishActivity;
import br.gov.sp.fatec.eatsconsumer.databinding.ActivityConsumerDataBinding;
import br.gov.sp.fatec.eatsconsumer.databinding.ActivityConsumerProfileBinding;
import br.gov.sp.fatec.eatsconsumer.databinding.ActivitySubscriptionBinding;
import br.gov.sp.fatec.eatsconsumer.databinding.ProgressDialogBinding;
import br.gov.sp.fatec.eatsconsumer.models.Consumer;
import br.gov.sp.fatec.eatsconsumer.models.Subscription;
import br.gov.sp.fatec.eatsconsumer.repository.ConsumerRepository;
import br.gov.sp.fatec.eatsconsumer.repository.SubscriptionRepository;
import br.gov.sp.fatec.eatsconsumer.tasks.AsyncTaskExecutor;
import br.gov.sp.fatec.eatsconsumer.utils.DataFormatter;

public class SubscriptionActivity extends AppCompatActivity {
    final String TAG_SCREEN = "TAG-SubscriptionActivity";
    private ActivitySubscriptionBinding binding;
    private AlertDialog progressDialog;
    private SubscriptionRepository subscriptionRepository;
    private Subscription objSubscription;
    private Consumer consumer;
    private int subscriptionActived;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG_SCREEN, "onCreate");
        binding = ActivitySubscriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        consumer = (Consumer)getIntent().getSerializableExtra("objConsumer");

        subscriptionActived = 0;

        subscriptionRepository = new SubscriptionRepository();

        binding.btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        binding.btNewSub.setOnClickListener(v -> {
             if (subscriptionActived == 0) {
                Subscription sub = new Subscription();
                sub.setActive(true);
                sub.setAmount(12.9);
                sub.setDescription("Assinatura Eats");
                sub.setRecurrence("Mensal");
                sub.setIdUser(consumer.getId());
                new AddSubscriptionConsumerTask().execute(sub);
            } else if (subscriptionActived == 1) {
                 objSubscription.setActive(false);
                 new UpdateSubscriptionTask().execute(objSubscription);
             } else {
                 objSubscription.setActive(true);
                 new UpdateSubscriptionTask().execute(objSubscription);
             }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG_SCREEN, "Entry onStart");
        new GetSubscriptionTask().execute(consumer.getId());
    }

    private void callDashboard() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private class UpdateSubscriptionTask extends AsyncTaskExecutor<Subscription, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected Boolean doInBackground(Subscription... subscriptions) {
            Subscription subscription = subscriptions[0];
            final boolean[] result = {false};
            final boolean[] completed = {false};
            final Handler handler = new Handler(Looper.getMainLooper());

            subscriptionRepository.updateSubscription(subscription,
                    aVoid -> {
                        handler.post(() -> {
                            result[0] = true;
                            completed[0] = true;
                        });
                    },
                    e -> {
                        handler.post(() -> {
                            Toast.makeText(SubscriptionActivity.this, "Error update Subscription", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SubscriptionActivity.this, "Assinatura atualizado com sucesso", Toast.LENGTH_SHORT).show();
                callDashboard();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else {
                Toast.makeText(SubscriptionActivity.this, "Erro ao atualizar Assinatura", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class AddSubscriptionConsumerTask extends AsyncTaskExecutor<Subscription, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected Boolean doInBackground(Subscription... subs) {
            Subscription sub = subs[0];
            final boolean[] result = {false};
            final boolean[] completed = {false};
            final Handler handler = new Handler(Looper.getMainLooper());

            subscriptionRepository.addSubscription(sub,
                    documentReference -> {
                        handler.post(() -> {
                            if (documentReference != null && documentReference.getId() != null) {
                                result[0] = true;
                            } else {
                                Toast.makeText(SubscriptionActivity.this, "Error adding subscription", Toast.LENGTH_SHORT).show();
                            }
                            completed[0] = true;
                        });
                    },
                    e -> {
                        handler.post(() -> {
                            Toast.makeText(SubscriptionActivity.this, "Error adding subscription", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    protected void onPostExecute(Boolean result) {
        hideProgressDialog();
        if (result) {
            callDashboard();
            Toast.makeText(SubscriptionActivity.this, "Assinatura realizada.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(SubscriptionActivity.this, "Failed Assinatura.", Toast.LENGTH_SHORT).show();
        }
    }
    }

    private class GetSubscriptionTask extends AsyncTaskExecutor<String, Void, Subscription> {
        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected Subscription doInBackground(String... strings) {
            String userId = strings[0];
            Log.d(TAG_SCREEN, "Buscando idUser: " + userId);
            Task<QuerySnapshot> task = subscriptionRepository.getSubscriptionUser(userId);
            try {
                QuerySnapshot querySnapshot = Tasks.await(task);
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    Subscription subscription = document.toObject(Subscription.class);
                    Log.d(TAG_SCREEN, "Sub: " + subscription.getDescription());
                    if (subscription != null) {
                        subscription.setId(document.getId());
                        return subscription;
                    }
                }

                return null;
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Subscription subscription) {
            hideProgressDialog();
            if (subscription != null) {
                Log.i(TAG_SCREEN, "Sub as: " + subscription.getDescription());
                objSubscription = subscription;
                binding.tvSubscriptionName.setText(subscription.getDescription());
                binding.tvSubscriptionPrice.setText(DataFormatter.formatCurrency(subscription.getAmount()));
                if (subscription.getActive()) {
                    subscriptionActived = 1;
                    binding.btNewSub.setText("Cancelar assinatura");
                } else {
                    subscriptionActived = 2;
                    binding.btNewSub.setText("Reativar assinatura");
                }
            } else {
                subscriptionActived = 0;
                binding.btNewSub.setText("Realizar assinatura");
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