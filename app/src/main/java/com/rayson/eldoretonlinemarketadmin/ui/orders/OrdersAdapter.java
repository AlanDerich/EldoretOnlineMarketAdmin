package com.rayson.eldoretonlinemarketadmin.ui.orders;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rayson.eldoretonlinemarketadmin.Common.OrderIds;
import com.rayson.eldoretonlinemarketadmin.R;
import com.rayson.eldoretonlinemarketadmin.ui.ViewOrderDetailsFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private static final String TAG = "MainRecyclerViewAd";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //vars
    private List<OrderIds> mOrders;
    private Context mContext;
    private RelativeLayout mainL;
    private String formattedDate;
    private View view;

    public OrdersAdapter(Context context, List<OrderIds> orders, RelativeLayout mainL) {
        mContext = context;
        mOrders = orders;
        this.mainL=mainL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_orders, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Locale locale = new Locale("en","KE");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        holder.tvName.setText(mOrders.get(position).getOrderId());
        holder.tvDate.setText(mOrders.get(position).getDateAndTime());
        holder.tvAmount.setText(fmt.format(mOrders.get(position).getTotalAmount()));
        if (mOrders.get(position).getStatus()==0){
            holder.tvStatus.setText("Pending");
            holder.btnApprove.setVisibility(View.VISIBLE);
        }
        else {
            holder.tvStatus.setText("Approved");
            holder.btnApprove.setVisibility(View.GONE);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                mainL.removeAllViews();
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment fragmentStaff = new ViewOrderDetailsFragment();
                FragmentTransaction transactionStaff = activity.getSupportFragmentManager().beginTransaction();
                transactionStaff.replace(R.id.nav_host_fragment,fragmentStaff);
                transactionStaff.addToBackStack(null);
                args.putString("orderId",mOrders.get(position).getOrderId());
                args.putString("location",mOrders.get(position).getLatitude() + " , " + mOrders.get(position).getLongitude());
                args.putInt("status",mOrders.get(position).getStatus());
                args.putString("date",mOrders.get(position).getDateAndTime());
                fragmentStaff.setArguments(args);
                transactionStaff.commit();
            }
        });
        holder.btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                approveOrder(position,holder);
            }
        });
    }
    public static String encode(String date){
        return date.replace("/",",");
    }
    private void approveOrder(int pos, final ViewHolder holder) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/mm/yyyy",Locale.US);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy h:mm a", Locale.US);
        String[] dts= mOrders.get(pos).getDateAndTime().split(" ");
       String formattedDateAndTime = dts[0];
//        Date date;
//        SimpleDateFormat formatter= sdf;
//        try{
//            date= formatter.parse(formattedDateAndTime);
//            long mills=date.getTime();
//            formattedDate = df.format(mills);
//        }
//        catch (ParseException e){
//            e.printStackTrace();
//        }


        OrderIds orderIds=new OrderIds(mOrders.get(pos).getOrderId(),mOrders.get(pos).getUsername(),mOrders.get(pos).getDateAndTime(),mOrders.get(pos).getLatitude(),mOrders.get(pos).getLongitude(),formattedDateAndTime,mOrders.get(pos).getTotalAmount(),1);
        db.collection("AllOrders").document(encode(formattedDateAndTime)).collection("allOrderIds").document(mOrders.get(pos).getOrderId())
                .set(orderIds)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                        //   Toast.makeText(ViewCartActivity.this, "Order has been placed.", Toast.LENGTH_SHORT).show();
                        //getCartProducts(randomOrder);
                        Toast.makeText(mContext,"Order successfully approved.",Toast.LENGTH_LONG).show();
                        holder.btnApprove.setVisibility(View.GONE);
                        holder.tvStatus.setText("Approved");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext,"Not ordered. Try again later.",Toast.LENGTH_LONG).show();
                    }
                });
    }

    private String convertDateFormat(String time) {
        String inputPattern= "dd/mm/yyyy h:mm a";
        String outputPattern= "dd/mm/yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern,Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern,Locale.US);
        Date date = null;
        String str = null;
        try{
            date = inputFormat.parse(time);
            str= outputFormat.format(date);
        }
        catch (ParseException e){
            e.printStackTrace();
        }
        return str;
    }


    @Override
    public int getItemCount() {
        return mOrders.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvName,tvStatus,tvDate,tvAmount;
        Button btnApprove;
        CardView cardView;
        RelativeLayout rv11;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.textView_order_order_id);
            tvDate = itemView.findViewById(R.id.textView_order_date);
            tvAmount = itemView.findViewById(R.id.textView_order_amount);
            tvStatus = itemView.findViewById(R.id.textView_order_status);
            btnApprove = itemView.findViewById(R.id.button_approve_order);
            cardView = itemView.findViewById(R.id.card_view_orders);
            rv11 = itemView.findViewById(R.id.rv111);
        }
    }
}