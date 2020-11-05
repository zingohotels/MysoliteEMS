package app.zingo.mysolite.ui.Employee;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import app.zingo.mysolite.adapter.EmployeeAdapter;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.Util;
import app.zingo.mysolite.WebApi.EmployeeApi;
import app.zingo.mysolite.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersListScreen extends AppCompatActivity {

    RecyclerView mProfileList;
    FloatingActionButton mAddProfiles;
    LinearLayout mNoEmpl;

    String type;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_orders_list_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Orders Details");

            mProfileList = findViewById(R.id.profile_list);
            mAddProfiles = findViewById(R.id.add_profile);
            mNoEmpl = findViewById(R.id.noEmployeeUpdate);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){
                type = bundle.getString("Type");
            }
            mAddProfiles.setVisibility(View.GONE);
            /*if(type!=null&&(type.equalsIgnoreCase("Meetings")||type.equalsIgnoreCase("Salary")||type.equalsIgnoreCase("Live"))){

                mAddProfiles.setVisibility(View.GONE);
            }*/

            getProfiles();

            mAddProfiles.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent employee =new Intent( OrdersListScreen.this, CreateEmployeeScreen.class);
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

    private void getProfiles(){


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Orders");
        progressDialog.setCancelable(false);
        progressDialog.show();
        EmployeeApi apiService = Util.getClient().create( EmployeeApi.class);
        Call<ArrayList<Employee>> call = apiService.getEmployeesByOrgId(PreferenceHandler.getInstance( OrdersListScreen.this).getCompanyId());

        call.enqueue(new Callback<ArrayList<Employee>>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                int statusCode = response.code();
                if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                    if (progressDialog != null&&progressDialog.isShowing())
                        progressDialog.dismiss();
                    ArrayList<Employee> list = response.body();


                    if (list !=null && list.size()!=0) {

                        ArrayList<Employee> employees = new ArrayList<>();
                        for(int i=0;i<list.size();i++){

                            if(PreferenceHandler.getInstance( OrdersListScreen.this).getUserRoleUniqueID()==2){

                                if(list.get(i).getEmployeeId()!= PreferenceHandler.getInstance( OrdersListScreen.this).getUserId()){

                                    employees.add(list.get(i));

                                }

                            }else{

                                if(list.get(i).getUserRoleId()!=2){

                                    employees.add(list.get(i));

                                }
                            }


                        }

                        if(employees!=null&&employees.size()!=0){
                            mNoEmpl.setVisibility(View.GONE);
                            mProfileList.setVisibility(View.VISIBLE);
                            Collections.sort(employees, Employee.compareEmployee);
                            EmployeeAdapter adapter = new EmployeeAdapter( OrdersListScreen.this, employees,type);
                            mProfileList.setAdapter(adapter);

                            if(PreferenceHandler.getInstance( OrdersListScreen.this).getEmployeeLimit()<=employees.size()){
                                mAddProfiles.setVisibility(View.GONE);
                            }
                        }else{
                            Toast.makeText( OrdersListScreen.this,"No Orders added",Toast.LENGTH_LONG).show();
                                    /*Intent employee =new Intent(OrdersListScreen.this,CreateEmployeeScreen.class);
                                    startActivity(employee);*/
                            mNoEmpl.setVisibility(View.VISIBLE);
                            mProfileList.setVisibility(View.GONE);
                        }


                        //}

                    }else{
                        Toast.makeText( OrdersListScreen.this,"No Orders added",Toast.LENGTH_LONG).show();
                               /* Intent employee =new Intent(OrdersListScreen.this,CreateEmployeeScreen.class);
                                startActivity(employee);*/
                        mNoEmpl.setVisibility(View.VISIBLE);
                        mProfileList.setVisibility(View.GONE);
                    }

                }else {


                    Toast.makeText( OrdersListScreen.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                    mNoEmpl.setVisibility(View.VISIBLE);
                    mProfileList.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {
                // Log error here since request failed
                if (progressDialog != null&&progressDialog.isShowing())
                    progressDialog.dismiss();
                Log.e("TAG", t.toString());
                mNoEmpl.setVisibility(View.VISIBLE);
                mProfileList.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                OrdersListScreen.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
