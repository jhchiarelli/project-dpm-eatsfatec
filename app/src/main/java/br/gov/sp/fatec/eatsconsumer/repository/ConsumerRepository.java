package br.gov.sp.fatec.eatsconsumer.repository;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import br.gov.sp.fatec.eatsconsumer.models.Consumer;

public class ConsumerRepository {
    private FirebaseFirestore db;
    private CollectionReference collection;
    private static final String COLLECTION_NAME = "consumers";

    public ConsumerRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.collection = db.collection(COLLECTION_NAME);
    }

    public void addUser(Consumer consumer, OnSuccessListener<DocumentReference> onSuccessListener, OnFailureListener onFailureListener) {
        collection.add(consumer)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void setRestaurant(Consumer consumer, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        collection.document().set(consumer)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void getConsumer(String userId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        collection.whereEqualTo("idUser", userId).get()
                .addOnCompleteListener(onCompleteListener);
    }

    public Task<DocumentSnapshot> getConsumerUser(String id) {
        return collection.document(id).get();
    }

    public Task<QuerySnapshot> getConsumerLogged(String id) {
        return collection.whereEqualTo("idUser", id).get();
    }

    public void updateRestaurant(Consumer consumer, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        collection.document(consumer.getId()).update("name", consumer.getName(), "email", consumer.getEmail(), "phone", consumer.getPhone(), "address", consumer.getAddress(), "level", consumer.getLevel(), "urlImage", consumer.getUrlImage(), "active", consumer.getActive())
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void deleteRestaurant(String id, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        collection.document(id).delete()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void getAll(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        collection.get()
                .addOnCompleteListener(onCompleteListener);
    }

    public Task<QuerySnapshot> getAllRestaurant() {
        return collection.get();
    }

    public void getByEmail(String email, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        collection.whereEqualTo("email", email).get()
                .addOnCompleteListener(onCompleteListener);
    }
}
