package com.digital.classes.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digital.classes.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.digital.classes.FirebaseViewModel;
import com.digital.classes.adapters.BookAdapter;
import com.digital.classes.listeners.BookClickListener;
import com.digital.classes.models.Book;

import java.util.ArrayList;

public class BookActivity extends AppCompatActivity implements BookClickListener {

    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private ArrayList<Book> books = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseViewModel viewModel;
    private String className;
    private long balance;
    private ProgressBar progressBar;
    private ArrayList<String> purchasedBooks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        viewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);
        className = getIntent().getStringExtra("classroom");
        progressBar=findViewById(R.id.booksProgressBarrr);

        setupRecyclerView();

        loadData(className);
        //   viewModel.getWallet().observe(this, wallet -> balance = wallet.getBalance());

    }

    private void loadData(final String className) {
        progressBar.setVisibility(View.VISIBLE);

        viewModel
                .getUserRef()
                .collection("books")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        purchasedBooks.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String book = document.getId();
                            purchasedBooks.add(book);
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                    setupBookList(className);
                });
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BookAdapter(books, this);
        recyclerView.setAdapter(adapter);
    }

    private void setupBookList(String className) {
        db
                .collection("classes")
                .document(className)
                .collection("Books")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        books.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            long price = document.getLong("price");
                            String book_id = document.getString("id");
                            boolean purchased = false;
                            if (purchasedBooks.contains(book_id)) {
                                purchased = true;
                            }
                            Book item = new Book(document.getId(), book_id, price, purchased);
                            books.add(item);
                        }
                        //   Collections.sort(books,new Sortbyprice());
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d("FETCHING", "Error getting documents: ", task.getException());
                    }
                });

    }

    @Override
    public void onBookClick(final View view, final Book book) {

        Intent intent = new Intent(this, ChaptersActivity.class);
        intent.putExtra("classroom", className);
        intent.putExtra("book", book.getName());
        startActivity(intent);
     /*   if(book.isPurchased()||book.getPrice()==0){
            Intent intent = new Intent(this,ChaptersActivity.class);
            intent.putExtra("classroom",className);
            intent.putExtra("book",book.getName());
            startActivity(intent);
        }
        else{
            AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                    .setTitle("Book Description")
                    .setMessage("It contains full chapters with solutions.\nPrice = \u20b9 "+book.getPrice())
                    .setPositiveButton("Buy Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(balance<book.getPrice()){
                                Snackbar.make(view,"Balance not sufficient.",Snackbar.LENGTH_LONG).setAction("Add Balance", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(BookActivity.this,AddMoneyActivity.class);
                                        startActivity(intent);
                                    }
                                }).show();
                                dialog.dismiss();
                            }
                            else{
                                Snackbar.make(view,"Book purchased successfully.",Snackbar.LENGTH_LONG).show();
                                viewModel.updateBalance(-book.getPrice());
                                purchasedBooks.add(book.getId());
                                Map<String,String> data = new HashMap<>();
                                data.put("id",book.getId());
                                viewModel.getUserRef().collection("books").document(book.getId()).set(data);
                                loadData(className);
                                dialog.dismiss();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }*/
    }

}


