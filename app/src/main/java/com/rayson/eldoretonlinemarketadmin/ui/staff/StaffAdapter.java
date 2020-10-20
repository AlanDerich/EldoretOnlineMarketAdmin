package com.rayson.eldoretonlinemarketadmin.ui.staff;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.rayson.eldoretonlinemarketadmin.Common.StaffDetails;
import com.rayson.eldoretonlinemarketadmin.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.ViewHolder> {

    private static final String TAG = "MainRecyclerViewAd";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //vars
    private List<StaffDetails> mStaff;
    private Context mContext;
    private ConstraintLayout mainL;
    public StaffAdapter(Context context, List<StaffDetails> staff, ConstraintLayout mainL) {
        mContext = context;
        mStaff = staff;
        this.mainL=mainL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_staff, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvName.setText(mStaff.get(position).getName());
        holder.tvEmail.setText(mStaff.get(position).getEmail());
        holder.tvRole.setText(mStaff.get(position).getRole());
        if (mStaff.get(position).getStatus()==0){
            holder.tvStatus.setText("Available");
        }
        else {
            holder.tvStatus.setText("Engaged");
        }

    }
    public static String encode(String date){
        return date.replace("/",",");
    }



    @Override
    public int getItemCount() {
        return mStaff.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvName,tvRole,tvEmail,tvStatus;
        ImageView userImg;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.textViewStaffName);
            userImg = itemView.findViewById(R.id.imageViewStaffImage);
            tvRole = itemView.findViewById(R.id.textViewStaffRole);
            tvEmail = itemView.findViewById(R.id.textViewStaffEmail);
            tvStatus = itemView.findViewById(R.id.textViewStaffStatus);
            cardView = itemView.findViewById(R.id.card_view_orders);
        }
    }
}