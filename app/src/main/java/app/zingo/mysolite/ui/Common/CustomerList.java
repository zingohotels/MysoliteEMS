package app.zingo.mysolite.ui.Common;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.ArrayList;

import app.zingo.mysolite.WebApi.EmployeeApi;
import app.zingo.mysolite.adapter.CustomerAdapter;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.ThreadExecuter;
import app.zingo.mysolite.utils.Util;
import app.zingo.mysolite.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerList extends AppCompatActivity {

    LinearLayout mNoCustomers;
    RecyclerView mCustomerList;
    FloatingActionButton mAddCustomer;
    ImageView mLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{

            setContentView(R.layout.activity_customer_list);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Customers");

            mNoCustomers = findViewById(R.id.noCustomerFound);
            mCustomerList = findViewById(R.id.customer_list_data);
            mAddCustomer = findViewById(R.id.add_customer_float);
            mLoader = findViewById(R.id.spin_loader);

            Glide.with(this)
                    .load(R.drawable.spin)
                    .into(new GlideDrawableImageViewTarget(mLoader));


            getCustomersEmployee(PreferenceHandler.getInstance( CustomerList.this).getCompanyId());

            mAddCustomer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent branch = new Intent( CustomerList.this, CustomerCreation.class);
                    startActivity(branch);
                }
            });


        }catch(Exception e){
            e.printStackTrace();
        }

    }

    /*public void getCustomers(final int id) {



        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {

                final CustomerAPI orgApi = Util.getClient().create( CustomerAPI.class);
                Call<ArrayList< Customer >> getProf = orgApi.getCustomerByOrganizationId(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList< Customer >>() {

                    @Override
                    public void onResponse( Call<ArrayList< Customer >> call, Response<ArrayList< Customer >> response) {



                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {
                            mLoader.setVisibility(View.GONE);

                            ArrayList< Customer > branches = response.body();

                            if(branches!=null&&branches.size()!=0){

                                mLoader.setVisibility(View.GONE);
                                mNoCustomers.setVisibility(View.GONE);

                                mCustomerList.removeAllViews();

                                CustomerAdapter adapter = new CustomerAdapter( CustomerList.this,branches);
                                mCustomerList.setAdapter(adapter);


                            }


                        }else{

                            mLoader.setVisibility(View.GONE);

                            Toast.makeText( CustomerList.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure( Call<ArrayList< Customer >> call, Throwable t) {

                        mLoader.setVisibility(View.GONE);


                        Toast.makeText( CustomerList.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
    }*/

    public void getCustomersEmployee(final int id) {



        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {

                final EmployeeApi orgApi = Util.getClient().create( EmployeeApi.class);
                Call<ArrayList< Employee >> getProf = orgApi.getEmployeesByOrgId (id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList< Employee >>() {

                    @Override
                    public void onResponse( Call<ArrayList< Employee >> call, Response<ArrayList< Employee >> response) {



                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {
                            mLoader.setVisibility(View.GONE);

                            ArrayList< Employee > branches = response.body();
                            ArrayList< Employee > employeeList = new ArrayList <> (  );

                            if(branches!=null&&branches.size()!=0){

                                for(Employee e:branches){

                                    if(e.getUserRoleId ()==10){
                                        employeeList.add(e);


                                    }

                                }


                                if(employeeList!=null &&employeeList.size ()!=0){
                                    mLoader.setVisibility(View.GONE);
                                    mNoCustomers.setVisibility(View.GONE);

                                    mCustomerList.removeAllViews();

                                    CustomerAdapter adapter = new CustomerAdapter( CustomerList.this,employeeList);
                                    mCustomerList.setAdapter(adapter);
                                }




                            }


                        }else{

                            mLoader.setVisibility(View.GONE);

                            Toast.makeText( CustomerList.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure( Call<ArrayList< Employee >> call, Throwable t) {

                        mLoader.setVisibility(View.GONE);


                        Toast.makeText( CustomerList.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getCustomersEmployee(PreferenceHandler.getInstance( CustomerList.this).getCompanyId());
    }

    public boolean onCreateOptionsMenu( Menu menu) {

        getMenuInflater().inflate(R.menu.task_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            CustomerList.this.finish();

        } else if(id==R.id.action_map){
            Intent chnage = new Intent( CustomerList.this, CustomerMapViewScreen.class);
            startActivity(chnage);
        }
        return super.onOptionsItemSelected(item);
    }
}
