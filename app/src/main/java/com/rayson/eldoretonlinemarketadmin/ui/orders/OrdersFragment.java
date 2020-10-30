package com.rayson.eldoretonlinemarketadmin.ui.orders;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rayson.eldoretonlinemarketadmin.Common.OrderIds;
import com.rayson.eldoretonlinemarketadmin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrdersFragment extends Fragment implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener{

    private static final int NUM_COLUMNS = 2;

    //vars
    OrdersAdapter mAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    List<String> allFilters=new ArrayList<>();
    private RelativeLayout mainLayout;
    List<OrderIds> mOrders;
    Context mContext;
    //widgets
    private RecyclerView mRecyclerView;
    private Spinner spCategories;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar pbOrders;
    private String userEmail = mUser.getEmail();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_orders, container, false);
        mRecyclerView = root.findViewById(R.id.recycler_view);
        mRecyclerView.setVisibility(View.INVISIBLE);
        pbOrders = root.findViewById(R.id.progressBarOrders);
        mainLayout = root.findViewById(R.id.relative_layout_orders);
        mSwipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout);
        spCategories=root.findViewById(R.id.spinnerOrders);
        mContext= getActivity();
        mSwipeRefreshLayout.setOnRefreshListener(this);
        populateSpinner();
        spCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String catName=spCategories.getSelectedItem().toString().trim();
                if (catName.equals("Pending orders")){
                    getPendingOrders();
                }
                if (catName.equals("Orders awaiting delivery")){
                    getOrdersWaitingDelivery();
                }
                if (catName.equals("Delivered orders")){
                    getDeliveredOrders();
                }
                else if (catName.equals("All Orders")){
                    getOrders();
                }
                else if (catName.equals("Today's orders")){
                    getTodaysOrders();
                }
                else if (catName.equals("Select date to show orders")){
                    getOrdersByDate();
                }
                else {
                    getOrders();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return root;
    }

    private void getOrdersByDate() {
        final Calendar mDate = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                mDate.set(Calendar.YEAR,year);
                mDate.set(Calendar.MONTH,month);
                mDate.set(Calendar.DAY_OF_MONTH,day);
                String format = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
//                spCategories.setText(sdf.format(mDate.getTime()));
                getByDate(sdf.format(mDate.getTime()));
            }
        };
        new DatePickerDialog(getContext(),date,mDate.get(Calendar.YEAR),mDate.get(Calendar.MONTH),mDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void getByDate(final String date) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.US);
        String formattedDate = sdf.format(c);
        db.collectionGroup("allOrderIds").whereEqualTo("date",date).whereEqualTo("ownerEmail",userEmail).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mOrders = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                                mOrders.add(snapshot.toObject(OrderIds.class));
                                snapshot.getId();
                            }

                        } else {
                            Toast.makeText(mContext, "No orders found for " +date, Toast.LENGTH_LONG).show();
                        }
                        initRecyclerView();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.w("MainActivity","Error "+ e);
                    }
                });
    }

    public static String encode(String date){
        return date.replace("/",",");
    }
    private void   getTodaysOrders() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.US);
        String formattedDate = df.format(c);
        String formattedDateAndTime = sdf.format(c);
        String[] dts= formattedDateAndTime.split(" ");
        String fdddd = dts[0];
        db.collection("AllOrders").document(encode(fdddd)).collection("allOrderIds").whereEqualTo("ownerEmail",userEmail).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mOrders = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mOrders.add(snapshot.toObject(OrderIds.class));
                        } else {
                            Toast.makeText(mContext, "Todays orders not found", Toast.LENGTH_LONG).show();
                        }
                        initRecyclerView();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void getPendingOrders() {
        db.collectionGroup("allOrderIds").whereEqualTo("status",0).whereEqualTo("ownerEmail",userEmail).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mOrders = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mOrders.add(snapshot.toObject(OrderIds.class));
                        } else {
                            Toast.makeText(mContext, "No pending orders found.", Toast.LENGTH_LONG).show();
                        }
                        initRecyclerView();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.w("MainActivity","Error "+ e);
                    }
                });
    }
    private void getOrdersWaitingDelivery() {
        db.collectionGroup("allOrderIds").whereEqualTo("status",1).whereEqualTo("ownerEmail",userEmail).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mOrders = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mOrders.add(snapshot.toObject(OrderIds.class));
                        } else {
                            Toast.makeText(mContext, "No orders waiting delivery found.", Toast.LENGTH_LONG).show();
                        }
                        initRecyclerView();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.w("MainActivity","Error "+ e);
                    }
                });
    }
    private void getDeliveredOrders() {
        db.collectionGroup("allOrderIds").whereEqualTo("status",2).whereEqualTo("ownerEmail",userEmail).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mOrders = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mOrders.add(snapshot.toObject(OrderIds.class));
                        } else {
                            Toast.makeText(mContext, "No delivered orders found.", Toast.LENGTH_LONG).show();
                        }
                        initRecyclerView();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.w("MainActivity","Error "+ e);
                    }
                });
    }
    private void populateSpinner() {
        allFilters.add("All Orders");
        allFilters.add("Pending orders");
        allFilters.add("Orders awaiting delivery");
        allFilters.add("Delivered orders");
        allFilters.add("Today's orders");
        allFilters.add("Select date to show orders");
        ArrayAdapter<String> usersAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, allFilters);
        spCategories.setAdapter(usersAdapter);
    }

    private void getOrders(){
        //mProducts.addAll(Arrays.asList(Products.FEATURED_PRODUCTS));
        db.collectionGroup("allOrderIds").whereEqualTo("ownerEmail",userEmail).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mOrders = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mOrders.add(snapshot.toObject(OrderIds.class));
                        } else {
                            Toast.makeText(mContext, "No orders placed yet.", Toast.LENGTH_LONG).show();
                        }
                        initRecyclerView();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.w("MainActivity","Error "+ e);
                    }
                });
    }

    private void initRecyclerView(){
        mAdapter = new OrdersAdapter(getContext(), mOrders,mainLayout);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), NUM_COLUMNS);
        LinearLayoutManager linearLayout=new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayout);
        mRecyclerView.setAdapter(mAdapter);
        pbOrders.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onRefresh() {
//        Collections.shuffle(mOrders);
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
//        (mRecyclerView.getAdapter()).notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}