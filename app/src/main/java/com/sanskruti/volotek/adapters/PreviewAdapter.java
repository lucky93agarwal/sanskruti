package com.sanskruti.volotek.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.sanskruti.volotek.MyApplication;
import com.sanskruti.volotek.R;
import com.sanskruti.volotek.databinding.ItemPostBinding;
import com.sanskruti.volotek.databinding.ItemPostPreviewBinding;
import com.sanskruti.volotek.listener.ClickListener;
import com.sanskruti.volotek.model.PostItem;
import com.bumptech.glide.Glide;

import java.util.List;

public class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.MyViewHolder> {

    Context context;
    ClickListener<Integer> listener;

    List<PostItem> postItemList;
    int column;
    float width;
    private int itemWidth = 0;
    int selectedPosision = 0;

    public PreviewAdapter(Context context, ClickListener<Integer> listener, int column, float width) {
        this.context = context;
        this.listener = listener;
        this.column = column;
        this.width = width;
        itemWidth = MyApplication.getColumnWidth(column, width);
    }

    public void setData(List<PostItem> postItemList) {
        this.postItemList = postItemList;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPostPreviewBinding binding = ItemPostPreviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        TransitionManager.beginDelayedTransition(binding.rootView);

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        holder.binding.setPostdata(postItemList.get(position));

        holder.itemView.setOnClickListener(v -> {

            listener.onClick(position);

        });

        if (postItemList.get(position).is_video) {

            Glide.with(context.getApplicationContext()).load(postItemList.get(position).image_url).thumbnail(Glide.with(context.getApplicationContext()).load(postItemList.get(position).image_url))
                    .placeholder(R.drawable.spaceholder).into(holder.binding.ivPost);

        } else {

            Glide.with(context.getApplicationContext()).load(postItemList.get(position).thumbnail).thumbnail(Glide.with(context.getApplicationContext()).load(postItemList.get(position).thumbnail))
                    .placeholder(R.drawable.spaceholder).into(holder.binding.ivPost);

        }

    }

    @Override
    public int getItemCount() {
        if (postItemList != null && postItemList.size() > 0) {
            return postItemList.size();
        } else {
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemPostPreviewBinding binding;

        public MyViewHolder(@NonNull ItemPostPreviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
