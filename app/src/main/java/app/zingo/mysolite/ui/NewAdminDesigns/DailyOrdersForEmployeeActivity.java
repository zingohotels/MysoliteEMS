package app.zingo.mysolite.ui.NewAdminDesigns;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.zingo.mysolite.adapter.OrderListAdapter;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.model.Tasks;
import app.zingo.mysolite.ui.Admin.CreateOrderScreen;
import app.zingo.mysolite.ui.Admin.EmployeeOrderMapScreen;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.ThreadExecuter;
import app.zingo.mysolite.utils.Util;
import app.zingo.mysolite.WebApi.TasksAPI;
import app.zingo.mysolite.R;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DailyOrdersForEmployeeActivity extends AppCompatActivity {


    FloatingActionButton floatingActionButton,refresh;
    TextView mDate;
    ImageView mPrevious,mNext;
    //View layout;
    private OrderListAdapter mAdapter;
    RecyclerView mTaskList;

    //CalendarDay mCalendarDay;
    private Employee mEmployee;
    private int mEmployeeId;
    List<Tasks> mTargetList = new ArrayList();
    Toolbar mToolbar;
    TextView movedTargets;
    TextView closedTargets;
    TextView totalTargets;
    TextView openTargets;
    TextView presentDate;
    ImageView prevDay;
    ImageView nextDay;


    LinearLayout mTotalTask,mPendingTask,mCompletedTask,mClosedTask,mNoRecord;

    ArrayList<Tasks> employeeTasks;
    ArrayList<Tasks> pendingTasks ;
    ArrayList<Tasks> completedTasks ;
    ArrayList<Tasks> closedTasks ;

    ArrayList<Tasks> dayemployeeTasks;
    ArrayList<Tasks> daypendingTasks ;
    ArrayList<Tasks> daycompletedTasks ;
    ArrayList<Tasks> dayclosedTasks ;

    int total=0,pending=0,complete=0,closed=0;
    int daytotal=0,daypending=0,daycomplete=0,dayclosed=0;

    SimpleDateFormat dateFormat;
    String passDate = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{

            setContentView(R.layout.activity_daily_orders_for_employee);
            setupToolbar();
            Bundle bundle = getIntent().getExtras();
            if (bundle!=null) {
                mEmployeeId = bundle.getInt("ProfileId");
            }

//            this.mCalendarDay = CalendarDay.from(new Date());
            this.presentDate = findViewById(R.id.presentDate);
            // this.presentDate.setText(DateUtil.getReadableDate(this.mCalendarDay));
            mTaskList = findViewById(R.id.targetList);
            mTaskList.setLayoutManager(new LinearLayoutManager(this));

            // this.prevDay = (ImageView) findViewById(R.id.previousDay);
            // this.nextDay = (ImageView) findViewById(R.id.nextDay);
            this.floatingActionButton = findViewById(R.id.addTargetOption);
            this.refresh = findViewById(R.id.refresh);
            totalTargets = findViewById(R.id.totalTargets);
            openTargets = findViewById(R.id.openTargets);
            closedTargets = findViewById(R.id.closedTargets);
            movedTargets = findViewById(R.id.movedTargets);

            mPendingTask = findViewById(R.id.openTargetsLayout);
            mCompletedTask = findViewById(R.id.closedTargetsLayout);
            mClosedTask = findViewById(R.id.movedTargetsLayout);
            mTotalTask = findViewById(R.id.totalTargetsLayout);

            mNoRecord = findViewById(R.id.noRecordFound);

            mDate = findViewById(R.id.presentDate);


            mPrevious = findViewById(R.id.previousDay);
            mNext = findViewById(R.id.nextDay);



            dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            mDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
            passDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            /*this.prevDay.setOnClickListener(new C13241());
            this.nextDay.setOnClickListener(new C13252());*/
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent createTask = new Intent( DailyOrdersForEmployeeActivity.this, CreateOrderScreen.class);
                    createTask.putExtra("EmployeeId", mEmployeeId);
                    startActivity(createTask);

                }
            });

            refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent createTask = new Intent( DailyOrdersForEmployeeActivity.this, DailyOrdersForEmployeeActivity.class);
                    createTask.putExtra("ProfileId", mEmployeeId);
                    startActivity(createTask);
                    DailyOrdersForEmployeeActivity.this.finish();

                }
            });

            mPendingTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mTaskList.removeAllViews();

                    if(daypendingTasks!=null&&daypendingTasks.size()!=0){
                        mAdapter = new OrderListAdapter( DailyOrdersForEmployeeActivity.this,daypendingTasks);
                        mTaskList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mAdapter = new OrderListAdapter( DailyOrdersForEmployeeActivity.this,new ArrayList<Tasks>());
                        mTaskList.setAdapter(mAdapter);
                        Toast.makeText( DailyOrdersForEmployeeActivity.this, "No Pending Orders given for this employee", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            mCompletedTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mTaskList.removeAllViews();

                    if(daycompletedTasks!=null&&daycompletedTasks.size()!=0){
                        mAdapter = new OrderListAdapter( DailyOrdersForEmployeeActivity.this,daycompletedTasks);
                        mTaskList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mAdapter = new OrderListAdapter( DailyOrdersForEmployeeActivity.this,new ArrayList<Tasks>());
                        mTaskList.setAdapter(mAdapter);
                        Toast.makeText( DailyOrdersForEmployeeActivity.this, "No Delivered Orders given for this employee", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            mClosedTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mTaskList.removeAllViews();

                    if(dayclosedTasks!=null&&dayclosedTasks.size()!=0){
                        mAdapter = new OrderListAdapter( DailyOrdersForEmployeeActivity.this,dayclosedTasks);
                        mTaskList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mAdapter = new OrderListAdapter( DailyOrdersForEmployeeActivity.this,new ArrayList<Tasks>());
                        mTaskList.setAdapter(mAdapter);
                        Toast.makeText( DailyOrdersForEmployeeActivity.this, "No Payment Done Orders given for this employee", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            mTotalTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mTaskList.removeAllViews();

                    if(dayemployeeTasks!=null&&dayemployeeTasks.size()!=0){
                        mAdapter = new OrderListAdapter( DailyOrdersForEmployeeActivity.this,dayemployeeTasks);
                        mTaskList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mAdapter = new OrderListAdapter( DailyOrdersForEmployeeActivity.this,new ArrayList<Tasks>());
                        mTaskList.setAdapter(mAdapter);
                        Toast.makeText( DailyOrdersForEmployeeActivity.this, "No Orders given for this employee", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            mPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        final Date date = dateFormat.parse(mDate.getText().toString());
                        final Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.add(Calendar.DAY_OF_YEAR, -1);

                        Date date2 = calendar.getTime();

                        mDate.setText(dateFormat.format(date2));
                        passDate = new SimpleDateFormat("yyyy-MM-dd").format(date2);

                        taskFilter(new SimpleDateFormat("yyyy-MM-dd").format(date2));


                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            mNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    try{
                        final Date date = dateFormat.parse(mDate.getText().toString());
                        final Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.add(Calendar.DAY_OF_YEAR, 1);

                        Date date2 = calendar.getTime();

                        mDate.setText(dateFormat.format(date2));
                        passDate = new SimpleDateFormat("yyyy-MM-dd").format(date2);
                        taskFilter(new SimpleDateFormat("yyyy-MM-dd").format(date2));


                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });

            mDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDatePicker(mDate);
                }
            });
            final Calendar calendar = Calendar.getInstance();
            Date date2 = calendar.getTime();

            getTasks(mEmployeeId,new SimpleDateFormat("yyyy-MM-dd").format(date2));


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void openDatePicker(final TextView tv) {
        // Get Current Date

        final Calendar c = Calendar.getInstance();
        int  mYear  = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay   = c.get(Calendar.DAY_OF_MONTH);
        //launch datepicker modal
        DatePickerDialog datePickerDialog = new DatePickerDialog( DailyOrdersForEmployeeActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        try {
                            Log.d("Date", "DATE SELECTED "+dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year,monthOfYear,dayOfMonth);


                            String date1 = (dayOfMonth)  + "-" + (monthOfYear + 1)+ "-" + year;



                            if (tv.equals(mDate)){


                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                try {
                                    Date fdate = simpleDateFormat.parse(date1);


                                    taskFilter(new SimpleDateFormat("yyyy-MM-dd").format(fdate));

                                    String startDate = simpleDateFormat.format(fdate);
                                    tv.setText(startDate);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                //


                            }

                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }


                    }
                }, mYear, mMonth, mDay);


        datePickerDialog.show();

    }

    public void setupToolbar() {
        this.mToolbar = findViewById(R.id.app_bar);
        setSupportActionBar(this.mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Orders");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order_excel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            DailyOrdersForEmployeeActivity.this.finish();

        } else if (id == R.id.action_map) {
            Intent map = new Intent( DailyOrdersForEmployeeActivity.this, EmployeeOrderMapScreen.class);
            map.putExtra("EmployeeId", mEmployeeId);
            map.putExtra("Date", passDate);
            startActivity(map);

        } else if (id == R.id.action_report) {

            if(dayemployeeTasks!=null&&dayemployeeTasks.size ()!=0){

                reportGeneration();

            }


        }
        return super.onOptionsItemSelected(item);
    }

  /*  public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }*/

    private void getTasks(final int employeeId,final String dateValue){


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Details..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                TasksAPI apiService = Util.getClient().create( TasksAPI.class);
                Call<ArrayList<Tasks>> call = apiService.getTasksByEmployeeId(employeeId);

                call.enqueue(new Callback<ArrayList<Tasks>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Tasks>> call, Response<ArrayList<Tasks>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();
                            ArrayList<Tasks> list = response.body();
                            employeeTasks = new ArrayList<>();
                            pendingTasks = new ArrayList<>();
                            completedTasks = new ArrayList<>();
                            closedTasks = new ArrayList<>();


                            dayemployeeTasks = new ArrayList<>();
                            daypendingTasks = new ArrayList<>();
                            daycompletedTasks = new ArrayList<>();
                            dayclosedTasks = new ArrayList<>();

                            Date date = new Date();
                            Date adate = new Date();
                            Date edate = new Date();

                            try {
                                date = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }



                            if (list !=null && list.size()!=0) {



                                for (Tasks task:list) {

                                    if(task.getCategory()!=null&&task.getCategory().equalsIgnoreCase("Order")){






                                        employeeTasks.add(task);
                                        total = total+1;

                                        if(task.getStatus().equalsIgnoreCase("Delivered")){
                                            completedTasks.add(task);
                                            complete = complete+1;
                                        }else if(task.getStatus().equalsIgnoreCase("Pending")){
                                            pendingTasks.add(task);
                                            pending = pending+1;
                                        }else if(task.getStatus().equalsIgnoreCase("Payment Done")){
                                            closedTasks.add(task);
                                            closed = closed+1;
                                        }




                                        String froms = task.getStartDate();
                                    //    String tos = task.getEndDate();

                                        Date afromDate = null;
                                        Date atoDate = null;

                                        if(froms!=null&&!froms.isEmpty()){

                                            if(froms.contains("T")){

                                                String dojs[] = froms.split("T");

                                                try {
                                                    afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }


                                            }

                                        }

                                        /*if(tos!=null&&!tos.isEmpty()){

                                            if(tos.contains("T")){

                                                String dojs[] = tos.split("T");

                                                try {
                                                    atoDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }

                                            }

                                        }*/

                                        if(afromDate!=null){

                                            if(date.getTime() == afromDate.getTime() ){


                                                dayemployeeTasks.add(task);
                                                daytotal = daytotal+1;

                                                if(task.getStatus().equalsIgnoreCase("Delivered")){
                                                    daycompletedTasks.add(task);
                                                    daycomplete = daycomplete+1;
                                                }else if(task.getStatus().equalsIgnoreCase("Pending")){
                                                    daypendingTasks.add(task);
                                                    daypending = daypending+1;
                                                }else if(task.getStatus().equalsIgnoreCase("Payment Done")){
                                                    dayclosedTasks.add(task);
                                                    dayclosed = dayclosed+1;
                                                }

                                            }
                                        }

                                    }


                                }

                                if(dayemployeeTasks!=null&&dayemployeeTasks.size()!=0){

                                    mNoRecord.setVisibility(View.GONE);
                                    mTaskList.setVisibility(View.VISIBLE);
                                    mAdapter = new OrderListAdapter( DailyOrdersForEmployeeActivity.this,dayemployeeTasks);
                                    mTaskList.setAdapter(mAdapter);

                                    totalTargets.setText(""+daytotal);
                                    openTargets.setText(""+daypending);
                                    closedTargets.setText(""+daycomplete);
                                    movedTargets.setText(""+dayclosed);
                                }else{

                                    mNoRecord.setVisibility(View.VISIBLE);
                                    mTaskList.setVisibility(View.GONE);
                                }



                            }else{

                                mNoRecord.setVisibility(View.VISIBLE);
                                mTaskList.setVisibility(View.GONE);

                                Toast.makeText( DailyOrdersForEmployeeActivity.this, "No Orders given for this employee ", Toast.LENGTH_SHORT).show();

                            }

                        }else {

                            if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();

                            Toast.makeText( DailyOrdersForEmployeeActivity.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Tasks>> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog != null&&progressDialog.isShowing())
                            progressDialog.dismiss();
                        mNoRecord.setVisibility(View.VISIBLE);
                        mTaskList.setVisibility(View.GONE);
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }


    public void taskFilter(final String dateValue){

        if(employeeTasks!=null&&employeeTasks.size()!=0){

            mNoRecord.setVisibility(View.VISIBLE);
            mTaskList.setVisibility(View.GONE);
            mTaskList.removeAllViews();

            dayemployeeTasks = new ArrayList<>();
            daypendingTasks = new ArrayList<>();
            daycompletedTasks = new ArrayList<>();
            dayclosedTasks = new ArrayList<>();

            daytotal=0;
            daypending=0;
            daycomplete=0;
            dayclosed=0;

            totalTargets.setText(""+daytotal);
            openTargets.setText(""+daypending);
            closedTargets.setText(""+daycomplete);
            movedTargets.setText(""+dayclosed);

            Date date = new Date();
            Date adate = new Date();
            Date edate = new Date();

            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);


            } catch (Exception e) {
                e.printStackTrace();
            }



            if (employeeTasks !=null && employeeTasks.size()!=0) {



                for (Tasks task:employeeTasks) {


                    String froms = task.getStartDate();
                    String tos = task.getEndDate();

                    Date afromDate = null;
                    Date atoDate = null;

                    if(froms!=null&&!froms.isEmpty()){

                        if(froms.contains("T")){

                            String dojs[] = froms.split("T");

                            try {
                                afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                        }

                    }

                    if(tos!=null&&!tos.isEmpty()){

                        if(tos.contains("T")){

                            String dojs[] = tos.split("T");

                            try {
                                atoDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }

                    }

                    if(afromDate!=null&&atoDate!=null){

                        if(date.getTime() == afromDate.getTime() ){


                            dayemployeeTasks.add(task);
                            daytotal = daytotal+1;

                            if(task.getStatus().equalsIgnoreCase("Delivered")){
                                daycompletedTasks.add(task);
                                daycomplete = daycomplete+1;
                            }else if(task.getStatus().equalsIgnoreCase("Pending")){
                                daypendingTasks.add(task);
                                daypending = daypending+1;
                            }else if(task.getStatus().equalsIgnoreCase("Payment Done")){
                                dayclosedTasks.add(task);
                                dayclosed = dayclosed+1;
                            }

                        }
                    }



                }

                if(dayemployeeTasks!=null&&dayemployeeTasks.size()!=0){

                    mNoRecord.setVisibility(View.GONE);
                    mTaskList.setVisibility(View.VISIBLE);

                    mAdapter = new OrderListAdapter( DailyOrdersForEmployeeActivity.this,dayemployeeTasks);
                    mTaskList.setAdapter(mAdapter);

                    totalTargets.setText(""+daytotal);
                    openTargets.setText(""+daypending);
                    closedTargets.setText(""+daycomplete);
                    movedTargets.setText(""+dayclosed);
                }else{

                }
            }else{
                //Toast.makeText(DailyOrdersForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();
                mNoRecord.setVisibility(View.VISIBLE);
                mTaskList.setVisibility(View.GONE);
                mTaskList.removeAllViews();
            }

        }else{
            mNoRecord.setVisibility(View.VISIBLE);
            mTaskList.setVisibility(View.GONE);
            mTaskList.removeAllViews();
        }
    }


    public void reportGeneration(){

        boolean isfilecreated = generateReport(dayemployeeTasks);
        if(isfilecreated)
        {

            try{

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
               // emailIntent.setType("text/plain");
                //emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"email@example.com"});
                //emailIntent.putExtra(Intent.EXTRA_SUBJECT, "subject here");
                //emailIntent.putExtra(Intent.EXTRA_TEXT, "body text");
                File root = Environment.getExternalStorageDirectory();
                String pathToMyAttachedFile = "/Order/"+"Orders"+new SimpleDateFormat("ddMMyy").format(new Date())+".xls";
                File file = new File(root, pathToMyAttachedFile);
                if (!file.exists() || !file.canRead()) {
                    return;
                }
                Uri uri = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    uri = FileProvider.getUriForFile( DailyOrdersForEmployeeActivity.this, "app.zingo.mysolite.fileprovider", file);
                }else{
                    uri = Uri.fromFile(file);
                }

                //Uri uri = Uri.fromFile(file);
                if(uri!=null){
                    emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                }else{
                    Toast.makeText( DailyOrdersForEmployeeActivity.this, "File cannot access", Toast.LENGTH_SHORT).show();
                }
           /* Uri uri = Uri.fromFile(file);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);*/
                startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private boolean generateReport( ArrayList<Tasks> list) {
        boolean success = false;
        WritableWorkbook workbook = null;

        try {

            File sd = Environment.getExternalStorageDirectory();
            String csvFile = "Order_"+new SimpleDateFormat("MMMyy").format(new Date())+".xls";

            File directory = new File(sd.getAbsolutePath()+"/Order");
            //create directory if not exist
            if (!directory.exists() && !directory.isDirectory()) {
                directory.mkdirs();
            }
            File file = new File(directory, csvFile);
            String sheetName = "Order_"+new SimpleDateFormat("MMMyy").format(new Date());//name of sheet

            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale ("en", "EN"));

            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet(sheetName, 0);


            sheet.addCell(new Label (5,0, PreferenceHandler.getInstance( DailyOrdersForEmployeeActivity.this).getCompanyName()));

            sheet.mergeCells(5,0,10,0);

            sheet.mergeCells(5,1,10,1);
            sheet.addCell(new Label(5,2,"Chart As On "+new SimpleDateFormat("dd/MM/yyyy").format(new Date())));
            sheet.mergeCells(5,2,8,2);


            sheet.addCell(new Label(3,3,"Generated On "+new SimpleDateFormat("dd/MM/yyyy, hh:mm aa").format(new Date())));
            sheet.mergeCells(3,3,6,3);

            sheet.addCell(new Label(7,3,"User : "+ PreferenceHandler.getInstance( DailyOrdersForEmployeeActivity.this).getUserFullName()));
            sheet.mergeCells(7,3,10,3);

            sheet.setColumnView(0, 20);
            sheet.setColumnView(1, 20);
            sheet.setColumnView(2, 30);
            sheet.setColumnView(3, 25);
            sheet.setColumnView(4, 100);
            sheet.setColumnView(5, 15);
            sheet.setColumnView(6, 20);
            sheet.setColumnView(7, 15);
            sheet.setColumnView(8, 15);
            sheet.setColumnView(9, 15);
            sheet.setColumnView(10, 15);
            sheet.setColumnView(11, 8);
            sheet.setColumnView(12, 18);
            sheet.setColumnView(13, 15);
            sheet.setColumnView(14, 15);
            sheet.setColumnView(15, 15);
            sheet.setColumnView(16, 15);
            sheet.setColumnView(17, 15);
            sheet.setColumnView(18, 15);
            sheet.setColumnView(19, 15);
            sheet.setColumnView(20, 15);
            sheet.setColumnView(21, 15);
            sheet.setColumnView(22, 15);
            sheet.setColumnView(23, 15);
            sheet.setColumnView(24, 15);

            WritableCellFormat cellFormats = new WritableCellFormat();
            cellFormats.setAlignment( Alignment.CENTRE);

            WritableCellFormat cellFormatL = new WritableCellFormat();
            cellFormats.setAlignment(Alignment.LEFT);





            sheet.addCell(new Label(0, 12, "Order Date",cellFormats));
            sheet.addCell(new Label(1, 12, "Payment Date",cellFormats));
            sheet.addCell(new Label(2, 12, "Client",cellFormats));
            sheet.addCell(new Label(3, 12, "Status",cellFormats));
            sheet.addCell(new Label(4, 12, "Order Details",cellFormats));



            if(list != null)
            {


                for (int i=0;i<list.size();i++)
                {
                    Tasks tasks = list.get(i);

                    if(tasks !=null )
                    {
                        /*CellView cell=sheet.getColumnView(i);
                        cell.setAutosize(true);
                        sheet.setColumnView(i, cell);*/
                        String client = tasks.getRemarks ();
                        String froms = tasks.getStartDate();
                        String tos = tasks.getEndDate();

                        String fromTime = "";
                        String toTime = "";

                        Date afromDate = null;
                        Date atoDate = null;

                        if(froms!=null&&!froms.isEmpty()){

                            if(froms.contains("T")){

                                String dojs[] = froms.split("T");

                                if(dojs[1].equalsIgnoreCase("00:00:00")){
                                    try {
                                        afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                                        froms = new SimpleDateFormat("MMM dd,yyyy").format(afromDate);
                                        fromTime = "00:00";

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    try {
                                        // afromDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dojs[0]+" "+dojs[1]);
                                        afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                                        Date time = new SimpleDateFormat("HH:mm:ss").parse(dojs[1]);
                                        //froms = new SimpleDateFormat("MMM dd,yyyy HH:mm").format(afromDate);
                                        froms = new SimpleDateFormat("MMM dd,yyyy").format(afromDate);
                                        fromTime = new SimpleDateFormat("HH:mm").format(time);

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }

                        if(tos!=null&&!tos.isEmpty()){

                            if(tos.contains("T")){

                                String dojs[] = tos.split("T");

                                if(dojs[1].equalsIgnoreCase("00:00:00")){
                                    try {
                                        atoDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                                        tos = new SimpleDateFormat("MMM dd,yyyy").format(atoDate);
                                        toTime = "00:00";
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    try {
                               /* atoDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]+" "+dojs[1]);
                                tos = new SimpleDateFormat("MMM dd,yyyy").format(atoDate);*/

                                        atoDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                                        Date time = new SimpleDateFormat("HH:mm:ss").parse(dojs[1]);
                                        //tos = new SimpleDateFormat("MMM dd,yyyy HH:mm").format(afromDate);
                                        tos = new SimpleDateFormat("MMM dd,yyyy").format(atoDate);
                                        toTime = new SimpleDateFormat("HH:mm").format(time);

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }


                                              /*  String parse = new SimpleDateFormat("MMM yyyy").format(atoDate);
                                                toDate = new SimpleDateFormat("MMM yyyy").parse(parse);*/

                            }

                        }




                        if(client!=null&&!client.isEmpty ()){

                            if(client.contains ( "%" )){
                                client = client.replaceAll ( "%"," " );
                            }
                        }
                        sheet.addCell(new Label(0, i+13, froms,cellFormats));
                        sheet.addCell(new Label(1, i+13, tos,cellFormats));
                        sheet.addCell(new Label(2, i+13,client,cellFormats));
                        sheet.addCell(new Label(3, i+13, tasks.getStatus (),cellFormats));
                        sheet.addCell(new Label(4, i+13, tasks.getTaskName (),cellFormats));




                    }
                }


            }

            workbook.write();
            System.out.println("Your file is stored in "+file.toString());
            Toast.makeText( DailyOrdersForEmployeeActivity.this,"Your file is stored in "+file.toString(),Toast.LENGTH_LONG).show();
            return true;
        } catch ( WriteException e) {
            e.printStackTrace();
            return false;
        } catch ( IOException e) {
            e.printStackTrace();
            return false;
        }
        finally {
            if(workbook != null)
            {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


