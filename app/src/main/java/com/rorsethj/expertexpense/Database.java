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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;

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
    //private FirebaseFirestore store;
    private DatabaseReference store;


    private Database() {
        //store = FirebaseFirestore.getInstance();
        store = FirebaseDatabase.getInstance().getReference();
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

        store.child("users")
                .child(currentUser.getUid())
                .child("accounts")
                .push()
                .setValue(account)
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


        store.child("users")
                .child(currentUser.getUid())
                .child("transactions")
                .push()
                .setValue(transaction)
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

        store.child("users")
                .child(currentUser.getUid())
                .child("bills")
                .push()
                .setValue(bill)
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


        store.child("users")
                .child(currentUser.getUid())
                .child("accounts")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        ArrayList<String> accountIDs = new ArrayList<>();
                        ArrayList<Account> accounts = new ArrayList<>();

                        for (DataSnapshot child : dataSnapshot.getChildren()) {

                            Log.d(TAG, child.getKey() + " => " + child.getValue());

                            Account account = child.getValue(Account.class);
                            accountIDs.add(child.getKey());
                            accounts.add(account);
                        }

                        callback.didGet(accounts, accountIDs, null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    // Asynchronous method with callback to return retrieved accounts for current user
    public void getUserTransactions(final DBGetTransactionsInterface callback) {


        store.child("users")
                .child(currentUser.getUid())
                .child("transactions")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        ArrayList<Transaction> transactions = new ArrayList<>();

                        for (DataSnapshot child : dataSnapshot.getChildren()) {

                            Log.d(TAG, child.getKey() + " => " + child.getValue());

                            Transaction transaction = child.getValue(Transaction.class);
                            transactions.add(transaction);
                        }

                        callback.didGet(transactions, null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    // Asynchronous method with callback to return retrieved accounts for current user
    public void getUserBills(final DBGetBillsInterface callback) {


        store.child("users")
                .child(currentUser.getUid())
                .child("bills")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        ArrayList<Bill> bills = new ArrayList<>();

                        for (DataSnapshot child : dataSnapshot.getChildren()) {

                            Log.d(TAG, child.getKey() + " => " + child.getValue());

                            Bill bill = child.getValue(Bill.class);
                            bills.add(bill);
                        }

                        callback.didGet(bills, null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
