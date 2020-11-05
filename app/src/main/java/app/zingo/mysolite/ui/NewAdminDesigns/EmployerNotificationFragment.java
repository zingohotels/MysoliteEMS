package app.zingo.mysolite.ui.NewAdminDesigns;


import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import app.zingo.mysolite.adapter.ExpenseReportAdapter;
import app.zingo.mysolite.adapter.LoginDetailsNotificationAdapter;
import app.zingo.mysolite.adapter.MeetingNotificationAdapter;
import app.zingo.mysolite.adapter.TaskListAdapter;
import app.zingo.mysolite.model.Expenses;
import app.zingo.mysolite.model.LoginDetailsNotificationManagers;
import app.zingo.mysolite.model.MeetingDetailsNotificationManagers;
import app.zingo.mysolite.model.Tasks;
import app.zingo.mysolite.ui.newemployeedesign.EmployeeNewMainScreen;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.Util;
import app.zingo.mysolite.WebApi.ExpensesApi;
import app.zingo.mysolite.WebApi.LoginNotificationAPI;
import app.zingo.mysolite.WebApi.MeetingNotificationAPI;
import app.zingo.mysolite.WebApi.TasksAPI;
import app.zingo.mysolite.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseTooltip;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmployerNotificationFragment extends Fragment {

    final String TAG = "Employer Task Employee";
    View layout;
    LinearLayout mNoNotification,mLoginNotification,mTaskNotification,mMeetingNotificaion,mExpenseNotification;
    TextView mDate,mLoginCount,mTaskCount,mMeetingCount,mExpenseCount;
    ImageView mPrevious,mNext;
    private static LoginDetailsNotificationAdapter mAdapter;
    AdminNewMainScreen mContext;

    SimpleDateFormat dateFormat,formats;
    String layputType;


    //ArrayList
    ArrayList<LoginDetailsNotificationManagers> dateLogins;
    ArrayList<MeetingDetailsNotificationManagers> dateMeetings;
    ArrayList<Tasks> todayTasks;
    ArrayList<Expenses> todayExpenses;

    private RecyclerView mNotificatioinRecyclerView;

    private  String SHOWCASE_ID_ADMIN;


    public EmployerNotificationFragment() {
        // Required empty public constructor
    }

    public static EmployerNotificationFragment getInstance() {
        return new EmployerNotificationFragment();
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        // GAUtil.trackScreen(mContext, "Employer Dashboard");
    }


    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        super.onCreateView(layoutInflater, viewGroup, savedInstanceState);
        try{
            this.layout = layoutInflater.inflate(R.layout.fragment_employer_notification, viewGroup, false);
            mNotificatioinRecyclerView = this.layout.findViewById(R.id.listNotifications);
            mNoNotification = this.layout.findViewById(R.id.noRecordFound);

            SHOWCASE_ID_ADMIN = "ToolsAdminsn"+ PreferenceHandler.getInstance(mContext).getUserId();

            mLoginNotification = this.layout.findViewById(R.id.loginNotiLay);
            mTaskNotification = this.layout.findViewById(R.id.taskNotiLay);
            mMeetingNotificaion = this.layout.findViewById(R.id.meetNotiLay);
            mExpenseNotification = this.layout.findViewById(R.id.expNotiLay);

            mDate = this.layout.findViewById(R.id.presentDate);
            mLoginCount = this.layout.findViewById(R.id.loginCount);
            mTaskCount = this.layout.findViewById(R.id.taskCount);
            mMeetingCount = this.layout.findViewById(R.id.meetingCount);
            mExpenseCount = this.layout.findViewById(R.id.expCount);

            mPrevious = this.layout.findViewById(R.id.previousDay);
            mNext = this.layout.findViewById(R.id.nextDay);

            mNotificatioinRecyclerView.setVisibility(View.VISIBLE);
            mNotificatioinRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            mDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));

            layputType = "login";

            getLoginNotifications(new SimpleDateFormat("MMM dd,yyyy").format(new Date()));
            getMeetingNotifications(new SimpleDateFormat("MMM dd,yyyy").format(new Date()));
            getTasks(PreferenceHandler.getInstance(mContext).getUserId(),new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            getExpense(PreferenceHandler.getInstance(mContext).getUserId(),new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

            if(layputType!=null&&!layputType.isEmpty()){

                if(layputType.equalsIgnoreCase("login")){
                    // getLoginNotifications(new SimpleDateFormat("MMM dd,yyyy").format(date2));
                    showLogin();

                }else if(layputType.equalsIgnoreCase("meeting")){
                    // getMeetingNotifications(new SimpleDateFormat("MMM dd,yyyy").format(date2));
                    showMeeting();

                }else if(layputType.equalsIgnoreCase("task")){
                    showTask();
                    // getTasks(PreferenceHandler.getInstance(mContext).getUserId(),new SimpleDateFormat("yyyy-MM-dd").format(date2));

                }else if(layputType.equalsIgnoreCase("expenses")){
                    showExpense();
                    // getExpense(PreferenceHandler.getInstance(mContext).getUserId(),new SimpleDateFormat("yyyy-MM-dd").format(date2));

                }else{

                    showLogin();
                    //getLoginNotifications(new SimpleDateFormat("MMM dd,yyyy").format(date2));

                }

            }else{

                showLogin();
                // getLoginNotifications(new SimpleDateFormat("MMM dd,yyyy").format(date2));

            }


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

                        getLoginNotifications(new SimpleDateFormat("MMM dd,yyyy").format(date2));
                        getMeetingNotifications(new SimpleDateFormat("MMM dd,yyyy").format(date2));
                        getTasks(PreferenceHandler.getInstance(mContext).getUserId(),new SimpleDateFormat("yyyy-MM-dd").format(date2));
                        getExpense(PreferenceHandler.getInstance(mContext).getUserId(),new SimpleDateFormat("yyyy-MM-dd").format(date2));

                        if(layputType!=null&&!layputType.isEmpty()){

                            if(layputType.equalsIgnoreCase("login")){
                               // getLoginNotifications(new SimpleDateFormat("MMM dd,yyyy").format(date2));
                                showLogin();

                            }else if(layputType.equalsIgnoreCase("meeting")){
                               // getMeetingNotifications(new SimpleDateFormat("MMM dd,yyyy").format(date2));
                                showMeeting();

                            }else if(layputType.equalsIgnoreCase("task")){
                                showTask();
                               // getTasks(PreferenceHandler.getInstance(mContext).getUserId(),new SimpleDateFormat("yyyy-MM-dd").format(date2));

                            }else if(layputType.equalsIgnoreCase("expenses")){
                                showExpense();
                               // getExpense(PreferenceHandler.getInstance(mContext).getUserId(),new SimpleDateFormat("yyyy-MM-dd").format(date2));

                            }else{

                                showLogin();
                                //getLoginNotifications(new SimpleDateFormat("MMM dd,yyyy").format(date2));

                            }

                        }else{

                            showLogin();
                           // getLoginNotifications(new SimpleDateFormat("MMM dd,yyyy").format(date2));

                        }


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
                        getLoginNotifications(new SimpleDateFormat("MMM dd,yyyy").format(date2));
                        getMeetingNotifications(new SimpleDateFormat("MMM dd,yyyy").format(date2));
                        getTasks(PreferenceHandler.getInstance(mContext).getUserId(),new SimpleDateFormat("yyyy-MM-dd").format(date2));
                        getExpense(PreferenceHandler.getInstance(mContext).getUserId(),new SimpleDateFormat("yyyy-MM-dd").format(date2));

                        if(layputType!=null&&!layputType.isEmpty()){

                            if(layputType.equalsIgnoreCase("login")){
                                // getLoginNotifications(new SimpleDateFormat("MMM dd,yyyy").format(date2));
                                showLogin();

                            }else if(layputType.equalsIgnoreCase("meeting")){
                                // getMeetingNotifications(new SimpleDateFormat("MMM dd,yyyy").format(date2));
                                showMeeting();

                            }else if(layputType.equalsIgnoreCase("task")){
                                showTask();
                                // getTasks(PreferenceHandler.getInstance(mContext).getUserId(),new SimpleDateFormat("yyyy-MM-dd").format(date2));

                            }else if(layputType.equalsIgnoreCase("expenses")){
                                showExpense();
                                // getExpense(PreferenceHandler.getInstance(mContext).getUserId(),new SimpleDateFormat("yyyy-MM-dd").format(date2));

                            }else{

                                showLogin();
                                //getLoginNotifications(new SimpleDateFormat("MMM dd,yyyy").format(date2));

                            }

                        }else{

                            showLogin();
                            // getLoginNotifications(new SimpleDateFormat("MMM dd,yyyy").format(date2));

                        }

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


            mLoginNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try{
                        final Date date = dateFormat.parse(mDate.getText().toString());
                        layputType = "login";

                        //getLoginNotifications(new SimpleDateFormat("MMM dd,yyyy").format(date));

                        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP) {
                            presentShowcaseView();
                        }

                        showLogin();


                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });

            mTaskNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try{
                        final Date date = dateFormat.parse(mDate.getText().toString());
                        layputType = "task";
                        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP) {
                            presentShowcaseView();
                        }
                        showTask();

                      //  getTasks(PreferenceHandler.getInstance(mContext).getUserId(),new SimpleDateFormat("yyyy-MM-dd").format(date));



                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });

            mMeetingNotificaion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try{
                        final Date date = dateFormat.parse(mDate.getText().toString());
                        layputType = "meeting";
                        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP) {
                            presentShowcaseView();
                        }
                        showMeeting();

                       // getMeetingNotifications(new SimpleDateFormat("MMM dd,yyyy").format(date));


                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });

            mExpenseNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try{
                        final Date date = dateFormat.parse(mDate.getText().toString());
                        layputType = "expenses";
                        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP) {
                            presentShowcaseView();
                        }
                        showExpense();

                      //  getExpense(PreferenceHandler.getInstance(mContext).getUserId(),new SimpleDateFormat("yyyy-MM-dd").format(date));


                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });




            return this.layout;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    public void openDatePicker(final TextView tv) {
        // Get Current Date

        final Calendar c = Calendar.getInstance();
        int  mYear  = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay   = c.get(Calendar.DAY_OF_MONTH);
        //launch datepicker modal
        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext,
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
                                    getLoginNotifications(new SimpleDateFormat("MMM dd,yyyy").format(fdate));
                                    getMeetingNotifications(new SimpleDateFormat("MMM dd,yyyy").format(fdate));
                                    getTasks(PreferenceHandler.getInstance(mContext).getUserId(),new SimpleDateFormat("yyyy-MM-dd").format(fdate));
                                    getExpense(PreferenceHandler.getInstance(mContext).getUserId(),new SimpleDateFormat("yyyy-MM-dd").format(fdate));

                                    if(layputType!=null&&!layputType.isEmpty()){

                                        if(layputType.equalsIgnoreCase("login")){
                                            // getLoginNotifications(new SimpleDateFormat("MMM dd,yyyy").format(date2));
                                            showLogin();

                                        }else if(layputType.equalsIgnoreCase("meeting")){
                                            // getMeetingNotifications(new SimpleDateFormat("MMM dd,yyyy").format(date2));
                                            showMeeting();

                                        }else if(layputType.equalsIgnoreCase("task")){
                                            showTask();
                                            // getTasks(PreferenceHandler.getInstance(mContext).getUserId(),new SimpleDateFormat("yyyy-MM-dd").format(date2));

                                        }else if(layputType.equalsIgnoreCase("expenses")){
                                            showExpense();
                                            // getExpense(PreferenceHandler.getInstance(mContext).getUserId(),new SimpleDateFormat("yyyy-MM-dd").format(date2));

                                        }else{

                                            showLogin();
                                            //getLoginNotifications(new SimpleDateFormat("MMM dd,yyyy").format(date2));

                                        }

                                    }else{

                                        showLogin();
                                        // getLoginNotifications(new SimpleDateFormat("MMM dd,yyyy").format(date2));

                                    }


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

        if(layputType!=null&&!layputType.isEmpty()){

            if(layputType.equalsIgnoreCase("login")){

                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

            }

        }
        datePickerDialog.show();

    }

    private void getLoginNotifications(final String dateValue){

       /* final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Loading Details");
        progressDialog.setCancelable(false);
        progressDialog.show();
*/
        LoginNotificationAPI apiService = Util.getClient().create(LoginNotificationAPI.class);
        Call<ArrayList<LoginDetailsNotificationManagers>> call = apiService.getNotificationByManagerId(PreferenceHandler.getInstance(mContext).getUserId());

        call.enqueue(new Callback<ArrayList<LoginDetailsNotificationManagers>>() {
            @Override
            public void onResponse(Call<ArrayList<LoginDetailsNotificationManagers>> call, Response<ArrayList<LoginDetailsNotificationManagers>> response) {
                int statusCode = response.code();
                if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                    ArrayList<LoginDetailsNotificationManagers> list = response.body();
                    dateLogins = new ArrayList<>();


                    if (list !=null && list.size()!=0) {

                        Collections.sort(list, LoginDetailsNotificationManagers.compareLoginNM);
                        Collections.reverse(list);

                        Date date = new Date();
                        Date adate = new Date();

                        String currentDateStart = dateValue+" 12:00 AM";
                        String currentDateEnd = dateValue+" 11:59 PM";



                        try {
                            date = new SimpleDateFormat("MMM dd,yyyy hh:mm a").parse(currentDateStart);
                            adate = new SimpleDateFormat("MMM dd,yyyy hh:mm a").parse(currentDateEnd);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        for (LoginDetailsNotificationManagers ln:list) {

                            String froms = ln.getLoginDate();
                            Date loginDate = null;
                            try {
                                loginDate = new SimpleDateFormat("MMM dd,yyyy hh:mm a").parse(froms);

                                if(date!=null&&adate!=null){

                                    if(date.getTime()<=loginDate.getTime()&&adate.getTime()>=loginDate.getTime()){

                                        dateLogins.add(ln);

                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }



                        if(dateLogins!=null&&dateLogins.size()!=0){

                            mLoginCount.setText(""+dateLogins.size());

                        }else{
                            mLoginCount.setText("0");

                        }

                        if(layputType!=null&&!layputType.isEmpty()){

                            if(layputType.equalsIgnoreCase("login")){
                                // getLoginNotifications(new SimpleDateFormat("MMM dd,yyyy").format(date2));
                                showLogin();

                            }

                        }else{

                            showLogin();
                            // getLoginNotifications(new SimpleDateFormat("MMM dd,yyyy").format(date2));

                        }



                    }else{

                        mLoginCount.setText("0");
                             /*   mNoNotification.setVisibility(View.VISIBLE);
                                mNotificatioinRecyclerView.setVisibility(View.GONE);*/

                    }

                }else {
                    mLoginCount.setText("0");
                           /* mNoNotification.setVisibility(View.VISIBLE);
                            mNotificatioinRecyclerView.setVisibility(View.GONE);*/


                    Toast.makeText(mContext, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<LoginDetailsNotificationManagers>> call, Throwable t) {
                // Log error here since request failed

                mLoginCount.setText("0");
                        /*mNoNotification.setVisibility(View.VISIBLE);
                        mNotificatioinRecyclerView.setVisibility(View.GONE);*/
                Log.e("TAG", t.toString());
            }
        });
    }

    private void getMeetingNotifications(final String dateValue){

     /*   final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Loading Details");
        progressDialog.setCancelable(false);
        progressDialog.show();*/
        MeetingNotificationAPI apiService = Util.getClient().create(MeetingNotificationAPI.class);
        Call<ArrayList<MeetingDetailsNotificationManagers>> call = apiService.getMeetingNotificationByManagerId(PreferenceHandler.getInstance(mContext).getUserId());

        call.enqueue(new Callback<ArrayList<MeetingDetailsNotificationManagers>>() {
            @Override
            public void onResponse(Call<ArrayList<MeetingDetailsNotificationManagers>> call, Response<ArrayList<MeetingDetailsNotificationManagers>> response) {
                int statusCode = response.code();
                if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                          /*  if (progressDialog!=null)
                                progressDialog.dismiss();*/
                    ArrayList<MeetingDetailsNotificationManagers> list = response.body();
                    dateMeetings = new ArrayList<>();


                    if (list !=null && list.size()!=0) {



                        Date date = new Date();
                        Date adate = new Date();

                        String currentDateStart = dateValue+" 12:00 AM";
                        String currentDateEnd = dateValue+" 11:59 PM";



                        try {
                            date = new SimpleDateFormat("MMM dd,yyyy hh:mm a").parse(currentDateStart);
                            adate = new SimpleDateFormat("MMM dd,yyyy hh:mm a").parse(currentDateEnd);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        for (MeetingDetailsNotificationManagers ln:list) {

                            String froms = ln.getMeetingDate();
                            Date loginDate = null;
                            try {
                                loginDate = new SimpleDateFormat("MMM dd,yyyy hh:mm a").parse(froms);

                                if(date!=null&&adate!=null){

                                    if(date.getTime()<=loginDate.getTime()&&adate.getTime()>=loginDate.getTime()){

                                        dateMeetings.add(ln);

                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }



                        if(dateMeetings!=null&&dateMeetings.size()!=0){

                            Collections.reverse(dateMeetings);

                            mMeetingCount.setText(""+dateMeetings.size());
                                   /* mNoNotification.setVisibility(View.GONE);
                                    mNotificatioinRecyclerView.setVisibility(View.VISIBLE);
                                    mNotificatioinRecyclerView.removeAllViews();
                                    MeetingNotificationAdapter adapter = new MeetingNotificationAdapter(mContext, dateMeetings);
                                    mNotificatioinRecyclerView.setAdapter(adapter);*/
                        }else{
                            mMeetingCount.setText("0");
                                  /*  mNoNotification.setVisibility(View.VISIBLE);
                                    mNotificatioinRecyclerView.setVisibility(View.GONE);*/
                        }


                    }else{

                        mMeetingCount.setText("0");
                               /* mNoNotification.setVisibility(View.VISIBLE);
                                mNotificatioinRecyclerView.setVisibility(View.GONE);*/

                    }

                }else {
                    mMeetingCount.setText("0");
                            /*mNoNotification.setVisibility(View.VISIBLE);
                            mNotificatioinRecyclerView.setVisibility(View.GONE);*/


                    Toast.makeText(mContext, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<MeetingDetailsNotificationManagers>> call, Throwable t) {
                // Log error here since request failed
                        /*if (progressDialog!=null)
                            progressDialog.dismiss();*/
                mMeetingCount.setText("0");
                        /*mNoNotification.setVisibility(View.VISIBLE);
                        mNotificatioinRecyclerView.setVisibility(View.GONE);*/
                Log.e("TAG", t.toString());
            }
        });
    }

    private void getTasks(final int employeeId,final String dateValue){


       /* final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Loading Details");
        progressDialog.setCancelable(false);
        progressDialog.show();*/

        TasksAPI apiService = Util.getClient().create(TasksAPI.class);
        Call<ArrayList<Tasks>> call = apiService.getTasks();

        call.enqueue(new Callback<ArrayList<Tasks>>() {
            @Override
            public void onResponse(Call<ArrayList<Tasks>> call, Response<ArrayList<Tasks>> response) {
                int statusCode = response.code();

                      /*  if (progressDialog!=null)
                            progressDialog.dismiss();*/
                if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {

                    ArrayList<Tasks> list = response.body();
                    todayTasks = new ArrayList<>();

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


                            if(task.getToReportEmployeeId()==employeeId){

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

                                    if(date.getTime() >= afromDate.getTime() && date.getTime() <= atoDate.getTime()){

                                        todayTasks.add(task);

                                    }
                                }

                            }

                        }

                        if(todayTasks!=null&&todayTasks.size()!=0){


                            mTaskCount.setText(""+todayTasks.size());
                                   /* mNoNotification.setVisibility(View.GONE);
                                    mNotificatioinRecyclerView.setVisibility(View.VISIBLE);
                                    mNotificatioinRecyclerView.removeAllViews();
                                    TaskListAdapter adapter = new TaskListAdapter(mContext, todayTasks);
                                    mNotificatioinRecyclerView.setAdapter(adapter);*/
                        }else{
                            mTaskCount.setText("0");
                                   /* mNoNotification.setVisibility(View.VISIBLE);
                                    mNotificatioinRecyclerView.setVisibility(View.GONE);*/
                        }


                    }else{

                        mTaskCount.setText("0");
                             /*   mNoNotification.setVisibility(View.VISIBLE);
                                mNotificatioinRecyclerView.setVisibility(View.GONE);*/


                    }

                }else {

                    mTaskCount.setText("0");
                       /*     mNoNotification.setVisibility(View.VISIBLE);
                            mNotificatioinRecyclerView.setVisibility(View.GONE);*/

                }
            }

            @Override
            public void onFailure(Call<ArrayList<Tasks>> call, Throwable t) {

                     /*   if (progressDialog!=null)
                            progressDialog.dismiss();*/
                Log.e("TAG", t.toString());

                mTaskCount.setText("0");
                       /* mNoNotification.setVisibility(View.VISIBLE);
                        mNotificatioinRecyclerView.setVisibility(View.GONE);*/
            }
        });
    }
    private void getExpense(final int employeeId,final String dateValue){


    /*    final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Loading Details");
        progressDialog.setCancelable(false);
        progressDialog.show();*/

        ExpensesApi apiService = Util.getClient().create(ExpensesApi.class);
        Call<ArrayList<Expenses>> call = apiService.getExpenseByManagerIdAndOrganizationId(PreferenceHandler.getInstance(mContext).getCompanyId(),employeeId);

        call.enqueue(new Callback<ArrayList<Expenses>>() {
            @Override
            public void onResponse(Call<ArrayList<Expenses>> call, Response<ArrayList<Expenses>> response) {

                        /*if (progressDialog!=null)
                            progressDialog.dismiss();
*/
                int statusCode = response.code();
                if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                    ArrayList<Expenses> list = response.body();
                    todayExpenses = new ArrayList<>();

                    Date date = new Date();
                    Date adate = new Date();
                    Date edate = new Date();

                    try {
                        date = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (list !=null && list.size()!=0) {



                        for (Expenses task:list) {

                            String froms = task.getDate();


                            Date afromDate = null;


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

                            if(afromDate!=null){

                                if(date.getTime() == afromDate.getTime() ){

                                    todayExpenses.add(task);

                                }
                            }

                        }

                        if(todayExpenses!=null&&todayExpenses.size()!=0){


                            mExpenseCount.setText(""+todayExpenses.size());
                                   /* mNoNotification.setVisibility(View.GONE);
                                    mNotificatioinRecyclerView.setVisibility(View.VISIBLE);
                                    mNotificatioinRecyclerView.removeAllViews();
                                    ExpenseReportAdapter adapter = new ExpenseReportAdapter(mContext, todayExpenses);
                                    mNotificatioinRecyclerView.setAdapter(adapter);*/
                        }else{
                            mExpenseCount.setText("0");
                                   /* mNoNotification.setVisibility(View.VISIBLE);
                                    mNotificatioinRecyclerView.setVisibility(View.GONE);*/
                        }



                    }else{
                        mExpenseCount.setText("0");
                               /* mNoNotification.setVisibility(View.VISIBLE);
                                mNotificatioinRecyclerView.setVisibility(View.GONE);*/
                    }

                }else {

                    mExpenseCount.setText("0");
/*                            mNoNotification.setVisibility(View.VISIBLE);
                            mNotificatioinRecyclerView.setVisibility(View.GONE);*/
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Expenses>> call, Throwable t) {
                // Log error here since request failed
                   /*     if (progressDialog!=null)
                            progressDialog.dismiss();*/
                Log.e("TAG", t.toString());
                mExpenseCount.setText("0");
                      /*  mNoNotification.setVisibility(View.VISIBLE);
                        mNotificatioinRecyclerView.setVisibility(View.GONE);*/
            }
        });
    }

    public void showLogin(){

        if(dateLogins!=null&&dateLogins.size()!=0){

            mLoginCount.setText(""+dateLogins.size());
            mNoNotification.setVisibility(View.GONE);
            mNotificatioinRecyclerView.setVisibility(View.VISIBLE);
            mNotificatioinRecyclerView.removeAllViews();
            mAdapter = new LoginDetailsNotificationAdapter(mContext, dateLogins);
            mNotificatioinRecyclerView.setAdapter(mAdapter);
        }else{
            mLoginCount.setText("0");
            mNoNotification.setVisibility(View.VISIBLE);
            mNotificatioinRecyclerView.setVisibility(View.GONE);
        }
    }


    public void showMeeting(){

        if(dateMeetings!=null&&dateMeetings.size()!=0){

            mMeetingCount.setText(""+dateMeetings.size());
            mNoNotification.setVisibility(View.GONE);
            mNotificatioinRecyclerView.setVisibility(View.VISIBLE);
            mNotificatioinRecyclerView.removeAllViews();

            MeetingNotificationAdapter adapter = new MeetingNotificationAdapter(mContext, dateMeetings);
            mNotificatioinRecyclerView.setAdapter(adapter);
        }else{
            mMeetingCount.setText("0");
            mNoNotification.setVisibility(View.VISIBLE);
            mNotificatioinRecyclerView.setVisibility(View.GONE);
        }
    }

    public void showTask(){

        if(todayTasks!=null&&todayTasks.size()!=0){


            mTaskCount.setText(""+todayTasks.size());
            mNoNotification.setVisibility(View.GONE);
            mNotificatioinRecyclerView.setVisibility(View.VISIBLE);
            mNotificatioinRecyclerView.removeAllViews();
            TaskListAdapter adapter = new TaskListAdapter(mContext, todayTasks);
            mNotificatioinRecyclerView.setAdapter(adapter);
        }else{
            mTaskCount.setText("0");
            mNoNotification.setVisibility(View.VISIBLE);
            mNotificatioinRecyclerView.setVisibility(View.GONE);
        }


    }

    public void showExpense(){

        if(todayExpenses!=null&&todayExpenses.size()!=0){


            mExpenseCount.setText(""+todayExpenses.size());
            mNoNotification.setVisibility(View.GONE);
            mNotificatioinRecyclerView.setVisibility(View.VISIBLE);
            mNotificatioinRecyclerView.removeAllViews();
            ExpenseReportAdapter adapter = new ExpenseReportAdapter(mContext, todayExpenses);
            mNotificatioinRecyclerView.setAdapter(adapter);
        }else{
            mExpenseCount.setText("0");
            mNoNotification.setVisibility(View.VISIBLE);
            mNotificatioinRecyclerView.setVisibility(View.GONE);
        }


    }

    void presentShowcaseView() {

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(mContext, SHOWCASE_ID_ADMIN);


        ShowcaseTooltip toolTip1 = ShowcaseTooltip.build(mContext)
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view login details of your employees<br><br>Tap anywhere to continue");


        sequence.addSequenceItem(

                new MaterialShowcaseView.Builder(mContext)
                        .setTarget(mLoginCount)
                        .setToolTip(toolTip1)
                        .withRectangleShape()
                        .setTooltipMargin(30)
                        .setShapePadding(50)
                        .setDismissOnTouch(true)
                        .setMaskColour(getResources().getColor(R.color.tooltip_mask))
                        .build()
        );


        ShowcaseTooltip toolTip2 = ShowcaseTooltip.build(mContext)
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view task list of your employees<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(mContext)
                        .setTarget(mTaskCount)
                        .setToolTip(toolTip2)
                        .setTooltipMargin(20)
                        .setShapePadding(30)
                        .setDismissOnTouch(true)
                        .setMaskColour(getResources().getColor(R.color.tooltip_mask))
                        .build()
        );

        ShowcaseTooltip toolTip3 = ShowcaseTooltip.build(mContext)
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view meetings list of your employees<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(mContext)
                        .setTarget(mMeetingCount)
                        .setToolTip(toolTip3)
                        .setTooltipMargin(20)
                        .setShapePadding(30)
                        .setDismissOnTouch(true)
                        .setMaskColour(getResources().getColor(R.color.tooltip_mask))
                        .build()
        );

        ShowcaseTooltip toolTip4 = ShowcaseTooltip.build(mContext)
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view expense list of your employees<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(mContext)
                        .setTarget(mExpenseCount)
                        .setToolTip(toolTip4)
                        .setTooltipMargin(20)
                        .setShapePadding(30)
                        .setDismissOnTouch(true)
                        .setMaskColour(getResources().getColor(R.color.tooltip_mask))
                        .build()
        );

        ShowcaseTooltip toolTip5 = ShowcaseTooltip.build(mContext)
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to change date and see below details based on selected date<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(mContext)
                        .setTarget(mDate)
                        .setToolTip(toolTip5)
                        .setTooltipMargin(20)
                        .setShapePadding(30)
                        .setDismissOnTouch(true)
                        .setMaskColour(getResources().getColor(R.color.tooltip_mask))
                        .build()
        );




        sequence.start();

    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = ( AdminNewMainScreen )context;
    }
}
