package app.zingo.mysolite.ui.newemployeedesign;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import androidx.annotation.RequiresApi;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import androidx.multidex.BuildConfig;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import app.zingo.mysolite.adapter.CustomerSpinnerAdapter;
import app.zingo.mysolite.Custom.Floating.RFABTextUtil;
import app.zingo.mysolite.Custom.Floating.RFACLabelItem;
import app.zingo.mysolite.Custom.Floating.RapidFloatingActionButton;
import app.zingo.mysolite.Custom.Floating.RapidFloatingActionContentLabelList;
import app.zingo.mysolite.Custom.Floating.RapidFloatingActionHelper;
import app.zingo.mysolite.Custom.Floating.RapidFloatingActionLayout;
import app.zingo.mysolite.Custom.MyRegulerText;
import app.zingo.mysolite.FireBase.SharedPrefManager;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.model.EmployeeDeviceMapping;
import app.zingo.mysolite.model.EmployeeImages;
import app.zingo.mysolite.model.MeetingDetailsNotificationManagers;
import app.zingo.mysolite.model.Meetings;
import app.zingo.mysolite.model.Organization;
import app.zingo.mysolite.ui.Admin.CreateTaskScreen;
import app.zingo.mysolite.ui.Common.CustomerCreation;
import app.zingo.mysolite.ui.Common.PlanExpireScreen;
import app.zingo.mysolite.ui.landing.InternalServerErrorScreen;
import app.zingo.mysolite.utils.Constants;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.ThreadExecuter;
import app.zingo.mysolite.utils.TrackGPS;
import app.zingo.mysolite.utils.Util;
import app.zingo.mysolite.WebApi.EmployeeApi;
import app.zingo.mysolite.WebApi.EmployeeDeviceApi;
import app.zingo.mysolite.WebApi.EmployeeImageAPI;
import app.zingo.mysolite.WebApi.MeetingNotificationAPI;
import app.zingo.mysolite.WebApi.MeetingsAPI;
import app.zingo.mysolite.WebApi.OrganizationApi;
import app.zingo.mysolite.WebApi.UploadApi;
import app.zingo.mysolite.R;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseTooltip;

public class EmployeeNewMainScreen extends AppCompatActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener  {

    static final String TAG = "FounderMainScreen";
    //View Declaration
    CircleImageView mProfileImage;
    LinearLayout mTrialInfoLay,mShareLayout,mQrLayout,mRefreshLayout;
    //Visiting card
    TextView mCardName,mCardMobile,mCardEmail,mCardDesign,mCardAddress;
    LinearLayout mCardLinear;
    ImageView mCardLogo;
    CardView  mCardView;
    LinearLayout mWhatsapp;
    MyRegulerText share_card;


    boolean doubleBackToExitPressedOnce = false;


    Employee profile;
    EmployeeImages employeeImages;
    int userId=0,imageId=0;
    String appType="",planType="",licensesStartDate="",licenseEndDate="";
    int planId=0;

    private int SELECT_FILE = 2;
    String selectedImage;

    String currentVersion;
    Dialog dialog;

    private static final int REQUEST_PERMISSIONS = 100;
    boolean boolean_permission,boolean_permissions,boolean_permissiont;
    private static final int REQUEST= 112;
    private static final int REQUESTS= 113;

    Context mContext = this;

    public static final int MEDIA_TYPE_IMAGE = 1;
    private Uri fileUri;
    private String mImageFileLocation = "";
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";
    private String postPath;




    int pos;
    boolean con = true;

    private static final String SHOWCASE_ID = "Tools";

    private RapidFloatingActionHelper rfabHelper;

    //meeting
    ImageView mImageView;
    File file;
    Dialog dialogs;
    LinearLayout mContent;
    View view;
    signature mSignature;
    Button  mClear, mGetSigns, mCancel;
    Bitmap bitmap;

    // Creating Separate Directory for saving Generated Images
    String DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/Mysolite Apps/";
    String pic_name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    String StoredPath = DIRECTORY + pic_name + ".png";
    String StoredPathSelfie = DIRECTORY + pic_name+"selfie" + ".png";

    static final int REQUEST_IMAGE_CAPTURE = 1;

    Meetings loginDetails;
    MeetingDetailsNotificationManagers md;
    boolean methodAdd = false;

    ArrayList< Employee > customerArrayList;
    Spinner customerSpinner;
    LinearLayout ClientNameLayout;
    int clientId = 0;

    private GoogleApiClient mLocationClient;
    Location currentLocation;

    //Location
    TrackGPS gps;
    double latitude, longitude;

    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_PICK_PHOTO = 2;
    private static final int CAMERA_PIC_REQUEST = 1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_employee_new_main_screen);
/*
            if(Build.VERSION.SDK_INT>25){
                Util.schedulerJob(getApplicationContext());
            }
*/

            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                pos = extras.getInt("viewpager_position");
                con = extras.getBoolean("Condition");

            }
            gps = new TrackGPS ( EmployeeNewMainScreen.this);
            PreferenceHandler.getInstance( EmployeeNewMainScreen.this).setFirstCheck(con);

            RapidFloatingActionLayout rfaLayout = findViewById ( R.id.rfab_group_sample_fragment_a_rfal );
            RapidFloatingActionButton rfaButton = findViewById ( R.id.label_list_sample_rfab );

            setupViewPager(findViewById(R.id.viewPager));
            mWhatsapp = findViewById(R.id.whatsapp_open);
            mCardLogo = findViewById(R.id.logo);
            mCardName = findViewById(R.id.name_text);
            mCardDesign = findViewById(R.id.designation_text);
            mCardMobile = findViewById(R.id.phone_text);
            mCardEmail = findViewById(R.id.email_text);
            mCardAddress = findViewById(R.id.address_text);
            mCardView = findViewById(R.id.card);
            mCardLinear = findViewById(R.id.business_linear);
            share_card = findViewById(R.id.share_card);

            mCardView.setDrawingCacheEnabled(true);

            if (mLocationClient == null) {

                mLocationClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }


            setupData();

            getCurrentVersion();


            mWhatsapp.setOnClickListener( view -> {

                String message = "Hi I'm "+ PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getUserFullName()+",\n My Organization Name is "+ PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getCompanyName()+".I am writing about the feedback of Mysolite app Ver: "+ BuildConfig.VERSION_NAME+".";

                PackageManager packageManager = getPackageManager();
                Intent i = new Intent(Intent.ACTION_VIEW);

                try {
                    String url = "https://api.whatsapp.com/send?phone=+919986128021" + "&text=" + URLEncoder.encode(message, "UTF-8");
                    i.setPackage("com.whatsapp");
                    i.setData(Uri.parse(url));
                    if (i.resolveActivity(packageManager) != null) {
                        startActivity(i);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText( EmployeeNewMainScreen.this, "WhatsApp not installed.", Toast.LENGTH_SHORT).show();
                }



            } );

            fn_permission();

            String meetingStatus = PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getMeetingLoginStatus();

            String meeting ;

            if (meetingStatus != null && meetingStatus.equalsIgnoreCase("Login")) {

                meeting = "Meeting Check-Out";

            }else{

                meeting = "Meeting Check-In";
            }

            RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(this);
            rfaContent.setOnRapidFloatingActionContentLabelListListener(this);

            List< RFACLabelItem > items = new ArrayList<>();
            items.add(new RFACLabelItem <Integer> ()
                    .setLabel(meeting)
                    .setResId(R.drawable.maintenance)
                    .setIconNormalColor(0xffd84315)
                    .setIconPressedColor(0xffbf360c)
                    .setWrapper(0)
            );

            items.add(new RFACLabelItem <Integer> ()
                            .setLabel("Create Task")
                            .setDrawable(getResources().getDrawable(R.drawable.employee_menu))
                            .setIconNormalColor(0xff283593)
                            .setIconPressedColor(0xff1a237e)
                            .setLabelColor(Color.BLUE)
                            .setLabelSizeSp(14)

                            .setWrapper(1)
            );
            items.add(new RFACLabelItem <Integer> ()
                    .setLabel("Create Expense")
                    .setResId(R.drawable.employee_menu)
                    .setIconNormalColor(0xff056f00)
                    .setIconPressedColor(0xff0d5302)
                    .setLabelColor(0xff056f00)
                    .setWrapper(2)
            );
            items.add(new RFACLabelItem <Integer> ()
                    .setLabel("Create Client")
                    .setResId(R.drawable.rating_client)
                    .setIconNormalColor(0xff283593)
                    .setIconPressedColor(0xff1a237e)
                    .setLabelColor(0xff283593)
                    .setWrapper(3)
            );
            rfaContent
                    .setItems(items)
                    .setIconShadowRadius( RFABTextUtil.dip2px(this, 5))
                    .setIconShadowColor(0xff888888)
                    .setIconShadowDy( RFABTextUtil.dip2px(this, 5))
            ;

            rfabHelper = new RapidFloatingActionHelper (
                    this,
                    rfaLayout ,
                    rfaButton ,
                    rfaContent
            ).build();


        }catch(Exception e){
            e.printStackTrace();
        }

    }



    private void fn_permission() {

        if (Build.VERSION.SDK_INT >= 23) {
            Log.d("TAG","@@@ IN IF Build.VERSION.SDK_INT >= 23");
            String[] PERMISSIONS = {

                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE

            };


            if (!hasPermissions(mContext, PERMISSIONS)) {
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST_PERMISSIONS );
            } else {
                boolean_permission = true;
            }
        }

    }

    private void fn_permission_photo() {

        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE

            };


            if (!hasPermissions(mContext, PERMISSIONS)) {
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST );
            } else {
                boolean_permissions = true;
            }
        }

    }

    private void fn_permission_telephony() {

        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {
                    Manifest.permission.READ_PHONE_STATE

            };


            if (!hasPermissions(mContext, PERMISSIONS)) {
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUESTS );
            } else {
                boolean_permissiont = true;
            }
        }

    }

    @RequiresApi (api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult( int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            switch (requestCode) {
                case REQUEST_PERMISSIONS: {
                    boolean_permission = true;
                }
                break;

                case REQUEST: {
                    boolean_permissions = true;
                }
                break;

                case REQUESTS: {
                    boolean_permissiont = true;
                }
                break;


            }

        }

    }




    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final ArrayList< Fragment > mFragmentList = new ArrayList <> (  );
        private final ArrayList<String> mFragmentTitleList = new ArrayList <> (  );

        ViewPagerAdapter ( FragmentManager fragmentManager ) {
            super(fragmentManager);
        }

        public Fragment getItem( int i) {
            return this.mFragmentList.get(i);
        }

        public int getCount() {
            return this.mFragmentList.size();
        }

        void addFragment ( Fragment fragment , String str ) {
            this.mFragmentList.add(fragment);
            this.mFragmentTitleList.add(str);
        }

        public CharSequence getPageTitle(int i) {
            return this.mFragmentTitleList.get(i);
        }
    }



    private void setupTabIcons(TabLayout tabLayout) {
        Objects.requireNonNull ( tabLayout.getTabAt ( 4 ) ).setIcon(R.drawable.white_navigation);
    }

    private void setupViewPager( ViewPager viewPager) {

        viewPager.setOffscreenPageLimit(5);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(EmployeeDashBoardFragment.getInstance(), "Dash Board");
        viewPagerAdapter.addFragment( EmployeeNotificationScrenFragment.getInstance(), "Notification");
        viewPagerAdapter.addFragment(EmployeeLoginFragment.getInstance(), "Attendance");
        viewPagerAdapter.addFragment(EmployeeTaskFragment.getInstance(), "Tasks");
        viewPagerAdapter.addFragment( EmployeeHomeFragment.getInstance(), "");
        viewPager.setAdapter(viewPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons(tabLayout);

        if(pos!=0){
            viewPager.setCurrentItem(pos);
        }
    }

    public void onBackPressed() {

        if (this.doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
        } else {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_LONG).show();
            mCardLinear.setVisibility(View.GONE);
            new Handler().postDelayed( ( ) -> EmployeeNewMainScreen.this.doubleBackToExitPressedOnce = false , 2000);
        }
    }



    protected void onStart() {
        super.onStart ();
        if (mLocationClient != null) {
            mLocationClient.connect ();
        }
    }



    public void setupData() {
        TextView organizationName = findViewById(R.id.organizationName);
        TextView userName = findViewById(R.id.userName);
        mProfileImage = findViewById(R.id.profilePicture);
        mTrialInfoLay = findViewById(R.id.trial_version_info_layout);
        mShareLayout = findViewById(R.id.share_layout);
        mQrLayout = findViewById(R.id.qr_layout);
        mRefreshLayout = findViewById(R.id.refresh_layout);
        organizationName.setText(PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getCompanyName());
        userName.setText(PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getUserFullName());
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            profile = (Employee) bundle.getSerializable("Profile");
        }
        userId = PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getUserId();
        int mapId = PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getMappingId();
        EmployeeDeviceMapping hm = new EmployeeDeviceMapping();
        String token = SharedPrefManager.getInstance( EmployeeNewMainScreen.this).getDeviceToken();
        if(userId!=0&&token!=null&&mapId==0){
            hm.setEmployeeId(userId);
            hm.setDeviceId(token);
            addDeviceId(hm);
        }

        mProfileImage.setOnClickListener( v -> {
            fn_permission_photo();
            if(boolean_permissions){

                new MaterialDialog.Builder( EmployeeNewMainScreen.this)
                        .title(R.string.uploadImages)
                        .items(R.array.uploadImages)
                        .itemsIds(R.array.itemIds)
                        .itemsCallback( ( dialog , view , which , text ) -> {
                            switch (which) {
                                case 0:
                                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(galleryIntent, REQUEST_PICK_PHOTO);
                                    break;
                                case 1:
                                    captureImage();
                                    break;
                                case 2:
                                    mProfileImage.setImageResource(R.drawable.ic_account_circle_black);
                                    postPath = "";

                                    if (employeeImages == null) {
                                        EmployeeImages employeeImages = new EmployeeImages ();
                                        employeeImages.setImage(null);

                                        employeeImages.setEmployeeId(PreferenceHandler.getInstance ( EmployeeNewMainScreen.this ).getUserId ());
                                        addProfileImage(employeeImages);
                                    } else {

                                        EmployeeImages employeeImagess = employeeImages;
                                        employeeImages.setImage(null);
                                        employeeImagess.setEmployeeImageId(employeeImages.getEmployeeImageId());
                                        employeeImages.setEmployeeId(PreferenceHandler.getInstance ( EmployeeNewMainScreen.this ).getUserId ());
                                        updateProfileImage(employeeImages);
                                    }

                                    break;
                            }
                        } )
                        .show();

            }else{
               /* new CustomDesignAlertDialog(this, CustomDesignAlertDialog.ERROR_TYPE,"PermissionDenied")
                        .setTitleText("Permission Denied")
                        .setContentText("Without this permission the app.")
                        .show();*/
            }


        } );
        if(profile==null){
            if(userId!=0){
                System.out.println("Going it");
                getProfile(userId,mProfileImage);
            }
        }else{

            profile.setAppOpen(true);
            String app_version = PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getAppVersion();
            profile.setLastUpdated(""+ app_version);
            profile.setLastseen(new SimpleDateFormat("MM/dd/yyyy",Locale.US).format(new Date()));
            mCardName.setText( PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getUserFullName());
            if(profile.getDesignation()!=null){
                mCardDesign.setText(profile.getDesignation().getDesignationTitle());
            }else{
                mCardDesign.setText("");
            }
            String imageLogo = PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getLogo();
            if(imageLogo!=null&&!imageLogo.isEmpty()){
                Picasso.with( EmployeeNewMainScreen.this).load(imageLogo).placeholder(R.drawable.profile_image).error(R.drawable.profile_image).into(mCardLogo);
            }
            mCardMobile.setText(PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getPhoneNumber());
            mCardEmail.setText(PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getUserEmail());
            String address = PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getCompanyName()+getResources ().getString( R.string.new_line)+ PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getCompanyAddress();
            mCardAddress.setText(address );
            updateProfile(profile);
            ArrayList< EmployeeImages > images = profile.getEmployeeImages();
            if(images!=null&&images.size()!=0){
                employeeImages = images.get(0);
                if(employeeImages!=null){
                    imageId = employeeImages.getEmployeeImageId();
                    String base=employeeImages.getImage();
                    if(base != null && !base.isEmpty()){
                        Picasso.with( EmployeeNewMainScreen.this).load(base).placeholder(R.drawable.ic_account_circle_black).
                                error(R.drawable.ic_account_circle_black).into(mProfileImage);
                    }
                }
            }
        }

        mQrLayout.setOnClickListener( view -> {
            fn_permission_telephony();
            if(boolean_permissiont){
                Intent qr = new Intent( EmployeeNewMainScreen.this, EmployeeQrCodeGenerate.class);
                startActivity(qr);
            }else{
                Toast.makeText ( EmployeeNewMainScreen.this , "Permission Required" , Toast.LENGTH_SHORT ).show ( );
            }
        } );


        try {

            if(PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getCompanyId()!=0){
                getCompany(PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getCompanyId());
            }

        } catch (Exception e) {
            e.printStackTrace();
            Intent i = new Intent( EmployeeNewMainScreen.this, InternalServerErrorScreen.class);
            startActivity(i);
        }

        mShareLayout.setOnClickListener( view -> {
            try{
                fn_permission_photo();
                if(boolean_permissions){
                    mCardLinear.setVisibility(View.VISIBLE);
                    mCardView.setVisibility(View.VISIBLE);
                    mCardView.post( ( ) -> openScreenshot(saveBitMap( EmployeeNewMainScreen.this,mCardView)) );
                }




            }catch (Exception e){
                e.printStackTrace();
            }
        } );

        mRefreshLayout.setOnClickListener( view -> {
            try{

                recreate();
            }catch (Exception e){
                e.printStackTrace();
            }
        } );


    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public void getProfile(final int id,final ImageView mProfileImage ){

        final EmployeeApi subCategoryAPI = Util.getClient().create( EmployeeApi.class);
        Call<ArrayList<Employee>> getProf = subCategoryAPI.getProfileById(id);
        getProf.enqueue(new Callback<ArrayList<Employee>>() {

            @Override
            public void onResponse( @NonNull Call<ArrayList<Employee>> call, @NonNull Response<ArrayList<Employee>> response) {

                if (response.code() == 200&&response.body ()!=null)
                {
                    if(response.body ().size ()!=0){

                        profile = response.body().get(0);
                        profile.setAppOpen(true);
                        PreferenceHandler.getInstance( EmployeeNewMainScreen.this).setShftName(""+profile.getDeviceModel());
                        String app_version = PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getAppVersion();
                        profile.setLastUpdated(""+ app_version);
                        profile.setLastseen(new SimpleDateFormat("MM/dd/yyyy",Locale.US).format(new Date()));
                        updateProfile(profile);

                        mCardName.setText(PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getUserFullName());
                        if(profile.getDesignation()!=null){
                            mCardDesign.setText(profile.getDesignation().getDesignationTitle());
                        }else{
                            mCardDesign.setText("");
                        }

                        mCardMobile.setText(PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getPhoneNumber());
                        mCardEmail.setText(PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getUserEmail());
                        String address =  PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getCompanyName()+"\n"+ PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getCompanyAddress();
                        mCardAddress.setText(address);

                        String imageLogo = PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getLogo();
                        if(imageLogo!=null&&!imageLogo.isEmpty()){

                            Picasso.with( EmployeeNewMainScreen.this).load(imageLogo).placeholder(R.drawable.profile_image).error(R.drawable.profile_image).into(mCardLogo);
                        }


                        ArrayList< EmployeeImages > images = profile.getEmployeeImages();

                        if(images!=null&&images.size()!=0){
                            employeeImages = images.get(0);

                            if(employeeImages!=null){
                                imageId = employeeImages.getEmployeeImageId();
                                String base=employeeImages.getImage();
                                if(base != null && !base.isEmpty()){
                                    Picasso.with( EmployeeNewMainScreen.this).load(base).placeholder(R.drawable.ic_account_circle_black).
                                            error(R.drawable.ic_account_circle_black).into(mProfileImage);

                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure( @NonNull Call<ArrayList<Employee>> call, @NonNull Throwable t) {

                Log.d("EmployeeNewMainScreen",t.getMessage ());

            }
        });
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

    private void galleryIntent(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        try {
            intent.putExtra("return-data", true);
            startActivityForResult(Intent.createChooser(intent,"Select File"), SELECT_FILE);
        } catch (ActivityNotFoundException e) {
            // Do nothing for now
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE){
                onSelectFromGalleryResult(data);
            }else if (requestCode == REQUEST_IMAGE_CAPTURE ) {
                try {
                    Bundle bundle = data.getExtras();
                    if(bundle!=null){
                        Bitmap bitmap = (Bitmap) bundle.get("data");
                        saveSelfie(bitmap,StoredPathSelfie);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if (requestCode == REQUEST_TAKE_PHOTO || requestCode == REQUEST_PICK_PHOTO) {
                if (data != null) {
                    // Get the Image from data
                    Uri selectedImages = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImages, filePathColumn, null, null, null);
                    assert cursor != null;
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String mediaPath = cursor.getString ( columnIndex );
                    // Set the Image in ImageView for Previewing the Media
                    mProfileImage.setImageBitmap(BitmapFactory.decodeFile( mediaPath ));
                    cursor.close();

                    postPath = mediaPath;

                    String[] all_path = {postPath};
                    selectedImage = all_path[0];
                    for (String s:all_path)
                    {
                        File imgFile = new  File(s);
                        if(imgFile.exists()) {
                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            addImage( Util.getResizedBitmap(myBitmap,700));
                        }
                    }
                }


            }else if (requestCode == CAMERA_PIC_REQUEST){
                if (Build.VERSION.SDK_INT > 21) {

                    Glide.with(this).load(mImageFileLocation).into(mProfileImage);
                    postPath = mImageFileLocation;

                }else{
                    Glide.with(this).load(fileUri).into(mProfileImage);
                    postPath = fileUri.getPath();
                }

                String[] all_path = {postPath};
                selectedImage = all_path[0];
                for (String s:all_path)
                {
                    File imgFile = new  File(s);
                    if(imgFile.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        addImage( Util.getResizedBitmap(myBitmap,700));
                    }
                }
            }
        }
    }

    private void onSelectFromGalleryResult(Intent data) {

        try{
            if(data!=null){

                Uri selectedImageUri = data.getData( );

                if(selectedImageUri!=null){

                    InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
                    final Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                    mProfileImage.setImageBitmap(bitmap);

                    Uri temUri = getImageUri( getApplicationContext(), bitmap );
                    String picturePath = getPath( getApplicationContext(), temUri );
                    Log.d("Picture Path", picturePath);
                    String[] all_path = {picturePath};
                    selectedImage = all_path[0];
                    for (String s:all_path)
                    {
                        File imgFile = new  File(s);
                        if(imgFile.exists()) {
                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            addImage( Util.getResizedBitmap(myBitmap,700));
                        }
                    }

                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Uri getImageUri(Context applicationContext, Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(applicationContext.getContentResolver(),bitmap,"Title",null);
        return Uri.parse(path);
    }

    public static String getPath(Context context, Uri uri ) {
        Cursor cursor = context.getContentResolver().query(uri,null,null,null,null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public void addImage(Bitmap bitmap)
    {
        try{
           if(bitmap != null)
            {
                mProfileImage.setImageBitmap(bitmap);
                if(selectedImage != null && !selectedImage.isEmpty())
                {
                    File file = new File(selectedImage);
                    if(file.length() <= 1024*1024)
                    {
                        FileOutputStream out = null;
                        String[] filearray = selectedImage.split("/");
                        final String filename = getFilename(filearray[filearray.length-1]);
                        out = new FileOutputStream(filename);
                        Bitmap myBitmap = BitmapFactory.decodeFile(selectedImage);
                        // write the compressed bitmap at the field_icon specified by filename.
                        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        uploadImage(filename,profile);
                    }
                    else
                    {
                        compressImage(selectedImage,profile);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void uploadImage(final String filePath,final Employee employee)
    {
        final File file = new File(filePath);
        int size = 1*1024*1024;
        if(file != null)
        {
            if(file.length() > size)
            {
                System.out.println(file.length());
                compressImage(filePath,employee);
            }
            else
            {
                final ProgressDialog dialog = new ProgressDialog( EmployeeNewMainScreen.this);
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
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 204) {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }

                            if (employeeImages == null) {
                                EmployeeImages employeeImages = new EmployeeImages ();

                                if ( Util.IMAGE_URL == null) {
                                    employeeImages.setImage( Constants.IMAGE_URL + response.body());
                                } else {
                                    employeeImages.setImage( Util.IMAGE_URL + response.body());
                                }

                                employeeImages.setEmployeeId(employee.getEmployeeId());
                                addProfileImage(employeeImages);
                            } else {

                                EmployeeImages employeeImagess = employeeImages;
                                if ( Util.IMAGE_URL == null) {
                                    employeeImages.setImage( Constants.IMAGE_URL + response.body());
                                } else {
                                    employeeImages.setImage( Util.IMAGE_URL + response.body());
                                }
                                employeeImagess.setEmployeeImageId(employeeImages.getEmployeeImageId());
                                employeeImages.setEmployeeId(employee.getEmployeeId());
                                updateProfileImage(employeeImages);
                            }

                            if (filePath.contains("MyFolder/Images")) {
                                file.delete();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        //Log.d("UpdateCate", "Error " + "Bad Internet Connection");
                    }
                });
            }
        }
    }

    public String compressImage(String filePath,final Employee Employee) {
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        //max Height and width values of the compressed image is taken as 816x612
        float maxHeight = actualHeight / 2;//2033.0f;
        float maxWidth = actualWidth / 2;//1011.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;
        // width and height values are set maintaining the aspect ratio of the image
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
        //setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        //inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;
        //this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];
        try {
            //load the bitmap from its path
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
        //check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
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
        final String filename = getFilename(filearray[filearray.length - 1]);
        try {
            out = new FileOutputStream(filename);
            //write the compressed bitmap at the field_icon specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            uploadImage(filename, Employee);
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

    public String getFilename(String filePath) {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        System.out.println("getFilePath = "+filePath);
        String uriSting;
        if(filePath.contains(".jpg"))
        {
            uriSting = (file.getAbsolutePath() + "/" + filePath);
        }
        else
        {
            uriSting = (file.getAbsolutePath() + "/" + filePath+".jpg" );
        }
        return uriSting;
    }

    private void updateProfileImage(final EmployeeImages employeeImages) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Updating Image..");
        dialog.show();

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                EmployeeImageAPI auditApi = Util.getClient().create( EmployeeImageAPI.class);
                Call< EmployeeImages > response = auditApi.updateEmployeeImage(employeeImages.getEmployeeImageId(),employeeImages);
                response.enqueue(new Callback< EmployeeImages >() {
                    @Override
                    public void onResponse( Call< EmployeeImages > call, Response< EmployeeImages > response) {
                        if(response.code() == 201||response.code() == 200||response.code() == 204)
                        {
                            if(dialog != null)
                            {
                                dialog.dismiss();
                            }
                            System.out.println(response.code());
                               // mProfileImage.setImageBitmap(bitmap);
                                Toast.makeText( EmployeeNewMainScreen.this,"Profile Image Updated",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText( EmployeeNewMainScreen.this,response.message(),Toast.LENGTH_SHORT).show();
                            }
                    }

                    @Override
                    public void onFailure( Call< EmployeeImages > call, Throwable t) {
                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }

                    }
                });
            }
        });
    }

    private void addProfileImage(final EmployeeImages employeeImages) {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Updating Image..");
        dialog.show();

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                EmployeeImageAPI auditApi = Util.getClient().create( EmployeeImageAPI.class);
                Call< EmployeeImages > response = auditApi.addEmployeeImage(employeeImages);
                response.enqueue(new Callback< EmployeeImages >() {
                    @Override
                    public void onResponse( Call< EmployeeImages > call, Response< EmployeeImages > response) {
                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        System.out.println(response.code());
                        mProfileImage.setImageBitmap(bitmap);

                        if(response.code() == 201||response.code() == 200||response.code() == 204)
                        {
                            Toast.makeText( EmployeeNewMainScreen.this,"Profile Image Updated",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText( EmployeeNewMainScreen.this,response.message(),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure( Call< EmployeeImages > call, Throwable t) {
                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }

                    }
                });
            }
        });
    }

    public void addDeviceId(final EmployeeDeviceMapping pf)
    {
        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {


                EmployeeDeviceApi hotelOperation = Util.getClient().create( EmployeeDeviceApi.class);
                Call<EmployeeDeviceMapping> response = hotelOperation.addProfileDevice(pf);

                response.enqueue(new Callback<EmployeeDeviceMapping>() {
                    @Override
                    public void onResponse(Call<EmployeeDeviceMapping> call, Response<EmployeeDeviceMapping> response) {
                        System.out.println("GetHotelByProfileId = "+response.code());


                        if(response.code() == 200||response.code() == 201||response.code() == 202||response.code() == 204)
                        {
                            try{
                                System.out.println("registered");
                                EmployeeDeviceMapping pr = response.body();

                                System.out.println();

                                if(pr != null)
                                {
                                    PreferenceHandler.getInstance( EmployeeNewMainScreen.this).setMappingId(pr.getEmployeeDeviceMappingId());
                                }

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else if(response.code() == 404){
                            System.out.println("already registered");
                        }
                        else
                        {

                        }
                    }

                    @Override
                    public void onFailure(Call<EmployeeDeviceMapping> call, Throwable t) {

                    }
                });
            }
        });
    }

    public void getCompany(final int id) {
        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {

                final OrganizationApi subCategoryAPI = Util.getClient().create( OrganizationApi.class);
                Call<ArrayList< Organization >> getProf = subCategoryAPI.getOrganizationById(id);
                getProf.enqueue(new Callback<ArrayList< Organization >>() {
                    @Override
                    public void onResponse( Call<ArrayList< Organization >> call, Response<ArrayList< Organization >> response) {

                        if (response.code() == 200||response.code() == 201||response.code() == 204&&response.body().size()!=0)
                        {
                            Organization organization = response.body().get(0);
                            System.out.println("Inside api");
                            PreferenceHandler.getInstance( EmployeeNewMainScreen.this).setCompanyId(organization.getOrganizationId());
                            PreferenceHandler.getInstance( EmployeeNewMainScreen.this).setCompanyName(organization.getOrganizationName());
                            PreferenceHandler.getInstance( EmployeeNewMainScreen.this).setAppType(organization.getAppType());

                            PreferenceHandler.getInstance( EmployeeNewMainScreen.this).setAppType(organization.getAppType());
                            PreferenceHandler.getInstance( EmployeeNewMainScreen.this).setLicenseStartDate(organization.getLicenseStartDate());
                            PreferenceHandler.getInstance( EmployeeNewMainScreen.this).setLicenseEndDate(organization.getLicenseEndDate());
                            PreferenceHandler.getInstance( EmployeeNewMainScreen.this).setSignupDate(organization.getSignupDate());
                            PreferenceHandler.getInstance( EmployeeNewMainScreen.this).setOrganizationLongi(organization.getLongitude());
                            PreferenceHandler.getInstance( EmployeeNewMainScreen.this).setOrganizationLati(organization.getLatitude());
                            PreferenceHandler.getInstance( EmployeeNewMainScreen.this).setPlanType(organization.getPlanType());
                            PreferenceHandler.getInstance( EmployeeNewMainScreen.this).setEmployeeLimit(organization.getEmployeeLimit());
                            PreferenceHandler.getInstance( EmployeeNewMainScreen.this).setPlanId(organization.getPlanId());

                            appType = PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getAppType();
                            planType = PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getPlanType();
                            licensesStartDate = PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getLicenseStartDate();
                            licenseEndDate = PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getLicenseEndDate();
                            planId = PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getPlanId();

                            try{

                                if(appType!=null){

                                    if(appType.equalsIgnoreCase("Trial")){

                                        SimpleDateFormat smdf = new SimpleDateFormat("MM/dd/yyyy");
                                        long days = dateCal(licenseEndDate);
                                        if((smdf.parse(licenseEndDate).getTime()<smdf.parse(smdf.format(new Date())).getTime())){

                                            Toast.makeText( EmployeeNewMainScreen.this, "Your Trial Period is Expired", Toast.LENGTH_SHORT).show();
                                            PreferenceHandler.getInstance( EmployeeNewMainScreen.this).clear();

                                            Intent log = new Intent( EmployeeNewMainScreen.this, PlanExpireScreen.class);
                                            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                            //Toast.makeText(EmployeeNewMainScreen.this,"Logout",Toast.LENGTH_SHORT).show();
                                            startActivity(log);
                                            finish();

                                        }else{
                                            //mTrialMsgInfo.setText("Your Trial version is going to expiry in "+days+" days");
                                            if(days>=1&&days<=5){
                                                //popupUpgrade("Hope your enjoying to use our Trial version.Get more features You need to Upgrade App","Your trial period is going to expire in "+days+" days");
                                            }else if(days==0){
                                                // popupUpgrade("Hope your enjoying to use our Trial version.Get more features You need to Upgrade App","Today is last day for your free trial");
                                                // mTrialMsgInfo.setText("Your Trial version is going to expiry in today");

                                            }else if(days<0){
                                                Toast.makeText( EmployeeNewMainScreen.this, "Your Trial Period is Expired", Toast.LENGTH_SHORT).show();
                                                PreferenceHandler.getInstance( EmployeeNewMainScreen.this).clear();

                                                Intent log = new Intent( EmployeeNewMainScreen.this, PlanExpireScreen.class);
                                                log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(log);
                                                finish();
                                            }
                                        }
                                    }else if(appType.equalsIgnoreCase("Paid")){
                                        mTrialInfoLay.setVisibility(View.GONE);
                                    }
                                }

                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }else{

                        }
                    }

                    @Override
                    public void onFailure( Call<ArrayList< Organization >> call, Throwable t) {

                    }
                });
            }
        });
    }

    public long dateCal(String date){

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date fd=null,td=null;

        try {
            fd = sdf.parse(""+date);
            td = sdf.parse(""+sdf.format(new Date()));

            long diff = fd.getTime() - td.getTime();
            long days = diff / (24 * 60 * 60 * 1000);
            return  days;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }




    private void getCurrentVersion(){

        PackageManager pm = this.getPackageManager();
        PackageInfo pInfo = null;

        try {

            pInfo =  pm.getPackageInfo(this.getPackageName(),0);

        } catch (PackageManager.NameNotFoundException e1) {

            e1.printStackTrace();
        }
         currentVersion = pInfo.versionName;
        //currentVersion = PreferenceHandler.getInstance ( EmployeeNewMainScreen.this ).getVersionCode ();


        new GetVersionCode().execute();

    }

    class GetVersionCode extends AsyncTask <Void, String, String> {

        @Override
        protected String doInBackground ( Void... voids ) {
            String newVersion = null;
            try {
                Connection connection = Jsoup.connect ( "https://play.google.com/store/apps/details?id=app.zingo.mysolite" + "&hl=en" );
                Document document = connection.timeout ( 30000 )
                        .userAgent ( "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6" )
                        .referrer ( "http://www.google.com" )
                        .get ( );

                Elements versions = document.getElementsByClass ( "htlgb" );

                for ( int i = 0 ; i < versions.size ( ) ; i++ ) {
                    newVersion = versions.get ( i ).text ( );
                    if ( Pattern.matches ( "^[0-9]{1}.[0-9]{2}$" , newVersion ) ) {
                        break;
                    }
                }

            } catch ( Exception e ) {
                return newVersion;
            }
            return newVersion;
        }

        @Override
        protected void onPostExecute ( String onlineVersion ) {
            super.onPostExecute ( onlineVersion );
            if ( onlineVersion != null && ! onlineVersion.isEmpty ( ) ) {

                if ( ! onlineVersion.equalsIgnoreCase ( currentVersion ) ) {
                    if ( EmployeeNewMainScreen.this != null && ! EmployeeNewMainScreen.this.isFinishing ( ) ) {

                        final AlertDialog.Builder builder = new AlertDialog.Builder ( EmployeeNewMainScreen.this );
                        builder.setTitle ( "A New Update is Available" );
                        builder.setPositiveButton ( "Update" , new DialogInterface.OnClickListener ( ) {
                            @Override
                            public void onClick ( DialogInterface dialog , int which ) {
                                startActivity ( new Intent ( Intent.ACTION_VIEW , Uri.parse
                                        ( "https://play.google.com/store/apps/details?id=app.zingo.mysolite" ) ) );
                                dialog.dismiss ( );
                            }
                        } );

                        builder.setNegativeButton ( "Cancel" , new DialogInterface.OnClickListener ( ) {
                            @Override
                            public void onClick ( DialogInterface dialog , int which ) {

                                //background.start();
                                // Toast.makeText(AdminNewMainScreen.this, "Check", Toast.LENGTH_SHORT).show();
                            }
                        } );

                        builder.setCancelable ( false );
                        dialog = builder.show ( );
                    }
                }


            }
        }
    }

    public void updateProfile(final Employee employee){



        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {

                final EmployeeApi subCategoryAPI = Util.getClient().create( EmployeeApi.class);
                Call<Employee> getProf = subCategoryAPI.updateEmployee(employee.getEmployeeId(),employee);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<Employee>() {

                    @Override
                    public void onResponse(Call<Employee> call, Response<Employee> response) {


                        if (response.code() == 200||response.code()==201||response.code()==204)
                        {


                        }else{
                            // Toast.makeText(ChangePasswordScreen.this, "Failed due to status code"+response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Employee> call, Throwable t) {


                        //  Toast.makeText(ChangePasswordScreen.this, "Something went wrong due to "+"Bad Internet Connection", Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if ( ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void presentShowcaseView() throws Exception {

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);


        ShowcaseTooltip toolTip1 = ShowcaseTooltip.build(this)
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("This is a <b>Employee QR</b> code.<br><br>Click on QR code symbol, Scan it from Admin mobile & Mark your attendance.. <br><br>Tap anywhere to continue");


        sequence.addSequenceItem(

                new MaterialShowcaseView.Builder(this)
                        .setTarget(mQrLayout)
                        .setToolTip(toolTip1)
                        .withRectangleShape()
                        .setTooltipMargin(30)
                        .setShapePadding(50)
                        .setDismissOnTouch(true)
                        .setMaskColour(getResources().getColor(R.color.tooltip_mask))
                        .build()
        );






        sequence.start();


    }

    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {

        rfabHelper.toggleContent();

        if(position==0){



            String loginStatus = PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getMeetingLoginStatus();
            String masterloginStatus = PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getLoginStatus();


            if (masterloginStatus.equals("Login")) {
                if (loginStatus != null && !loginStatus.isEmpty()) {

                    if (loginStatus.equalsIgnoreCase("Login")) {
                        //meetingloginalert("Login");
                        getMeetings(PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getMeetingId());

                    } else if (loginStatus.equalsIgnoreCase("Logout")) {

                        meetingloginalert("Logout");
                    }

                } else {
                    meetingloginalert("Logout");
                }
            } else {
                Toast.makeText( EmployeeNewMainScreen.this, "Please put your Master Check-In and do Meeting Check-In", Toast.LENGTH_SHORT).show();
            }



        }else if(position==1){

            Intent createTask = new Intent( EmployeeNewMainScreen.this, CreateTaskScreen.class);
            createTask.putExtra("EmployeeId", PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getUserId());
            startActivity(createTask);

        }else if(position==2){

            Intent leave = new Intent( EmployeeNewMainScreen.this, ExpenseManageHost.class);
            Bundle bundle = new Bundle();
            bundle.putInt("EmployeeId",profile.getEmployeeId());
            bundle.putSerializable("Employee",profile);
            leave.putExtras(bundle);
            startActivity(leave);

        }else if(position==3){

            Intent branch = new Intent( EmployeeNewMainScreen.this, CustomerCreation.class);
            startActivity(branch);

        }
    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {

        rfabHelper.toggleContent();
        if(position==0){
            String loginStatus = PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getMeetingLoginStatus();
            String masterloginStatus = PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getLoginStatus();

            if (masterloginStatus.equals("Login")) {
                if (loginStatus != null && !loginStatus.isEmpty()) {

                    if (loginStatus.equalsIgnoreCase("Login")) {
                        //meetingloginalert("Login");
                        getMeetings(PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getMeetingId());

                    } else if (loginStatus.equalsIgnoreCase("Logout")) {

                        meetingloginalert("Logout");
                    }

                } else {
                    meetingloginalert("Logout");
                }
            } else {
                Toast.makeText( EmployeeNewMainScreen.this, "Please put your Master Check-In and do Meeting Check-In", Toast.LENGTH_SHORT).show();
            }


        }else if(position==1){

            Intent createTask = new Intent( EmployeeNewMainScreen.this, CreateTaskScreen.class);
            createTask.putExtra("EmployeeId", PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getUserId());
            startActivity(createTask);

        }else if(position==2){

            Intent leave = new Intent( EmployeeNewMainScreen.this, ExpenseManageHost.class);
            Bundle bundle = new Bundle();
            bundle.putInt("EmployeeId",profile.getEmployeeId());
            bundle.putSerializable("Employee",profile);
            leave.putExtras(bundle);
            startActivity(leave);


        }else if(position==3){

            Intent branch = new Intent( EmployeeNewMainScreen.this, CustomerCreation.class);
            startActivity(branch);

        }
    }

    private void openScreenshot(File imageFile) {

        String message = "My Contact Details \n Name : "+mCardName.getText().toString()+",\n Designation: "+mCardDesign.getText().toString()+"\n Email: "+mCardEmail.getText().toString()+",\n Mobile: "+mCardMobile.getText().toString()+",\n Address: "+mCardAddress.getText().toString();

        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            uri = FileProvider.getUriForFile( EmployeeNewMainScreen.this, "app.zingo.mysolite.fileprovider", imageFile);
        }else{
            uri = Uri.fromFile(imageFile);
        }

        mCardLinear.setVisibility(View.GONE);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/jpeg");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "send"));

       /* Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);



        intent.setDataAndType(uri, "image/*");
        startActivity(intent);*/
    }

    private File saveBitMap(Context context, View drawView){
        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Mysolite");
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if(!isDirectoryCreated)
                Log.i("ATG", "Can't create directory to save the image");
            return null;
        }
        String filename = pictureFileDir.getPath() +File.separator+ System.currentTimeMillis()+".jpeg";
        File pictureFile = new File(filename);
        Bitmap bitmap =getBitmapFromView(drawView);
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
        //scanGallery( context,pictureFile.getAbsolutePath());
        return pictureFile;
    }
    //create bitmap from view and returns it
    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        System.out.println("Height "+view.getHeight()+" Width "+view.getWidth());
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    public void meetingloginalert(final String status){

        try{

            if(locationCheck()){
                String message = "Login";
                String option = "Meeting-In";

                if(status.equalsIgnoreCase("Login")){

                    message = "Do you want to Check-Out?";
                    option = "Meeting-Out";

                }else if(status.equalsIgnoreCase("Logout")){

                    message = "Do you want to Check-In?";
                    option = "Check-In";
                }

                AlertDialog.Builder builder = new AlertDialog.Builder( EmployeeNewMainScreen.this);
                LayoutInflater inflater = (LayoutInflater) EmployeeNewMainScreen.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View views = inflater.inflate(R.layout.activity_meeting_add_with_sign_screen, null);

                builder.setView(views);


                final Button mSave = views.findViewById(R.id.save);
                mSave.setText(option);
                final EditText mDetails = views.findViewById(R.id.meeting_remarks);
                final TextInputEditText mClientName = views.findViewById(R.id.client_name);
                final TextInputEditText mClientMobile = views.findViewById(R.id.client_contact_number);
                final TextInputEditText  mClientMail = views.findViewById(R.id.client_contact_email);
                final TextInputEditText mPurpose = views.findViewById(R.id.purpose_meeting);

                final CheckBox mGetSign = views.findViewById(R.id.get_sign_check);
                final CheckBox mTakeImage = views.findViewById(R.id.get_image_check);
                final LinearLayout mTakeImageLay = views.findViewById(R.id.selfie_lay);
                final LinearLayout mGetSignLay = views.findViewById(R.id.sign_lay);
                mImageView = views.findViewById(R.id.selfie_pic);
                customerSpinner = views.findViewById(R.id.customer_spinner_adpter);
                ClientNameLayout =  views.findViewById(R.id.client_name_layout);

                getCustomers(PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getCompanyId());

                mGetSignLay.setVisibility(View.GONE);
                mTakeImageLay.setVisibility(View.GONE);


                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.setCanceledOnTouchOutside(false);

                customerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        if(customerArrayList!=null && customerArrayList.size()!=0){


                            if(customerArrayList.get(position).getEmployeeName ()!=null && customerArrayList.get(position).getEmployeeName().equalsIgnoreCase("Others"))
                            {
                                mClientMobile.setText("");
                                mClientName.setText("");
                                mClientMail.setText("");
                                ClientNameLayout.setVisibility(View.VISIBLE);

                            }
                            else {
                                mClientMobile.setText(""+customerArrayList.get(position).getPhoneNumber ());
                                mClientName.setText(""+customerArrayList.get(position).getEmployeeName());
                                mClientMail.setText(""+customerArrayList.get(position).getPrimaryEmailAddress ());
                                clientId = customerArrayList.get(position).getEmployeeId ();
                                ClientNameLayout.setVisibility(View.GONE);

                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                mSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String client = mClientName.getText().toString();
                        String purpose = mPurpose.getText().toString();
                        String detail = mDetails.getText().toString();
                        String mobile = mClientMobile.getText().toString();
                        String email = mClientMail.getText().toString();
                        String customer = customerSpinner.getSelectedItem().toString();

                        if(client==null||client.isEmpty()){

                            Toast.makeText( EmployeeNewMainScreen.this, "Please mention client name", Toast.LENGTH_SHORT).show();

                        }else if(purpose==null||purpose.isEmpty()){

                            Toast.makeText( EmployeeNewMainScreen.this, "Please mention purpose of meeting", Toast.LENGTH_SHORT).show();

                        }else if(detail==null||detail.isEmpty()){

                            Toast.makeText( EmployeeNewMainScreen.this, "Please mention remarks about meeting", Toast.LENGTH_SHORT).show();

                        }else{

                            //gps = new TrackGPS(EmployeeNewMainScreen.this);

                            if(locationCheck()){

                                if(currentLocation!=null) {

                                    latitude = currentLocation.getLatitude();
                                    longitude = currentLocation.getLongitude();

                                    LatLng masters = new LatLng(latitude, longitude);
                                    String addresss = null;
                                    try {
                                        addresss = getAddress(masters);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                   /* latLong.setText(addresss);
                                    centreMapOnLocationWithLatLng(masters, "" + PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getUserFullName());
*/
                                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                    SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");

                                    LatLng master = new LatLng(latitude,longitude);
                                    String address = null;
                                    try {
                                        address = getAddress(master);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    loginDetails = new Meetings ();
                                    loginDetails.setEmployeeId(PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getUserId());
                                    loginDetails.setStartLatitude(""+latitude);
                                    loginDetails.setStartLongitude(""+longitude);
                                    loginDetails.setStartLocation(""+address);
                                    loginDetails.setStartTime(""+sdt.format(new Date()));
                                   /* loginDetails.setEndLatitude(""+latitude);
                                    loginDetails.setEndLongitude(""+longitude);
                                    loginDetails.setEndLocation(""+address);
                                    loginDetails.setEndTime(""+sdt.format(new Date()));*/
                                    loginDetails.setMeetingDate(""+sdf.format(new Date()));
                                    loginDetails.setMeetingAgenda(purpose);
                                    loginDetails.setMeetingDetails(detail);
                                    loginDetails.setStatus("In Meeting");

                                    if(customer!=null&&!customer.equalsIgnoreCase("Others")){

                                        if(customerArrayList!=null&&customerArrayList.size()!=0)
                                            loginDetails.setCustomerId(clientId);

                                    }

                                    methodAdd = false;

                                    String contact = "";

                                    if(email!=null&&!email.isEmpty()){
                                        contact = contact+"%"+email;
                                    }

                                    if(mobile!=null&&!mobile.isEmpty()){
                                        contact = contact+"%"+mobile;
                                    }

                                    if(contact!=null&&!contact.isEmpty()){
                                        loginDetails.setMeetingPersonDetails(client+""+contact);
                                    }else{
                                        loginDetails.setMeetingPersonDetails(client);
                                    }

                                    try {

                                        md = new MeetingDetailsNotificationManagers();
                                        md.setTitle("Meeting Details from "+ PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getUserFullName());
                                        md.setMessage("Meeting with "+client+" for "+purpose);
                                        md.setLocation(address);
                                        md.setLongitude(""+longitude);
                                        md.setLatitude(""+latitude);
                                        md.setMeetingDate(""+sdt.format(new Date()));
                                        md.setStatus("In meeting");
                                        md.setEmployeeId(PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getUserId());
                                        md.setManagerId(PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getManagerId());
                                        md.setMeetingPerson(client);
                                        md.setMeetingsDetails(purpose);
                                        md.setMeetingComments(detail);

                                        if (mGetSign.isChecked()&&!mTakeImage.isChecked()){
                                            // Method to create Directory, if the Directory doesn't exists
                                            file = new File(DIRECTORY);
                                            if (!file.exists()) {
                                                file.mkdir();
                                            }

                                            // Dialog Function
                                            dialogs = new Dialog( EmployeeNewMainScreen.this);
                                            // Removing the features of Normal Dialogs
                                            dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            dialogs.setContentView(R.layout.dialog_signature);
                                            dialogs.setCancelable(true);

                                            dialog_action(loginDetails,md,"null",dialog);

                                        }else if (mGetSign.isChecked()&&mTakeImage.isChecked()){

                                            file = new File(DIRECTORY);
                                            if (!file.exists()) {
                                                file.mkdir();
                                            }

                                            // Dialog Function
                                            dialogs = new Dialog( EmployeeNewMainScreen.this);
                                            // Removing the features of Normal Dialogs
                                            dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            dialogs.setContentView(R.layout.dialog_signature);
                                            dialogs.setCancelable(true);

                                            dialog_action(loginDetails,md,"Selfie",dialog);

                                        }else{
                                            addMeeting(loginDetails,md);
                                        }

                                        dialog.dismiss();


                                        //   addMeeting(loginDetails,dialog,md);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        dialog.dismiss();
                                    }

                                }else if(latitude!=0&&longitude!=0){


                               /*     latitude = currentLocation.getLatitude();
                                    longitude = currentLocation.getLongitude();*/

                                    LatLng masters = new LatLng(latitude, longitude);
                                    String addresss = null;
                                    try {
                                        addresss = getAddress(masters);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                   /* latLong.setText(addresss);
                                    centreMapOnLocationWithLatLng(masters, "" + PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getUserFullName());
*/
                                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                    SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");

                                    LatLng master = new LatLng(latitude,longitude);
                                    String address = null;
                                    try {
                                        address = getAddress(master);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    loginDetails = new Meetings ();
                                    loginDetails.setEmployeeId(PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getUserId());
                                    loginDetails.setStartLatitude(""+latitude);
                                    loginDetails.setStartLongitude(""+longitude);
                                    loginDetails.setStartLocation(""+address);
                                    loginDetails.setStartTime(""+sdt.format(new Date()));
                                   /* loginDetails.setEndLatitude(""+latitude);
                                    loginDetails.setEndLongitude(""+longitude);
                                    loginDetails.setEndLocation(""+address);
                                    loginDetails.setEndTime(""+sdt.format(new Date()));*/
                                    loginDetails.setMeetingDate(""+sdf.format(new Date()));
                                    loginDetails.setMeetingAgenda(purpose);
                                    loginDetails.setMeetingDetails(detail);
                                    loginDetails.setStatus("In Meeting");
                                    methodAdd = false;

                                    String contact = "";

                                    if(email!=null&&!email.isEmpty()){
                                        contact = contact+"%"+email;
                                    }

                                    if(mobile!=null&&!mobile.isEmpty()){
                                        contact = contact+"%"+mobile;
                                    }

                                    if(contact!=null&&!contact.isEmpty()){
                                        loginDetails.setMeetingPersonDetails(client+""+contact);
                                    }else{
                                        loginDetails.setMeetingPersonDetails(client);
                                    }

                                    try {

                                        md = new MeetingDetailsNotificationManagers();
                                        md.setTitle("Meeting Details from "+ PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getUserFullName());
                                        md.setMessage("Meeting with "+client+" for "+purpose);
                                        md.setLocation(address);
                                        md.setLongitude(""+longitude);
                                        md.setLatitude(""+latitude);
                                        md.setMeetingDate(""+sdt.format(new Date()));
                                        md.setStatus("In meeting");
                                        md.setEmployeeId(PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getUserId());
                                        md.setManagerId(PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getManagerId());
                                        md.setMeetingPerson(client);
                                        md.setMeetingsDetails(purpose);
                                        md.setMeetingComments(detail);

                                        if (mGetSign.isChecked()&&!mTakeImage.isChecked()){
                                            // Method to create Directory, if the Directory doesn't exists
                                            file = new File(DIRECTORY);
                                            if (!file.exists()) {
                                                file.mkdir();
                                            }

                                            // Dialog Function
                                            dialogs = new Dialog( EmployeeNewMainScreen.this);
                                            // Removing the features of Normal Dialogs
                                            dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            dialogs.setContentView(R.layout.dialog_signature);
                                            dialogs.setCancelable(true);

                                            dialog_action(loginDetails,md,"null",dialog);

                                        }else if (mGetSign.isChecked()&&mTakeImage.isChecked()){

                                            file = new File(DIRECTORY);
                                            if (!file.exists()) {
                                                file.mkdir();
                                            }

                                            // Dialog Function
                                            dialogs = new Dialog( EmployeeNewMainScreen.this);
                                            // Removing the features of Normal Dialogs
                                            dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            dialogs.setContentView(R.layout.dialog_signature);
                                            dialogs.setCancelable(true);

                                            dialog_action(loginDetails,md,"Selfie",dialog);

                                        }else{
                                            addMeeting(loginDetails,md);
                                        }

                                        dialog.dismiss();


                                        //   addMeeting(loginDetails,dialog,md);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        dialog.dismiss();
                                    }
                                }
                            }


                        }
                    }
                });

            }else{

            }




        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void addMeeting( final Meetings loginDetails, final MeetingDetailsNotificationManagers md) {



        final ProgressDialog dialog = new ProgressDialog( EmployeeNewMainScreen.this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        MeetingsAPI apiService = Util.getClient().create( MeetingsAPI.class);

        Call< Meetings > call = apiService.addMeeting(loginDetails);

        call.enqueue(new Callback< Meetings >() {
            @Override
            public void onResponse( Call< Meetings > call, Response< Meetings > response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {
                        Meetings s = response.body();
                        if(s!=null){

                            if(dialogs!=null){
                                dialogs.dismiss();
                            }
                            md.setMeetingsId(s.getMeetingsId());
                            Toast.makeText( EmployeeNewMainScreen.this, "You Checked in", Toast.LENGTH_SHORT).show();
                            PreferenceHandler.getInstance( EmployeeNewMainScreen.this).setMeetingId(s.getMeetingsId());
                            saveMeetingNotification(md);
                            PreferenceHandler.getInstance( EmployeeNewMainScreen.this).setMeetingLoginStatus("Login");
                        }

                    }else {
                        Toast.makeText( EmployeeNewMainScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex)
                {

                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure( Call< Meetings > call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                // Toast.makeText(EmployeeNewMainScreen.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });
    }

    public void getMeetings(final int id){

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                final MeetingsAPI subCategoryAPI = Util.getClient().create( MeetingsAPI.class);
                Call< Meetings > getProf = subCategoryAPI.getMeetingById(id);
                getProf.enqueue(new Callback< Meetings >() {
                    @Override
                    public void onResponse( Call< Meetings > call, Response< Meetings > response) {

                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {
                            System.out.println("Inside api");

                            final Meetings dto = response.body();

                            if(dto!=null){

                                try{
                                    if(locationCheck()){
                                        String message = "Login";
                                        message = "Do you want to Check-Out?";
                                        String option = "Meeting-Out";



                                        AlertDialog.Builder builder = new AlertDialog.Builder( EmployeeNewMainScreen.this);
                                        LayoutInflater inflater = (LayoutInflater) EmployeeNewMainScreen.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View views = inflater.inflate(R.layout.activity_meeting_add_with_sign_screen, null);

                                        builder.setView(views);

                                        final Button  mSave = views.findViewById(R.id.save);
                                        mSave.setText(option);
                                        final  EditText mDetails = views.findViewById(R.id.meeting_remarks);
                                        final  LinearLayout mSpinnerLay = views.findViewById(R.id.spinner_lay);
                                        final TextInputEditText mClientName = views.findViewById(R.id.client_name);
                                        final TextInputEditText mClientMobile = views.findViewById(R.id.client_contact_number);
                                        final TextInputEditText  mClientMail = views.findViewById(R.id.client_contact_email);
                                        final TextInputEditText mPurpose = views.findViewById(R.id.purpose_meeting);
                                        final CheckBox mGetSign = views.findViewById(R.id.get_sign_check);
                                        final CheckBox mTakeImage = views.findViewById(R.id.get_image_check);
                                        final ImageView mImageView = views.findViewById(R.id.selfie_pic);
                                        customerSpinner = views.findViewById(R.id.customer_spinner_adpter);
                                        ClientNameLayout =  views.findViewById(R.id.client_name_layout);
                                        mSpinnerLay.setVisibility(View.GONE);
                                        ClientNameLayout.setVisibility(View.VISIBLE);
                                        mDetails.setText(""+dto.getMeetingDetails());
                                        methodAdd = true;
                                        if(dto.getMeetingPersonDetails().contains("%")){
                                            String person[] = dto.getMeetingPersonDetails().split("%");
                                            if(person.length==1){
                                                mClientName.setText(""+dto.getMeetingPersonDetails());
                                            }else if(person.length==2){
                                                mClientName.setText(""+person[0]);
                                                mClientMail.setText(""+person[1]);
                                            }else if(person.length==3){
                                                mClientName.setText(""+person[0]);
                                                mClientMail.setText(""+person[1]);
                                                mClientMobile.setText(""+person[2]);
                                            }

                                        }else{
                                            mClientName.setText(""+dto.getMeetingPersonDetails());
                                        }

                                        mPurpose.setText(""+dto.getMeetingAgenda());

                                        if(dto.getEndPlaceID()!=null&&!dto.getEndPlaceID().isEmpty()){
                                            Picasso.with( EmployeeNewMainScreen.this).load(dto.getEndPlaceID()).placeholder(R.drawable.profile_image).error(R.drawable.no_image).into(mImageView);
                                        }

                                        final AlertDialog dialog = builder.create();
                                        dialog.show();
                                        dialog.setCanceledOnTouchOutside(true);

                                        /*customerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                                if(customerArrayList!=null && customerArrayList.size()!=0){


                                                    if(customerArrayList.get(position).getCustomerName()!=null && customerArrayList.get(position).getCustomerName().equalsIgnoreCase("Others"))
                                                    {
                                                        mClientMobile.setText("");
                                                        mClientName.setText("");
                                                        mClientMail.setText("");
                                                        ClientNameLayout.setVisibility(View.VISIBLE);

                                                    }
                                                    else {
                                                        mClientMobile.setText(""+customerArrayList.get(position).getCustomerMobile());
                                                        mClientName.setText(""+customerArrayList.get(position).getCustomerName());
                                                        mClientMail.setText(""+customerArrayList.get(position).getCustomerEmail());
                                                        clientId = customerArrayList.get(position).getCustomerId();
                                                        ClientNameLayout.setVisibility(View.GONE);

                                                    }
                                                }
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {

                                            }
                                        });
*/
                                        /*if(dto.getCustomerId()!=0){

                                            getCustomersWithId(PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getCompanyId(),dto.getCustomerId());
                                        }else{
                                            getCustomersWithId(PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getCompanyId(),0);
                                        }*/

                                        mSave.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                String client = mClientName.getText().toString();
                                                String purpose = mPurpose.getText().toString();
                                                String detail = mDetails.getText().toString();
                                                String mobile = mClientMobile.getText().toString();
                                                String email = mClientMail.getText().toString();
                                                // String customer = customerSpinner.getSelectedItem().toString();

                                                if(client==null||client.isEmpty()){

                                                    Toast.makeText( EmployeeNewMainScreen.this, "Please mention client name", Toast.LENGTH_SHORT).show();

                                                }else if(purpose==null||purpose.isEmpty()){

                                                    Toast.makeText( EmployeeNewMainScreen.this, "Please mention purpose of meeting", Toast.LENGTH_SHORT).show();

                                                }else if(detail==null||detail.isEmpty()){

                                                    Toast.makeText( EmployeeNewMainScreen.this, "Please mention remarks about meeting", Toast.LENGTH_SHORT).show();

                                                }else{

                                                    //gps = new TrackGPS(EmployeeNewMainScreen.this);

                                                    if(locationCheck()){

                                                        if(currentLocation!=null) {

                                                            if(gps.isMockLocationOn(currentLocation, EmployeeNewMainScreen.this)){


                                                            }

                                                            latitude = currentLocation.getLatitude();
                                                            longitude = currentLocation.getLongitude();

                                                            LatLng masters = new LatLng(latitude, longitude);
                                                            String addresss = null;
                                                            try {
                                                                addresss = getAddress(masters);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }



                                                            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                                            SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");

                                                            LatLng master = new LatLng(latitude,longitude);
                                                            String address = null;
                                                            try {
                                                                address = getAddress(master);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                            loginDetails = dto;
                                                            loginDetails.setEmployeeId(PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getUserId());

                                                            loginDetails.setEndLatitude(""+latitude);
                                                            loginDetails.setEndLongitude(""+longitude);
                                                            loginDetails.setEndLocation(""+address);
                                                            loginDetails.setEndTime(""+sdt.format(new Date()));
                                                            loginDetails.setMeetingDate(""+sdf.format(new Date()));
                                                            loginDetails.setMeetingAgenda(purpose);
                                                            loginDetails.setMeetingDetails(detail);
                                                            loginDetails.setStatus("Completed");

                                                            /*if(customer!=null&&!customer.equalsIgnoreCase("Others")){

                                                                if(customerArrayList!=null&&customerArrayList.size()!=0)
                                                                    loginDetails.setCustomerId(clientId);

                                                            }*/

                                                            String contact = "";

                                                            if(email!=null&&!email.isEmpty()){
                                                                contact = contact+"%"+email;
                                                            }

                                                            if(mobile!=null&&!mobile.isEmpty()){
                                                                contact = contact+"%"+mobile;
                                                            }

                                                            if(contact!=null&&!contact.isEmpty()){
                                                                loginDetails.setMeetingPersonDetails(client+""+contact);
                                                            }else{
                                                                loginDetails.setMeetingPersonDetails(client);
                                                            }

                                                            try {

                                                                md = new MeetingDetailsNotificationManagers();
                                                                md.setTitle("Meeting Details from "+ PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getUserFullName());
                                                                md.setMessage("Meeting with "+client+" for "+purpose);
                                                                md.setLocation(address);
                                                                md.setLongitude(""+longitude);
                                                                md.setLatitude(""+latitude);
                                                                md.setMeetingDate(""+sdt.format(new Date()));
                                                                md.setStatus("Completed");
                                                                md.setEmployeeId(PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getUserId());
                                                                md.setManagerId(PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getManagerId());
                                                                md.setMeetingPerson(client);
                                                                md.setMeetingsId(loginDetails.getMeetingsId());
                                                                md.setMeetingsDetails(purpose);
                                                                md.setMeetingComments(detail);

                                                                if (mGetSign.isChecked()&&!mTakeImage.isChecked()){
                                                                    // Method to create Directory, if the Directory doesn't exists
                                                                    file = new File(DIRECTORY);
                                                                    if (!file.exists()) {
                                                                        file.mkdir();
                                                                    }

                                                                    // Dialog Function
                                                                    dialogs = new Dialog( EmployeeNewMainScreen.this);
                                                                    // Removing the features of Normal Dialogs
                                                                    dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                    dialogs.setContentView(R.layout.dialog_signature);
                                                                    dialogs.setCancelable(true);

                                                                    dialog_action(loginDetails,md,"null",dialog);

                                                                }else if (!mGetSign.isChecked()&&mTakeImage.isChecked()){

                                                                    file = new File(DIRECTORY);
                                                                    if (!file.exists()) {
                                                                        file.mkdir();
                                                                    }

                                                                       /* // Dialog Function
                                                                        dialog = new Dialog(MeetingAddWithSignScreen.this);
                                                                        // Removing the features of Normal Dialogs
                                                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                        dialog.setContentView(R.layout.dialog_signature);
                                                                        dialog.setCancelable(true);*/

                                                                    dispatchTakePictureIntent();
                                                                    dialog.dismiss();

                                                                    //dialog_action(loginDetails,md,"Selfie");

                                                                }else if (mGetSign.isChecked()&&mTakeImage.isChecked()){

                                                                    file = new File(DIRECTORY);
                                                                    if (!file.exists()) {
                                                                        file.mkdir();
                                                                    }

                                                                    // Dialog Function
                                                                    dialogs = new Dialog( EmployeeNewMainScreen.this);
                                                                    // Removing the features of Normal Dialogs
                                                                    dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                    dialogs.setContentView(R.layout.dialog_signature);
                                                                    dialogs.setCancelable(true);

                                                                    dialog_action(loginDetails,md,"Selfie",dialog);

                                                                }else{
                                                                    updateMeeting(loginDetails,md);
                                                                }

                                                                dialog.dismiss();


                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }else if(latitude!=0&&longitude!=0){



                                                          /*  latitude = currentLocation.getLatitude();
                                                            longitude = currentLocation.getLongitude();*/

                                                            LatLng masters = new LatLng(latitude, longitude);
                                                            String addresss = null;
                                                            try {
                                                                addresss = getAddress(masters);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                           /* latLong.setText(addresss);
                                                            centreMapOnLocationWithLatLng(masters, "" + PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getUserFullName());
*/

                                                            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                                            SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");

                                                            LatLng master = new LatLng(latitude,longitude);
                                                            String address = null;
                                                            try {
                                                                address = getAddress(master);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                            loginDetails = dto;
                                                            loginDetails.setEmployeeId(PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getUserId());
                                                          /*  loginDetails.setStartLatitude(""+latitude);
                                                            loginDetails.setStartLongitude(""+longitude);
                                                            loginDetails.setStartLocation(""+address);
                                                            loginDetails.setStartTime(""+sdt.format(new Date()));*/
                                                            loginDetails.setEndLatitude(""+latitude);
                                                            loginDetails.setEndLongitude(""+longitude);
                                                            loginDetails.setEndLocation(""+address);
                                                            loginDetails.setEndTime(""+sdt.format(new Date()));
                                                            loginDetails.setMeetingDate(""+sdf.format(new Date()));
                                                            loginDetails.setMeetingAgenda(purpose);
                                                            loginDetails.setMeetingDetails(detail);
                                                            loginDetails.setStatus("Completed");

                                                            String contact = "";

                                                            if(email!=null&&!email.isEmpty()){
                                                                contact = contact+"%"+email;
                                                            }

                                                            if(mobile!=null&&!mobile.isEmpty()){
                                                                contact = contact+"%"+mobile;
                                                            }

                                                            if(contact!=null&&!contact.isEmpty()){
                                                                loginDetails.setMeetingPersonDetails(client+""+contact);
                                                            }else{
                                                                loginDetails.setMeetingPersonDetails(client);
                                                            }

                                                            try {

                                                                md = new MeetingDetailsNotificationManagers();
                                                                md.setTitle("Meeting Details from "+ PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getUserFullName());
                                                                md.setMessage("Meeting with "+client+" for "+purpose);
                                                                md.setLocation(address);
                                                                md.setLongitude(""+longitude);
                                                                md.setLatitude(""+latitude);
                                                                md.setMeetingDate(""+sdt.format(new Date()));
                                                                md.setStatus("Completed");
                                                                md.setEmployeeId(PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getUserId());
                                                                md.setManagerId(PreferenceHandler.getInstance( EmployeeNewMainScreen.this).getManagerId());
                                                                md.setMeetingPerson(client);
                                                                md.setMeetingsId(loginDetails.getMeetingsId());
                                                                md.setMeetingsDetails(purpose);
                                                                md.setMeetingComments(detail);

                                                                if (mGetSign.isChecked()&&!mTakeImage.isChecked()){
                                                                    // Method to create Directory, if the Directory doesn't exists
                                                                    file = new File(DIRECTORY);
                                                                    if (!file.exists()) {
                                                                        file.mkdir();
                                                                    }

                                                                    // Dialog Function
                                                                    dialogs = new Dialog( EmployeeNewMainScreen.this);
                                                                    // Removing the features of Normal Dialogs
                                                                    dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                    dialogs.setContentView(R.layout.dialog_signature);
                                                                    dialogs.setCancelable(true);

                                                                    dialog_action(loginDetails,md,"null",dialog);

                                                                }else if (mGetSign.isChecked()&&mTakeImage.isChecked()){

                                                                    file = new File(DIRECTORY);
                                                                    if (!file.exists()) {
                                                                        file.mkdir();
                                                                    }

                                                                    // Dialog Function
                                                                    dialogs = new Dialog( EmployeeNewMainScreen.this);
                                                                    // Removing the features of Normal Dialogs
                                                                    dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                    dialogs.setContentView(R.layout.dialog_signature);
                                                                    dialogs.setCancelable(true);

                                                                    dialog_action(loginDetails,md,"Selfie",dialog);

                                                                }else if (!mGetSign.isChecked()&&mTakeImage.isChecked()){

                                                                    file = new File(DIRECTORY);
                                                                    if (!file.exists()) {
                                                                        file.mkdir();
                                                                    }

                                                                       /* // Dialog Function
                                                                        dialog = new Dialog(MeetingAddWithSignScreen.this);
                                                                        // Removing the features of Normal Dialogs
                                                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                        dialog.setContentView(R.layout.dialog_signature);
                                                                        dialog.setCancelable(true);*/

                                                                    dispatchTakePictureIntent();
                                                                    dialog.dismiss();
                                                                    //dialog_action(loginDetails,md,"Selfie");
                                                                }else{
                                                                    updateMeeting(loginDetails,md);
                                                                }
                                                                dialog.dismiss();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                   /* if(gps!=null&&gps.canGetLocation())
                                                    {
                                                        System.out.println("Long and lat Rev"+gps.getLatitude()+" = "+gps.getLongitude());
                                                        latitude = gps.getLatitude();
                                                        longitude = gps.getLongitude();
                                                    }
                                                    else
                                                    {

                                                    }*/
                                                }
                                            }
                                        });

                                    }else{

                                    }

                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }

                        }else{

                            //meet
                        }
                    }

                    @Override
                    public void onFailure( Call< Meetings > call, Throwable t) {

                    }
                });
            }
        });
    }

    public void updateMeeting( final Meetings loginDetails, final MeetingDetailsNotificationManagers md) {

        final ProgressDialog dialog = new ProgressDialog( EmployeeNewMainScreen.this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();
        MeetingsAPI apiService = Util.getClient().create( MeetingsAPI.class);
        Call< Meetings > call = apiService.updateMeetingById(loginDetails.getMeetingsId(),loginDetails);
        call.enqueue(new Callback< Meetings >() {
            @Override
            public void onResponse( Call< Meetings > call, Response< Meetings > response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||response.code()==204) {

                        if(dialogs!=null){
                            dialogs.dismiss();
                        }
                        saveMeetingNotification(md);
                        Toast.makeText( EmployeeNewMainScreen.this, "You Checked out", Toast.LENGTH_SHORT).show();
                        PreferenceHandler.getInstance( EmployeeNewMainScreen.this).setMeetingId(0);
                        PreferenceHandler.getInstance( EmployeeNewMainScreen.this).setMeetingLoginStatus("Logout");

                    }else {
                        Toast.makeText( EmployeeNewMainScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex)
                {

                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure( Call< Meetings > call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }

                // Toast.makeText(EmployeeNewMainScreen.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });
    }

    public void saveMeetingNotification(final MeetingDetailsNotificationManagers md) {
        final ProgressDialog dialog = new ProgressDialog( EmployeeNewMainScreen.this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();
        MeetingNotificationAPI apiService = Util.getClient().create( MeetingNotificationAPI.class);
        Call<MeetingDetailsNotificationManagers> call = apiService.saveMeetingNotification(md);
        call.enqueue(new Callback<MeetingDetailsNotificationManagers>() {
            @Override
            public void onResponse(Call<MeetingDetailsNotificationManagers> call, Response<MeetingDetailsNotificationManagers> response) { try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {
                        MeetingDetailsNotificationManagers s = response.body();
                        if(s!=null){
                            MeetingDetailsNotificationManagers md = new MeetingDetailsNotificationManagers();
                            md.setTitle(s.getTitle());
                            md.setMessage(s.getMessage());
                            md.setLocation(s.getLocation());
                            md.setLongitude(""+s.getLongitude());
                            md.setLatitude(""+s.getLatitude());
                            md.setMeetingDate(""+s.getMeetingDate());
                            md.setStatus(s.getStatus());
                            md.setEmployeeId(s.getManagerId());
                            md.setManagerId(s.getEmployeeId());
                            md.setMeetingPerson(s.getMeetingPerson());
                            md.setMeetingsDetails(s.getMeetingsDetails());
                            md.setMeetingComments(s.getMeetingComments());
                            md.setMeetingsId(s.getMeetingsId());
                            md.setSenderId( Constants.SENDER_ID);
                            md.setServerId( Constants.SERVER_ID);
                            sendMeetingNotification(md);
                        }

                    }else {
                        Toast.makeText( EmployeeNewMainScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex)
                {

                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<MeetingDetailsNotificationManagers> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                // Toast.makeText(EmployeeNewMainScreen.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });
    }

    public void sendMeetingNotification(final MeetingDetailsNotificationManagers md) {
        final ProgressDialog dialog = new ProgressDialog( EmployeeNewMainScreen.this);
        dialog.setMessage("Sending Details..");
        dialog.setCancelable(false);
        dialog.show();

        MeetingNotificationAPI apiService = Util.getClient().create( MeetingNotificationAPI.class);
        Call<ArrayList<String>> call = apiService.sendMeetingNotification(md);
        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {
                        recreate();
                    }else {
                        Toast.makeText( EmployeeNewMainScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex)
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                //  Toast.makeText(EmployeeNewMainScreen.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });
    }

    public boolean locationCheck(){

        final boolean status = false;
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder( EmployeeNewMainScreen.this);
            dialog.setMessage("Location is not enable");
            dialog.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                }
            });
            dialog.show();
            return false;
        }else{
            return true;
        }
    }

    private String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder( EmployeeNewMainScreen.this, Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
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

    public void getCustomers(final int id) {

        final EmployeeApi orgApi = Util.getClient().create( EmployeeApi.class);
        Call<ArrayList< Employee >> getProf = orgApi.getEmployeesByOrgId (id);
        getProf.enqueue(new Callback<ArrayList< Employee >>() {
            @Override
            public void onResponse( Call<ArrayList< Employee >> call, Response<ArrayList< Employee >> response) {

                if (response.code() == 200||response.code() == 201||response.code() == 204)
                {
                    ArrayList<Employee> employeeArrayList = response.body();
                    customerArrayList = new ArrayList <> (  );
                    if(employeeArrayList!=null&&employeeArrayList.size()!=0){
                        Employee customer = new Employee ();
                        customer.setEmployeeName ("Others");
                        customerArrayList.add(customer);

                        for ( Employee e: employeeArrayList) {

                            if(e.getUserRoleId ()==10&&e.getManagerId ()==PreferenceHandler.getInstance ( EmployeeNewMainScreen.this ).getUserId ()){
                               customerArrayList.add ( e );
                            }

                        }
                        CustomerSpinnerAdapter adapter = new CustomerSpinnerAdapter( EmployeeNewMainScreen.this,customerArrayList);
                        customerSpinner.setAdapter(adapter);
                    }
                    else {
                        ClientNameLayout.setVisibility(View.VISIBLE);
                        customerSpinner.setVisibility(View.GONE);
                    }

                }else{

                }
            }

            @Override
            public void onFailure( Call<ArrayList< Employee >> call, Throwable t) {

            }
        });
    }

    // Function for Digital Signature
    public void dialog_action( final Meetings loginDetails, final MeetingDetailsNotificationManagers md, final String type, final AlertDialog alertDialog) {

        mContent = dialogs.findViewById(R.id.linearLayout);
        mSignature = new signature( EmployeeNewMainScreen.this, null);
        mSignature.setBackgroundColor(Color.WHITE);

        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mClear = dialogs.findViewById(R.id.clear);
        mGetSigns = dialogs.findViewById(R.id.getsign);
        mGetSigns.setEnabled(false);
        mCancel = dialogs.findViewById(R.id.cancel);
        view = mContent;

        mClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
                mGetSigns.setEnabled(false);
            }
        });

        mGetSigns.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.v("log_tag", "Panel Saved");
                view.setDrawingCacheEnabled(true);
                mSignature.save(view, StoredPath, loginDetails, md, type, alertDialog);
                if (dialogs != null) {
                    dialogs.dismiss();
                }
                Toast.makeText( EmployeeNewMainScreen.this, "Successfully Saved", Toast.LENGTH_SHORT).show();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Canceled");
                if(dialogs!=null){

                    dialogs.dismiss();
                }
               recreate();
            }
        });
        dialogs.show();
    }

    public class signature extends View {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();
        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save( View v, String StoredPath, final Meetings loginDetails, final MeetingDetailsNotificationManagers md, final String type, final AlertDialog alertDialog) {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            try {
                // Output the file
                FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);
                v.draw(canvas);
                // Convert the output file to Image such as .png
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();
                File file = new File(StoredPath);
                if(file.length() <= 1*1024*1024)
                {
                    FileOutputStream out = null;
                    String[] filearray = StoredPath.split("/");
                    final String filename = getFilename(filearray[filearray.length-1]);
                    out = new FileOutputStream(filename);
                    Bitmap myBitmap = BitmapFactory.decodeFile(StoredPath);
                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    uploadImage(filename,loginDetails,md,type);
                }
                else
                {
                    compressImage(StoredPath,loginDetails,md,type);
                }

            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }
        }

        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSigns.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {

            Log.v("log_tag", string);

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }



    private void uploadImage( final String filePath, final Meetings loginDetails, final MeetingDetailsNotificationManagers md, final String type)
    {
        //String filePath = getRealPathFromURIPath(uri, ImageUploadActivity.this);

        final File file = new File(filePath);
        int size = 1*1024*1024;

        if(file != null)
        {
            if(file.length() > size)
            {
                System.out.println(file.length());
                compressImage(filePath,loginDetails,md,type);
            }
            else
            {
                final ProgressDialog dialog = new ProgressDialog( EmployeeNewMainScreen.this);
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
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 204) {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            Toast.makeText( EmployeeNewMainScreen.this,"Success "+response.message(),Toast.LENGTH_LONG).show();
                            try {

                                if (type != null && type.equalsIgnoreCase("Selfie")) {
                                    if ( Util.IMAGE_URL == null) {
                                        loginDetails.setEndPlaceID( Constants.IMAGE_URL + response.body());
                                    } else {
                                        loginDetails.setEndPlaceID( Util.IMAGE_URL + response.body());
                                    }

                                    dispatchTakePictureIntent();

                                } else if (type != null && type.equalsIgnoreCase("Done")) {

                                    if ( Util.IMAGE_URL == null) {
                                        loginDetails.setStartPlaceID( Constants.IMAGE_URL + response.body());
                                    } else {
                                        loginDetails.setStartPlaceID( Util.IMAGE_URL + response.body());
                                    }

                                    if (methodAdd) {
                                        updateMeeting(loginDetails, md);
                                    } else {
                                        addMeeting(loginDetails, md);
                                    }

                                } else {

                                    if ( Util.IMAGE_URL == null) {
                                        loginDetails.setEndPlaceID( Constants.IMAGE_URL + response.body());
                                    } else {
                                        loginDetails.setEndPlaceID( Util.IMAGE_URL + response.body());
                                    }
                                    if (methodAdd) {
                                        updateMeeting(loginDetails, md);
                                    } else {
                                        addMeeting(loginDetails, md);
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (filePath.contains("MyFolder/Images")) {
                                file.delete();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        // Log.d("UpdateCate", "Error " + "Bad Internet Connection");
                        //  Toast.makeText(EmployeeNewMainScreen.this,"Please Check your Internet permissions "+"Bad Internet Connection",Toast.LENGTH_LONG).show();
                    }

                });
            }
        }
    }


    public String compressImage( String filePath, final Meetings loginDetails, final MeetingDetailsNotificationManagers md, final String type) {

        //String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
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
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
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

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
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
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            uploadImage(filename,loginDetails,md,type);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filename;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void saveSelfie(Bitmap bitmap, String StoredPath) {
        if (bitmap == null) {
            Toast.makeText( EmployeeNewMainScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
        try {
            // Output the file
            FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);
            // Convert the output file to Image such as .png
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
            mFileOutStream.flush();
            mFileOutStream.close();
            File file = new File(StoredPath);
            if(file.length() <= 1*1024*1024)
            {
                FileOutputStream out = null;
                String[] filearray = StoredPath.split("/");
                final String filename = getFilename(filearray[filearray.length-1]);
                out = new FileOutputStream(filename);
                Bitmap myBitmap = BitmapFactory.decodeFile(StoredPath);
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                uploadImage(filename,loginDetails,md,"Done");
                mImageView.setVisibility(View.VISIBLE);
                mImageView.setImageBitmap(bitmap);
            }
            else
            {
                compressImage(StoredPath,loginDetails,md,"Done");
            }

        } catch (Exception e) {
            Log.v("log_tag", e.toString());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("salam", " Connected");

        if ( ActivityCompat.checkSelfPermission( EmployeeNewMainScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( EmployeeNewMainScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mLocationClient);



        ArrayList<String> appNames = new ArrayList<>();


        if (currentLocation != null) {


            //  latLong.setText("Latitude : " + currentLocation.getLatitude() + " , Longitude : " + currentLocation.getLongitude());

            if(Settings.Secure.getString( EmployeeNewMainScreen.this.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")){

                //Toast.makeText(mContext, "Mock Location Enabled" , Toast.LENGTH_SHORT).show();

                if(gps.isMockLocationOn(currentLocation, EmployeeNewMainScreen.this)){

                    appNames.addAll(gps.listofApps( EmployeeNewMainScreen.this));


                }



            }

            if(appNames!=null&&appNames.size()!=0){

               /* new CustomDesignAlertDialog(this, CustomDesignAlertDialog.ERROR_TYPE,"Fake")
                        .setTitleText("Fake Activity")
                        .setContentText(appNames.get(0)+" is sending fake location.")
                        .show();*/

            }else{
                latitude = currentLocation.getLatitude();
                longitude = currentLocation.getLongitude();

                LatLng master = new LatLng(latitude,longitude);
                String address = null;
                try {
                    address = getAddress(master);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startLocationUpdates();

            }

        }

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    protected void startLocationUpdates() {

        LocationRequest mLocationRequest = new LocationRequest ( );
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        /* 60 secs */
        long UPDATE_INTERVAL = 60000;
        mLocationRequest.setInterval( UPDATE_INTERVAL );
        /* 30 secs */
        long FASTEST_INTERVAL = 30000;
        mLocationRequest.setFastestInterval( FASTEST_INTERVAL );
        if ( ActivityCompat.checkSelfPermission( EmployeeNewMainScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( EmployeeNewMainScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText( EmployeeNewMainScreen.this, "Enable Permissions", Toast.LENGTH_LONG).show();
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mLocationClient, mLocationRequest , this);
    }

    @Override
    public void onLocationChanged(Location location) {

        if(location!=null){

            latitude = location.getLatitude();
            longitude = location.getLongitude();

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}
