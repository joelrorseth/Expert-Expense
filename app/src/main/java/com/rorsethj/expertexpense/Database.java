//
// com.rorsethj.expertexpense.Database
// This Singleton class is the main delegate for storage / retrieval from the backend
//

package com.rorsethj.expertexpense;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class Database {


    private static Database db = null;
    private static FirebaseUser currentUser = null;
    private FirebaseFirestore store;


    private Database() {
        store = FirebaseFirestore.getInstance();
    }

    public static Database getCurrentUserDatabase() {
        if(db == null) { db = new Database(); }
        return db;
    }

    // Set the single current user for this singleton database
    public static void setCurrentUser(FirebaseUser user) {
        currentUser = user;
    }


    // MARK: Firebase Firestore
    // The root of the database is a collection of users, where each user is a document identified
    // by uid. Inside each document, several collections will exist, representing the set of
    // properties of this user.

    public void saveNewAccount(Account account) throws Exception {

        if (currentUser == null) {
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
        }

        // Create a new map representation of the account
        Map<String, Object> accountMap = account.toMap();

        // Add a new document with a generated ID
        store.collection("users")
                .document(currentUser.getUid())
                .collection("accounts")
                .add(accountMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {

                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }

                }).addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);

                        try {
                            throw e;
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

}
