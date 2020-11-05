package app.zingo.mysolite.ui.Admin;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.widget.AppCompatButton;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import app.zingo.mysolite.Custom.MapViewScroll;
import app.zingo.mysolite.WebApi.EmployeeApi;
import app.zingo.mysolite.WebApi.LeaveAPI;
import app.zingo.mysolite.WebApi.LoginDetailsAPI;
import app.zingo.mysolite.adapter.CustomerSpinnerAdapter;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.model.Leaves;
import app.zingo.mysolite.model.LoginDetails;
import app.zingo.mysolite.model.TaskNotificationManagers;
import app.zingo.mysolite.model.Tasks;
import app.zingo.mysolite.ui.NewAdminDesigns.DailyOrdersForEmployeeActivity;
import app.zingo.mysolite.utils.Constants;
import app.zingo.mysolite.utils.NetworkUtil;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.TrackGPS;
import app.zingo.mysolite.utils.Util;
import app.zingo.mysolite.WebApi.TaskNotificationAPI;
import app.zingo.mysolite.WebApi.TasksAPI;
import app.zingo.mysolite.R;
import app.zingo.mysolite.utils.ValidationClass;
import app.zingo.mysolite.utils.ValidationConst;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateOrderScreen extends ValidationClass {
    TextInputEditText mTaskName, mFrom, paymentDate, mFromTime, paymenTime,mdesc,mOrderAmount;//mDead
    TextInputEditText mClientName,mClientMobile,mClientMail;
    Spinner customerSpinner;
    LinearLayout mSpinnerLay;
    TextInputLayout ClientNameLayout;
    AppCompatButton mCreate;
    RelativeLayout mMapLay;
    Switch mShow;

    private EditText  lat, lng;
    private TextView location;

    private GoogleMap mMap;
    MapViewScroll mapView;
    Marker marker;

    int employeeId, deptId;
    double lati, lngi;
    String type;
    String mStatus;
    DecimalFormat df2 = new DecimalFormat(".##########");
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public String TAG = "MAPLOCATION",placeId;
    private ArrayList< Leaves> totalLeaves;
    private ArrayList< Leaves> approvedLeaves;
    private ArrayList< Leaves> pendingLeaves;
    private ArrayList< Leaves> rejectedLeaves;
    private ArrayList< Leaves> unpaidLeaves;
    private ArrayList< Leaves> paidLeaves;
    ArrayList< Employee > customerArrayList = new ArrayList <> (  );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_create_order_screen);
            Objects.requireNonNull ( getSupportActionBar ( ) ).setHomeButtonEnabled(true);
            Objects.requireNonNull ( getSupportActionBar ( ) ).setDisplayHomeAsUpEnabled(true);
            setTitle("Create Order");
            mTaskName = findViewById(R.id.task_name);
            mFrom = findViewById(R.id.from_date);
            paymentDate = findViewById(R.id.payment_date);
            mFromTime = findViewById(R.id.from_time);
            paymenTime = findViewById(R.id.payment_time);
            // mDead = (TextInputEditText) findViewById(R.id.dead_line);
            mdesc = findViewById(R.id.task_desc);
            mOrderAmount = findViewById(R.id.amount_order);
            mCreate = findViewById(R.id.create_order);
            mapView = findViewById(R.id.task_location_map);
            mShow = findViewById(R.id.show_map);
            mMapLay = findViewById(R.id.map_layout);
            location = findViewById(R.id.location_et);
            ClientNameLayout = findViewById(R.id.client_name_layout);
            mClientName = findViewById(R.id.client_name);
            mClientMobile = findViewById(R.id.client_contact_number);
            mClientMail = findViewById(R.id.client_contact_email);
            customerSpinner = findViewById(R.id.customer_spinner_adpter);
            mSpinnerLay = findViewById(R.id.spinner_lay);
            lat = findViewById(R.id.lat_et);
            lng = findViewById(R.id.lng_et);

            Bundle bundle = getIntent().getExtras();

            Employee customer = new Employee ();
            customer.setEmployeeName ("Others");
            customerArrayList.add(customer);

            getCustomers(PreferenceHandler.getInstance( CreateOrderScreen.this).getCompanyId());

            if (bundle != null) {
                employeeId = bundle.getInt("EmployeeId");
                deptId = bundle.getInt("DepartmentId");
                type = bundle.getString("Type");
            }

            try{
                Places.initialize(getApplicationContext(), Constants.locationApiKey);
            }catch ( Exception e ){
                e.printStackTrace ();
            }

            customerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(customerArrayList!=null && customerArrayList.size()!=0){
                        if(customerArrayList.get(position).getEmployeeName ()!=null && customerArrayList.get(position).getEmployeeName ().equalsIgnoreCase("Others")) {
                            mClientMobile.setText("");
                            mClientName.setText("");
                            mClientMail.setText("");
                           // ClientNameLayout.setVisibility(View.GONE); 23/09/2020 changes
                            ClientNameLayout.setVisibility(View.VISIBLE);

                        }
                        else {
                            mClientMobile.setText(""+customerArrayList.get(position).getPhoneNumber ());
                            mClientName.setText(""+customerArrayList.get(position).getEmployeeName ());
                            mClientMail.setText(""+customerArrayList.get(position).getPrimaryEmailAddress ());
                           // clientId = customerArrayList.get(position).getCustomerId();
                            // ClientNameLayout.setVisibility(View.GONE); 23/09/2020 changes
                            ClientNameLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            mFrom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    openDatePicker(mFrom);
                }
            });

            paymentDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    openPaymentDatePicker(paymentDate);
                }
            });

            mFromTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //openTimePicker();
                    // TODO Auto-generated method stub
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog( CreateOrderScreen.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

                            try{
                                Date fromTime = sdf.parse(selectedHour + ":" + selectedMinute);
                                mFromTime.setText( sdf.format(fromTime));
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                }
            });

            paymenTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //openTimePicker();

                    // TODO Auto-generated method stub
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog( CreateOrderScreen.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

                            try{
                                Date totime = sdf.parse(selectedHour + ":" + selectedMinute);
                                paymenTime.setText( sdf.format(totime));
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                }
            });

           /* mDead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    openDatePicker(mDead);
                }
            });
*/


            Calendar cal = Calendar.getInstance();
            int monthCal = cal.get(Calendar.MONTH);
            int yearCal = cal.get(Calendar.YEAR);
            int monthValue = monthCal+1;
            int yearValue = yearCal;
            if ( NetworkUtil.checkInternetConnection ( CreateOrderScreen.this ) ) {
                getLeaveDetails(employeeId, monthValue,yearValue);
            } else {
                noInternetConnection ();
            }


            mCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    validate ();
                  /*  LoginDetails loginDetails = new LoginDetails();
                    loginDetails.setEmployeeId(employeeId);
                    loginDetails.setLoginDate(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                    getLoginDetails(loginDetails);*/
                }
            });

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
                MapsInitializer.initialize( CreateOrderScreen.this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                       /* Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY*//*MODE_FULLSCREEN*//*)
                                  .build( CreateOrderScreen.this);
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);*/
                        List< com.google.android.libraries.places.api.model.Place.Field> fields = Arrays.asList( com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.LAT_LNG, com.google.android.libraries.places.api.model.Place.Field.NAME, com.google.android.libraries.places.api.model.Place.Field.ADDRESS);
                        Intent intent = new Autocomplete.IntentBuilder( AutocompleteActivityMode.FULLSCREEN, fields).build(getApplicationContext());
                        startActivityForResult(intent,PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        // TODO: Handle the error.
                    }
                }
            });

            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;


                    if ( ActivityCompat.checkSelfPermission( CreateOrderScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( CreateOrderScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

                    TrackGPS trackGPS = new TrackGPS ( CreateOrderScreen.this);

                    if(trackGPS.canGetLocation()) {
                        lati = trackGPS.getLatitude();
                        lngi = trackGPS.getLongitude();
                    }

                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            DecimalFormat df2 = new DecimalFormat(".##########");

                            lati = latLng.latitude;
                            lngi = latLng.longitude;

                            lat.setText(df2.format(latLng.latitude)+"");
                            lng.setText(df2.format(latLng.longitude)+"");
                            String add = getAddress(latLng);
                            location.setText(add);
                            mMap.clear();
                            marker = mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                    .position(latLng));
                            CameraPosition cameraPosition1 = new CameraPosition.Builder().target(latLng).zoom(80).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
                        }
                    });
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void openDatePicker(final TextInputEditText tv) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear  = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay   = c.get(Calendar.DAY_OF_MONTH);
        final Calendar newDate = Calendar.getInstance();
        //launch datepicker modal
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                        try {
                            newDate.set(year,monthOfYear,dayOfMonth);
                            String date = ((monthOfYear+1)+"/"+dayOfMonth+"/"+year);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");
                            try {
                                Date parse_date = simpleDateFormat.parse(date);
                                String date_format = sdf.format(parse_date);
                                if(tv.equals(mFrom)) {
                                    tv.setText(date_format);
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();

    }

    public void openPaymentDatePicker(final TextInputEditText tv) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear  = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay   = c.get(Calendar.DAY_OF_MONTH);
        final Calendar newDate = Calendar.getInstance();
        //launch datepicker modal
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                        try {
                            newDate.set(year,monthOfYear,dayOfMonth);
                            String date = ((monthOfYear+1)+"/"+dayOfMonth+"/"+year);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");
                            try {
                                Date parse_date = simpleDateFormat.parse(date);
                                String date_format = sdf.format(parse_date);
                                 if(tv.equals(paymentDate)) {
                                    tv.setText(date_format);
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();

    }

    public void validate(){
        String from = mFrom.getText().toString();
        String mPaymentDate = paymentDate.getText().toString();
        String fromTime = mFromTime.getText().toString();
        String mPaymenTime = paymenTime.getText().toString();
        //   String dead = mDead.getText().toString();
        String taskName = mTaskName.getText().toString();
        String desc = mdesc.getText().toString();
        String orderAmount = mOrderAmount.getText().toString();

        String client = mClientName.getText().toString();
        String mobile = mClientMobile.getText().toString();
        String email = mClientMail.getText().toString();
        if(taskName.isEmpty()){
            Toast.makeText(this, "Order Name is required", Toast.LENGTH_SHORT).show();
        }else if(from.isEmpty()){
            Toast.makeText(this, "Order date is required", Toast.LENGTH_SHORT).show();
        }else if(mPaymentDate.isEmpty()){
            Toast.makeText(this, "Payment date is required", Toast.LENGTH_SHORT).show();
        }else if(mPaymentDate.equals ( from )){
            Toast.makeText(this, "Payment date should be greater than Order date", Toast.LENGTH_SHORT).show();
        }else if(fromTime.isEmpty()){
            Toast.makeText(this, "Please Select Order time", Toast.LENGTH_SHORT).show();
        }else if(mPaymenTime.isEmpty()){
            Toast.makeText(this, "Please Select Payment Time", Toast.LENGTH_SHORT).show();
        }else if(desc.isEmpty()){
            Toast.makeText(this, "Comment is required", Toast.LENGTH_SHORT).show();
        }else if(orderAmount.isEmpty()){
            Toast.makeText(this, "Order Amount is required", Toast.LENGTH_SHORT).show();
        }else if(client.isEmpty()){
            Toast.makeText( CreateOrderScreen.this, "Please mention client name", Toast.LENGTH_SHORT).show();

        }else{
            try{
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
                Tasks tasks = new Tasks();
                tasks.setTaskName(taskName);
                tasks.setTaskDescription(desc);
                tasks.setDeadLine(mPaymentDate);
                tasks.setStartDate(new SimpleDateFormat("MM/dd/yyyy HH:mm").format(sdf.parse(from+" "+fromTime)));
                tasks.setReminderDate(new SimpleDateFormat("MM/dd/yyyy HH:mm").format(sdf.parse(from+" "+fromTime)));
                tasks.setEndDate(new SimpleDateFormat("MM/dd/yyyy HH:mm").format(sdf.parse(mPaymentDate+" "+mPaymenTime)));
                tasks.setStatus("Pending");
                tasks.setPriority(orderAmount);
                tasks.setCategory("Order");
                tasks.setComments("");

                String contact = "";

                if(email!=null&&!email.isEmpty()){
                    contact = contact+"%"+email;
                }

                if(mobile!=null&&!mobile.isEmpty()){
                    contact = contact+"%"+mobile;
                }
                tasks.setRemarks(""+client+""+contact);

                if(mShow.isChecked()){
                    tasks.setLatitude(lati+"");
                    tasks.setLongitude(lngi+"");
                }
                if(type!=null&&type.equalsIgnoreCase("Employee")){
                    tasks.setToReportEmployeeId(PreferenceHandler.getInstance( CreateOrderScreen.this).getManagerId());
                    tasks.setEmployeeId(employeeId);

                }else{
                    tasks.setToReportEmployeeId(PreferenceHandler.getInstance( CreateOrderScreen.this).getUserId());
                    tasks.setEmployeeId(employeeId);

                }

                tasks.setDepartmentId(0);

                try {
                    Date taskStartDate = new SimpleDateFormat("MM/dd/yyyy").parse(tasks.getStartDate ());
                    Date taskEndDate = new SimpleDateFormat("MM/dd/yyyy").parse(tasks.getEndDate ());

                if(approvedLeaves!=null&&approvedLeaves.size ()!=0){
                    for( Leaves leaves:approvedLeaves){
                        Date leaveStartDate = new SimpleDateFormat("yyyy-MM-dd").parse(leaves.getFromDate ( ));
                        Date leaveEndDate = new SimpleDateFormat("yyyy-MM-dd").parse(leaves.getToDate ( ));

                        if(taskStartDate.getTime ()>=leaveEndDate.getTime ()&&taskEndDate.getTime ()>leaveEndDate.getTime ()){
                            addTask(tasks);

                        }else{
                            Toast.makeText ( this , "Employee is on Leave from : "+leaveStartDate +" to :"+leaveEndDate, Toast.LENGTH_LONG ).show ( );
                        }
                    }
                }else{
                    addTask(tasks);
                }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public long dateCal(String start,String end){

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");
        System.out.println("Loigin "+start);
        System.out.println("Logout "+end);


        Date fd=null,td=null;

        try {
            fd = sdf.parse(""+start);
            td = sdf.parse(""+end);

            long diff = td.getTime() - fd.getTime();
            long Hours = diff / (60 * 60 * 1000) % 24;
            long Minutes = diff / (60 * 1000) % 60;
            long diffDays = diff / (24 * 60 * 60 * 1000);
            System.out.println("Diff "+diff);
            System.out.println("Hours "+Hours);
            System.out.println("Minutes "+Minutes);


            return  diffDays;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void addTask(final Tasks tasks) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();
        TasksAPI apiService = Util.getClient().create( TasksAPI.class);
        Call<Tasks> call = apiService.addTasks(tasks);
        call.enqueue(new Callback<Tasks>() {
            @Override
            public void onResponse(Call<Tasks> call, Response<Tasks> response) {
                try {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {
                        Tasks s = response.body();
                        if(s!=null){
                            Toast.makeText( CreateOrderScreen.this, "Order Created Successfully", Toast.LENGTH_SHORT).show();
                            //  CreateOrderScreen.this.finish();
                            TaskNotificationManagers tn = new TaskNotificationManagers();
                            tn.setEmployeeId(""+s.getEmployeeId());
                            tn.setTaskName(s.getTaskName());
                            tn.setTaskDescription(s.getTaskDescription());
                            tn.setDeadLine(s.getDeadLine());
                            tn.setComments(s.getComments());
                            tn.setRemarks(s.getRemarks());
                            tn.setToReportEmployeeId(s.getToReportEmployeeId());
                            tn.setTitle("Task Allocated");
                            tn.setMessage(""+s.getTaskName());
                            tn.setTaskId(s.getTaskId());
                            tn.setDepartmentId(1);
                            savetask(tn);
                        }

                    }else {
                        Toast.makeText( CreateOrderScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<Tasks> call, Throwable t) {
                if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText( CreateOrderScreen.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });
    }

    public void savetask(final TaskNotificationManagers task) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();
        TaskNotificationAPI apiService = Util.getClient().create(TaskNotificationAPI.class);
        Call<TaskNotificationManagers> call = apiService.saveTask(task);
        call.enqueue(new Callback<TaskNotificationManagers>() {
            @Override
            public void onResponse(Call<TaskNotificationManagers> call, Response<TaskNotificationManagers> response) {
                try {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText ( CreateOrderScreen.this , "Save Task" , Toast.LENGTH_SHORT ).show ( );
                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {
                        TaskNotificationManagers s = response.body();
                        if(s!=null){
                            task.setSenderId( Constants.SENDER_ID);
                            task.setServerId( Constants.SERVER_ID);
                            sendTask(task);
                        }

                    }else {
                        Toast.makeText( CreateOrderScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<TaskNotificationManagers> call, Throwable t) {
                if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText( CreateOrderScreen.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });
    }

    public void sendTask(final TaskNotificationManagers lm) {
        Gson gson = new Gson ();
        String json = gson.toJson ( lm );
        System.out.println ( "Suree :"+json );
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Sending Details..");
        dialog.setCancelable(false);
        dialog.show();
        TaskNotificationAPI apiService = Util.getClient().create(TaskNotificationAPI.class);
        Call<ArrayList<String>> call = apiService.sendTask(lm);
        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                try {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText ( CreateOrderScreen.this , "Send Task Notification" , Toast.LENGTH_SHORT ).show ( );
                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {
                        CreateOrderScreen.this.finish();
                    }else {
                        Toast.makeText( CreateOrderScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText( CreateOrderScreen.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent ( data );
                    //System.out.println(place.getLatLng());
                    location.setText(place.getName()+","+place.getAddress());
                    //location.setText(""+place.getId());
                    placeId= place.getId();

                    lati = place.getLatLng().latitude;
                    lngi = place.getLatLng().longitude;

                    lat.setText(df2.format(place.getLatLng().latitude)+"");
                    lng.setText(df2.format(place.getLatLng().longitude)+"");
                    System.out.println("Star Rating = "+place.getRating());
                    if(mMap != null)
                    {
                        mMap.clear();
                        marker = mMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                .position(place.getLatLng()));
                        CameraPosition cameraPosition1 = new CameraPosition.Builder().target(place.getLatLng()).zoom(17).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
                    }
                    //address.setText(place.getAddress());*/
                    Log.i(TAG, "Place: " + place.getName());
                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    Status status = Autocomplete.getStatusFromIntent (data);
                    // TODO: Handle the error.
                    Log.i(TAG, status.getStatusMessage());

                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getCustomers(final int id) {
        final ProgressDialog dialog = new ProgressDialog( CreateOrderScreen.this);
        dialog.setMessage("Loading Details..");
        dialog.setCancelable(false);
        dialog.show();
        final EmployeeApi orgApi = Util.getClient().create( EmployeeApi.class);
        Call<ArrayList< Employee >> getProf = orgApi.getEmployeesByOrgId (id);
        getProf.enqueue(new Callback<ArrayList< Employee >>() {
            @Override
            public void onResponse( Call<ArrayList< Employee >> call, Response<ArrayList< Employee >> response) {
                if (response.code() == 200||response.code() == 201||response.code() == 204) {
                    dialog.dismiss();
                    //customerArrayList.addAll ( response.body () );
                    ArrayList<Employee> employeeArrayList = response.body();
                    if(employeeArrayList!=null&&employeeArrayList.size()!=0){
                        for(Employee e:employeeArrayList){
                            if(e.getUserRoleId ()==10&&e.getManagerId ()==employeeId){
                                customerArrayList.add ( e );
                            }
                        }

                        if(customerArrayList!=null&&customerArrayList.size ()!=0){
                            CustomerSpinnerAdapter adapter = new CustomerSpinnerAdapter( CreateOrderScreen.this,customerArrayList);
                            customerSpinner.setAdapter(adapter);
                        }
                    }

                }else{
                    dialog.dismiss();
                    Toast.makeText( CreateOrderScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    if(customerArrayList!=null&&customerArrayList.size()!=0){
                        CustomerSpinnerAdapter adapter = new CustomerSpinnerAdapter( CreateOrderScreen.this,customerArrayList);
                        customerSpinner.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure( Call<ArrayList< Employee >> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText( CreateOrderScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                if(customerArrayList!=null&&customerArrayList.size()!=0){
                    CustomerSpinnerAdapter adapter = new CustomerSpinnerAdapter( CreateOrderScreen.this,customerArrayList);
                    customerSpinner.setAdapter(adapter);
                }
            }
        });
    }

    private void getLeaveDetails(final int employeeId,final int month,final int year){
        totalLeaves = new ArrayList<>();
        LeaveAPI apiService = Util.getClient().create(LeaveAPI.class);
        Call<ArrayList< Leaves >> call = apiService.getLeavesByEmployeeId(employeeId);
        call.enqueue(new Callback<ArrayList<Leaves>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Leaves>> call, @NonNull Response<ArrayList<Leaves>> response) {
                int statusCode = response.code();
                if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {
                    try{
                        totalLeaves = new ArrayList<> ();
                        approvedLeaves = new ArrayList<>();
                        rejectedLeaves = new ArrayList<>();
                        paidLeaves = new ArrayList<>();
                        unpaidLeaves = new ArrayList<>();
                        pendingLeaves = new ArrayList<>();

                        if(response.body ()!=null&&response.body ().size ()!=0){
                            for (Leaves leaves:response.body ()) {
                                if(leaves.getApproverComment ()==null){
                                    totalLeaves.add(leaves);
                                }
                                if(leaves.getStatus()!=null&&!leaves.getStatus().isEmpty()){
                                    if(leaves.getStatus().equalsIgnoreCase("Approved")){
                                        approvedLeaves.add(leaves);
                                    }else if(leaves.getStatus().equalsIgnoreCase("Rejected")){
                                        rejectedLeaves.add(leaves);
                                    }else if(leaves.getStatus().equalsIgnoreCase("Pending")){
                                        pendingLeaves.add(leaves);
                                    }
                                }
                            }
                        }
                        if (totalLeaves !=null && totalLeaves.size()!=0) {
                            //getMonthlyLeave(totalLeaves,month,year);
                        }else{
                            //mNoLeavesLay.setVisibility(View.VISIBLE);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else {
                    ShowToast ( ValidationConst.FAILES_DUE_TO+statusCode );
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Leaves>> call, @NonNull Throwable t) {
                Log.e("TAG", t.toString());
            }
        });
    }


  /*  private void getLoginDetails( final LoginDetails loginDetails){
        LoginDetailsAPI apiService = Util.getClient().create(LoginDetailsAPI.class);
        Call<ArrayList<LoginDetails>> call = apiService.getLoginByEmployeeIdAndDate(loginDetails);
        call.enqueue(new Callback<ArrayList<LoginDetails>>() {
            @Override
            public void onResponse( @NonNull Call<ArrayList<LoginDetails>> call, @NonNull Response<ArrayList<LoginDetails>> response) {
                int statusCode = response.code();
                if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {
                    ArrayList<LoginDetails> list = response.body();
                    if (list !=null && list.size()!=0) {
                        if(list.get(list.size()-1).getLogOutTime()==null||list.get(list.size()-1).getLogOutTime().isEmpty()){
                            if(list.get(0).getTotalMeeting()!=null&&!list.get(0).getTotalMeeting().isEmpty()){
                                if(list.get(0).getTotalMeeting().equalsIgnoreCase("Absent")){
                                    mStatus = "Absent";
                                    Toast.makeText ( getApplicationContext (),"Employee is Absent",Toast.LENGTH_LONG ).show ();
                                }else{
                                    mStatus = "Present";
                                    validate();
                                }
                            }else{
                                mStatus = "Present";
                                validate();
                            }

                        }else{
                            mStatus = "Absent";
                            Toast.makeText ( getApplicationContext (),"Employee is Absent",Toast.LENGTH_LONG ).show ();
                        }

                    }else{
                        mStatus = "Absent";
                        Toast.makeText ( getApplicationContext (),"Employee is Absent",Toast.LENGTH_LONG ).show ();
                    }
                }else{
                    Toast.makeText ( getApplicationContext (),ValidationConst.FAILES_DUE_TO+statusCode,Toast.LENGTH_LONG ).show ();
                }
            }

            @Override
            public void onFailure( @NonNull Call<ArrayList< LoginDetails >> call, @NonNull Throwable t) {
                Log.e("TAG", t.toString());
            }
        });
    }*/


    @Override
    public void onBackPressed ( ) {
        super.onBackPressed ( );
        this.finish ();
    }
    @Override
    public boolean onOptionsItemSelected ( @NonNull MenuItem item ) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            CreateOrderScreen.this.finish();
        }
        return super.onOptionsItemSelected ( item );
    }
}
