package br.gov.sp.fatec.eatsconsumer.repository;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import br.gov.sp.fatec.eatsconsumer.models.Restaurant;

public class RestaurantRepository {
    private FirebaseFirestore db;
    private CollectionReference collection;
    private static final String COLLECTION_NAME = "restaurants";

    public RestaurantRepository() {
        db = FirebaseFirestore.getInstance();
        collection = db.collection(COLLECTION_NAME);
    }

    public void addUser(Restaurant restaurant, OnSuccessListener<DocumentReference> onSuccessListener, OnFailureListener onFailureListener) {
        collection.add(restaurant)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void setRestaurant(Restaurant restaurant, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        collection.document().set(restaurant)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void getRestaurant(String userId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        collection.document(userId).get()
                .addOnCompleteListener(onCompleteListener);
    }

    public Task<DocumentSnapshot> getRestaurant(String id) {
        return collection.document(id).get();
    }

    public void updateRestaurant(Restaurant restaurant, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        collection.document(restaurant.getId()).update("name", restaurant.getName(), "email", restaurant.getEmail(), "phone", restaurant.getPhone(), "address", restaurant.getAddress(), "level", restaurant.getLevel(), "urlImage", restaurant.getUrlImage(), "rating", restaurant.getRating(),  "active", restaurant.getActive())
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

