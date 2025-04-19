package com.digital.classes.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digital.classes.R;
import com.digital.classes.listeners.ClassClickListener;
import com.digital.classes.models.Class;

import java.util.ArrayList;


public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> {

    private ArrayList<Class> classes;
    private ClassClickListener listener;

    public ClassAdapter(ArrayList<Class> classes, ClassClickListener listener){
        this.classes = classes;
        this.listener = listener;
    }
    @NonNull
    @Override
    public ClassAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassAdapter.ViewHolder holder, int position) {
        holder.bind(classes.get(position));
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private TextView name;

        ViewHolder(View view) {
            super(view);
            mView = view;
            name = view.findViewById(R.id.name);

        }
        void bind(final Class item){
            name.setText(item.getName());
            mView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}
