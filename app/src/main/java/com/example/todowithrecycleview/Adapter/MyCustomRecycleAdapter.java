package com.example.todowithrecycleview.Adapter;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todowithrecycleview.MainActivity;
import com.example.todowithrecycleview.Model.PersonDetails;
import com.example.todowithrecycleview.RecyclerViewClickInterface;
import com.example.todowithrecycleview.Show_data;
import com.example.todowithrecycleview.databinding.CustomListItemBinding;

import java.util.List;

public class MyCustomRecycleAdapter extends RecyclerView.Adapter<MyCustomRecycleAdapter.MyViewHolder> {

    Context context;
    List<PersonDetails> personDetails;
    private RecyclerViewClickInterface recyclerViewClickInterface;

    public MyCustomRecycleAdapter(Context context, List<PersonDetails> personDetails,RecyclerViewClickInterface recyclerViewClickInterface) {
        this.context = context;
        this.personDetails = personDetails;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        CustomListItemBinding binding = CustomListItemBinding.inflate(LayoutInflater.from(context),
                parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(binding);
        return myViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

       holder.binding.tvTitle.setText(personDetails.get(position).getTitle());
       holder. binding.tvDiscription.setText(personDetails.get(position).getDescription());
       holder.binding.tvDatepik.setText(personDetails.get(position).getDatepicker());
       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

           }
       });

       holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View view) {

               return false;
           }
       });
    }

    @Override
    public int getItemCount() {
        ;
       return personDetails.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

       CustomListItemBinding binding;

        public MyViewHolder(CustomListItemBinding binding) {

            super(binding.getRoot());
            this.binding = binding;

        }
    }

}