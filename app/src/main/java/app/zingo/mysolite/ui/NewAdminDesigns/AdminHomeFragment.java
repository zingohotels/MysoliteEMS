package app.zingo.mysolite.ui.NewAdminDesigns;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.ui.Common.AllEmployeeLiveLocation;
import app.zingo.mysolite.ui.Common.ChangePasswordScreen;
import app.zingo.mysolite.ui.Common.CustomerMapViewScreen;
import app.zingo.mysolite.ui.Common.NotificationShowActivity;
import app.zingo.mysolite.ui.Common.ReportManagementScreen;
import app.zingo.mysolite.ui.Employee.EmployeeListScreen;
import app.zingo.mysolite.ui.FAQ.FAQFragment;
import app.zingo.mysolite.ui.Plan.PlanDesignActivity;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.Util;
import app.zingo.mysolite.WebApi.EmployeeApi;
import app.zingo.mysolite.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;
import uk.co.deanwild.materialshowcaseview.ShowcaseTooltip;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminHomeFragment extends Fragment{
    private View layout;
    private LinearLayout attendance,leaveApplications,employees,retailers;
    private LinearLayout departments,liveTracking,tasks,expenses,team,client,meeting,stocks;
    private LinearLayout salary;
    private LinearLayout deptOrg;
    private LinearLayout chngPwd;
    private LinearLayout plans;
    private LinearLayout reports;
    private LinearLayout holiday;
    private LinearLayout settings;
    private LinearLayout faq;
    private LinearLayout orders;
    private LinearLayout weekOff;
    private Employee employee;
    private String SHOWCASE_ID_ADMIN ;
    private AdminNewMainScreen mContext;

    public AdminHomeFragment() {
    }

    public static AdminHomeFragment getInstance() {
        return new AdminHomeFragment ();
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
    }

    @SuppressLint ("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(layoutInflater, viewGroup, savedInstanceState);
        try{
            this.layout = layoutInflater.inflate(R.layout.fragment_admin_home, viewGroup, false);
            getEmployees();
            initViews ();
            viewGroup = this.layout.findViewById(R.id.renewWarning);
            viewGroup.setVisibility(View.GONE);
            TextView qr_OrgId = this.layout.findViewById ( R.id.organization_id );
            SHOWCASE_ID_ADMIN = "ToolsAdminshome"+PreferenceHandler.getInstance( mContext ).getUserId();
            if(PreferenceHandler.getInstance( mContext ).getCompanyName().length()>=4){
                String upToNCharacters = PreferenceHandler.getInstance( mContext ).getCompanyName().substring(0, Math.min(PreferenceHandler.getInstance( mContext ).getCompanyName().length(), 4));
                qr_OrgId.setText(getResources ().getString ( R.string.organization_code )+upToNCharacters+""+PreferenceHandler.getInstance( mContext ).getCompanyId());
            }else{
                qr_OrgId.setText(getResources ().getString ( R.string.organization_code )+PreferenceHandler.getInstance( mContext ).getCompanyName()+""+PreferenceHandler.getInstance( mContext ).getCompanyId());
            }
            return this.layout;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (AdminNewMainScreen)context;
    }

    private void initViews ( ) {
        attendance = this.layout.findViewById ( R.id.attendance );
        leaveApplications = this.layout.findViewById ( R.id.leaveApplications );
        employees = this.layout.findViewById ( R.id.employees );
        retailers = this.layout.findViewById ( R.id.retailers );
        holiday = this.layout.findViewById ( R.id.holiday );
        settings = this.layout.findViewById ( R.id.settings );
        faq = this.layout.findViewById ( R.id.faq_home );
        meeting = this.layout.findViewById ( R.id.meetings );
        stocks = this.layout.findViewById ( R.id.stocks );
        departments = this.layout.findViewById ( R.id.department );
        liveTracking = this.layout.findViewById ( R.id.live_tracking );
        tasks = this.layout.findViewById ( R.id.task_layout );
        client = this.layout.findViewById ( R.id.customer_creation );
        expenses = this.layout.findViewById ( R.id.expenses_mgmt );
        team = this.layout.findViewById ( R.id.team );
        plans = this.layout.findViewById ( R.id.plan_detail );
        weekOff = this.layout.findViewById ( R.id.week_off_home );
        salary = this.layout.findViewById ( R.id.salary );
        deptOrg = this.layout.findViewById ( R.id.department_org );
        chngPwd = this.layout.findViewById ( R.id.change_password );
        reports = this.layout.findViewById ( R.id.report_mgmt );
        LinearLayout calender = this.layout.findViewById ( R.id.admin_calender );
        orders = this.layout.findViewById ( R.id.orders );
        if ( PreferenceHandler.getInstance ( mContext ).getResellerUserId ( ) != 0 ) {
            plans.setVisibility ( View.GONE );
        }
        //App new version available
        //View updatedText = this.layout.findViewById(R.id.updateText);
        calender.setOnClickListener ( v -> {
            Intent intent = new Intent ( mContext , CalenderDashBoardActivity.class );
            startActivity ( intent );
        } );
        meeting.setOnClickListener ( view -> {
            try {
                openMenuViews ( meeting );
            } catch ( Exception e ) {
                e.printStackTrace ( );
            }
        } );
        attendance.setOnClickListener ( view -> openMenuViews ( attendance ) );
        faq.setOnClickListener ( view -> openMenuViews ( faq ) );
        leaveApplications.setOnClickListener ( view -> openMenuViews ( leaveApplications ) );
        employees.setOnClickListener ( view -> openMenuViews ( employees ) );
        retailers.setOnClickListener ( view -> openMenuViews ( retailers ) );
        expenses.setOnClickListener ( view -> openMenuViews ( expenses ) );
        departments.setOnClickListener ( view -> openMenuViews ( departments ) );
        liveTracking.setOnClickListener ( view -> openMenuViews ( liveTracking ) );
        team.setOnClickListener ( view -> openMenuViews ( team ) );
        tasks.setOnClickListener ( view -> openMenuViews ( tasks ) );
        orders.setOnClickListener ( view -> openMenuViews ( orders ) );
        deptOrg.setOnClickListener ( view -> openMenuViews ( deptOrg ) );
        salary.setOnClickListener ( view -> openMenuViews ( salary ) );
        chngPwd.setOnClickListener ( view -> openMenuViews ( chngPwd ) );
        plans.setOnClickListener ( view -> openMenuViews ( plans ) );
        reports.setOnClickListener ( view -> openMenuViews ( reports ) );
        holiday.setOnClickListener ( view -> openMenuViews ( holiday ) );
        weekOff.setOnClickListener ( view -> openMenuViews ( weekOff ) );
        client.setOnClickListener ( view -> openMenuViews ( client ) );
        settings.setOnClickListener ( view -> openMenuViews ( settings ) );
        stocks.setOnClickListener ( view -> openMenuViews ( stocks ) );
    }

    private void openMenuViews ( View view ) {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence( mContext , SHOWCASE_ID_ADMIN);
        sequence.setOnItemShownListener( ( itemView , position ) -> {

        } );

        sequence.setConfig(config);

       /* .setSkipText("SKIP")
                .setDismissText("GOT IT")
                .withRectangleShape(true)
                .build()*/
/*
        ShowcaseTooltip toolTip1 = ShowcaseTooltip.build(mContext)
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view attendance report of your employees<br><br>Tap anywhere to continue");
        sequence.addSequenceItem(attendance, "Click here to view attendance report of your employees", "GOT IT");*/

        ShowcaseTooltip toolTip2 = ShowcaseTooltip.build( mContext )
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view last updated location of your employees and entire route map of your employees also<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder( mContext )
                        .setTarget(liveTracking)
                        .setToolTip(toolTip2)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );

        ShowcaseTooltip toolTip3 = ShowcaseTooltip.build( mContext )
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view all task of your employees based on date and status.<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder( mContext )
                        .setTarget(tasks)
                        .setToolTip(toolTip3)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );

        ShowcaseTooltip toolTip16 = ShowcaseTooltip.build( mContext )
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view all orders of your employees based on date and status.<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder( mContext )
                        .setTarget(orders)
                        .setToolTip(toolTip16)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );


        ShowcaseTooltip toolTip4 = ShowcaseTooltip.build( mContext )
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view all expenses of your employees based on monthly report<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder( mContext )
                        .setTarget(expenses)
                        .setToolTip(toolTip4)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );

        ShowcaseTooltip toolTip5 = ShowcaseTooltip.build( mContext )
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view employees leave dashboard based on monthly dashboard and you can approve/reject also.<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder( mContext )
                        .setTarget(leaveApplications)
                        .setToolTip(toolTip5)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );

        ShowcaseTooltip toolTip6 = ShowcaseTooltip.build( mContext )
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to generate salary slip for your employees and send it also.<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder( mContext )
                        .setTarget(salary)
                        .setToolTip(toolTip6)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );


        ShowcaseTooltip toolTip7 = ShowcaseTooltip.build( mContext )
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view the team members of you.<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder( mContext )
                        .setTarget(team)
                        .setToolTip(toolTip7)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );

        ShowcaseTooltip toolTip8 = ShowcaseTooltip.build( mContext )
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view report of your employees based on date.<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder( mContext )
                        .setTarget(reports)
                        .setToolTip(toolTip8)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );

        ShowcaseTooltip toolTip9 = ShowcaseTooltip.build( mContext )
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view department and create department.You can also view employees based on departments<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder( mContext )
                        .setTarget(departments)
                        .setToolTip(toolTip9)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );

        ShowcaseTooltip toolTip10 = ShowcaseTooltip.build( mContext )
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view the employee list and add/edit employees<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder( mContext )
                        .setTarget(employees)
                        .setToolTip(toolTip10)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );

        ShowcaseTooltip toolTip11 = ShowcaseTooltip.build( mContext )
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to create and view customer<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder( mContext )
                        .setTarget(client)
                        .setToolTip(toolTip11)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );

        ShowcaseTooltip toolTip12 = ShowcaseTooltip.build( mContext )
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to change your password.<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder( mContext )
                        .setTarget(chngPwd)
                        .setToolTip(toolTip12)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );

        ShowcaseTooltip toolTip13 = ShowcaseTooltip.build( mContext )
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view/update/create your organizations,holidays,office timings.<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder( mContext )
                        .setTarget(departments)
                        .setToolTip(toolTip13)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );


        ShowcaseTooltip toolTip14 = ShowcaseTooltip.build( mContext )
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view plan features and subscribe plan<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder( mContext )
                        .setTarget(plans)
                        .setToolTip(toolTip14)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );


        ShowcaseTooltip toolTip15 = ShowcaseTooltip.build( mContext )
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to do logout.<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder( mContext )
                        .setTarget(settings)
                        .setToolTip(toolTip15)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );

        sequence.start();

        if(sequence.hasFired()){
            Intent intent;
            if (view.getId() == R.id.employees) {
                startActivity ( new Intent ( mContext, EmployeeUpdateListScreen.class ) );
            }else if (view.getId() == R.id.retailers) {
                startActivity ( new Intent ( mContext, RetailerListScreen.class ) );
            }else if (view.getId() == R.id.settings) {
                Intent employees = new Intent( mContext , NotificationShowActivity.class);
                Bundle bun = new Bundle();
                bun.putSerializable("Employee",employee);
                employees.putExtras(bun);
                mContext.startActivity(employees);
            } else if (view.getId() == R.id.plan_detail) {
                Intent employee = new Intent( mContext , PlanDesignActivity.class);
                mContext.startActivity(employee);
            } else if (view.getId() == R.id.week_off_home) {
                intent = new Intent( mContext , EmployeeListScreen.class);
                intent.putExtra("Type","WeekOff");
                intent.putExtra("viewId", view.getId());
                mContext.startActivity(intent);
            }else if (view.getId() == R.id.team) {
                Intent employee = new Intent( mContext , TeamMembersList.class);
                mContext.startActivity(employee);
            }else if (view.getId() == R.id.stocks) {
                Intent employee = new Intent( mContext , StockOptionsScreen.class);
                mContext.startActivity(employee);
            }else if (view.getId() == R.id.meetings) {
                intent = new Intent( mContext , ShowAllMeetings.class);
                intent.putExtra("viewId", view.getId());
                intent.putExtra("Type","meetingsList");
                mContext.startActivity(intent);
            } else if (view.getId() == R.id.holiday) {
                startActivity ( new Intent ( mContext,ShiftScreenList.class ) );
            } else if (view.getId() == R.id.faq_home) {
                startActivity ( new Intent ( mContext,FAQFragment.class ) );
            } else if (view.getId() == R.id.attendance) {
                intent = new Intent( mContext , EmployeeListScreen.class);
                intent.putExtra("viewId", view.getId());
                intent.putExtra("Type","attendance");
                mContext.startActivity(intent);
            }else if (view.getId() == R.id.report_mgmt) {
                intent = new Intent( mContext , ReportManagementScreen.class);
                intent.putExtra("viewId", view.getId());
                intent.putExtra("Type","Report");
                mContext.startActivity(intent);
            } else if (view.getId() == R.id.leaveApplications) {
                intent = new Intent( mContext , EmployeeListScreen.class);
                intent.putExtra("Type","Leave");
                intent.putExtra("viewId", view.getId());
                mContext.startActivity(intent);
            }else if (view.getId() == R.id.expenses_mgmt) {
                Intent live = new Intent( mContext , ExpensesListAdmin.class);
                live.putExtra("Type","Expense");
                mContext.startActivity(live);
            }else if (view.getId() == R.id.department) {
                startActivity ( new Intent ( mContext, OrganizationProfileScreen.class ) );
            }else if (view.getId() == R.id.department_org) {
                startActivity ( new Intent ( mContext, DepartmentLilstScreen.class ) );
            }else if (view.getId() == R.id.live_tracking) {
                Intent live = new Intent( mContext , AllEmployeeLiveLocation.class);
                live.putExtra("Type","Live");
                mContext.startActivity(live);
            }else if (view.getId() == R.id.task_layout) {
                Intent task = new Intent( mContext , EmployeeListScreen.class);
                task.putExtra("Type","Task");
                mContext.startActivity(task);
            }else if (view.getId() == R.id.orders) {
                Intent orders = new Intent( mContext , EmployeeListScreen.class);
                orders.putExtra("Type","Orders");
                mContext.startActivity(orders);
            }else if (view.getId() == R.id.salary) {
                Intent salary = new Intent( mContext , EmployeeListScreen.class);
                salary.putExtra("Type","Salary");
                startActivity(salary);
            }else if (view.getId() == R.id.change_password) {
                startActivity ( new Intent ( mContext, ChangePasswordScreen.class ) );
            }else if (view.getId() == R.id.customer_creation) {
                startActivity ( new Intent ( mContext, CustomerMapViewScreen.class ) );
            }
        }
    }

    private void getEmployees(){
        EmployeeApi apiService = Util.getClient().create( EmployeeApi.class);
        Call<ArrayList<Employee>> call = apiService.getProfileById(PreferenceHandler.getInstance( mContext ).getUserId());
        call.enqueue(new Callback<ArrayList<Employee>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Employee>> call, @NonNull Response<ArrayList<Employee>> response) {
                int statusCode = response.code();
                if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {
                    ArrayList<Employee> list = response.body();
                    if (list !=null && list.size()!=0) {
                        employee = list.get(0);
                    }

                }else {
                    Toast.makeText( mContext , "Failed due to : "+statusCode, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Employee>> call, @NonNull Throwable t) {
                Log.e("TAG", t.toString());
            }
        });
    }
}
