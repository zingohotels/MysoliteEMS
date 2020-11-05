package app.zingo.mysolite.ui.Common;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import app.zingo.mysolite.adapter.OrgSpinnerAdapter;
import app.zingo.mysolite.Custom.MyRegulerText;
import app.zingo.mysolite.model.EmailData;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.model.Expenses;
import app.zingo.mysolite.model.LiveTracking;
import app.zingo.mysolite.model.LoginDetails;
import app.zingo.mysolite.model.Meetings;
import app.zingo.mysolite.model.Organization;
import app.zingo.mysolite.model.ReportDataEmployee;
import app.zingo.mysolite.model.ReportDataModel;
import app.zingo.mysolite.model.Tasks;
import app.zingo.mysolite.ui.NewAdminDesigns.ReportExpenseList;
import app.zingo.mysolite.ui.NewAdminDesigns.ReportTaskListScreen;
import app.zingo.mysolite.ui.NewAdminDesigns.ReportVisitsListScreen;
import app.zingo.mysolite.utils.Const;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.ThreadExecuter;
import app.zingo.mysolite.utils.Util;
import app.zingo.mysolite.WebApi.EmployeeApi;
import app.zingo.mysolite.WebApi.ExpensesApi;
import app.zingo.mysolite.WebApi.LiveTrackingAPI;
import app.zingo.mysolite.WebApi.LoginDetailsAPI;
import app.zingo.mysolite.WebApi.MeetingsAPI;
import app.zingo.mysolite.WebApi.OrganizationApi;
import app.zingo.mysolite.WebApi.SendEmailAPI;
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

public class InvokeService extends AppCompatActivity {

    MyRegulerText mTotalEmployee,mPresentEmployee,mTotalTask,mCompletedTask,mMeetings,mExpenses;
    LinearLayout mTaskShow,mExpenseList,mShowVisit;
    RecyclerView mEmpList;
    TextInputEditText mEmail;
    Spinner mOrg;
    Button mReport;

    ArrayList<Organization> chainsList;

    ArrayList<Tasks> employeeTasks;
    ArrayList<Expenses> employeeExpense;
    ArrayList<Meetings> employeeMeetings;

    ArrayList<Tasks> completedTasks ;

    String orgName = "";


    ArrayList<Integer> preEmpId;
    ReportDataModel reportDataModel;
    ArrayList<ReportDataEmployee> reportDataEmployees;
    ReportDetailEmployeeAdapter adapter;
    ReportDetailEmployeeAdapter.ViewHolder viewHolder;



    int totalEmp,preEmp=0,totaltask =0,compTask=0,visit=0,expense=0;

    int empCount = 0,companyCount=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_invoke_service);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Report Management");

            mTotalEmployee = findViewById(R.id.total_employee_count);
            mPresentEmployee = findViewById(R.id.present_employee_count_text);
            mTotalTask = findViewById(R.id.total_tasks);
            mCompletedTask = findViewById(R.id.completed_task_count_text);
            mMeetings = findViewById(R.id.meeting_count);
            mExpenses = findViewById(R.id.expense_count_text);
            mTaskShow = findViewById(R.id.task_list_show);
            mExpenseList = findViewById(R.id.report_expense_list);
            mShowVisit = findViewById(R.id.show_visits);

            mEmpList = findViewById(R.id.report_list);
            mReport = findViewById(R.id.generate_report_btn);

            mOrg = findViewById(R.id.organization_spinner);
            mEmail = findViewById(R.id.email);

            preEmpId = new ArrayList<>();

            getCompany();

            mOrg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {



                    int orgId = chainsList.get(position).getOrganizationId();


                    orgName = chainsList.get(position).getOrganizationName();

                    Date date= null;
                    try {
                        date = new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    final Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_YEAR, -1);

                    Toast.makeText( InvokeService.this, ""+new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()), Toast.LENGTH_SHORT).show();
                    getEmployees(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()),orgId,position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            mReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(reportDataModel!=null) {

                        reportDataModel.setPreEmply(mPresentEmployee.getText().toString());
                        reportDataModel.setTotalEmp(mTotalEmployee.getText().toString());
                        reportDataModel.setComplTask(mCompletedTask.getText().toString());
                        reportDataModel.setTotalTas(mTotalTask.getText().toString());
                        reportDataModel.setVisit(mMeetings.getText().toString());
                        reportDataModel.setExpenses(mExpenses.getText().toString());


                        reportDataEmployees = new ArrayList<>();

                        for (int i = 0; i < adapter.getItemCount(); i++) {
                            viewHolder = (ReportDetailEmployeeAdapter.ViewHolder) mEmpList.findViewHolderForAdapterPosition(i);

                            TextView name = viewHolder.mName;
                            TextView login = viewHolder.mLogin;
                            TextView logout = viewHolder.mLogout;
                            TextView hours = viewHolder.mHours;
                            TextView visits = viewHolder.mVisits;
                            TextView tasks = viewHolder.mTasks;
                            TextView expense = viewHolder.mExpenses;
                            TextView expenAmount = viewHolder.mExpAmt;
                            TextView kmss = viewHolder.mKm;

                            ReportDataEmployee rd = new ReportDataEmployee();

                            rd.setName(name.getText().toString());
                            rd.setLoginTime(login.getText().toString());
                            rd.setLogoutTime(logout.getText().toString());
                            rd.setHours(hours.getText().toString());
                            rd.setVisits(visits.getText().toString());
                            rd.setTasks(tasks.getText().toString());
                            rd.setExpenses(expense.getText().toString());
                            rd.setExpensesAmt(expenAmount.getText().toString());
                            rd.setKms(kmss.getText().toString());

                            reportDataEmployees.add(rd);


                        }

                        reportDataModel.setReportDataEmployees(reportDataEmployees);
                        if (reportDataModel != null) {

                            EmailData emailData = new EmailData();
                            final Calendar calendar = Calendar.getInstance();
                            calendar.setTime(new Date());
                            calendar.add(Calendar.DAY_OF_YEAR, -1);

                            String data = "";
                            for (int j = 0; j < reportDataModel.getReportDataEmployees().size(); j++) {


                                data = data + "<tr><td style=\"text-align:center; border: 2px solid green;\">" + reportDataModel.getReportDataEmployees().get(j).getName() + "</td>"
                                        + "<td style=\"text-align:center; border: 2px solid green;\">" + reportDataModel.getReportDataEmployees().get(j).getLoginTime() + "</td>"
                                        + "<td style=\"text-align:center; border: 2px solid green;\">" + reportDataModel.getReportDataEmployees().get(j).getLogoutTime() + "</td>"
                                        + "<td style=\"text-align:center; border: 2px solid green;\">" + reportDataModel.getReportDataEmployees().get(j).getHours() + "</td>"
                                        + "<td style=\"text-align:center; border: 2px solid green;\">" + reportDataModel.getReportDataEmployees().get(j).getVisits() + "</td>"
                                        + "<td style=\"text-align:center; border: 2px solid green;\">" + reportDataModel.getReportDataEmployees().get(j).getTasks() + "</td>"
                                        + "<td style=\"text-align:center; border: 2px solid green;\">" + reportDataModel.getReportDataEmployees().get(j).getExpenses() + "</td>"
                                        + "<td style=\"text-align:center; border: 2px solid green;\">" + reportDataModel.getReportDataEmployees().get(j).getExpensesAmt() + "</td>"
                                        + "<td style=\"text-align:center; border: 2px solid green;\">" + reportDataModel.getReportDataEmployees().get(j).getKms()
                                        + "</td></tr>";
                            }

                            String body = "<!DOCTYPE html> \n" +
                                    "<html> \n" +
                                    "<head>\n" +
                                    "  <meta charset=\"utf-8\">\n" +
                                    "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                                    "  \n" +
                                    "  <link href='https://fonts.googleapis.com/css?family=Roboto' rel='stylesheet'>\n" +
                                    "  <link rel=\"stylesheet\" type=\"text/css\" href=\"css/main.css\" />\n" +
                                    "  <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">\n" +
                                    "  <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">\n" +
                                    "  <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js\"></script>\n" +
                                    "  <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script>\n" +
                                    "  <style text=\"text/css\">\n" +
                                    "body{\n" +
                                    "    font-family: 'Roboto';\n" +
                                    "\tfont-size:15px;\n" +
                                    "\tcolor:#454545;\n" +
                                    "}\n" +
                                    "\n" +
                                    "#form-check{\n" +
                                    "\ttext-align:center;\n" +
                                    // "\tpadding:100px 410px 0px 410px;\n" +
                                    "}\n" +
                                    "\n" +
                                    ".bg-frm{\n" +
                                    "\tbackground-color: #EE596C;\n" +
                                    // "\tpadding:10px 10px 10px 10px;\n" +
                                    "\twidth:100%;\n" +
                                    "}\n" +
                                    "\n" +
                                    "\n" +
                                    "#form-check h5{\n" +
                                    "\tcolor:#fff;\n" +
                                    "\ttext-align:left;\n" +
                                    // "\tpadding:0px 13px;\n" +
                                    "\tfont-size:15px;\n" +
                                    "}\n" +
                                    "\n" +
                                    "#form-check-one{\n" +
                                    // "\tpadding:0px 410px 0px 410px;\n" +
                                    "}\n" +
                                    "\n" +
                                    ".box-check-one{\n" +
                                    "\twidth:100%;\n" +
                                    "\tborder:1px solid gray;\n" +
                                    //  "\tpadding:10px 10px;\n" +
                                    "}\n" +
                                    "\n" +
                                    "#form-check-one form{\n" +
                                    //"\tpadding:10px 0px;\n" +
                                    "}\n" +
                                    " table, th, td { \n" +
                                    //"                border: 2px solid green; \n" +
                                    "                text-align:center; \n" +
                                    "                border-collapse: collapse;\n" +
                                    "                padding: 10px;\n" +
                                /*"                padding-left: 34px;\n" +
                                "                padding-right: 34px;\n" +*/
                                    "              \n" +
                                    "            } \n" +
                                    "            h1 { \n" +
                                    "            color:green; \n" +
                                    "            } \n" +
                                    "  </style>\n" +
                                    "</head>\n" +
                                    "<body>\n" +
                                    "<div id=\"form-check-one\">\n" +
                                    "\t<div class=\"bg-frm\">\n" +
                                    "\t\t<div class=\"row\">\n" +
                                    "\t\t\t<div class=\"col-lg-5 col-md-5 col-sm-5 col-xs-12\">\t\t\t\n" +
                                    "\t\t\t\t  <h2 style=\"color: white\">" + orgName + "</h2>\t \t\t\t\n" +
                                    "\t\t\t</div>\t\n" +
                                    "\t\t\t<div class=\"col-lg-7 col-md-7 col-sm-7 col-xs-12\">\n" +
                                    "\t\t\t\t<h4 style=\"color: white; text-align:left\">Team Activity Report</h4>\n" +
                                    "\t\t\t\t<h4 style=\"color: white; text-align:left\">" + new SimpleDateFormat("dd MMM,yyyy").format(calendar.getTime()) + "</h4>\n" +
                                    "\t\t\t</div>\t\t\n" +
                                    "\t\t</div>\n" +
                                    "\t\t<hr>\n" +
                                    "\t\t<div class=\"row\" style=\" position:center;>\n" +
                                    "\t\t\t<div class=\"col-lg-12 col-md-12 col-sm-12 col-xs-12\">\n" +
                                    "\t\t\t\t<table style=\"background-color: white; margin:0 auto;  color:black; \">\n" +
                                    "\t\t\t\t\t<tr>\n" +
                                    "\t\t\t\t\t\t<th style=\"border: 2px solid green;\">ATTENDANCE</th>\n" +
                                    "\t\t\t\t\t\t<th style=\"border: 2px solid green;\">TASK</th>\n" +
                                    "\t\t\t\t\t\t<th style=\"border: 2px solid green;\">VISIT</th>\n" +
                                    "\t\t\t\t\t\t<th style=\"border: 2px solid green;\">EXPENSES</th>\n" +
                                    "\t\t\t\t\t</tr>\n" +
                                    "\t\t\t\t\t<tr>\n" +
                                    "\t\t\t\t\t\t<td style=\"border: 2px solid green;\">" + reportDataModel.getPreEmply() + "" + reportDataModel.getTotalEmp() + "</td>\n" +
                                    "\t\t\t\t\t\t<td style=\"border: 2px solid green;\">" + reportDataModel.getComplTask() + "" + reportDataModel.getTotalTas() + "</td>\n" +
                                    "\t\t\t\t\t\t<td style=\"border: 2px solid green;\">" + reportDataModel.getVisit() + "</td>\n" +
                                    "\t\t\t\t\t\t<td style=\"border: 2px solid green;\">" + reportDataModel.getExpenses() + "</td>\n" +
                                    "\t\t\t\t\t</tr>\n" +
                                    "\t\t\t\t</table>\n" +
                                    "\t\t\t</div>\n" +
                                    "\t\t</div>\t\t\n" +
                                    "\t\t<br /><div class=\"row\">\n" +
                                    "\t\t\t<div class=\"col-lg-12 col-md-12 col-sm-12 col-xs-12\">\n" +
                                    "\t\t\t\t<table style=\"background-color: white; color:black; margin:0 auto; border: 2px solid green;\">\n" +
                                    "\t\t\t\t\t<tr>\n" +
                                    "\t\t\t\t\t\t<th style=\"border: 2px solid green;\">NAME</th>\n" +
                                    "\t\t\t\t\t\t<th style=\"border: 2px solid green;\">LOGIN</th>\n" +
                                    "\t\t\t\t\t\t<th style=\"border: 2px solid green;\">LOGOUT</th>\n" +
                                    "\t\t\t\t\t\t<th style=\"border: 2px solid green;\">HOURS</th>\n" +
                                    "\t\t\t\t\t\t<th style=\"border: 2px solid green;\">VISITS</th>\n" +
                                    "\t\t\t\t\t\t<th style=\"border: 2px solid green;\">TASKS</th>\n" +
                                    "\t\t\t\t\t\t<th style=\"border: 2px solid green;\">EXPENSES</th>\n" +
                                    "\t\t\t\t\t\t<th style=\"border: 2px solid green;\">EXP AMOUNT</th>\n" +
                                    "\t\t\t\t\t\t<th style=\"border: 2px solid green;\">Km</th>\n" +
                                    "\t\t\t\t\t</tr>\n" +
                                    "\t\t\t\t\t<tr>\n" +
                                    "\t\t\t\t\t\t" + data + "\n" +
                                    "\t\t\t\t</table>\n" +
                                    "\t\t\t</div>\n" +
                                    "\t\t</div>\t\t\n" +
                                    "\t</div>\n" +
                                    "</div>\n" +
                                    "</div>\n</br><p><h3>You can also view and download this report by login to our website <a href=\"www.kronyapp.com/Signin.php\" target = \"_blank\">Click here to login.</a> To View this report in our Mysolite App <a href=\"https://play.google.com/store/apps/details?id=app.zingo.mysolite\" target = \"_blank\">Click here</a>.</h3></p><br><br>" +
                                    "<div class=\"col-md-4\">\n" +
                                    "      <h3>Download Now</h3>\n" +
                                    "      <div class=\"contact-item\">\n" +
                                    "        <a href=\"https://play.google.com/store/apps/details?id=app.zingo.mysolite\" target = \"_blank\"><img src=\"http://zingoapi.azurewebsites.net/HotelImage/g1.jpg\" alt=\"\" style=\"width:205px; height:125px;\"/></a>\n" +
                                    "      </div>\n" +
                                    "    </div>" +
                                    "</body>\n" +
                                    "</html>\n" +
                                    "\n";
                            // emailData.setEmailAddress(mEmail.getText().toString()+",nishar@zingohotels.com");
                            emailData.setEmailAddress("abhinav@kronyapp.com");
                            emailData.setBody(body);
                            emailData.setSubject("Team Report for " + new SimpleDateFormat("dd MMM,yyyy").format(calendar.getTime()));
                            emailData.setUserName("kronyapp@gmail.com");
                            emailData.setPassword("krony@admin@123");
                            emailData.setFromName("Mysolite App");
                            emailData.setFromEmail("kronyapp@gmail.com");


                            if (Util.isNetworkAvailable( InvokeService.this)) {

                                sendEmailAutomatic(emailData);

                            } else {

                                Toast.makeText( InvokeService.this, "Please check your Internet Connection", Toast.LENGTH_LONG).show();

                            }

                        }
                    }



                }
            });


            mTaskShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(employeeTasks!=null&&employeeTasks.size()!=0){
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("EmployeeTask",employeeTasks);
                        Intent tsak = new Intent( InvokeService.this, ReportTaskListScreen.class);
                        tsak.putExtras(bundle);
                        startActivity(tsak);

                    }else{
                        Toast.makeText( InvokeService.this, "No Task available on selected Date", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            mExpenseList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(employeeExpense!=null&&employeeExpense.size()!=0){
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("EmployeeExpense",employeeExpense);
                        Intent tsak = new Intent( InvokeService.this, ReportExpenseList.class);
                        tsak.putExtras(bundle);
                        startActivity(tsak);

                    }else{
                        Toast.makeText( InvokeService.this, "No Expenses available on selected Date", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            mShowVisit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(employeeMeetings!=null&&employeeMeetings.size()!=0){
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("EmployeeMeetings",employeeMeetings);
                        Intent tsak = new Intent( InvokeService.this,ReportVisitsListScreen.class);
                        tsak.putExtras(bundle);
                        startActivity(tsak);

                    }else{
                        Toast.makeText( InvokeService.this, "No Meetings available on selected Date", Toast.LENGTH_SHORT).show();
                    }

                }
            });




        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void getEmployees(final String dateValue,final int orgId,final int position){


        final ProgressDialog progressDialog = new ProgressDialog( InvokeService.this);
        progressDialog.setTitle("Loading Employees");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create(EmployeeApi.class);
                Call<ArrayList<Employee>> call = apiService.getEmployeesByOrgId(orgId);

                call.enqueue(new Callback<ArrayList<Employee>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog!=null)
                                progressDialog.dismiss();
                            ArrayList<Employee> lists = response.body();
                            ArrayList<Employee> list = new ArrayList<>();
                            reportDataModel = new ReportDataModel();

                            employeeTasks = new ArrayList<>();
                            employeeExpense = new ArrayList<>();
                            employeeMeetings = new ArrayList<>();
                            // pendingTasks = new ArrayList<>();
                            completedTasks = new ArrayList<>();
                            /*closedTasks = new ArrayList<>();
                            onTask = new ArrayList<>();*/

                            totalEmp =0;
                            preEmp=0;
                            totaltask=0;
                            compTask=0;
                            visit=0;
                            expense=0;

                            if (lists !=null && lists.size()!=0) {

                                ArrayList<Employee> employees = new ArrayList<>();

                                for(int i=0;i<lists.size();i++){

                                    if(lists.get(i).getUserRoleId()==2){

                                        mEmail.setText(""+lists.get(i).getPrimaryEmailAddress());



                                    }else{
                                        list.add(lists.get(i));
                                    }
                                }


                                if(list!=null&&list.size()!=0){

                                    reportDataEmployees = new ArrayList<>();

                                    mTotalEmployee.setText("/"+list.size());
                                    totalEmp = list.size();

                                    mEmpList.removeAllViews();

                                    adapter = new ReportDetailEmployeeAdapter( InvokeService.this,list,dateValue);
                                    mEmpList.setAdapter(adapter);
                                }


                                //}

                            }else{

                                Toast.makeText( InvokeService.this, "No employees", Toast.LENGTH_SHORT).show();

                                mOrg.setSelection(position+1);

                            }

                        }else {


                            Toast.makeText( InvokeService.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog!=null)
                            progressDialog.dismiss();

                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }


    public class ReportDetailEmployeeAdapter extends RecyclerView.Adapter<ReportDetailEmployeeAdapter.ViewHolder>{

        private Context context;
        private ArrayList<Employee> list;
        private String dateValue;


        public ReportDetailEmployeeAdapter(Context context, ArrayList<Employee> list, String dateValue) {

            this.context = context;
            this.list = list;
            this.dateValue = dateValue;


        }

        @Override
        public ReportDetailEmployeeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_report_details, parent, false);
            ReportDetailEmployeeAdapter.ViewHolder viewHolder = new ReportDetailEmployeeAdapter.ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ReportDetailEmployeeAdapter.ViewHolder holder, final int position) {
            final Employee dto = list.get(position);

            if(dto!=null){

                ReportDataEmployee rd = new ReportDataEmployee();

                Date date=null;
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                holder.mName.setText(""+dto.getEmployeeName());

                LoginDetails ld  = new LoginDetails();
                ld.setEmployeeId(dto.getEmployeeId());
                ld.setLoginDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
                String logDate = new SimpleDateFormat("MMM dd,yyyy").format(date);
                getLoginDetails(ld,holder.mLogin,holder.mLogout,holder.mHours,logDate);


                LiveTracking lv = new LiveTracking();
                lv.setEmployeeId(dto.getEmployeeId());
                lv.setTrackingDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
                BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
                if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Const.BATTERY_PERCENTAGE = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                }
                lv.setBatteryPercentage (String.valueOf ( Const.BATTERY_PERCENTAGE ));
                getLiveLocation(lv,holder.mKm);

                Meetings md  = new Meetings();
                md.setEmployeeId(dto.getEmployeeId());
                md.setMeetingDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
                getMeetingsDetails(md,holder.mVisits);

                getTasks(dto.getEmployeeId(),"Completed",holder.mTasks,dateValue);
                getExpense(dto.getEmployeeId(),holder.mExpenses, holder.mExpAmt,dateValue);
            }

        }




        @Override
        public int getItemCount() {
            return list.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

            public TextView mName,mLogin,mLogout,mHours,mVisits,mTasks,mExpenses,mExpAmt,mKm;

            public ViewHolder(View itemView) {
                super(itemView);
                itemView.setClickable(true);

                mName = itemView.findViewById(R.id.report_name);
                mLogin = itemView.findViewById(R.id.report_login);
                mLogout = itemView.findViewById(R.id.report_logout);
                mHours = itemView.findViewById(R.id.report_hours);
                mVisits = itemView.findViewById(R.id.report_visit);
                mTasks = itemView.findViewById(R.id.report_task);
                mExpenses = itemView.findViewById(R.id.report_expense);
                mExpAmt = itemView.findViewById(R.id.report_expense_amount);
                mKm = itemView.findViewById(R.id.report_km);



            }
        }

        private void getLoginDetails(final LoginDetails loginDetails, final TextView login, final TextView logout, final TextView workingHrs, final String comDate){




            new ThreadExecuter().execute(new Runnable() {
                @Override
                public void run() {
                    LoginDetailsAPI apiService = Util.getClient().create(LoginDetailsAPI.class);
                    Call<ArrayList<LoginDetails>> call = apiService.getLoginByEmployeeIdAndDate(loginDetails);

                    call.enqueue(new Callback<ArrayList<LoginDetails>>() {
                        @Override
                        public void onResponse(Call<ArrayList<LoginDetails>> call, Response<ArrayList<LoginDetails>> response) {
                            int statusCode = response.code();
                            if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                                ArrayList<LoginDetails> list = response.body();

                                if (list !=null && list.size()!=0) {

                                    preEmp = preEmp+1;

                                    mPresentEmployee.setText(""+preEmp);

                                    String loginTime = list.get(0).getLoginTime();
                                    String logoutTime = list.get(list.size()-1).getLogOutTime();

                                    if(loginTime!=null&&!loginTime.isEmpty()){
                                        login.setText(""+loginTime);
                                    }else{
                                        login.setText("");
                                    }

                                    if(logoutTime!=null&&!logoutTime.isEmpty()){
                                        logout.setText(""+logoutTime);
                                    }else{
                                        logout.setText("Working");
                                    }

                                    long diffHrs = 0;

                                    for (LoginDetails lg:list) {

                                        if((loginTime==null||loginTime.isEmpty())&&(lg.getLogOutTime()==null||lg.getLogOutTime().isEmpty())){

                                        }else{
                                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                                            SimpleDateFormat sdfs = new SimpleDateFormat("MMM dd,yyyy");

                                            Date fd=null,td=null;

                                            String logoutT = lg.getLogOutTime();
                                            String loginT = lg.getLoginTime();

                                            if(loginT==null||loginT.isEmpty()){

                                                loginT = comDate +" 00:00 am";
                                            }

                                            if(logoutT==null||logoutT.isEmpty()){

                                                logoutT = comDate  +" "+new SimpleDateFormat("hh:mm a").format(new Date()) ;
                                            }

                                            try {
                                                fd = sdf.parse(""+loginT);
                                                td = sdf.parse(""+logoutT);

                                                long diff = td.getTime() - fd.getTime();
                                                diffHrs = diffHrs+diff;

                                            } catch (ParseException e) {
                                                e.printStackTrace();

                                            }
                                        }

                                    }


                                    long Hours = diffHrs / (60 * 60 * 1000) ;
                                    long Minutes = diffHrs / (60 * 1000);

                                    int minutes = (int) ((diffHrs / (1000*60)) % 60);
                                    int hours   = (int) ((diffHrs / (1000*60*60)) % 24);
                                    int days   = (int) ((diffHrs / (1000*60*60*24)));


                                    DecimalFormat df = new DecimalFormat("00");

                                    workingHrs.setText(String.format("%02d", days)+":"+String.format("%02d", hours) +":"+String.format("%02d", minutes));

                                }else{

                                    login.setText("ABSENT");
                                    login.setTextColor(Color.parseColor("#FF0000"));
                                    logout.setTextColor(Color.parseColor("#FF0000"));
                                    logout.setText("ABSENT");
                                    mPresentEmployee.setText(""+preEmp);

                                    // Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();

                                }

                            }else {



                                //Toast.makeText(DailyTargetsForEmployeeActivity.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ArrayList<LoginDetails>> call, Throwable t) {
                            // Log error here since request failed
                       /* if (progressDialog!=null)
                            progressDialog.dismiss();*/
                            Log.e("TAG", t.toString());
                        }
                    });
                }


            });
        }
        private void getMeetingsDetails(final Meetings loginDetails, final TextView visits){




            new ThreadExecuter().execute(new Runnable() {
                @Override
                public void run() {
                    MeetingsAPI apiService = Util.getClient().create(MeetingsAPI.class);
                    Call<ArrayList<Meetings>> call = apiService.getMeetingsByEmployeeIdAndDate(loginDetails);

                    call.enqueue(new Callback<ArrayList<Meetings>>() {
                        @Override
                        public void onResponse(Call<ArrayList<Meetings>> call, Response<ArrayList<Meetings>> response) {
                            int statusCode = response.code();
                            if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                                ArrayList<Meetings> list = response.body();

                                if (list !=null && list.size()!=0) {

                                    visits.setText(""+list.size());
                                    visit = visit + list.size();
                                    employeeMeetings.addAll(list);
                                    mMeetings.setText(""+visit);

                                }else{
                                    mMeetings.setText(""+visit);

                                    // Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();

                                }

                            }else {



                                //Toast.makeText(DailyTargetsForEmployeeActivity.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ArrayList<Meetings>> call, Throwable t) {
                            // Log error here since request failed
                       /* if (progressDialog!=null)
                            progressDialog.dismiss();*/
                            Log.e("TAG", t.toString());
                        }
                    });
                }


            });
        }
        private void getTasks(final int employeeId,final String status,final  TextView task,final String dateValue){




            new ThreadExecuter().execute(new Runnable() {
                @Override
                public void run() {
                    TasksAPI apiService = Util.getClient().create(TasksAPI.class);
                    Call<ArrayList<Tasks>> call = apiService.getTasksByEmployeeId(employeeId);

                    call.enqueue(new Callback<ArrayList<Tasks>>() {
                        @Override
                        public void onResponse(Call<ArrayList<Tasks>> call, Response<ArrayList<Tasks>> response) {
                            int statusCode = response.code();
                            if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                                ArrayList<Tasks> list = response.body();
                                ArrayList<Tasks> todayTasks = new ArrayList<>();


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
                                             /*   String parse = new SimpleDateFormat("MMM yyyy").format(afromDate);
                                                fromDate = new SimpleDateFormat("MMM yyyy").parse(parse);*/

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
                                              /*  String parse = new SimpleDateFormat("MMM yyyy").format(atoDate);
                                                toDate = new SimpleDateFormat("MMM yyyy").parse(parse);*/

                                            }

                                        }

                                        if(afromDate!=null&&atoDate!=null){



                                            if(date.getTime() >= afromDate.getTime() && date.getTime() <= atoDate.getTime()){

                                                todayTasks.add(task);
                                                employeeTasks.add(task);

                                                if (task.getStatus().equalsIgnoreCase(status)) {

                                                    compTask = compTask+1;
                                                    mCompletedTask.setText(""+compTask);
                                                }else{
                                                    mCompletedTask.setText(""+compTask);
                                                }


                                            }
                                        }






                                    }

                                    if(todayTasks!=null&&todayTasks.size()!=0){

                                        task.setText(""+todayTasks.size());

                                        totaltask = totaltask+1;
                                        mTotalTask.setText("/"+employeeTasks.size());



                                    }else{
                                        mTotalTask.setText("/"+employeeTasks.size());
                                    }



                                }else{

                                    // Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();

                                }

                            }else {



                                //Toast.makeText(DailyTargetsForEmployeeActivity.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ArrayList<Tasks>> call, Throwable t) {
                            // Log error here since request failed
                       /* if (progressDialog!=null)
                            progressDialog.dismiss();*/
                            Log.e("TAG", t.toString());
                        }
                    });
                }


            });
        }
        private void getExpense(final int employeeId,final TextView exp,final  TextView expeAmt,final String dateValue){




            new ThreadExecuter().execute(new Runnable() {
                @Override
                public void run() {
                    ExpensesApi apiService = Util.getClient().create(ExpensesApi.class);
                    Call<ArrayList<Expenses>> call = apiService.getExpenseByEmployeeIdAndOrganizationId(PreferenceHandler.getInstance(context).getCompanyId(),employeeId);

                    call.enqueue(new Callback<ArrayList<Expenses>>() {
                        @Override
                        public void onResponse(Call<ArrayList<Expenses>> call, Response<ArrayList<Expenses>> response) {
                            int statusCode = response.code();
                            if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                                ArrayList<Expenses> list = response.body();
                                ArrayList<Expenses> todayTasks = new ArrayList<>();


                                Date date = new Date();
                                Date adate = new Date();
                                Date edate = new Date();



                                try {
                                    date = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }



                                if (list !=null && list.size()!=0) {


                                    double amt = 0;

                                    for (Expenses task:list) {



                                        String froms = task.getDate();


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
                                             /*   String parse = new SimpleDateFormat("MMM yyyy").format(afromDate);
                                                fromDate = new SimpleDateFormat("MMM yyyy").parse(parse);*/

                                            }

                                        }



                                        if(afromDate!=null){



                                            if(date.getTime() == afromDate.getTime() ){

                                                employeeExpense.add(task);

                                                todayTasks.add(task);
                                                amt = amt+task.getAmount();


                                            }
                                        }






                                    }

                                    if(todayTasks!=null&&todayTasks.size()!=0){



                                        exp.setText(""+todayTasks.size());
                                        expeAmt.setText("Rs "+new DecimalFormat("#.##").format(amt));
                                        expense = expense+todayTasks.size();
                                        mExpenses.setText(""+expense);



                                    }else{
                                        mExpenses.setText(""+expense);


                                    }



                                }else{

                                    // Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();

                                }

                            }else {



                                //Toast.makeText(DailyTargetsForEmployeeActivity.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ArrayList<Expenses>> call, Throwable t) {
                            // Log error here since request failed
                       /* if (progressDialog!=null)
                            progressDialog.dismiss();*/
                            Log.e("TAG", t.toString());
                        }
                    });
                }


            });
        }

        private void getLiveLocation(final LiveTracking lv, final TextView km){


            final ProgressDialog progressDialog = new ProgressDialog( InvokeService.this);
            progressDialog.setTitle("Loading Details..");
            progressDialog.setCancelable(false);
            progressDialog.show();



            new ThreadExecuter().execute(new Runnable() {
                @Override
                public void run() {
                    LiveTrackingAPI apiService = Util.getClient().create(LiveTrackingAPI.class);
                    Call<ArrayList<LiveTracking>> call = apiService.getLiveTrackingByEmployeeIdAndDate(lv);

                    call.enqueue(new Callback<ArrayList<LiveTracking>>() {
                        @Override
                        public void onResponse(Call<ArrayList<LiveTracking>> call, Response<ArrayList<LiveTracking>> response) {
                            int statusCode = response.code();
                            if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                                if (progressDialog!=null)
                                    progressDialog.dismiss();
                                ArrayList<LiveTracking> list = response.body();
                                float distance = 0;


                                if (list !=null && list.size()!=0) {

                                    Collections.sort(list, LiveTracking.compareLiveTrack);


                                    double lati = 0,lngi=0;
                                    double latis = 0,lngis=0;

                                    for(int i=1;i<list.size();i++){

                                        if(list.get(i).getLongitude()!=null||list.get(i).getLatitude()!=null){

                                            double lat = Double.parseDouble(list.get(i).getLatitude());
                                            double lng = Double.parseDouble(list.get(i).getLongitude());



                                            if(lat==0&&lng==0){

                                            }else{

                                                if(list.size()==1){

                                                }else{
                                                    Location locationA = new Location("point A");

                                                    lati = Double.parseDouble(list.get(i-1).getLatitude());
                                                    lngi = Double.parseDouble(list.get(i-1).getLongitude());


                                                    if(lati==0&&lngi==0){

                                                        lati = Double.parseDouble(list.get(i-1).getLatitude());
                                                        lngi = Double.parseDouble(list.get(i-1).getLongitude());


                                                    }else{

                                                        latis = lati;
                                                        lngis = lngi;

                                                        locationA.setLatitude(latis);
                                                        locationA.setLongitude(lngis);
                                                        Location locationB = new Location("point B");

                                                        locationB.setLatitude(Double.parseDouble(list.get(i).getLatitude()));
                                                        locationB.setLongitude(Double.parseDouble(list.get(i).getLongitude()));

                                                        distance = distance+locationA.distanceTo(locationB);
                                                        double kms = distance/1000.0;
                                                        double miles = distance*0.000621371192;
                                                        km.setText(new DecimalFormat("#.##").format(kms)+" Km/"+new DecimalFormat("#.##").format(miles)+" miles");
                                                        reportDataModel.setKmt(""+new DecimalFormat("#.##").format(kms)+" Km/"+new DecimalFormat("#.##").format(miles)+" miles");

                                                    }


                                                }



                                            }



                                        }


                                    }











                                }else{



                                }

                            }else {



                            }
                        }

                        @Override
                        public void onFailure(Call<ArrayList<LiveTracking>> call, Throwable t) {
                            // Log error here since request failed
                            if (progressDialog!=null)
                                progressDialog.dismiss();
                            Log.e("TAG", t.toString());
                        }
                    });
                }


            });
        }
    }

    private boolean generateReport(ReportDataModel list) {
        boolean success = false;
        WritableWorkbook workbook = null;

        try {
            File sd = Environment.getExternalStorageDirectory();
            String csvFile = "TeamActivity"+new SimpleDateFormat("ddMMyy").format(new Date())+".xls";

            File directory = new File(sd.getAbsolutePath()+"/TeamActivity");
            //create directory if not exist
            if (!directory.exists() && !directory.isDirectory()) {
                directory.mkdirs();
            }
            File file = new File(directory, csvFile);
            String sheetName = "TeamActivity_"+new SimpleDateFormat("ddMMyy").format(new Date());//name of sheet

            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));

            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet(sheetName, 0);


            sheet.addCell(new Label(5,0, PreferenceHandler.getInstance( InvokeService.this).getCompanyName()));

            sheet.mergeCells(5,0,10,0);

            sheet.mergeCells(5,1,10,1);
            sheet.addCell(new Label(5,2,"Chart As On "+new SimpleDateFormat("dd/MM/yyyy").format(new Date())));
            sheet.mergeCells(5,2,8,2);


            sheet.addCell(new Label(3,3,"Generated On "+new SimpleDateFormat("dd/MM/yyyy, hh:mm aa").format(new Date())));
            sheet.mergeCells(3,3,6,3);

            sheet.addCell(new Label(7,3,"User : "+ PreferenceHandler.getInstance( InvokeService.this).getUserFullName()));
            sheet.mergeCells(7,3,10,3);

            sheet.setColumnView(0, 20);
            sheet.setColumnView(1, 20);
            sheet.setColumnView(2, 20);
            sheet.setColumnView(3, 25);
            sheet.setColumnView(4, 10);
            sheet.setColumnView(5, 15);
            sheet.setColumnView(6, 20);
            sheet.setColumnView(7, 15);
            sheet.setColumnView(8, 15);
            sheet.setColumnView(9, 15);
            sheet.setColumnView(10, 15);
            sheet.setColumnView(11, 8);
            sheet.setColumnView(12, 15);
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
            cellFormats.setAlignment(Alignment.CENTRE);

            WritableCellFormat cellFormatL = new WritableCellFormat();
            cellFormats.setAlignment(Alignment.LEFT);


            sheet.addCell(new Label(2, 6, "Employee",cellFormats));
            sheet.addCell(new Label(3, 6, "Task",cellFormats));
            sheet.addCell(new Label(4, 6, "Visit",cellFormats));
            sheet.addCell(new Label(5, 6, "Expenses",cellFormats));
            sheet.addCell(new Label(6, 6, "Kms",cellFormats));


            sheet.addCell(new Label(0, 12, "Name",cellFormats));
            sheet.addCell(new Label(1, 12, "Login",cellFormats));
            sheet.addCell(new Label(2, 12, "Logout",cellFormats));
            sheet.addCell(new Label(3, 12, "Hours",cellFormats));
            sheet.addCell(new Label(4, 12, "Visits",cellFormats));
            sheet.addCell(new Label(5, 12, "Tasks",cellFormats));
            sheet.addCell(new Label(6, 12, "Expense",cellFormats));
            sheet.addCell(new Label(7, 12, "Expense Amount",cellFormats));
            sheet.addCell(new Label(8, 12, "Kms",cellFormats));


            if(list != null)
            {
                sheet.addCell(new Label(2, 7, list.getPreEmply()+""+list.getTotalEmp(),cellFormats));
                sheet.addCell(new Label(3, 7, list.getComplTask()+""+list.getTotalTas(),cellFormats));
                sheet.addCell(new Label(4, 7, list.getVisit(),cellFormats));
                sheet.addCell(new Label(5, 7, list.getExpenses(),cellFormats));
                sheet.addCell(new Label(6, 7, list.getKmt(),cellFormats));

                for (int i=0;i<list.getReportDataEmployees().size();i++)
                {
                    ReportDataEmployee rd = list.getReportDataEmployees().get(i);

                    if(rd !=null )
                    {
                        /*CellView cell=sheet.getColumnView(i);
                        cell.setAutosize(true);
                        sheet.setColumnView(i, cell);*/
                        sheet.addCell(new Label(0, i+13, rd.getName(),cellFormats));
                        sheet.addCell(new Label(1, i+13, rd.getLoginTime(),cellFormats));
                        sheet.addCell(new Label(2, i+13, rd.getLogoutTime(),cellFormats));
                        sheet.addCell(new Label(3, i+13, rd.getHours(),cellFormats));
                        sheet.addCell(new Label(4, i+13, rd.getVisits(),cellFormats));
                        sheet.addCell(new Label(5, i+13, rd.getTasks(),cellFormats));
                        sheet.addCell(new Label(6, i+13, rd.getExpenses(),cellFormats));
                        sheet.addCell(new Label(7, i+13, rd.getExpensesAmt(),cellFormats));
                        sheet.addCell(new Label(8, i+13, rd.getKms(),cellFormats));



                    }
                }


            }

            workbook.write();
            System.out.println("Your file is stored in "+file.toString());
            Toast.makeText( InvokeService.this,"Your file is stored in "+file.toString(),Toast.LENGTH_LONG).show();
            return true;
        } catch (WriteException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            InvokeService.this.finish();


            //   openDatePicker();
        } else if (id == R.id.action_calendar) {//   openDatePicker();
        }
        return super.onOptionsItemSelected(item);
    }

/*    public void openDatePicker() {
        // Get Current Date

        final Calendar c = Calendar.getInstance();
        int mYear  = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay   = c.get(Calendar.DAY_OF_MONTH);
        //launch datepicker modal
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        try {
                            Log.d("Date", "DATE SELECTED "+dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year,monthOfYear,dayOfMonth);


                            String date1 = (monthOfYear + 1)  + "/" + (dayOfMonth) + "/" + year;
                            String date2 = year  + "-" +(monthOfYear + 1)+ "-" +  (dayOfMonth);

                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");



                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                            try {
                                Date fdate = simpleDateFormat.parse(date1);

                                *//*LiveTracking lv = new LiveTracking();
            new SimpleDateFormat("yyyy-MM-dd").format(new Date())
                                lv.setTrackingDate(date1);
                                getLiveLocation(lv);*//*

                                getEmployees(date2);


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

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();

    }*/

    public void getCompany() {

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final OrganizationApi subCategoryAPI = Util.getClient().create(OrganizationApi.class);
                Call<ArrayList<Organization>> getProf = subCategoryAPI.getOrganization();
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList<Organization>>() {

                    @Override
                    public void onResponse(Call<ArrayList<Organization>> call, Response<ArrayList<Organization>> response) {

                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {

                            ArrayList<Organization> list = response.body();

                            if(list!=null&&list.size()!=0){

                                chainsList = list;

                                OrgSpinnerAdapter arrayAdapter = new OrgSpinnerAdapter( InvokeService.this, list);
                                mOrg.setAdapter(arrayAdapter);
                            }



                        }else{

                            //Toast.makeText(BasicPlanScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Organization>> call, Throwable t) {

                        // Toast.makeText(BasicPlanScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
    }

    private void sendEmailAutomatic(final EmailData emailData) {




        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {


                SendEmailAPI travellerApi = Util.getClient().create(SendEmailAPI.class);
                Call<String> response = travellerApi.sendEmail(emailData);

                response.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {


                        try{


                            System.out.println(response.code());
                            if(response.code() == 200||response.code() == 201)
                            {
                                if(response.body() != null)
                                {


                                    if(response.body().equalsIgnoreCase("Email Sent Successfully")){

                                        Toast.makeText( InvokeService.this, "Email Sent Successfully", Toast.LENGTH_SHORT).show();
                                    }else{

                                        Toast.makeText( InvokeService.this, ""+response.body(), Toast.LENGTH_SHORT).show();
                                        //Toast.makeText(ContactUsScreen.this, "Something went wrong. So please contact through Call", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }else{
                                // Toast.makeText(ContactUsScreen.this, "Something went wrong due to "+response.code()+". So please contact through Call", Toast.LENGTH_LONG).show();
                            }
                        }catch (Exception e){

                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
            }
        });
    }


}
