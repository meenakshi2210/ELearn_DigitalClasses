package com.digital.classes;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.digital.classes.models.Wallet;

public class FirebaseViewModel extends ViewModel {
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DocumentReference userRef = firestore.collection("users").document(user.getUid());
    private MutableLiveData<Wallet> wallet = new MutableLiveData<>(new Wallet(0));

    public DocumentReference getUserRef() {
        return userRef;
    }

    public Task<Void> updateBalance(long amount){
        return userRef.update("balance", FieldValue.increment(amount));
    }

    public LiveData<Wallet> getWallet() {
        userRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.w("WALLET", "Listen failed.", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                Wallet temp = documentSnapshot.toObject(Wallet.class);
                if(wallet==null){
                    wallet = new MutableLiveData<>();
                    wallet.setValue(temp);
                }
                else{
                    wallet.postValue(temp);
                }
            }
            else{
                userRef.set(new Wallet(0));
            }
        });
        return wallet;
    }

}
