package com.example.getfeed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder>{
    private List<Shop> shops;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public ShopAdapter(List<Shop> shops, OnItemClickListener listener) {
        this.shops = shops;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop, parent, false);
        return new ShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
        Shop shop = shops.get(position);
        holder.bind(shop);
    }

    @Override
    public int getItemCount() {
        return shops.size();
    }

    public Shop getShop(int position) {
        return shops.get(position);
    }

    class ShopViewHolder extends RecyclerView.ViewHolder {

        private TextView shopNameTextView;

        public ShopViewHolder(@NonNull View itemView) {
            super(itemView);
            shopNameTextView = itemView.findViewById(R.id.shop_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }

        public void bind(Shop shop) {
            shopNameTextView.setText(shop.getShopName());
        }
    }
}
