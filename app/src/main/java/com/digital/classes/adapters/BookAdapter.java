package com.digital.classes.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digital.classes.R;

import com.digital.classes.listeners.BookClickListener;
import com.digital.classes.models.Book;

import java.util.ArrayList;


public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private ArrayList<Book> books;
    private BookClickListener listener;

    public BookAdapter(ArrayList<Book> books, BookClickListener listener){
        this.books = books;
        this.listener = listener;
    }
    @NonNull
    @Override
    public BookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookAdapter.ViewHolder holder, int position) {
        holder.bind(books.get(position));
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private  View mView;
        private TextView name;
        private LinearLayout purchased_panel;

        ViewHolder(View view) {
            super(view);
            mView = view;
            name = view.findViewById(R.id.name);
            purchased_panel = view.findViewById(R.id.purchased_panel);
        }
        void bind(final Book item){
            name.setText(item.getName());
            if(item.getPrice()==0){
              //  price.setText("Free");
            }
            else{
               // price.setText("Price = \u20b9 "+item.getPrice());
            }
            if(item.isPurchased()){
                purchased_panel.setVisibility(View.VISIBLE);
            }
            else{
                purchased_panel.setVisibility(View.GONE);
            }
            mView.setOnClickListener(v -> listener.onBookClick(v,item));
        }
    }
}
