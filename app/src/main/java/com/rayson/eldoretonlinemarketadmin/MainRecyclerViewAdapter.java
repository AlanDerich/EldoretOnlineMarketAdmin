package com.rayson.eldoretonlinemarketadmin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rayson.eldoretonlinemarketadmin.ui.items.ItemsFragment;
import com.rayson.eldoretonlinemarketadmin.ui.orders.OrdersFragment;
import com.rayson.eldoretonlinemarketadmin.ui.staff.StaffFragment;

import java.util.ArrayList;


public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "MainRecyclerViewAd";

    //vars
    private ArrayList<Menus> mProducts = new ArrayList<>();
    private Context mContext;
    private ConstraintLayout mainL;
    public MainRecyclerViewAdapter(Context context, ArrayList<Menus> products, ConstraintLayout mainL) {
        mContext = context;
        mProducts = products;
        this.mainL=mainL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        RequestOptions requestOptions = new RequestOptions()
                        .placeholder(R.drawable.ic_launcher_background);
        Glide.with(mContext)
                .setDefaultRequestOptions(requestOptions)
                .load(mProducts.get(position).getItemImg())
                .into(holder.image);
        holder.tvName.setText(mProducts.get(position).getItemName());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                mainL.removeAllViews();
                switch (position) {
            case 0:
                Fragment newFragment = new OrdersFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,newFragment).addToBackStack(null).commit();
                break;
            case 1:
                Fragment fragmentStaff = new StaffFragment();
                FragmentTransaction transactionStaff = activity.getSupportFragmentManager().beginTransaction();
                transactionStaff.replace(R.id.nav_host_fragment,fragmentStaff);
                transactionStaff.addToBackStack(null);
                //newFragment.setArguments(args);
                transactionStaff.commit();
                break;
            case 2:
                Fragment fragmentItems = new ItemsFragment();
                FragmentTransaction transactionItems = activity.getSupportFragmentManager().beginTransaction();
                transactionItems.replace(R.id.nav_host_fragment,fragmentItems);
                transactionItems.addToBackStack(null);
                //newFragment.setArguments(args);
                transactionItems.commit();
                break;
//            case 3:
//                holder.serviceName.setText(listServices[3]);
//                holder.imgService.setImageResource(R.drawable.gardening);
//                break;
//
                default:
                    break;
            }
              //  Toast.makeText(mContext,"RecyclerView clicked",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return mProducts.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView tvName;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageView_menu_item);
            tvName = itemView.findViewById(R.id.textView_menu_name);
            cardView = itemView.findViewById(R.id.card_view_menu);
        }
    }
}