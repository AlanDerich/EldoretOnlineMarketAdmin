package com.rayson.eldoretonlinemarketadmin.ui.maps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rayson.eldoretonlinemarketadmin.BoundLocationManager;
import com.rayson.eldoretonlinemarketadmin.Common.OrderIds;
import com.rayson.eldoretonlinemarketadmin.R;
import com.rayson.eldoretonlinemarketadmin.ui.ViewOrderDetailsFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements LifecycleOwner, GoogleMap.OnMarkerClickListener {
    private GoogleMap mMap;
    private FirebaseUser mUser;
    // LifecycleOwner lifecycleOwner;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final LatLng mDefaultLocation = new LatLng(0.5143, 35.2698);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    private boolean mLocationPermissionGranted;
    private LocationListener mGpsListener = new MyLocationListener();

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.


    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private Location mLastKnownLocation;
    private Context mContext;
    private boolean mStoragePermissionGranted;
    List<OrderIds> mOrders;
    private String longitude;
    private String latitude;
    private Boolean open;
    private List<OrderIds> stagesList;
    private TextView edtLatitude,edtLongitude;
    private EditText edtNickname;
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private String TAG= "MainActivity";
    private String orderID,location,date;
    private int status;
    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);
        mContext = getContext();
        getLocationPermission();
        DevicePermission();
        open = false;
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        if (getArguments()!=null) {
            orderID = getArguments().getString("orderId");
            location = getArguments().getString("location");
            status = getArguments().getInt("status");
            date = getArguments().getString("date");

        }
        Places.initialize(getContext(), getString(R.string.google_maps_key));

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap map) {
                mMap = map;
                getDeviceLocation();
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);
                updateLocationUI();
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                if (getArguments()!=null){
                    getLocations();
                }
                else {
                    getLocations(orderID);
                }


                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    @Override
                    public View getInfoWindow(Marker arg0) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {

                        LinearLayout info = new LinearLayout(mContext);
                        info.setOrientation(LinearLayout.VERTICAL);

                        TextView title = new TextView(mContext);
                        title.setTextColor(Color.parseColor("#0BF5AB"));
                        SpannableString spanTitle = new SpannableString(marker.getTitle());
                        spanTitle.setSpan(new UnderlineSpan(), 0, spanTitle.length(), 0);
                        title.setGravity(Gravity.CENTER);
                        title.setTypeface(null, Typeface.BOLD);
                        title.setTextSize(22);
                        title.setText(spanTitle);

                        TextView snippet = new TextView(mContext);
                        snippet.setTextColor(Color.BLACK);
                        snippet.setTypeface(null, Typeface.BOLD);
                        snippet.setTextSize(20);
                        snippet.setText(marker.getSnippet());

                        info.addView(title);
                        info.addView(snippet);

                        return info;
                    }
                });

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(final LatLng latLng) {

                    }

                });
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                        if (open) {
                            marker.hideInfoWindow();
                            open = false;
                        } else {
                            marker.showInfoWindow();
                            open = true;
                        }
                        return true;
                    }
                });
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(final Marker marker) {
                        final LatLng markerPos = marker.getPosition();
                        latitude = String.valueOf(markerPos.latitude);
                        longitude = String.valueOf(markerPos.longitude);
                        if (checkLogIn()) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Choose an action");
                            String[] options = {"View order info","Get directions to destination"};
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            String det = marker.getSnippet();
                                            String id = marker.getTitle();
                                            String[] parts = det.split(":");
                                            String date = parts[2];
                                            String[] part2 = parts[3].split("\n");
                                            String date2=part2[0];
                                            String status = parts[4].trim();
                                            int statusCode;
                                            Bundle args = new Bundle();
                                            if (status.equals("Approved")){
                                                statusCode=1;
                                            }
                                            else {
                                                statusCode=0;
                                            }
                                            AppCompatActivity activity = (AppCompatActivity) getContext();
                                            Fragment fragmentStaff = new ViewOrderDetailsFragment();
                                            FragmentTransaction transactionStaff = activity.getSupportFragmentManager().beginTransaction();
                                            transactionStaff.remove(MapsFragment.this);
                                            transactionStaff.add(R.id.nav_host_fragment,fragmentStaff);
                                            transactionStaff.addToBackStack(null);
                                            args.putString("orderId",id);
                                            args.putString("location",latitude + " , " + longitude);
                                            args.putInt("status",statusCode);
                                            args.putString("date",date+":"+ date2);
                                            fragmentStaff.setArguments(args);
                                            transactionStaff.commit();
//                                            getOrderInfo(latitude,longitude);
                                            break;
                                        case 1:
                                             Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                startActivity(mapIntent);
                                            break;

                                    }
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            Toast.makeText(getContext(), "Please login to get more information on this stage", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        return rootView;
    }

    private void getOrderInfo(String latitude, String longitude) {
        db.collectionGroup("allOrderIds").whereEqualTo("latitude",latitude).whereEqualTo("longitude",longitude).get()
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
                        //initRecyclerView();
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

    //    private void showEditDialog(String lat,String longt) {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
//        alertDialog.setTitle("Edit current Location");
//        alertDialog.setMessage("Fill all the details.");
//
//        LayoutInflater inflater = this.getLayoutInflater();
//        View add_menu_layout = inflater.inflate(R.layout.activity_add_location,null);
//
//        edtLatitude = add_menu_layout.findViewById(R.id.textViewAddLocationLatitude);
//        edtLongitude.setText(lat);
//        edtLongitude = add_menu_layout.findViewById(R.id.textViewAddLocationLongitude);
//        edtLongitude.setText(longt);
//        edtNickname = add_menu_layout.findViewById(R.id.editTextLocationNickNameAdd);
//
//        alertDialog.setView(add_menu_layout);
//        alertDialog.setIcon(R.drawable.jamaa);
//
//        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int i) {
//                dialog.dismiss();
//                UserDestinationInfo newDest = new UserDestinationInfo(edtLatitude.getText().toString(),edtLongitude.getText().toString(),mUser.getEmail(),edtNickname.getText().toString());
//                if(newDest !=  null)
//                {
//
//                    db.collection(mUser.getEmail()).document(encode(latitude)+ ":" + encode(longitude)).collection("allUserLocations").document(encode(latitude)+ ":" + encode(longitude))
//                            .set(newDest)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
////                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
//                                    Toast.makeText(getContext(),"Location saved successfully",Toast.LENGTH_LONG).show();
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(getContext(),"Not saved. Try again later.",Toast.LENGTH_LONG).show();
//                                }
//                            });
//                }
//            }
//        });
//
//        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int i) {
//                dialog.dismiss();
//            }
//        });
//        alertDialog.show();
//
//    }
    private void getLocations() {
        String currentString = location;
        String[] separated = currentString.split(",");
        latitude= separated[0].trim(); // this will contain latitude
        longitude = separated[1].trim(); // this will contain longitude
        mMap.clear();
        open = false;
        String stat;
        int picId;
        if (status==0){
            stat= "Pending Approval";
            picId=R.drawable.ic_baseline_undelivered_location;
        }
        else {
            stat= "Approved";
            picId=R.drawable.ic_baseline_location;
        }
        String snip = "Order info :" + "\n" + "Order date: "+ date + "\n" + "Order status: " + stat;
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude)))
                .icon(bitmapDescriptorFromVector(mContext,picId))
                .title(orderID)
                .snippet(snip)
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(Double.parseDouble(latitude),
                        Double.parseDouble(longitude)), DEFAULT_ZOOM));

    }
    public void getLocations(String d){
        db.collectionGroup("allOrderIds").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mOrders = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mOrders.add(snapshot.toObject(OrderIds.class));
                            int size = mOrders.size();
                            int position;
                            for (position=0;position<size;position++) {
                                String lat = mOrders.get(position).getLatitude();
                                String longtd = mOrders.get(position).getLongitude();
                                orderID = mOrders.get(position).getOrderId();
                                location = mOrders.get(position).getLatitude() + " , " + mOrders.get(position).getLongitude();
                                status = mOrders.get(position).getStatus();
                                date = mOrders.get(position).getDateAndTime();
                                String stat;
                                int picId;
                                if (status==0){
                                    stat= "Pending Approval";
                                    picId=R.drawable.ic_baseline_undelivered_location;
                                }
                                else {
                                    stat= "Approved";
                                    picId=R.drawable.ic_baseline_location;
                                }
                                String snip = "Order info :" + "\n" + "Order date: "+ date + "\n" + "Order status: " + stat;
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(Double.parseDouble(lat),Double.parseDouble(longtd)))
                                        .icon(bitmapDescriptorFromVector(mContext,picId))
                                        .title(mOrders.get(position).getOrderId())
                                        .snippet(snip)
                                );
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(Double.parseDouble(lat),
                                                Double.parseDouble(longtd)), DEFAULT_ZOOM));

                            }
                        } else {
                            Toast.makeText(mContext, "No orders found. Placed orders will appear here", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.w("OrdersFragment", "error " + e);
                    }
                });

    }
    public static String encode(String coOrdns){
        return coOrdns.replace(".",",");
    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(mContext, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {

                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        mStoragePermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    bindLocationListener();
                }
                else {
                    Toast.makeText(mContext,"Location permission denied",Toast.LENGTH_LONG).show();
                }
            }
            case PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mStoragePermissionGranted = true;
                }
                else {
                    DevicePermission();
                }
            }
        }
        updateLocationUI();
    }
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(mContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            bindLocationListener();
        }
        else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(mContext,"The app needs this permission to show you your places.",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    private void DevicePermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            mStoragePermissionGranted = true;
        }
        else if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(mContext,"The app needs this permission to upload your profile image.",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        if (open) {
            marker.hideInfoWindow();
            open = false;
        } else {
            marker.showInfoWindow();
            open = true;
        }
        return true;
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            //textView.setText(location.getLatitude() + ", " + location.getLongitude());
            mLastKnownLocation = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getContext(),
                    "Provider enabled: " + provider, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }
    private void bindLocationListener() {
        BoundLocationManager.bindLocationListenerIn(this, mGpsListener, getContext());
    }
    private Boolean checkLogIn() {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            return true;

        } else {
            return false;
        }
    }

}