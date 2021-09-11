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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class GymProListAdapter extends Adapter<GymProListAdapter.GymListHolder> {

    private List<User> viewItemList;
    private TextView nameTextView,addressTextView,timingTextView;
    DatabaseReference dbr;
    String usertype;


    Context mContext;
    public GymProListAdapter(Context context, List<User> viewItemList) {
        this.viewItemList = viewItemList;
        mContext=context;
    }

    @Override
    public GymListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get LayoutInflater object.
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Inflate the RecyclerView item layout xml.
        View itemView = layoutInflater.inflate(R.layout.view_all_gym_list, parent, false);

        // Create and return our customRecycler View Holder object.
        GymListHolder ret = new GymListHolder(itemView);
        return ret;
    }

    @Override
    public void onBindViewHolder(GymListHolder holder, final int position) {
        if(viewItemList!=null) {
            // Get car item dto in list.
            final User viewItem = viewItemList.get(position);

            usertype = viewItem.getUsertype();

            if(viewItem != null) {
                // Set car item title.
                RequestOptions req= new RequestOptions();
                req.placeholder(R.drawable.chat);
                Glide.with(holder.getGymIcon()).load(viewItem.getPic()).apply(req).into(holder.getGymIcon());
                holder.getNameTextView().setText(viewItem.getName());

               // holder.getAddressTextView().setText(viewItem.getAddress());
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("usertype",usertype);
                    User a =viewItemList.get(position);
                    String ab=a.getName();
                    if(usertype.equals("gym")) {
                        Intent intent = new Intent(mContext, GymDescription.class);
                        intent.putExtra("professional_name", ab);
                        mContext.startActivity(intent);
                    }
                    else if(usertype.equals("professional"))
                    {
                        Intent intent = new Intent(mContext, ChatActivity.class);
                        intent.putExtra("professional_name", ab);
                        mContext.startActivity(intent);
                    }
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


    public class GymListHolder extends RecyclerView.ViewHolder {

        private ImageView gymIcon;

        public GymListHolder(View itemView) {
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
