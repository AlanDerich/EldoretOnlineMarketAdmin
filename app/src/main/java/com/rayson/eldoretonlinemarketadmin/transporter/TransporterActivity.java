package com.rayson.eldoretonlinemarketadmin.transporter;

import android.os.Bundle;

import com.rayson.eldoretonlinemarketadmin.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class TransporterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transporter);
        BottomNavigationView navView = findViewById(R.id.nav_view_transporter);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_undelivered, R.id.navigation_delivered, R.id.nav_transporter_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_transporter);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

}