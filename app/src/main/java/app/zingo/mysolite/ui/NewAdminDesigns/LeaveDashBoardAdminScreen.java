package app.zingo.mysolite.ui.NewAdminDesigns;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import app.zingo.mysolite.adapter.LeaveDashBoardAdapter;
import app.zingo.mysolite.Custom.CustomCalendar.CustomMonthPicker;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.model.EmployeeImages;
import app.zingo.mysolite.model.Leaves;
import app.zingo.mysolite.ui.Common.LeaveListScreen;
import app.zingo.mysolite.utils.NetworkUtil;
import app.zingo.mysolite.utils.ProgressBarUtil;
import app.zingo.mysolite.utils.Util;
import app.zingo.mysolite.WebApi.LeaveAPI;
import app.zingo.mysolite.R;
import app.zingo.mysolite.utils.ValidationClass;
import app.zingo.mysolite.utils.ValidationConst;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaveDashBoardAdminScreen extends ValidationClass {
    private Employee employee;
    private TextView mDate;
    private TextView mPaidCount;
    private TextView mUnpaidCount;
    private TextView mTotalCount;
    private TextView mApprovedCound;
    private TextView mRejectedCount;
    private TextView mPendingCount;
    private LinearLayout mNoLeavesLay;
    private RecyclerView mLeaveList;
    private SimpleDateFormat dateFormat;
    private ArrayList<Leaves> totalLeaves,paidLeaves,unpaidLeaves,approvedLeaves,rejectedLeaves,pendingLeaves,monthtotalLeaves,
            monthpaidLeaves,monthunpaidLeaves,monthapprovedLeaves,monthrejectedLeaves,monthpendingLeaves;
    private int monthValue = 0;
    private int yearValue = 0;
    private boolean employeeScreen = false;
    private ProgressBarUtil progressBarUtil;

    @SuppressLint ("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_dash_board_admin_screen);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            employee = (Employee)bundle.getSerializable("Employee");
            employeeScreen = bundle.getBoolean("EmployeeScreen");
        }

        if(employeeScreen){

        }else{
            Objects.requireNonNull ( getSupportActionBar ( ) ).setHomeButtonEnabled(true);
            Objects.requireNonNull ( getSupportActionBar ( ) ).setDisplayHomeAsUpEnabled(true);
            Objects.requireNonNull ( getSupportActionBar ( ) ).setTitle("Leave Dashboard");
        }
      initViews();
    }

    @SuppressLint ("SimpleDateFormat")
    private void initViews ( ) {
        try{
            progressBarUtil = new ProgressBarUtil ( this );
            totalLeaves = new ArrayList<>();
            paidLeaves = new ArrayList<>();
            unpaidLeaves = new ArrayList<>();
            approvedLeaves = new ArrayList<>();
            rejectedLeaves = new ArrayList<>();
            pendingLeaves = new ArrayList<>();
            TextView mEmployeeName = findViewById ( R.id.name );
            CircleImageView mProfilePic = findViewById ( R.id.employee_pic );
            mDate = findViewById(R.id.presentDate);
            mPaidCount = findViewById(R.id.paid_leaves);
            mUnpaidCount = findViewById(R.id.un_paid_leaves);
            mTotalCount = findViewById(R.id.total_leaves);
            mApprovedCound = findViewById(R.id.approved_leaves);
            mRejectedCount = findViewById(R.id.rejected_leaves);
            mPendingCount = findViewById(R.id.pending_leaves);
            // CustomSpinner mDay;
            ImageView mPrevious = findViewById ( R.id.previousDay );
            ImageView mNext = findViewById ( R.id.nextDay );
            mLeaveList = findViewById(R.id.leaves_list_dash);
            mLeaveList.setLayoutManager(new LinearLayoutManager( LeaveDashBoardAdminScreen.this));
            mNoLeavesLay = findViewById(R.id.noRecordFound);

            dateFormat = new SimpleDateFormat("MMM yyyy");
            mDate.setText(dateFormat.format(new Date()));
            if(employee!=null){
                mEmployeeName.setText(String.valueOf ( employee.getEmployeeName() ));
                ArrayList<EmployeeImages> images = employee.getEmployeeImages();
                if(images!=null&&images.size()!=0){
                    EmployeeImages employeeImages = images.get(0);
                    if(employeeImages!=null){
                        String base=employeeImages.getImage();
                        if(base != null && !base.isEmpty()){
                            Picasso.get ().load(base).placeholder(R.drawable.profile_image).error(R.drawable.profile_image).into( mProfilePic );
                        }
                    }
                }

                Calendar cal = Calendar.getInstance();
                int monthCal = cal.get(Calendar.MONTH);
                int yearCal = cal.get(Calendar.YEAR);
                monthValue = monthCal+1;
                yearValue = yearCal;
                if ( NetworkUtil.checkInternetConnection ( LeaveDashBoardAdminScreen.this ) ) {
                    getLeaveDetails(employee.getEmployeeId(), monthValue,yearValue);
                } else {
                    noInternetConnection ();
                }
            }

            mPrevious.setOnClickListener( view -> {
                try{
                    final Date date = dateFormat.parse(mDate.getText().toString());
                    final Calendar calendar = Calendar.getInstance();
                    if ( date != null ) {
                        calendar.setTime(date);
                    }
                    calendar.add(Calendar.MONTH, -1);
                    Date date2 = calendar.getTime();

                    mDate.setText(dateFormat.format(date2));
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);

                    monthValue = month+1;
                    yearValue = year;

                    if(totalLeaves!=null&&totalLeaves.size()!=0){
                        getMonthlyLeave(totalLeaves,monthValue,yearValue);
                    }else{
                        mNoLeavesLay.setVisibility(View.VISIBLE);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            } );

            mNext.setOnClickListener( view -> {
                try{
                    final Date date = dateFormat.parse(mDate.getText().toString());
                    String newDate = dateFormat.format(new Date());
                    final Date newDates = dateFormat.parse(newDate);
                    if ( newDates != null ) {
                        if ( date != null ) {
                            if(newDates.getTime()<=date.getTime()){
                                Toast.makeText( LeaveDashBoardAdminScreen.this, "Data will not available for future date", Toast.LENGTH_SHORT).show();
                            }else{
                                final Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date);
                                calendar.add(Calendar.MONTH, 1);

                                Date date2 = calendar.getTime();
                                mDate.setText(dateFormat.format(date2));

                                int month = calendar.get(Calendar.MONTH);
                                int year = calendar.get(Calendar.YEAR);

                                monthValue = month+1;
                                yearValue = year;
                                if(totalLeaves!=null&&totalLeaves.size()!=0){
                                    getMonthlyLeave(totalLeaves,monthValue,yearValue);
                                }else{
                                    mNoLeavesLay.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            } );

            mDate.setOnClickListener( view -> monthYearPicker().show() );

            mPaidCount.setOnClickListener( v -> {
                if(monthpaidLeaves!=null&&monthpaidLeaves.size()!=0){
                    Intent pending = new Intent( LeaveDashBoardAdminScreen.this, LeaveListScreen.class);
                    Bundle bundle16 = new Bundle();
                    bundle16.putSerializable("Leaves",monthpaidLeaves);
                    bundle16.putString("Title","Paid Leaves");
                    pending.putExtras( bundle16 );
                    startActivity(pending);
                }
            } );

            mUnpaidCount.setOnClickListener( v -> {
                if(monthunpaidLeaves!=null&&monthunpaidLeaves.size()!=0){
                    Intent pending = new Intent( LeaveDashBoardAdminScreen.this, LeaveListScreen.class);
                    Bundle bundle15 = new Bundle();
                    bundle15.putSerializable("Leaves",monthunpaidLeaves);
                    bundle15.putString("Title","Un paid Leaves");
                    pending.putExtras( bundle15 );
                    startActivity(pending);
                }
            } );

            mTotalCount.setOnClickListener( v -> {
                if(monthtotalLeaves!=null&&monthtotalLeaves.size()!=0){
                    Intent pending = new Intent( LeaveDashBoardAdminScreen.this, LeaveListScreen.class);
                    Bundle bundle14 = new Bundle();
                    bundle14.putSerializable("Leaves",monthtotalLeaves);
                    bundle14.putString("Title","Total Leaves");
                    pending.putExtras( bundle14 );
                    startActivity(pending);
                }
            } );

            mApprovedCound.setOnClickListener( v -> {
                if(monthapprovedLeaves!=null&&monthapprovedLeaves.size()!=0){
                    Intent pending = new Intent( LeaveDashBoardAdminScreen.this, LeaveListScreen.class);
                    Bundle bundle13 = new Bundle();
                    bundle13.putSerializable("Leaves",monthapprovedLeaves);
                    bundle13.putString("Title","Approved Leaves");
                    pending.putExtras( bundle13 );
                    startActivity(pending);
                }
            } );

            mRejectedCount.setOnClickListener( v -> {
                if(monthrejectedLeaves!=null&&monthrejectedLeaves.size()!=0){
                    Intent pending = new Intent( LeaveDashBoardAdminScreen.this, LeaveListScreen.class);
                    Bundle bundle12 = new Bundle();
                    bundle12.putSerializable("Leaves",monthrejectedLeaves);
                    bundle12.putString("Title","Rejected Leaves");
                    pending.putExtras( bundle12 );
                    startActivity(pending);
                }
            } );

            mPendingCount.setOnClickListener( v -> {
                if(monthpendingLeaves!=null&&monthpendingLeaves.size()!=0){
                    Intent pending = new Intent( LeaveDashBoardAdminScreen.this, LeaveListScreen.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putSerializable("Leaves",monthpendingLeaves);
                    bundle1.putString("Title","Pending Leaves");
                    pending.putExtras( bundle1 );
                    startActivity(pending);
                }
            } );

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void openDatePicker(final TextView tv) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear  = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay   = c.get(Calendar.DAY_OF_MONTH);
        //launch datepicker modal
        @SuppressLint ({ "SetTextI18n" , "SimpleDateFormat" }) DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                ( view , year , monthOfYear , dayOfMonth ) -> {
                    try {
                        Log.d("Date", "DATE SELECTED "+dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year,monthOfYear,dayOfMonth);

                        String date1 = (monthOfYear + 1)  + "/" + (dayOfMonth) + "/" + year;
                   /*     String date2 = year  + "-" +(monthOfYear + 1)+ "-" +  (dayOfMonth);
                        @SuppressLint ("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");*/
                        @SuppressLint ("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                        try {
                            Date fdate = simpleDateFormat.parse(date1);
                           /* if ( fdate != null ) {
                                *//*@SuppressLint ("SimpleDateFormat") String dateValue = new SimpleDateFormat("yyyy-MM-dd").format(fdate);*//*
                            }*/
                            if ( fdate != null ) {
                                tv.setText(new SimpleDateFormat("MMM yyyy").format(fdate)+"");
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }

                } , mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();

    }

    public CustomMonthPicker monthYearPicker(){
        Calendar calendar = Calendar.getInstance();
        int years = calendar.get(Calendar.YEAR);
        int months = calendar.get(Calendar.MONTH);
        @SuppressLint ("SetTextI18n")
        final CustomMonthPicker customMonthPicker = new CustomMonthPicker(this)
                .setLocale(Locale.ENGLISH)
                .setSelectedMonth(months)
                .setSelectedYear(years)
                .setColorTheme(R.color.colorPrimary)
                .setPositiveButton( ( month , startDate , endDate , year , monthLabel ) -> {
                    System.out.println(month);
                    System.out.println(startDate);
                    System.out.println(endDate);
                    System.out.println(year);
                    System.out.println(monthLabel);

                    String[] months1 = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
                    String selectedMonthName = months1[month-1];
                    mDate.setText(selectedMonthName+" "+year);
                    monthValue = month;
                    yearValue = year;

                    if(totalLeaves!=null&&totalLeaves.size()!=0){
                        try {
                            getMonthlyLeave(totalLeaves,monthValue,yearValue);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        mNoLeavesLay.setVisibility(View.VISIBLE);
                    }
                } )
                .setNegativeButton( AppCompatDialog::dismiss );

        return customMonthPicker;
    }

    private void getLeaveDetails(final int employeeId,final int month,final int year){
        progressBarUtil.showProgress ( "Loading Details..." );
        totalLeaves = new ArrayList<>();
        LeaveAPI apiService = Util.getClient().create(LeaveAPI.class);
        Call<ArrayList<Leaves>> call = apiService.getLeavesByEmployeeId(employeeId);
        call.enqueue(new Callback<ArrayList<Leaves>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Leaves>> call, @NonNull Response<ArrayList<Leaves>> response) {
                int statusCode = response.code();
                if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {
                    progressBarUtil.hideProgress ();
                    try{
                        totalLeaves = new ArrayList<> ();
                        approvedLeaves = new ArrayList<>();
                        rejectedLeaves = new ArrayList<>();
                        paidLeaves = new ArrayList<>();
                        unpaidLeaves = new ArrayList<>();
                        pendingLeaves = new ArrayList<>();

                        if(response.body ()!=null&&response.body ().size ()!=0){
                            for (Leaves leave:response.body ()) {
                                if(leave.getApproverComment ()==null){
                                    totalLeaves.add(leave);
                                }
                            }
                        }
                        if (totalLeaves !=null && totalLeaves.size()!=0) {
                            getMonthlyLeave(totalLeaves,month,year);
                        }else{
                            mNoLeavesLay.setVisibility(View.VISIBLE);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        mNoLeavesLay.setVisibility(View.VISIBLE);
                    }

                }else {
                    ShowToast ( ValidationConst.FAILES_DUE_TO+statusCode );
                    progressBarUtil.hideProgress ();
                    mNoLeavesLay.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Leaves>> call, @NonNull Throwable t) {
                progressBarUtil.hideProgress ();
                noInternetConnection ();
                Log.e("TAG", t.toString());
                mNoLeavesLay.setVisibility(View.VISIBLE);
            }
        });
    }


    @SuppressLint ({ "SetTextI18n" , "SimpleDateFormat" })
    public void getMonthlyLeave( ArrayList<Leaves> list, final int month, final int year) throws Exception{
        monthtotalLeaves = new ArrayList<>();
        monthpaidLeaves = new ArrayList<>();
        monthunpaidLeaves = new ArrayList<>();
        monthapprovedLeaves = new ArrayList<>();
        monthrejectedLeaves = new ArrayList<>();
        monthpendingLeaves = new ArrayList<>();

        //Date date = new Date();
        Date adate = new Date();
        Date edate = new Date();

        if(month!=0&&year!=0){

            try {
              //  date = new SimpleDateFormat("MM yyyy").parse(month+" "+year);
                adate = new SimpleDateFormat("yyyy-MM-dd").parse(year+"-"+month+"-01");
                //edate = new SimpleDateFormat("yyyy-MMM-dd").parse(year+"-"+month+"-0");
                Date edates = new SimpleDateFormat("MM").parse(month+"");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                if ( edates != null ) {
                    calendar.set(Calendar.MONTH, (Integer.parseInt(new SimpleDateFormat("MM").format(edates))-1));
                }
                int daysInMonth = calendar.getActualMaximum(Calendar.DATE);
                edate = new SimpleDateFormat("yyyy-MM-dd").parse(year+"-"+month+"-"+daysInMonth);

            } catch (ParseException e) {
                e.printStackTrace();

            }
        }

        for (Leaves leaves:list) {
            String froms = leaves.getFromDate();
            String tos = leaves.getToDate();

            Date afromDate = null;
            Date atoDate = null;

            if(froms!=null&&!froms.isEmpty()){
                if(froms.contains("T")){
                    String[] dojs = froms.split ( "T" );
                    afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                }
            }

            if(tos!=null&&!tos.isEmpty()){
                if(tos.contains("T")){
                    String[] dojs = tos.split ( "T" );
                    atoDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                }
            }

            if(afromDate!=null&&atoDate!=null){
                if ( edate != null ) {
                    if ( adate != null ) {
                        if((edate.getTime()>=afromDate.getTime())
                                &&(adate.getTime()<=atoDate.getTime())) {
                            monthtotalLeaves.add(leaves);
                            if(leaves.getStatus()!=null&&!leaves.getStatus().isEmpty()){
                                if(leaves.getStatus().equalsIgnoreCase("Approved")){
                                    monthapprovedLeaves.add(leaves);
                                }else if(leaves.getStatus().equalsIgnoreCase("Rejected")){
                                    monthrejectedLeaves.add(leaves);
                                }else if(leaves.getStatus().equalsIgnoreCase("Pending")){
                                    monthpendingLeaves.add(leaves);
                                }
                            }

                            if(leaves.getLeaveType()!=null&&!leaves.getLeaveType().isEmpty()){
                                if(leaves.getLeaveType().equalsIgnoreCase("Paid")){
                                    monthpaidLeaves.add(leaves);
                                }else if(leaves.getLeaveType().equalsIgnoreCase("Un Paid")){
                                    monthunpaidLeaves.add(leaves);
                                }
                            }
                        }
                    }
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

                if(leaves.getLeaveType()!=null&&!leaves.getLeaveType().isEmpty()){
                    if(leaves.getLeaveType().equalsIgnoreCase("Paid")){
                        paidLeaves.add(leaves);
                    }else if(leaves.getLeaveType().equalsIgnoreCase("Un Paid")){
                        unpaidLeaves.add(leaves);
                    }
                }
            }
        }

        if(monthtotalLeaves!=null&&monthtotalLeaves.size()!=0){
            mTotalCount.setText(""+monthtotalLeaves.size());
            mNoLeavesLay.setVisibility(View.GONE);
            mLeaveList.setVisibility(View.VISIBLE);
            LeaveDashBoardAdapter adapter = new LeaveDashBoardAdapter ( LeaveDashBoardAdminScreen.this , monthtotalLeaves , null );
            mLeaveList.setAdapter( adapter );

            try{
                mPaidCount.setText(""+monthpaidLeaves.size());
                mUnpaidCount.setText(""+monthunpaidLeaves.size());
                mApprovedCound.setText(""+monthapprovedLeaves.size());
                mRejectedCount.setText(""+monthrejectedLeaves.size());
                mPendingCount.setText(""+monthpendingLeaves.size());

            }catch (Exception e){
                e.printStackTrace();
            }

        }else{
            mNoLeavesLay.setVisibility(View.VISIBLE);
            mLeaveList.setVisibility(View.GONE);
            mTotalCount.setText("0");
            mPaidCount.setText("0");
            mUnpaidCount.setText("0");
            mApprovedCound.setText("0");
            mRejectedCount.setText("0");
            mPendingCount.setText("0");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ( NetworkUtil.checkInternetConnection ( LeaveDashBoardAdminScreen.this ) ) {
            getLeaveDetails(employee.getEmployeeId(), monthValue,yearValue);
        } else {
            noInternetConnection ();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if ( id == android.R.id.home ) {
            LeaveDashBoardAdminScreen.this.finish ( );
        }
        return super.onOptionsItemSelected(item);
    }
}
