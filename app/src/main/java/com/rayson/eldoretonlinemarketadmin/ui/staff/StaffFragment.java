package com.rayson.eldoretonlinemarketadmin.ui.staff;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rayson.eldoretonlinemarketadmin.Common.StaffDetails;
import com.rayson.eldoretonlinemarketadmin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

public class StaffFragment extends Fragment {
    private StaffAdapter staffAdapter;
    MaterialEditText edtName,edtEmail;
    ImageView imgStaff;
    List<String> allRoles=new ArrayList<>();
    Spinner spRole;
    Context mContext;
    ConstraintLayout mainLayout;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<StaffDetails> mStaff;
    private RecyclerView rvStaff;
    private FloatingActionButton fabAdd;
    private static final int NUM_COLUMNS = 2;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_staff, container, false);
        rvStaff = root.findViewById(R.id.rv_staff_members);
        fabAdd = root.findViewById(R.id.floatingActionButtonAddStaff);
        mainLayout = root.findViewById(R.id.constraintLayoutStaffMain);
        mContext= getActivity();
        getStaffMembers();
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddStaffDialog();
            }
        });

        return root;
    }

    private void showAddStaffDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Add new Staff Member");
        alertDialog.setMessage("Fill all the details.");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_staff_member,null);
        edtName = add_menu_layout.findViewById(R.id.edtStaffName);
        imgStaff=add_menu_layout.findViewById(R.id.imgAddStaff);
        spRole = add_menu_layout.findViewById(R.id.spinner_staff_role);
        populateSpinner();
        edtEmail = add_menu_layout.findViewById(R.id.edtSTaffEmail);


        //event for button

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();

                StaffDetails staffDetails =new StaffDetails(edtName.getText().toString(),spRole.getSelectedItem().toString(),edtEmail.getText().toString(),0);
                    db.collection("allStaff").document(edtEmail.getText().toString())
                            .set(staffDetails)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                                    Toast.makeText(getContext(),"Staff added successfully",Toast.LENGTH_LONG).show();
                                    getStaffMembers();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(),"Not saved. Try again later.",Toast.LENGTH_LONG).show();
                                }
                            });
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void populateSpinner() {
        allRoles.add("Transporter");
        allRoles.add("Packager");
        allRoles.add("Admin");
        ArrayAdapter<String> usersAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, allRoles);
        spRole.setAdapter(usersAdapter);
    }

    private void getStaffMembers() {
        db.collectionGroup("allStaff").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mStaff = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mStaff.add(snapshot.toObject(StaffDetails.class));
                        } else {
                            Toast.makeText(mContext, "No staff members found yet.", Toast.LENGTH_LONG).show();
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

    private void initRecyclerView(){
        staffAdapter = new StaffAdapter(getContext(), mStaff,mainLayout);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), NUM_COLUMNS);
        LinearLayoutManager linearLayout=new LinearLayoutManager(getContext());
        rvStaff.setLayoutManager(linearLayout);
        rvStaff.setAdapter(staffAdapter);
    }
}