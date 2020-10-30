package com.rayson.eldoretonlinemarketadmin;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.rayson.eldoretonlinemarketadmin.transporter.TransporterActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FirebaseUI extends AppCompatActivity {
    private Context mContext;
    private FirebaseFirestore mFirestone;
    private List<UserDetails> mUserr;
    private static final int RC_SIGN_IN = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_ui);
        getConnectivityStatus(this);
        mContext =this;
        mFirestone = FirebaseFirestore.getInstance();
        if (getConnectivityStatus(this)){
        themeAndLogo();
            }
        else {
            Toast.makeText(this, "No network connection!", Toast.LENGTH_SHORT).show();
        }
    }
    public static boolean getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return true;
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
        }
        Toast.makeText(context, "No network connection!", Toast.LENGTH_SHORT).show();
        return false;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                checkUser();
            }
            if(resultCode == RESULT_CANCELED){
                displayMessage(("Sign in failed"));
                finish();
            }
            return;
        }
        displayMessage("Unknown response");
    }
    private void checkUser() {
        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirestone.collectionGroup("allStaff").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                mUserr= new ArrayList<>();
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        mUserr.add(snapshot.toObject(UserDetails.class));
                    }
                    int size = mUserr.size();
                    int position;
                    for (position=0;position<size;position++){
                        if (mUser.getEmail().equals(mUserr.get(position).getEmail())){
                            UserDetails userDetails= mUserr.get(position);
                            String namee=userDetails.getName();
                            String section=userDetails.getRole();
                            if (section.equals("Admin")){
                                Intent intent=new Intent(FirebaseUI.this, MainActivity.class);
                                startActivity(intent);
                            }
                            else if (section.equals("Transporter")){
                                Intent intentMpesa=new Intent(FirebaseUI.this, TransporterActivity.class);
                                startActivity(intentMpesa);
                            }
                            else if (section.equals("Packager")){
                                Intent intentMpesa=new Intent(FirebaseUI.this, MainActivity.class);
                                startActivity(intentMpesa);

                            }
                            else {
                                Toast.makeText(FirebaseUI.this,"Error validating details. Please login again",Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    }


                } else {

                    Toast.makeText(FirebaseUI.this,"No users found.",Toast.LENGTH_LONG).show();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FirebaseUI.this,"Something went terribly wrong." + e,Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void displayMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // [END auth_fui_result]
    public void themeAndLogo() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
               // new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
        // [START auth_fui_theme_logo]
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.logo)      // Set logo drawable
                        .setTosAndPrivacyPolicyUrls(
                        "https://example.com/terms.html",
                        "https://example.com/privacy.html")
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_theme_logo]
    }
}