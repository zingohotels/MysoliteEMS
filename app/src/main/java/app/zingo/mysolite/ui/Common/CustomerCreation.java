package app.zingo.mysolite.ui.Common;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import com.google.android.material.textfield.TextInputEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import app.zingo.mysolite.Custom.MapViewScroll;
import app.zingo.mysolite.WebApi.EmployeeApi;
import app.zingo.mysolite.adapter.ManagerSpinnerAdapter;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.utils.Constants;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.ThreadExecuter;
import app.zingo.mysolite.utils.TrackGPS;
import app.zingo.mysolite.utils.Util;
import app.zingo.mysolite.WebApi.UploadApi;
import app.zingo.mysolite.R;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerCreation extends AppCompatActivity {
    private TextInputEditText mClientName,mClientMobile,mClientMail;
    private Button mSave;
    private Spinner mtoReport;
    private RelativeLayout mMapLay;
    private Switch mShow;
    private EditText  lat, lng;
    private TextView location;
    private GoogleMap mMap;
    private MapViewScroll mapView;
    private Marker marker;
    private String add;
    private double lati, lngi;
    private DecimalFormat df2 = new DecimalFormat(".##########");
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public String TAG = "MAPLOCATION",placeId;
    private Employee updateCustomers;
    private ArrayList <Employee> employees;
    //Image Upload
    private CircleImageView profileImageView;
    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_PICK_PHOTO = 2;
    private static final int CAMERA_PIC_REQUEST = 1111;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private Uri fileUri;
    private String mediaPath;
    private String mImageFileLocation = "";
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";
    private String postPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.activity_customer_creation);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Customer Details");
            mSave = findViewById(R.id.save);
            mClientName = findViewById(R.id.client_name);
            mClientMobile = findViewById(R.id.client_contact_number);
            mClientMail = findViewById(R.id.client_contact_email);
            mapView = findViewById(R.id.task_location_map);
            mShow = findViewById(R.id.show_map);
            mMapLay = findViewById(R.id.map_layout);
            mtoReport = findViewById(R.id.managers_list);
            location = findViewById(R.id.location_et);
            lat = findViewById(R.id.lat_et);
            lng = findViewById(R.id.lng_et);

            //Image
            profileImageView = findViewById(R.id.profileImageView);
            //Permission for image upload
            if ( ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                profileImageView.setEnabled(false);
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
            } else {
                profileImageView.setEnabled(true);
            }

            Bundle bun = getIntent().getExtras();
            if(bun!=null){
                updateCustomers = ( Employee ) bun.getSerializable("Customer");
            }
            mShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mMapLay.setVisibility(View.VISIBLE);
                    }else{
                        mMapLay.setVisibility(View.GONE);
                    }
                }
            });
            mapView.onCreate(savedInstanceState);
            mapView.onResume();
            try {
                MapsInitializer.initialize( CustomerCreation.this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY/*MODE_FULLSCREEN*/).build( CustomerCreation.this);
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                        e.printStackTrace();
                    }
                }
            });

            getmanagerProfile(PreferenceHandler.getInstance ( CustomerCreation.this ).getCompanyId ());
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    if ( ActivityCompat.checkSelfPermission( CustomerCreation.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( CustomerCreation.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    mMap.getUiSettings().setAllGesturesEnabled(true);
                    mMap.setMyLocationEnabled(true);
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();
                    TrackGPS trackGPS = new TrackGPS ( CustomerCreation.this);
                    if(trackGPS.canGetLocation()) {
                        lati = trackGPS.getLatitude();
                        lngi = trackGPS.getLongitude();
                    }

                    if(updateCustomers!=null){
                        setData(updateCustomers);
                    }
                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            DecimalFormat df2 = new DecimalFormat(".##########");
                            lati = latLng.latitude;
                            lngi = latLng.longitude;
                            lat.setText(df2.format(latLng.latitude)+"");
                            lng.setText(df2.format(latLng.longitude)+"");
                            add = getAddress(latLng);
                            location.setText(add);
                            mMap.clear();
                            marker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(latLng));
                            CameraPosition cameraPosition1 = new CameraPosition.Builder().target(latLng).zoom(80).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
                        }
                    });
                }
            });

            mSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = mClientName.getText().toString();
                    String mail = mClientMail.getText().toString();
                    String mobile = mClientMobile.getText().toString();
                    if(name==null||name.isEmpty()){
                        Toast.makeText( CustomerCreation.this, "Please enter name", Toast.LENGTH_SHORT).show();
                    }else if(mail==null||mail.isEmpty()){
                        Toast.makeText( CustomerCreation.this, "Please enter mail", Toast.LENGTH_SHORT).show();
                    }else if(mail==null||mail.isEmpty()){
                        Toast.makeText( CustomerCreation.this, "Please enter mobile number", Toast.LENGTH_SHORT).show();
                    }else{
                        String emailList = "";
                        String phoneList="";
                        if(updateCustomers!=null){
                            Employee customer = updateCustomers;
                            customer.setEmployeeName (name);
                            customer.setPrimaryEmailAddress (mail);
                            customer.setPhoneNumber (mobile);
                            customer.setDateOfBirth ( new SimpleDateFormat ( "MM/dd/yyyy",Locale.US ).format ( new Date (  ) ) );
                            customer.setDateOfJoining ( new SimpleDateFormat ( "MM/dd/yyyy",Locale.US ).format ( new Date (  ) ) );
                            customer.setLati (""+lati);
                            customer.setLongi (""+lngi);
                            customer.setAlternateEmailAddress (""+emailList);
                            customer.setPhoneNumber (""+mobile);
                            customer.setUserRoleId ( 10 );
                            customer.setDesignationId ( 1 );
                            if(employees!=null&&employees.size()!=0){
                                customer.setManagerId(employees.get(mtoReport.getSelectedItemPosition()).getEmployeeId());
                            }else{
                                customer.setManagerId(PreferenceHandler.getInstance ( CustomerCreation.this ).getUserId ());
                            }
                            if(add!=null){
                                customer.setAddress (""+add);
                            }else{
                                if(lati!=0&&lngi!=0){
                                    customer.setAddress(""+getAddress(new LatLng(lati,lngi)));
                                }
                            }
                            customer.setDepartmentId (1);
                            if(postPath!=null&&!postPath.isEmpty()){
                                File file = new File(postPath);
                                if(file.length() <= 1*1024*1024) {
                                    FileOutputStream out = null;
                                    String[] filearray = postPath.split("/");
                                    final String filename = getFilename(filearray[filearray.length-1]);
                                    try {
                                        out = new FileOutputStream(filename);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    Bitmap myBitmap = BitmapFactory.decodeFile(postPath);
                                    //write the compressed bitmap at the field_icon specified by filename.
                                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                    uploadImage(filename,customer);
                                }
                                else {
                                    compressImage(postPath,customer);
                                }

                            }else{
                                if(postPath!=null&&postPath.isEmpty()){
                                    customer.setImage (null);
                                }
                                updateCustomer(customer);
                            }
                        }else{
                            Employee customer = new Employee ();
                            customer.setEmployeeName (name);
                            customer.setPrimaryEmailAddress (mail);
                            customer.setDateOfBirth ( new SimpleDateFormat ( "MM/dd/yyyy",Locale.US ).format ( new Date (  ) ) );
                            customer.setDateOfJoining ( new SimpleDateFormat ( "MM/dd/yyyy",Locale.US ).format ( new Date (  ) ) );
                            customer.setPhoneNumber (mobile);
                            customer.setLati (""+lati);
                            customer.setLongi (""+lngi);
                            customer.setAlternateEmailAddress (""+emailList);
                            customer.setPhoneNumber (""+mobile);
                            customer.setDesignationId ( 1 );
                            if(employees!=null&&employees.size()!=0){
                                customer.setManagerId(employees.get(mtoReport.getSelectedItemPosition()).getEmployeeId());
                            }else{
                                customer.setManagerId(PreferenceHandler.getInstance ( CustomerCreation.this ).getUserId ());
                            }
                            customer.setUserRoleId ( 10 );
                            if(lati!=0&&lngi!=0){
                                customer.setAddress (""+getAddress(new LatLng(lati,lngi)));
                            }
                            customer.setDepartmentId (1);
                            if(postPath!=null&&!postPath.isEmpty()){
                                File file = new File(postPath);
                                if(file.length() <= 1*1024*1024) {
                                    FileOutputStream out = null;
                                    String[] filearray = postPath.split("/");
                                    final String filename = getFilename(filearray[filearray.length-1]);
                                    try {
                                        out = new FileOutputStream(filename);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    Bitmap myBitmap = BitmapFactory.decodeFile(postPath);
                                    //write the compressed bitmap at the field_icon specified by filename.
                                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                    uploadImage(filename,customer);
                                }
                                else {
                                    compressImage(postPath,customer);
                                }
                            }else{
                                if(postPath!=null&&postPath.isEmpty()){
                                    customer.setImage (null);
                                }
                                 addCustomer(customer);
                            }
                        }
                    }
                }
            });

            //imageview onclicklistner for choose image
            profileImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialDialog.Builder( CustomerCreation.this)
                            .title(R.string.uploadImages)
                            .items(R.array.uploadImages)
                            .itemsIds(R.array.itemIds)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    switch (which) {
                                        case 0:
                                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                            startActivityForResult(galleryIntent, REQUEST_PICK_PHOTO);
                                            break;
                                        case 1:
                                            captureImage();
                                            break;
                                        case 2:
                                            profileImageView.setImageResource(R.drawable.ic_account_circle_black);
                                            postPath = "";
                                            break;
                                    }
                                }
                            })
                            .show();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder( CustomerCreation.this, Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation( latLng.latitude, latLng.longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append(",");
                }
                result = address.getAddressLine(0);
                return result;
            }
            return result;
        } catch (IOException e) {
            Log.e("MapLocation", "Unable connect to Geocoder", e);
            return result;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult ( requestCode , resultCode , data );
        try {
            if ( resultCode == RESULT_OK ) {
                if ( requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE ) {
                    Place place = PlaceAutocomplete.getPlace ( this , data );
                    //System.out.println(place.getLatLng());
                    location.setText ( place.getName ( ) + "," + place.getAddress ( ) );
                    //location.setText(""+place.getId());
                    placeId = place.getId ( );
                    lati = place.getLatLng ( ).latitude;
                    lngi = place.getLatLng ( ).longitude;
                    lat.setText ( df2.format ( place.getLatLng ( ).latitude ) + "" );
                    lng.setText ( df2.format ( place.getLatLng ( ).longitude ) + "" );
                    System.out.println ( "Star Rating = " + place.getRating ( ) );
                    if ( mMap != null ) {
                        mMap.clear ( );
                        marker = mMap.addMarker ( new MarkerOptions ( ).icon ( BitmapDescriptorFactory.defaultMarker ( BitmapDescriptorFactory.HUE_RED ) ).position ( place.getLatLng ( ) ) );
                        CameraPosition cameraPosition1 = new CameraPosition.Builder ( ).target ( place.getLatLng ( ) ).zoom ( 17 ).build ( );
                        mMap.animateCamera ( CameraUpdateFactory.newCameraPosition ( cameraPosition1 ) );
                    }
                    //address.setText(place.getAddress());*/
                    Log.i ( TAG , "Place: " + place.getName ( ) );
                } else if ( requestCode == REQUEST_TAKE_PHOTO || requestCode == REQUEST_PICK_PHOTO ) {
                    if ( data != null ) {
                        // Get the Image from data
                        Uri selectedImage = data.getData ( );
                        String[] filePathColumn = { MediaStore.Images.Media.DATA };
                        Cursor cursor = getContentResolver ( ).query ( selectedImage , filePathColumn , null , null , null );
                        assert cursor != null;
                        cursor.moveToFirst ( );
                        int columnIndex = cursor.getColumnIndex ( filePathColumn[ 0 ] );
                        mediaPath = cursor.getString ( columnIndex );
                        // Set the Image in ImageView for Previewing the Media
                        profileImageView.setImageBitmap ( BitmapFactory.decodeFile ( mediaPath ) );
                        cursor.close ( );
                        postPath = mediaPath;
                    }

                } else if ( requestCode == CAMERA_PIC_REQUEST ) {
                    if ( Build.VERSION.SDK_INT > 21 ) {
                        Glide.with ( this ).load ( mImageFileLocation ).into ( profileImageView );
                        postPath = mImageFileLocation;
                    } else {
                        Glide.with ( this ).load ( fileUri ).into ( profileImageView );
                        postPath = fileUri.getPath ( );
                    }
                }
            } else if ( resultCode != RESULT_CANCELED ) {
                if ( requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE ) {
                    Status status = PlaceAutocomplete.getStatus ( this , data );
                    // TODO: Handle the error.
                    Log.i ( TAG , status.getStatusMessage ( ) );

                } else {
                    Toast.makeText ( this , "Sorry, there was an error!" , Toast.LENGTH_LONG ).show ( );
                }
            }

        } catch ( Exception e ) {
            e.printStackTrace ( );
        }
    }

    public void addCustomer(final Employee customer) {
        Gson gson = new Gson ();
        String json = gson.toJson ( customer );
        System.out.println ("Suree  "+ json );
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();
        EmployeeApi apiService = Util.getClient().create( EmployeeApi.class);
        Call< Employee > call = apiService.addEmployee (customer);
        call.enqueue(new Callback< Employee >() {
            @Override
            public void onResponse( Call< Employee > call, Response< Employee > response) {
                try {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {
                        Employee s = response.body();
                        if(s!=null){
                            Toast.makeText( CustomerCreation.this, "Customer created successfully", Toast.LENGTH_SHORT).show();
                            CustomerCreation.this.finish();
                        }
                    }else {
                        Toast.makeText( CustomerCreation.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex) {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure( Call< Employee > call, Throwable t) {
                if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Log.e("TAG", t.toString());
            }
        });
    }

    public void updateCustomer(final Employee customer) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();
        EmployeeApi apiService = Util.getClient().create( EmployeeApi.class);
        Call< Employee > call = apiService.updateEmployee (customer.getEmployeeId (),customer);
        call.enqueue(new Callback< Employee >() {
            @Override
            public void onResponse( Call< Employee > call, Response< Employee > response) {
                try {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||statusCode==204) {
                        Toast.makeText( CustomerCreation.this, "Customer updates successfully", Toast.LENGTH_SHORT).show();
                        CustomerCreation.this.finish();
                    }else {
                        Toast.makeText( CustomerCreation.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex) {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure( Call< Employee > call, Throwable t) {
                if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Log.e("TAG", t.toString());
            }
        });
    }

    public void setData(final Employee customer) {
        mClientName.setText ( customer.getEmployeeName ( ) );
        mClientMobile.setText ( customer.getPhoneNumber ( ) );
        mClientMail.setText ( customer.getPrimaryEmailAddress ( ) );
        if ( customer.getLati ( ) == null || customer.getLati ( ).isEmpty ( ) ) {
            lati = 0;
        } else {
            lati = Double.parseDouble ( customer.getLati ( ) );
        }
        if ( customer.getLongi ( ) == null || customer.getLongi ( ).isEmpty ( ) ) {
            lngi = 0;
        } else {
            lngi = Double.parseDouble ( customer.getLongi ( ) );
        }
        lat.setText ( String.valueOf ( lati ) );
        lng.setText ( String.valueOf ( lngi ) );
        String image = customer.getImage ( );
        if ( image != null && ! image.isEmpty ( ) ) {
            Picasso.with ( CustomerCreation.this ).load ( image ).placeholder ( R.drawable.ic_account_circle_black ).error ( R.drawable.ic_account_circle_black ).into ( profileImageView );
        }
        add = getAddress ( new LatLng ( lati , lngi ) );
        location.setText ( add );
        mMap.clear ( );
        marker = mMap.addMarker ( new MarkerOptions ( ).icon ( BitmapDescriptorFactory.defaultMarker ( BitmapDescriptorFactory.HUE_RED ) ).position ( new LatLng ( lati , lngi ) ) );
        CameraPosition cameraPosition1 = new CameraPosition.Builder ( ).target ( new LatLng ( lati , lngi ) ).zoom ( 80 ).build ( );
        mMap.animateCamera ( CameraUpdateFactory.newCameraPosition ( cameraPosition1 ) );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                CustomerCreation.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                profileImageView.setEnabled(true);
            }
        }
    }

    private void captureImage() {
        if (Build.VERSION.SDK_INT > 21) { //use this if Lollipop_Mr1 (API 22) or above
            Intent callCameraApplicationIntent = new Intent();
            callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            // We give some instruction to the intent to save the image
            File photoFile = null;
            try {
                // If the createImageFile will be successful, the photo file will have the address of the file
                photoFile = createImageFile();
                // Here we call the function that will try to catch the exception made by the throw function
            } catch (IOException e) {
                Logger.getAnonymousLogger().info("Exception error in generating the file");
                e.printStackTrace();
            }
            // Here we add an extra file to the intent to put the address on to. For this purpose we use the FileProvider, declared in the AndroidManifest.
            Uri outputUri = FileProvider.getUriForFile(
                    this,
                    "app.zingo.mysolite.fileprovider",
                    photoFile);
            callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
            // The following is a new line with a trying attempt
            callCameraApplicationIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Logger.getAnonymousLogger().info("Calling the camera App by intent");
            // The following strings calls the camera app and wait for his file in return.
            startActivityForResult(callCameraApplicationIntent, CAMERA_PIC_REQUEST);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // start the image capture Intent
            startActivityForResult(intent, CAMERA_PIC_REQUEST);
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO || requestCode == REQUEST_PICK_PHOTO) {
                if (data != null) {
                    // Get the Image from data
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    assert cursor != null;
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    mediaPath = cursor.getString(columnIndex);
                    // Set the Image in ImageView for Previewing the Media
                    profileImageView.setImageBitmap(BitmapFactory.decodeFile(mediaPath));
                    cursor.close();

                    postPath = mediaPath;
                }


            }else if (requestCode == CAMERA_PIC_REQUEST){
                if (Build.VERSION.SDK_INT > 21) {

                    Glide.with(this).load(mImageFileLocation).into(profileImageView);
                    postPath = mImageFileLocation;

                }else{
                    Glide.with(this).load(fileUri).into(profileImageView);
                    postPath = fileUri.getPath();
                }
            }
        }
        else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, "Sorry, there was an error!", Toast.LENGTH_LONG).show();
        }
    }*/

    File createImageFile() throws IOException {
        Logger.getAnonymousLogger().info("Generating the image - method started");

        // Here we create a "non-collision file name", alternatively said, "an unique filename" using the "timeStamp" functionality
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmSS").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp;
        // Here we specify the environment location and the exact path where we want to save the so-created file
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/photo_saving_app");
        Logger.getAnonymousLogger().info("Storage directory set");

        // Then we create the storage directory if does not exists
        if (!storageDirectory.exists()) storageDirectory.mkdir();

        // Here we create the file using a prefix, a suffix and a directory
        File image = new File(storageDirectory, imageFileName + ".jpg");
        // File image = File.createTempFile(imageFileName, ".jpg", storageDirectory);

        // Here the location is saved into the string mImageFileLocation
        Logger.getAnonymousLogger().info("File name and path set");

        mImageFileLocation = image.getAbsolutePath();
        fileUri = Uri.parse(mImageFileLocation);
        // The file is returned to the previous intent across the camera application
        return image;
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Image", "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + ".jpg");
        }  else {
            return null;
        }

        return mediaFile;
    }

    public String getFilename(String filePath) {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        System.out.println("getFilePath = "+filePath);
        String uriSting;
        if(filePath.contains(".jpg")) {
            uriSting = (file.getAbsolutePath() + "/" + filePath);
        }
        else {
            uriSting = (file.getAbsolutePath() + "/" + filePath+".jpg" );
        }
        return uriSting;
    }

    private void uploadImage(final String filePath,final Employee customer) {
        //String filePath = getRealPathFromURIPath(uri, ImageUploadActivity.this);
        final File file = new File(filePath);
        int size = 1*1024*1024;
        if(file != null) {
            if(file.length() > size) {
                System.out.println(file.length());
                compressImage(filePath,customer);
            }
            else {
                final ProgressDialog dialog = new ProgressDialog( CustomerCreation.this);
                dialog.setCancelable(false);
                dialog.setTitle("Uploading Image..");
                dialog.show();
                Log.d("Image Upload", "Filename " + file.getName());
                RequestBody mFile = RequestBody.create(MediaType.parse("image"), file);
                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
                RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                UploadApi uploadImage = Util.getClient().create(UploadApi.class);
                Call<String> fileUpload = uploadImage.uploadProfileImages(fileToUpload, filename);
                fileUpload.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        if( Util.IMAGE_URL==null){
                            customer.setImage ( Constants.IMAGE_URL+ response.body());
                        }else{
                            customer.setImage ( Util.IMAGE_URL+ response.body());
                        }
                        // expenses.setImageUrl(Constants.IMAGE_URL+response.body().toString());
                        if(customer.getEmployeeId ()!=0){
                            updateCustomer(customer);
                        }else{
                            addCustomer(customer);
                        }
                        if(filePath.contains("MyFolder/Images")) {
                            file.delete();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d( "UpdateCate" , "Error " + "Bad Internet Connection" );
                    }
                });
            }
        }
    }

    public String compressImage(String filePath,final Employee customer) {
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = actualHeight/2;//2033.0f;
        float maxWidth = actualWidth/2;//1011.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt( ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String[] filearray = filePath.split("/");
        final String filename = getFilename(filearray[filearray.length-1]);
        try {
            out = new FileOutputStream(filename);
            // write the compressed bitmap at the field_icon specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            uploadImage(filename,customer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filename;
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    private void getmanagerProfile(final int id){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        EmployeeApi apiService = Util.getClient().create( EmployeeApi.class);
        Call<ArrayList<Employee>> call = apiService.getEmployeesByOrgId(id);
        call.enqueue(new Callback<ArrayList<Employee>>() {
            @Override
            public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                int statusCode = response.code();
                if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {
                    if (progressDialog != null&&progressDialog.isShowing())
                        progressDialog.dismiss();
                    ArrayList<Employee> list = response.body();
                    employees = new ArrayList <> (  );
                    if (list !=null && list.size()!=0) {
                        for ( Employee e: list) {
                            if(e.getUserRoleId ()!=8&&e.getUserRoleId ()!=10){
                                employees.add ( e );
                            }
                        }

                        if(employees!=null&&employees.size()!=0){
                            Collections.sort(employees, Employee.compareEmployee);
                            ManagerSpinnerAdapter arrayAdapter = new ManagerSpinnerAdapter( CustomerCreation.this, employees);
                            mtoReport.setAdapter(arrayAdapter);
                        }else{
                            Toast.makeText( CustomerCreation.this,"No Employees added",Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText( CustomerCreation.this,"No Employees added",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText( CustomerCreation.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {
                if (progressDialog != null&&progressDialog.isShowing())
                    progressDialog.dismiss();
                Log.e("TAG", t.toString());
            }
        });
    }
}
