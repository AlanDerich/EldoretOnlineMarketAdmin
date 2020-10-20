package com.rayson.eldoretonlinemarketadmin.ui.categoriesitems;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rayson.eldoretonlinemarketadmin.Product;
import com.rayson.eldoretonlinemarketadmin.R;

import java.io.File;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AllItemsAdapter extends RecyclerView.Adapter<AllItemsAdapter.ViewHolder>{
        Context mContext;
        List<Product> mItemInfo;
        private File localFile;
        private Bitmap bmp;
        private ViewHolder holder1;
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
            }
        });
        }

@Override
public int getItemCount() {
        return mItemInfo.size();
        }

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
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
//        itemView.setOnCreateContextMenuListener(this);
    }

//    @Override
//    public boolean onMenuItemClick(MenuItem menuItem) {
//        String kkk=menuItem.getTitle().toString();
//        switch (menuItem.getTitle().toString()){
//            case "View":
//                Toast.makeText(mContext, "View", Toast.LENGTH_LONG).show();
//                break;
//            case "Update":
//                Toast.makeText(mContext, "Update", Toast.LENGTH_LONG).show();
//                break;
//            case "Delete":
//                Toast.makeText(mContext, "Delete", Toast.LENGTH_LONG).show();
//                break;
//        }
//        return true;
//    }
//
//    @Override
//    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
//        contextMenu.setHeaderTitle("Select an action");
//        contextMenu.add(0, 0,0,"View");
//        contextMenu.add(0, 1,0,"Update");
//        contextMenu.add(0, 2,0,"Delete");
//    }

    @Override
    public void onClick(View view) {
        onItemsClickListener.onItemsClick(getAdapterPosition());
    }
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
