package com.rayson.eldoretonlinemarketadmin.ui.categoriesitems;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rayson.eldoretonlinemarketadmin.Product;
import com.rayson.eldoretonlinemarketadmin.R;
import com.rayson.eldoretonlinemarketadmin.ui.items.ItemsFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class AllItems extends Fragment implements AllItemsAdapter.OnItemsClickListener{
    String categoryId="";
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    ProgressBar progressBar;
    List<Product> mCategory;
    private Context mContext;
    StorageReference storageReference;
    FirebaseStorage storage;
    MaterialEditText edtName,edtDescription,edtPrice,edtDiscount;
    Button btnUpload, btnSelect;
    Product newProduct;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Uri saveUri;
    public static final int PICK_IMAGE_REQUEST = 71;
    private RecyclerView rvCategories;
    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;
    private String isIherited;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (getArguments()!=null) {
            isIherited = getArguments().getString("category");
        }
        else {
            Fragment fragmentStaff = new ItemsFragment();
            FragmentTransaction transactionStaff = getParentFragmentManager().beginTransaction();
            transactionStaff.replace(R.id.nav_host_fragment,fragmentStaff);
            transactionStaff.addToBackStack(null);
            transactionStaff.commit();
        }
        View root = inflater.inflate(R.layout.fragment_all_items, container, false);
        final FloatingActionButton fabAdd = root.findViewById(R.id.fabAddItem);
        mContext = getActivity();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        rvCategories = root.findViewById(R.id.recycler_all_items);
        progressBar=root.findViewById(R.id.progress_bar_all_items);
        rvCategories.setHasFixedSize(true);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        init();
        getCategoryList();
        return root;
    }


    private void init(){
        gridLayoutManager=new GridLayoutManager(mContext,2);
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rvCategories.setLayoutManager(linearLayoutManager);
    }
    private void getCategoryList(){
        db.collectionGroup("AllItems").whereEqualTo("menuId",isIherited).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mCategory = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mCategory.add(snapshot.toObject(Product.class));
                            populate();
                        } else {
                            Toast.makeText(mContext, "No Products found. Please add a new product", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.e(getActivity().toString(),"Error" +e);
                    }
                });
    }
    private void populate(){
        AllItemsAdapter itemsAdapter = new AllItemsAdapter(mCategory,this);
        itemsAdapter.setHasStableIds(true);
        itemsAdapter.notifyDataSetChanged();
        rvCategories.setAdapter(itemsAdapter);
        progressBar.setVisibility(View.GONE);

    }
    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Add new Product");
        alertDialog.setMessage("Fill all the details.");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_item_layout,null);

        edtName = add_menu_layout.findViewById(R.id.edtProductName);
        edtDescription = add_menu_layout.findViewById(R.id.edtProductDescription);
        edtPrice = add_menu_layout.findViewById(R.id.edtProductPrice);

        btnSelect = add_menu_layout.findViewById(R.id.btnProductSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnProductUpload);

        //event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                if(newProduct !=  null)
                {

                    db.collection(isIherited).document(newProduct.getName()).collection("AllItems").document(mUser.getEmail())
                            .set(newProduct)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                                    Toast.makeText(getContext(),"Product saved successfully",Toast.LENGTH_LONG).show();
                                    init();
                                    getCategoryList();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(),"Not saved. Try again later.",Toast.LENGTH_LONG).show();
                                }
                            });
                }
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

    private void uploadImage() {
        if(saveUri != null)
        {
            final ProgressDialog mDialog = new ProgressDialog(mContext);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("image/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(mContext,"Image Uploaded!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newProduct = new Product();
                                    newProduct.setName(edtName.getText().toString());
                                    newProduct.setDescription(edtDescription.getText().toString());
                                    newProduct.setPrice(edtPrice.getText().toString());
                                    newProduct.setUsername(mUser.getEmail());
                                    if (!(isIherited.isEmpty())){
                                        newProduct.setMenuId(isIherited);
                                    }
                                    newProduct.setImage(uri.toString());

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(mContext,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded"+progress+"%");
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data!= null && data.getData() != null)
        {
            saveUri = data.getData();
            btnSelect.setText("Image Selected");
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select an action");
        menu.add(0, 0,0,"View");
        menu.add(0, 1,0,"Update");
        menu.add(0, 2,0,"Delete");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getTitle().toString()){
            case "View":
                Toast.makeText(mContext, "View", Toast.LENGTH_LONG).show();
                break;
            case "Update":
                Toast.makeText(mContext, "Update", Toast.LENGTH_LONG).show();
                break;
            case "Delete":
                Toast.makeText(mContext, "Delete", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onItemsClick(int position) {

        Toast.makeText(mContext, "Clicked", Toast.LENGTH_LONG).show();
    }
}