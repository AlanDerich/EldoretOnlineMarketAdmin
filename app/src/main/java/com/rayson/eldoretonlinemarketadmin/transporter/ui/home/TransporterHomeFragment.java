package com.rayson.eldoretonlinemarketadmin.transporter.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.rayson.eldoretonlinemarketadmin.R;

public class TransporterHomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home_transporter, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
        return root;
    }
}