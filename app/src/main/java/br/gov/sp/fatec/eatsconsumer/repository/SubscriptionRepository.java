package br.gov.sp.fatec.eatsconsumer.repository;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import br.gov.sp.fatec.eatsconsumer.models.Subscription;

public class SubscriptionRepository {
    private FirebaseFirestore db;
    private CollectionReference collection;
    private static final String COLLECTION_NAME = "subscriptions";

    public SubscriptionRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.collection = db.collection(COLLECTION_NAME);
    }

    public void addSubscription(Subscription subscription, OnSuccessListener<DocumentReference> onSuccessListener, OnFailureListener onFailureListener) {
        collection.add(subscription)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void updateSubscription(Subscription subscription, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        collection.document(subscription.getId()).update("description", subscription.getDescription(), "recurrence", subscription.getRecurrence(), "amount", subscription.getAmount(), "active", subscription.getActive(), "idUser", subscription.getIdUser())
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void deleteSubscription(String id, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        collection.document(id).delete()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public Task<QuerySnapshot> getSubscriptionUser(String id) {
        return collection.whereEqualTo("idUser", id).get();
    }
}
