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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class Database {

    public interface DBGetAccountsInterface {
        void didGet(List<Account> accounts, List<String> accountIDs, Exception e);
    }

    public interface DBGetTransactionsInterface {
        void didGet(List<Transaction> transactions, Exception e);
    }

    public interface DBGetBillsInterface {
        void didGet(List<Bill> bills, Exception e);
    }


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
                .document()
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


    // Save transaction under  users/<current_uid>/transactions/
    public void saveNewTransaction(Transaction transaction,
                                   final AddNewTransactionFragment.AddTransactionCallback callback) {


        store.collection("users")
                .document(currentUser.getUid())
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
    


    // Asynchronous method with callback to return retrieved accounts for current user
    public void getUserAccounts(final DBGetAccountsInterface callback) {


        store.collection("users")
                .document(currentUser.getUid())
                .collection("accounts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        ArrayList<String> accountIDs = new ArrayList<>();
                        ArrayList<Account> accounts = new ArrayList<>();

                        if (task.isSuccessful()) {

                            // For each Account document
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Log.d(TAG, document.getId() + " => " + document.getData());

                                // Transform into Account object and add to ArrayList
                                accountIDs.add(document.getId());
                                accounts.add(document.toObject(Account.class));
                            }

                            callback.didGet(accounts, accountIDs, null);

                        } else {

                            // Report back Exception to caller
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            callback.didGet(accounts, accountIDs, task.getException());
                        }
                    }
                });
    }


    // Asynchronous method with callback to return retrieved accounts for current user
    public void getUserTransactions(final DBGetTransactionsInterface callback) {


        store.collection("users")
                .document(currentUser.getUid())
                .collection("transactions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        ArrayList<Transaction> transactions = new ArrayList<>();

                        if (task.isSuccessful()) {

                            // Return list of transactions to caller
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                transactions.add(document.toObject(Transaction.class));
                            }

                            callback.didGet(transactions, null);

                        } else {

                            // Report back Exception to caller
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            callback.didGet(transactions, task.getException());
                        }
                    }
                });
    }


    // Asynchronous method with callback to return retrieved accounts for current user
    public void getUserBills(final DBGetBillsInterface callback) {


        store.collection("users")
                .document(currentUser.getUid())
                .collection("bills")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        ArrayList<Bill> bills = new ArrayList<>();

                        if (task.isSuccessful()) {

                            // Make a list of Bills
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                bills.add(document.toObject(Bill.class));
                            }

                            callback.didGet(bills, null);

                        } else {

                            // Report back Exception to caller
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            callback.didGet(bills, task.getException());
                        }
                    }
                });
    }
}
