package br.gov.sp.fatec.eatsconsumer.repository;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.gov.sp.fatec.eatsconsumer.model.ResAuthModel;

public class AuthRepository {
    private final FirebaseAuth firebaseAuth;

    public AuthRepository() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public void auth(String email, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        final ResAuthModel resAuthModel = new ResAuthModel(firebaseAuth.getCurrentUser(), "Usuário autenticado com sucesso.");
                        callback.onSuccess(resAuthModel);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void createAuth(String email, String password, AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        final ResAuthModel resAuthModel = new ResAuthModel(firebaseAuth.getCurrentUser(), "Usuário criado com sucesso.");
                        callback.onSuccess(resAuthModel);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public interface AuthCallback {
        void onSuccess(ResAuthModel user);
        void onFailure(Exception e);
    }
}

