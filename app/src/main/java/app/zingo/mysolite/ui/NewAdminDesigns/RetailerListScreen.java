package app.zingo.mysolite.ui.NewAdminDesigns;
import android.app.ProgressDialog;
import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import app.zingo.mysolite.R;
import app.zingo.mysolite.WebApi.EmployeeApi;
import app.zingo.mysolite.adapter.RetailerAdapter;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetailerListScreen extends AppCompatActivity {
    RecyclerView mProfileList;
    FloatingActionButton mAddProfiles;
    LinearLayout mNoEmpl;
    String type;
    int employeeSize = 0;
    int orgId;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        try{
            setContentView ( R.layout.activity_retailer_list_screen );
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Retailer Details");
            mProfileList = findViewById(R.id.profile_list);
            mAddProfiles = findViewById(R.id.add_profile);
            mNoEmpl = findViewById(R.id.noEmployeeUpdate);
            Bundle bun = getIntent().getExtras();
            if(bun!=null){
                orgId = bun.getInt("OrganizationId",0);
            }
            if(orgId!=0){
                getProfiles(orgId);
            }else{
                getProfiles(PreferenceHandler.getInstance( RetailerListScreen.this).getCompanyId());
            }
            //getProfiles();
            mAddProfiles.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(employeeSize>= PreferenceHandler.getInstance( RetailerListScreen.this).getEmployeeLimit()){
                        if(PreferenceHandler.getInstance( RetailerListScreen.this).getAppType().equalsIgnoreCase("Trial")){
                            Intent employee =new Intent( RetailerListScreen.this, RetailerCreation.class);
                            employee.putExtra("BranchId",orgId);
                            startActivity(employee);
                        }else{
                            Intent employee =new Intent( RetailerListScreen.this, RetailerCreation.class);
                            employee.putExtra("BranchId",orgId);
                            startActivity(employee);
                        }

                    }else{
                        Intent employee =new Intent( RetailerListScreen.this, RetailerCreation.class);
                        employee.putExtra("BranchId",orgId);
                        startActivity(employee);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getProfiles(final int id){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Employees");
        progressDialog.setCancelable(false);
        progressDialog.show();
        EmployeeApi apiService = Util.getClient().create( EmployeeApi.class);
        Call <ArrayList< Employee >> call = apiService.getEmployeesByOrgId(id);
        call.enqueue(new Callback <ArrayList<Employee>> () {
            @Override
            public void onResponse(Call<ArrayList<Employee>> call, Response <ArrayList<Employee>> response) {
                int statusCode = response.code();
                if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {
                    if (progressDialog != null&&progressDialog.isShowing())
                        progressDialog.dismiss();
                        ArrayList<Employee> list = response.body();
                    if (list !=null && list.size()!=0) {
                        ArrayList<Employee> employees = new ArrayList <> ();
                        employeeSize = list.size();
                        for(int i=0;i<list.size();i++){
                            if((list.get(i).getUserRoleId()==8)){
                                employees.add(list.get(i));
                            }
                        }
                        if(employees!=null&&employees.size()!=0){
                            mNoEmpl.setVisibility(View.GONE);
                            mProfileList.setVisibility(View.VISIBLE);
                            Collections.sort(employees, Employee.compareEmployee);
                            RetailerAdapter adapter = new RetailerAdapter ( RetailerListScreen.this, employees,type);
                            mProfileList.setAdapter(adapter);
                        }else{
                            Toast.makeText( RetailerListScreen.this,"No Retailers added",Toast.LENGTH_LONG).show();
                            mNoEmpl.setVisibility(View.VISIBLE);
                            mProfileList.setVisibility(View.GONE);
                                   /* Intent employee =new Intent(EmployeeUpdateListScreen.this,CreateEmployeeScreen.class);
                                    startActivity(employee);*/
                        }

                    }else{
                        Toast.makeText( RetailerListScreen.this,"No Retailers added",Toast.LENGTH_LONG).show();
                        mNoEmpl.setVisibility(View.VISIBLE);
                        mProfileList.setVisibility(View.GONE);
                               /* Intent employee =new Intent(EmployeeUpdateListScreen.this,CreateEmployeeScreen.class);
                                startActivity(employee);*/
                    }

                }else {
                    Toast.makeText( RetailerListScreen.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                    mNoEmpl.setVisibility(View.VISIBLE);
                    mProfileList.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {
                if (progressDialog != null&&progressDialog.isShowing())
                    progressDialog.dismiss();
                    Log.e("TAG", t.toString());
                    mNoEmpl.setVisibility(View.VISIBLE);
                    mProfileList.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                RetailerListScreen.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(orgId!=0){
            getProfiles(orgId);
        }else{
            getProfiles( PreferenceHandler.getInstance( RetailerListScreen.this).getCompanyId());
        }
    }
}
