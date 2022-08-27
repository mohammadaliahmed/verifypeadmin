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

import com.appsinventiv.verifypeadmin.Activites.ChatScreen;
import com.appsinventiv.verifypeadmin.Models.NotificationModel;
import com.appsinventiv.verifypeadmin.Models.SupportChatModel;
import com.appsinventiv.verifypeadmin.R;
import com.appsinventiv.verifypeadmin.Utils.CommonUtils;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    Context context;
    List<NotificationModel> itemList;
    NotificationAdapterCallbacks callbacks;
    public NotificationsAdapter(Context context, List<NotificationModel> itemList,NotificationAdapterCallbacks callbacks) {
        this.context = context;
        this.itemList = itemList;
        this.callbacks = callbacks;
    }

    public void setItemList(List<NotificationModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel model = itemList.get(position);

        holder.title.setText(model.getTitle());
        holder.time.setText(CommonUtils.getFormattedDate(model.getTime()));
        holder.description.setText(model.getDescription());
        holder.url.setText(model.getUrl());
        Glide.with(context).load(model.getImageUrl()).into(holder.image);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callbacks.onDelete(model);
            }
        });



    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, time,url;
        ImageView image,delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.description);
            time = itemView.findViewById(R.id.time);
            image = itemView.findViewById(R.id.image);
            delete = itemView.findViewById(R.id.delete);
            title = itemView.findViewById(R.id.title);
            url = itemView.findViewById(R.id.url);
        }
    }

    public interface NotificationAdapterCallbacks{
        public void onDelete(NotificationModel model);
    }

}
