package app.zingo.mysolite.ui.Employee;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import app.zingo.mysolite.adapter.EmployeeAdapter;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.utils.NetworkUtil;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.ProgressBarUtil;
import app.zingo.mysolite.utils.Util;
import app.zingo.mysolite.WebApi.EmployeeApi;
import app.zingo.mysolite.R;
import app.zingo.mysolite.utils.ValidationClass;
import app.zingo.mysolite.utils.ValidationConst;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeeListScreen extends ValidationClass {
    private RecyclerView mProfileList;
    private FloatingActionButton mAddProfiles;
    private String type;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBarUtil progressBarUtil;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list_screen);
        Objects.requireNonNull ( getSupportActionBar ( ) ).setHomeButtonEnabled(true);
        Objects.requireNonNull ( getSupportActionBar ( ) ).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull ( getSupportActionBar ( ) ).setTitle("Employee Details");
        initViews();

    }
    @SuppressLint ("RestrictedApi")
    private void initViews ( ) {
        try{
            progressBarUtil = new ProgressBarUtil ( this );
            mProfileList = findViewById(R.id.profile_list);
            mAddProfiles = findViewById(R.id.add_profile);
            mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
            mAddProfiles.setVisibility (View.GONE);
            Bundle bundle = getIntent().getExtras();
            if(bundle!=null){
                type = bundle.getString("Type");
            }

            if ( NetworkUtil.checkInternetConnection ( EmployeeListScreen.this ) ) {
                getProfiles();
            } else {
                noInternetConnection ();
            }
            mAddProfiles.setOnClickListener( view -> {
                startActivity ( new Intent ( EmployeeListScreen.this,CreateEmployeeScreen.class ) );
            });

            mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
            mSwipeRefreshLayout.setColorSchemeResources(R.color.color_primary);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new Handler ().postDelayed( new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(false);
                            if(NetworkUtil.checkInternetConnection ( EmployeeListScreen.this )){
                                getProfiles();
                            }else{
                                noInternetConnection ();
                            }
                        }
                    }, 2000);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(NetworkUtil.checkInternetConnection ( EmployeeListScreen.this )){
            getProfiles();
        }else{
            noInternetConnection ();
        }
    }

    private void getProfiles(){
        progressBarUtil.showProgress ( "Loading..." );
        EmployeeApi apiService = Util.getClient().create( EmployeeApi.class);
        Call<ArrayList<Employee>> call = apiService.getEmployeesByOrgId(PreferenceHandler.getInstance( EmployeeListScreen.this).getCompanyId());
        call.enqueue(new Callback<ArrayList<Employee>>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onResponse( @NonNull Call<ArrayList<Employee>> call, @NonNull Response<ArrayList<Employee>> response) {
                int statusCode = response.code();
                if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {
                    progressBarUtil.hideProgress ();
                    ArrayList<Employee> list = response.body();
                    if (list !=null && list.size()!=0) {
                        ArrayList<Employee> employees = new ArrayList<>();
                        for(int i=0;i<list.size();i++){
                            if(PreferenceHandler.getInstance( EmployeeListScreen.this).getUserRoleUniqueID()==2){
                                if(list.get(i).getEmployeeId()!= PreferenceHandler.getInstance( EmployeeListScreen.this).getUserId()&&list.get(i).getUserRoleId()!=8&&list.get(i).getUserRoleId()!=10){
                                    employees.add(list.get(i));
                                }

                            }else{
                                if(list.get(i).getUserRoleId()!=2&&list.get(i).getUserRoleId()!=8&&list.get(i).getUserRoleId()!=10){
                                    employees.add(list.get(i));
                                }
                            }
                        }

                        if(employees.size()!=0){
                            mProfileList.setVisibility(View.VISIBLE);
                            Collections.sort(employees, Employee.compareEmployee);
                            EmployeeAdapter adapter = new EmployeeAdapter ( EmployeeListScreen.this, employees,type);
                            mProfileList.setAdapter(adapter);
                            if(PreferenceHandler.getInstance( EmployeeListScreen.this).getEmployeeLimit()<=employees.size()){
                                mAddProfiles.setVisibility(View.GONE);
                            }
                        }else{
                            ShowToast ( ValidationConst.NO_EMPLOYEE_ADDED );
                            mProfileList.setVisibility(View.GONE);
                        }

                    }else{
                        ShowToast ( ValidationConst.NO_EMPLOYEE_ADDED );
                        mProfileList.setVisibility(View.GONE);
                    }

                }else {
                    ShowToast ( ValidationConst.FAILES_DUE_TO+statusCode );
                    mProfileList.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Employee>> call, @NonNull Throwable t) {
                // Log error here since request failed
                progressBarUtil.hideProgress ();
                //noInternetConnection ();
                Log.e("TAG", t.toString());
                mProfileList.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if ( id == android.R.id.home ) {
            EmployeeListScreen.this.finish ( );
        }
        return super.onOptionsItemSelected(item);
    }
}
