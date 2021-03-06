package app.zingo.mysolite.ui.Common;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ipaulpro.afilechooser.FileListAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import app.zingo.mysolite.WebApi.ExpensesApi;
import app.zingo.mysolite.WebApi.LiveTrackingAPI;
import app.zingo.mysolite.WebApi.LoginDetailsAPI;
import app.zingo.mysolite.WebApi.MeetingsAPI;
import app.zingo.mysolite.WebApi.TasksAPI;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.model.Expenses;
import app.zingo.mysolite.model.LiveTracking;
import app.zingo.mysolite.model.LoginDetails;
import app.zingo.mysolite.model.Meetings;
import app.zingo.mysolite.model.ObservableReportData;
import app.zingo.mysolite.model.ReportDataEmployee;
import app.zingo.mysolite.Service.ReportDownloadingDataService;
import app.zingo.mysolite.model.Tasks;
import app.zingo.mysolite.utils.Common;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.Util;
import app.zingo.mysolite.WebApi.MultpleAPI;
import app.zingo.mysolite.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import io.reactivex.Observable;
import rx.Subscriber;
import rx.functions.Func5;
import rx.functions.FuncN;

public class ReportBulkdataScreen extends AppCompatActivity {
    private ArrayList< ReportDataEmployee > reportDataEmployees;
    private ArrayList<ObservableReportData> data = new ArrayList<>();
    private TextView mFromDate,mToDate;
    private LinearLayout mCurrent,mLast,mPast;
    private TasksAPI mTaskService = Common.getTaskAPI ();
    private LoginDetailsAPI mLoginDetailsService = Common.getLoginDetailsAPI ();
    private MeetingsAPI mMeetingsService = Common.getMeetingsAPI ();
    private LiveTrackingAPI mLiveLocationsService = Common.getLiveTrackingAPI ();
    private ExpensesApi mExpensesService = Common.getExpensesAPI ();
    private CompositeDisposable compositeDisposable = new CompositeDisposable (  );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_report_bulkdata_screen );
        getSupportActionBar ( ).setHomeButtonEnabled ( true );
        getSupportActionBar ( ).setDisplayHomeAsUpEnabled ( true );
        setTitle ( "Report Management" );
        initViews ( );
        getAllData();
    }
    private void initViews ( ) {
        try{
            mCurrent = findViewById(R.id.currentMonth_layout);
            mLast = findViewById(R.id.lastMonth_layout);
            mPast = findViewById(R.id.pastMonth_layout);

            mCurrent.setOnClickListener( v -> {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                String monthValue = "";

                if((month+1)<10){
                    monthValue = "0"+(month+1);
                }else{
                    monthValue = ""+(month+1);
                }
                String startDate = year+"-"+monthValue+"-01";
                String endDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                Intent intent = new Intent( ReportBulkdataScreen.this, ReportDownloadingDataService.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("StartDate", startDate);
                mBundle.putString("EndDate", endDate);
                intent.setAction( ReportDownloadingDataService.ACTION_START_FOREGROUND_SERVICE);
                intent.putExtras(mBundle);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                } else {
                    startService(intent);
                }
            } );

            mLast.setOnClickListener( v -> {
                Calendar c = Calendar.getInstance();
                c.add(Calendar.MONTH, -1);
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                String monthValue = "";
                int res = c.getActualMaximum(Calendar.DATE);

                if((month+1)<10){
                    monthValue = "0"+(month+1);
                }else{
                    monthValue = ""+(month+1);
                }
                String startDate = year+"-"+monthValue+"-01";
                String endDate = year+"-"+monthValue+"-"+res;

                Intent intent = new Intent( ReportBulkdataScreen.this, ReportDownloadingDataService.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("StartDate", startDate);
                mBundle.putString("EndDate", endDate);
                intent.setAction( ReportDownloadingDataService.ACTION_START_FOREGROUND_SERVICE);
                intent.putExtras(mBundle);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                } else {
                    startService(intent);
                }
            } );

            mPast.setOnClickListener( v -> {
                try {
                    showAlert();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } );

        }catch (Exception e){
            e.printStackTrace();
        }
    }

   /* public void getData( final ReportDataEmployee reportDataEmployee, final Employee dto, final String dateValue){
        Date date=null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        LoginDetails ld  = new LoginDetails ();
        ld.setEmployeeId(dto.getEmployeeId());
        ld.setLoginDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
        String logDate = new SimpleDateFormat("MMM dd,yyyy").format(date);
        //getLoginDetails(ld,holder.mLogin,holder.mLogout,holder.mHours,logDate);
        LiveTracking lv = new LiveTracking ();
        lv.setEmployeeId(dto.getEmployeeId());
        lv.setTrackingDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
        //getLiveLocation(lv,holder.mKm);
        Meetings md  = new Meetings ();
        md.setEmployeeId(dto.getEmployeeId());
        md.setMeetingDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
        //getMeetingsDetails(md,holder.mVisits);
        //getTasks(dto.getEmployeeId(),"Completed",holder.mTasks,dateValue);
        //getExpense(dto.getEmployeeId(),holder.mExpenses, holder.mExpAmt,dateValue);
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://zingolocals.azurewebsites.net/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        MultpleAPI backendApi = Util.getClient().create( MultpleAPI.class);
        List<Observable<?>> requests = new ArrayList<>();
        Observable<ArrayList<LoginDetails>> obs1= backendApi.getLoginByEmployeeIdAndDate(ld);
        Observable<ArrayList<LiveTracking>> obs2=backendApi.getLiveTrackingByEmployeeIdAndDate(lv);
        Observable<ArrayList<Meetings>> obs3= backendApi.getMeetingsByEmployeeIdAndDate(md);
        Observable<ArrayList<Tasks>> obs4=backendApi.getTasksByEmployeeId(dto.getEmployeeId());
        Observable<ArrayList<Expenses>> obs5= backendApi.getExpenseByEmployeeIdAndOrganizationId( PreferenceHandler.getInstance(ReportBulkdataScreen.this).getCompanyId(),dto.getEmployeeId());
        //  requests.add(backendApi.getLoginByEmployeeIdAndDate(ld));
        requests.add(backendApi.getLiveTrackingByEmployeeIdAndDate(lv));
        // requests.add(backendApi.getMeetingsByEmployeeIdAndDate(md));
        //requests.add(backendApi.getTasksByEmployeeId(dto.getEmployeeId()));
        //requests.add(backendApi.getExpenseByEmployeeIdAndOrganizationId(PreferenceHandler.getInstance(ReportBulkdataScreen.this).getCompanyId(),dto.getEmployeeId()));
        Observable<ObservableReportData> combinedObservable = Observable.zip(requests, new FuncN <ObservableReportData> () {
            @Override
            public ObservableReportData call(Object... args) {
                ObservableReportData result = new ObservableReportData();
                if(args!=null&&args.length==1){
                    //   result.setLoginDetailsArrayList((ArrayList<LoginDetails>)args[0]);
                    result.setLiveTrackingArrayList((ArrayList< LiveTracking >)args[0]);
                    //  result.setMeetingsArrayList((ArrayList<Meetings>)args[2]);
                    // result.setTasksArrayList((ArrayList<Tasks>)args[3]);
                    // result.setExpensesArrayList((ArrayList<Expenses>)args[4]);
                    data.add(result);
                }
                return result;
            }
        });

        System.out.println("Obser "+data.size());
        if(data.size()!=0){
            //  System.out.println("value "+data.get(0).getLoginDetailsArrayList().size());
        }

        combinedObservable.subscribe(item -> System.out.print(item), error -> error.printStackTrace(),
                () -> System.out.println());
        combinedObservable.observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber <ObservableReportData> () {
                    @Override
                    public void onCompleted() {
                        System.out.println(" ");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onNext(ObservableReportData wrapperObj) {
                        System.out.println("Data "+wrapperObj.getLoginDetailsArrayList().size()+" ");
                    }
                });
    }*/

    private void getAllData( ){
        compositeDisposable.add(mLoginDetailsService.getLoginDetailsRx ()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( loginDetails -> {
                    setLoginData(loginDetails);
                    compositeDisposable.add(mTaskService.getTasksRx ()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe( tasks -> { setTaskData (tasks);
                                compositeDisposable.add(mMeetingsService.getMeetingDetailsRx ()
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe( meetings -> { setMeetingData (meetings);
                                            compositeDisposable.add(mLiveLocationsService.getLiveTrackingRx ()
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe( liveTrackings -> { setLiveTrackingData (liveTrackings);
                                                        compositeDisposable.add(mExpensesService.getExpensesRx ()
                                                                .subscribeOn(Schedulers.io())
                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                .subscribe( expenses -> setExpenseData (expenses) ,
                                                                        throwable -> { } ));
                                                    } , throwable -> { } ));
                                        } , throwable -> { } ));
                            } , throwable -> { } ));
                } , throwable -> { } ));
    }

    private void setLoginData ( ArrayList< LoginDetails> loginDetails ) {
        Gson gson = new Gson ();
        String json = gson.toJson ( loginDetails );
        System.out.println ( "Suree LoginData : "+json );
    }

    private void setTaskData ( ArrayList< Tasks> tasks ) {
        Gson gson = new Gson ();
        String json = gson.toJson ( tasks );
        System.out.println ( "Suree TaskData : "+json );
    }

    private void setMeetingData ( ArrayList< Meetings> meetings ) {
        Gson gson = new Gson ();
        String json = gson.toJson ( meetings );
        System.out.println ( "Suree MeetingData  : "+json );
    }

    private void setLiveTrackingData ( ArrayList< LiveTracking> liveTrackings ) {
        Gson gson = new Gson ();
        String json = gson.toJson ( liveTrackings );
        System.out.println ( "Suree TrackingData : "+json );
    }

    private void setExpenseData ( ArrayList< Expenses> expenses ) {
        Gson gson = new Gson ();
        String json = gson.toJson ( expenses );
        System.out.println ( "Suree ExpenseData : "+json );
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            ReportBulkdataScreen.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAlert() throws Exception {
        AlertDialog.Builder builder = new AlertDialog.Builder( ReportBulkdataScreen.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.pop_custom_date,null);
        mFromDate = (TextView)view.findViewById(R.id.filter_book_from_date);
        mToDate = (TextView)view.findViewById(R.id.filter_book_to_date);
        Button mFilterBookings = (Button)view.findViewById(R.id.filter_search_reports);
        builder.setView(view);
        builder.setTitle("Select Date to Filter");

        final AlertDialog dialog = builder.create();
        dialog.show();
        //builder.show();
        mFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(mFromDate);
            }
        });

        mToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(mToDate);
            }
        });

        mFilterBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String fromdate = mFromDate.getText().toString();
                    String todate = mToDate.getText().toString();

                    if(fromdate == null || fromdate.isEmpty()) {
                        Toast.makeText( ReportBulkdataScreen.this,"Please select from date",Toast.LENGTH_LONG).show();
                    }
                    else if(todate == null || todate.isEmpty()) {
                        Toast.makeText( ReportBulkdataScreen.this,"Please select to date",Toast.LENGTH_LONG).show();
                    }
                    else {
                        if(dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Intent intent = new Intent( ReportBulkdataScreen.this, ReportDownloadingDataService.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putString("StartDate", fromdate);
                        mBundle.putString("EndDate", todate);
                        intent.setAction( ReportDownloadingDataService.ACTION_START_FOREGROUND_SERVICE);
                        intent.putExtras(mBundle);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(intent);
                        } else {
                            startService(intent);
                        }
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void openDatePicker(final TextView tv) {
        // Get Current Date
        try{
            final Calendar c = Calendar.getInstance();
            int mYear  = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay   = c.get(Calendar.DAY_OF_MONTH);
            //launch datepicker modal
            DatePickerDialog datePickerDialog = new DatePickerDialog( ReportBulkdataScreen.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            Log.d("Date", "DATE SELECTED "+dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year,monthOfYear,dayOfMonth);
                            String date1 = year+"-"+(monthOfYear + 1)  + "-" + dayOfMonth;
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                            if (tv.equals(mFromDate)){

                                try {
                                    Date tdate = sdf.parse(date1);
                                    String from = sdf.format(tdate);
                                    String to = mToDate.getText().toString();
                                    if(to!=null&&!to.isEmpty()){
                                        Date toDate = sdf.parse(to);
                                        if(tdate.getTime()>=toDate.getTime()){
                                            Toast.makeText( ReportBulkdataScreen.this, "Please select lesser than To Date", Toast.LENGTH_SHORT).show();
                                        }else{
                                            tv.setText(from);
                                        }

                                    }else{
                                        tv.setText(from);
                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }else if (tv.equals(mToDate)) {

                                try {
                                    Date tdate = sdf.parse(date1);
                                    String to = sdf.format(tdate);
                                    String from = mFromDate.getText().toString();

                                    if(from!=null&&!from.isEmpty()){
                                        Date fromDate = sdf.parse(from);
                                        if(tdate.getTime()<=fromDate.getTime()){
                                            Toast.makeText( ReportBulkdataScreen.this, "Please select greater than From Date", Toast.LENGTH_SHORT).show();
                                            tv.setText("");
                                        }else{
                                            tv.setText(to);
                                        }
                                    }else{
                                        Toast.makeText( ReportBulkdataScreen.this, "Please Select From Date", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
