package com.rayson.eldoretonlinemarketadmin.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rayson.eldoretonlinemarketadmin.MainRecyclerViewAdapter;
import com.rayson.eldoretonlinemarketadmin.Menus;
import com.rayson.eldoretonlinemarketadmin.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView mRecyclerView;
    MainRecyclerViewAdapter mAdapter;
    private static final int NUM_COLUMNS = 1;
    private ArrayList<Menus> mProducts;
    private Context mContext;
    private ConstraintLayout mainLayout;
    private ProgressBar pbLoading;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView = root.findViewById(R.id.recycler_view);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mainLayout = root.findViewById(R.id.constraint_layout_home);
        pbLoading = root.findViewById(R.id.progressBarHome);
        pbLoading.setVisibility(View.VISIBLE);
        mContext = getActivity();
        getProducts();
        initRecyclerView();
        return root;
    }

    private void initRecyclerView(){
        mAdapter = new MainRecyclerViewAdapter(getContext(), mProducts, mainLayout);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), NUM_COLUMNS);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        pbLoading.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
    private void getProducts(){
        Menus menuItems=new Menus("Products",getContext().getResources().getDrawable(R.drawable.basket));
//        mProducts.addAll(Arrays.asList(menuOrders,menuStaff,menuItems));
        mProducts=new ArrayList<>();
        mProducts.add(menuItems);
    }
}