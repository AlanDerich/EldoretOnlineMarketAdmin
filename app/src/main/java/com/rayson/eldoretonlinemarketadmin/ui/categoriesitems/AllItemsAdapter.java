package com.rayson.eldoretonlinemarketadmin.ui.categoriesitems;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rayson.eldoretonlinemarketadmin.Product;
import com.rayson.eldoretonlinemarketadmin.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class AllItemsAdapter extends RecyclerView.Adapter<AllItemsAdapter.ViewHolder>{
        Context mContext;
        List<Product> mItemInfo;
        private File localFile;
        private Bitmap bmp;
        private ViewHolder holder1;
        private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        Uri saveUri;
        MaterialEditText edtName,edtDescription,edtPrice,edtDiscount;
        Button btnUpload, btnSelect;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        private AllItemsAdapter.OnItemsClickListener onItemsClickListener;
        private int pos;

    public AllItemsAdapter(List<Product> mItemInfo, AllItemsAdapter.OnItemsClickListener onItemsClickListener){
        this.mItemInfo = mItemInfo;
        this.onItemsClickListener = onItemsClickListener;
        }
@NonNull
@Override
public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_products,parent,false);
        mContext = parent.getContext();
        return new AllItemsAdapter.ViewHolder(view,onItemsClickListener);
        }

@Override
public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder1 = holder;
//        try {
//          //  getImage(mItemInfo.get(position).getImage());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        new DownloadImageTask(holder.imgCategory).execute(mItemInfo.get(position).getImage());
        Locale locale = new Locale("en","KE");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(mItemInfo.get(position).getPrice()));
        holder.tvPrice.setText(fmt.format(price));
        holder.tvDescription.setText(mItemInfo.get(position).getDescription());
        holder.tvName.setText(mItemInfo.get(position).getName());
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            pos = position;
            view.showContextMenu();
        }
    });
        }

@Override
public int getItemCount() {
        return mItemInfo.size();
        }

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnCreateContextMenuListener{
    private TextView tvName,tvDescription,tvPrice;
    private ImageView imgCategory;
    private CardView mainLayout;
    AllItemsAdapter.OnItemsClickListener onItemsClickListener;
    public ViewHolder(@NonNull View itemView, AllItemsAdapter.OnItemsClickListener onItemsClickListener) {
        super(itemView);
        this.onItemsClickListener=onItemsClickListener;
        tvName=itemView.findViewById(R.id.textView_list_product_name);
        tvDescription=itemView.findViewById(R.id.textView_item_description);
        tvPrice=itemView.findViewById(R.id.textView_list_product_price);
        imgCategory=itemView.findViewById(R.id.imageView_list_product_item);
        mainLayout=itemView.findViewById(R.id.card_view_list_product);
        itemView.setOnClickListener(this);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View view) {
        onItemsClickListener.onItemsClick(getAdapterPosition());
    }
    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select an action");
        MenuItem Update = contextMenu.add(Menu.NONE, 1, 1, "Update");
        MenuItem Delete = contextMenu.add(Menu.NONE, 2, 2, "Delete");
        Update.setOnMenuItemClickListener(onEditMenu);
        Delete.setOnMenuItemClickListener(onEditMenu);

    }

}
    private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()) {
                case 0:
                    //Do stuff
                    Toast.makeText(mContext, "View", Toast.LENGTH_LONG).show();
                    break;

                case 1:
                    //Do stuff
                    showUpdateDialog();
                    Toast.makeText(mContext, "Update", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    //Do stuff
                    deleteItem(mItemInfo.get(pos));
                    break;
            }
            return true;
        }
    };

    private void showUpdateDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Add new Product");
        alertDialog.setMessage("Fill all the details.");

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View add_menu_layout = inflater.inflate(R.layout.add_new_item_layout,null);

        edtName = add_menu_layout.findViewById(R.id.edtProductName);
        edtName.setText(mItemInfo.get(pos).getName());
        edtDescription = add_menu_layout.findViewById(R.id.edtProductDescription);
        edtDescription.setText(mItemInfo.get(pos).getDescription());
        edtPrice = add_menu_layout.findViewById(R.id.edtProductPrice);
        edtPrice.setText(mItemInfo.get(pos).getPrice());

        btnSelect = add_menu_layout.findViewById(R.id.btnProductSelect);
        btnSelect.setVisibility(View.GONE);
        btnUpload = add_menu_layout.findViewById(R.id.btnProductUpload);
        btnUpload.setVisibility(View.GONE);

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                if(edtPrice.getText().toString() !=  null)
                {
                    Product product=new Product(edtName.getText().toString().trim(),mItemInfo.get(pos).getImage(),edtDescription.getText().toString().trim(),edtPrice.getText().toString().trim(),mItemInfo.get(pos).getMenuId(),mUser.getEmail());
                    db.collection(mItemInfo.get(pos).getMenuId()).document(mItemInfo.get(pos).getName()).collection("AllItems").document(mItemInfo.get(pos).getName())
                            .set(product)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                                    Toast.makeText(mContext,"Product updated successfully",Toast.LENGTH_LONG).show();
                                    notifyDataSetChanged();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(mContext,"Not saved. Try again later.",Toast.LENGTH_LONG).show();
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
    private void deleteItem(Product productDetails) {
        db.collection(mItemInfo.get(pos).getMenuId()).document(mItemInfo.get(pos).getName()).collection("AllItems").document(mItemInfo.get(pos).getName())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        Toast.makeText(mContext, "successfully deleted!", Toast.LENGTH_LONG).show();
                        mItemInfo.remove(pos);
                        notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }
    private class DownloadImageTask extends AsyncTask<String, Integer, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(Bitmap result) {
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.ordersn);
            // bmImage.setImageBitmap(result);
            Glide.with(mContext)
                    .setDefaultRequestOptions(requestOptions)
                    .load(result)
                    .into(bmImage);

        }
    }
    public interface OnItemsClickListener{
        void onItemsClick(int position);
    }
}

