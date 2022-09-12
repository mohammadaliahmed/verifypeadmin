package com.appsinventiv.verifypeadmin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.verifypeadmin.Activites.UserProfile;
import com.appsinventiv.verifypeadmin.Models.BannerModel;
import com.appsinventiv.verifypeadmin.Models.User;
import com.appsinventiv.verifypeadmin.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    Context context;
    List<User> itemList;

    public UserListAdapter(Context context, List<User> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public void setItemList(List<User> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User model = itemList.get(position);
        holder.name.setText(model.getName());
        holder.details.setText(model.getPhone());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(context, UserProfile.class);
                i.putExtra("userId",model.getPhone());
                context.startActivity(i);
            }
        });
    }
    @Override
    public int getItemCount() {
        return itemList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, details;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            details = itemView.findViewById(R.id.details);
        }
    }
}
