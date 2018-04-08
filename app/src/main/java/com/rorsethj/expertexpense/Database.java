//
// com.rorsethj.expertexpense.Database
// This Singleton class is the main delegate for storage / retrieval from the backend
//

package com.rorsethj.expertexpense;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
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

    public static void fetchAndUpdateCurrentUser() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    // MARK: Firebase Firestore
    // The root of the database is a collection of users, where each user is a document identified
    // by uid. Inside each document, several collections will exist, representing the set of
    // properties of this user.


    // Save account under  users/<current_uid>/accounts/
    public void saveNewAccount(Account account,
                               final AddNewAccountFragment.AddAccountCallback callback) {

        store.collection("users")
                .document(currentUser.getUid())
                .collection("accounts")
                .document(account.getAccountName())
                .set(account)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        callback.didAddAccount(true, null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        callback.didAddAccount(false, e);
                    }
                });
    }


    // Save transaction under  users/<current_uid>/accounts/<account_name>/transactions/
    public void saveNewTransaction(Transaction transaction, Account associatedAccount,
                                   final AddNewTransactionFragment.AddTransactionCallback callback) {


        store.collection("users")
                .document(currentUser.getUid())
                .collection("accounts")
                .document(associatedAccount.getAccountName())
                .collection("transactions")
                .document()
                .set(transaction)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        callback.didAddTransaction(true, null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        callback.didAddTransaction(false, e);
                    }
                });
    }


    // Save bill under  users/<current_uid>/bills/
    public void saveNewBill(Bill bill, final AddNewBillFragment.AddBillCallback callback) {

        store.collection("users")
                .document(currentUser.getUid())
                .collection("bills")
                .document()
                .set(bill)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        callback.didAddBill(true, null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        callback.didAddBill(false, e);
                    }
                });
    }








    /*
    // Generic function to save new document under  users/<current_uid>/<collection>/
    private void saveDocumentUnderUserCollection(
            String collection, Map<String, Object> document) throws Exception {


        // Check current user, should be set upon login
        if (currentUser == null) {
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
        }

        // Add a new document with a generated ID
        store.collection("users")
                .document(currentUser.getUid())
                .collection(collection)
                .add(document)
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
    */


    // Asynchronous method with callback to return retrieved accounts for current user
    public void getUserAccountNames(final AddNewTransactionFragment.GetAccountsCallback callback) {


        store.collection("users")
                .document(currentUser.getUid())
                .collection("accounts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        ArrayList<Account> accounts = new ArrayList<>();

                        if (task.isSuccessful()) {

                            // For each Account document
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                // Transform into Account object and add to ArrayList
                                accounts.add(document.toObject(Account.class));
                            }

                            callback.didGetAccounts(accounts, null);

                        } else {

                            // Report back Exception to caller
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            callback.didGetAccounts(accounts, task.getException());
                        }
                    }
                });
    }
}
