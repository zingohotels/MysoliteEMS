package app.zingo.mysolite.ui.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import app.zingo.mysolite.adapter.EmployeeDepartmentAdapter;
import app.zingo.mysolite.model.Departments;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.ui.Employee.CreateEmployeeScreen;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.ThreadExecuter;
import app.zingo.mysolite.utils.Util;
import app.zingo.mysolite.WebApi.EmployeeApi;
import app.zingo.mysolite.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepartmentEmployeeListScreen extends AppCompatActivity {

    RecyclerView mProfileList;
    FloatingActionButton mAddProfiles;

    Departments departments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_department_employee_list_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Employee Details");

            mProfileList = findViewById(R.id.profile_list);
            mAddProfiles = findViewById(R.id.add_profile);


            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                departments = (Departments)bundle.getSerializable("Department");
            }

            if(departments!=null){
                getProfiles(departments.getDepartmentId());
            }

            mAddProfiles.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent employee =new Intent( DepartmentEmployeeListScreen.this, CreateEmployeeScreen.class);
                    startActivity(employee);
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

    private void getProfiles(final int deptId){


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Employees");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create(EmployeeApi.class);
                Call<ArrayList<Employee>> call = apiService.getEmployeesByDepId(deptId);

                call.enqueue(new Callback<ArrayList<Employee>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog!=null)
                                progressDialog.dismiss();
                            ArrayList<Employee> list = response.body();


                            if (list !=null && list.size()!=0) {

                                ArrayList<Employee> employees = new ArrayList<>();
                                for(int i=0;i<list.size();i++){

                                    if(list.get(i).getEmployeeId()!= PreferenceHandler.getInstance( DepartmentEmployeeListScreen.this).getUserId()){

                                        employees.add(list.get(i));

                                    }
                                }

                                if(employees!=null&&employees.size()!=0){
                                    Collections.sort(employees, Employee.compareEmployee);
                                    EmployeeDepartmentAdapter adapter = new EmployeeDepartmentAdapter( DepartmentEmployeeListScreen.this, employees);
                                    mProfileList.setAdapter(adapter);
                                }else{
                                    Toast.makeText( DepartmentEmployeeListScreen.this,"No Employees added",Toast.LENGTH_LONG).show();
                                }


                                //}

                            }else{
                                Toast.makeText( DepartmentEmployeeListScreen.this,"No Employees added",Toast.LENGTH_LONG).show();
                            }

                        }else {


                            Toast.makeText( DepartmentEmployeeListScreen.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                DepartmentEmployeeListScreen.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
