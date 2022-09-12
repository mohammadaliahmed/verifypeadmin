package com.appsinventiv.verifypeadmin.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.verifypeadmin.Models.BannerModel;
import com.appsinventiv.verifypeadmin.Models.NotificationModel;
import com.appsinventiv.verifypeadmin.R;
import com.appsinventiv.verifypeadmin.Utils.CommonUtils;
import com.bumptech.glide.Glide;

import java.util.List;

public class BannersAdapter extends RecyclerView.Adapter<BannersAdapter.ViewHolder> {
    Context context;
    List<BannerModel> itemList;
    BannerAdapterCallbacks callbacks;
    public BannersAdapter(Context context, List<BannerModel> itemList, BannerAdapterCallbacks callbacks) {
        this.context = context;
        this.itemList = itemList;
        this.callbacks = callbacks;
    }

    public void setItemList(List<BannerModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.banner_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BannerModel model = itemList.get(position);

        holder.title.setText(model.getMessage());
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
        TextView title,url;
        ImageView image,delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            url = itemView.findViewById(R.id.url);
            image = itemView.findViewById(R.id.image);
            delete = itemView.findViewById(R.id.delete);
            title = itemView.findViewById(R.id.title);

        }
    }

    public interface BannerAdapterCallbacks{
        public void onDelete(BannerModel model);
    }

}
