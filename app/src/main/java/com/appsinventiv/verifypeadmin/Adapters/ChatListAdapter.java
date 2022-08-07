package com.appsinventiv.verifypeadmin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.appsinventiv.verifypeadmin.Activites.ChatScreen;
import com.appsinventiv.verifypeadmin.Models.ChatModel;
import com.appsinventiv.verifypeadmin.Models.SupportChatModel;
import com.appsinventiv.verifypeadmin.R;
import com.appsinventiv.verifypeadmin.Utils.CommonUtils;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    Context context;
    List<SupportChatModel> itemList;

    public ChatListAdapter(Context context, List<SupportChatModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public void setItemList(List<SupportChatModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_list_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SupportChatModel model = itemList.get(position);

        holder.name.setText(model.getName());
        holder.time.setText(CommonUtils.getFormattedDate(model.getTime()));
        holder.message.setText(model.getMessage());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ChatScreen.class);
                if(model.getSenderId().equals("admin")){
                    i.putExtra("phone", model.getSendTo());
                    context.startActivity(i);
                }else{
                    i.putExtra("phone", model.getSenderId());
                    context.startActivity(i);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, message, time;
        CircleImageView picture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
            picture = itemView.findViewById(R.id.picture);
            name = itemView.findViewById(R.id.name);
        }
    }


}
