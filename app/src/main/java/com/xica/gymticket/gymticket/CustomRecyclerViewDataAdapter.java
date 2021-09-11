package com.xica.gymticket.gymticket;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
/**
 * Created by Jerry on 3/19/2018.
 */

public class CustomRecyclerViewDataAdapter extends Adapter<CustomRecyclerViewDataAdapter.CustomRecyclerViewHolder> {

    private List<User> viewItemList;
    public Context mContext;
    public CustomRecyclerViewDataAdapter(Context context,List<User> viewItemList) {
        this.viewItemList = viewItemList;
        mContext=context;
    }

    @Override
    public CustomRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get LayoutInflater object.
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Inflate the RecyclerView item layout xml.
        View itemView = layoutInflater.inflate(R.layout.gym_pro_list_item, parent, false);

        // Create and return our customRecycler View Holder object.
        CustomRecyclerViewHolder ret = new CustomRecyclerViewHolder(itemView);
        return ret;
    }

    @Override
    public void onBindViewHolder(CustomRecyclerViewHolder holder, final int position) {
        if(viewItemList!=null) {
            // Get car item dto in list.
            User viewItem = viewItemList.get(position);

            if(viewItem != null) {
                // Set car item title.
                holder.getNameTextView().setText(viewItem.getName());
                holder.getAddressTextView().setText(viewItem.getAddress());
                //holder.getTimingTextView().setText(viewItem.getTimings());
                RequestOptions req= new RequestOptions();
                req.placeholder(R.drawable.chat);
                Glide.with(holder.getGymIcon()).load(viewItem.getPic()).apply(req).into(holder.getGymIcon());

            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User a =viewItemList.get(position);
                    String ab=a.getName();
                    Intent intent=new Intent(mContext,GymDescription.class);
                    intent.putExtra("gym_name",ab);
                    mContext.startActivity(intent);

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        int ret = 0;
        if(viewItemList!=null)
        {
            ret = viewItemList.size();
        }
        return ret;
    }

    public class CustomRecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView,addressTextView,timingTextView;
        private ImageView gymIcon;

        public CustomRecyclerViewHolder(View itemView) {
            super(itemView);

            if(itemView != null)
            {
                nameTextView = itemView.findViewById(R.id.custom_refresh_recycler_view_text_view);
                addressTextView=itemView.findViewById(R.id.address);
                timingTextView=itemView.findViewById(R.id.timings);
                gymIcon=itemView.findViewById(R.id.GymIcon);
            }
        }

        public TextView getNameTextView() {
            return nameTextView;
        }

        public TextView getTimingTextView() {
            return timingTextView;
        }

        public ImageView getGymIcon() {

            return gymIcon;
        }

        public TextView getAddressTextView() {

            return addressTextView;
        }
    }
}
