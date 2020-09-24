package app.zingo.mysolite.ui.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;

import app.zingo.mysolite.FireBase.SharedPrefManager;
import app.zingo.mysolite.model.Departments;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.model.Organization;
import app.zingo.mysolite.ui.landing.InternalServerErrorScreen;
import app.zingo.mysolite.ui.NewAdminDesigns.AdminNewMainScreen;
import app.zingo.mysolite.ui.newemployeedesign.EmployeeNewMainScreen;
import app.zingo.mysolite.utils.Constants;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.ThreadExecuter;
import app.zingo.mysolite.utils.Util;
import app.zingo.mysolite.WebApi.DepartmentApi;
import app.zingo.mysolite.WebApi.EmployeeApi;
import app.zingo.mysolite.WebApi.OrganizationApi;
import app.zingo.mysolite.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginScreen extends AppCompatActivity {
     TextInputEditText mEmail,mPassword;
     AppCompatButton mLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_login_screen);

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            Toolbar toolbar = findViewById(R.id.toolbar);

            mEmail = findViewById(R.id.email);
            mPassword = findViewById(R.id.password);

            mLogin = findViewById(R.id.loginAccount);

            String token = SharedPrefManager.getInstance( LoginScreen.this).getDeviceToken();

            System.out.println("Splash Token  = "+token);


            mLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    validate();
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void validate(){

        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        if(email==null||email.isEmpty()){

            Toast.makeText(this, "Please enter Email", Toast.LENGTH_SHORT).show();

        }else if(password==null||password.isEmpty()){

            Toast.makeText(this, "Please enter Password", Toast.LENGTH_SHORT).show();

        }else{

            Employee employee = new Employee();
            employee.setPassword(password);

            employee.setEmail(email);
            loginEmployee(employee);

        }


    }

    private void loginEmployee( final Employee p){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();


        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {


                EmployeeApi apiService = Util.getClient().create( EmployeeApi.class);


                Call<ArrayList<Employee>> call = apiService.getEmployeeforLogin(p);

                call.enqueue(new Callback<ArrayList<Employee>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                        int statusCode = response.code();
                        if (progressDialog != null&&progressDialog.isShowing())
                            progressDialog.dismiss();
                        if (statusCode == 200 || statusCode == 201) {

                            ArrayList<Employee> dto1 = response.body();//-------------------should not be list------------
                            if (dto1!=null && dto1.size()!=0) {
                                Employee dto = dto1.get(0);


                                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( LoginScreen.this);
                                SharedPreferences.Editor spe = sp.edit();
                                spe.putInt( Constants.USER_ID, dto.getEmployeeId());
                                PreferenceHandler.getInstance( LoginScreen.this).setUserId(dto.getEmployeeId());
                                PreferenceHandler.getInstance( LoginScreen.this).setManagerId(dto.getManagerId());
                                PreferenceHandler.getInstance( LoginScreen.this).setUserRoleUniqueID(dto.getUserRoleId());
                                PreferenceHandler.getInstance( LoginScreen.this).setUserName(dto.getEmployeeName());
                                PreferenceHandler.getInstance( LoginScreen.this).setUserEmail(dto.getPrimaryEmailAddress());
                                PreferenceHandler.getInstance( LoginScreen.this).setUserFullName(dto.getEmployeeName());
                                PreferenceHandler.getInstance( LoginScreen.this).setPhoneNumber(dto.getPhoneNumber());
                                spe.putString("FullName", dto.getEmployeeName());
                                spe.putString("Password", dto.getPassword());
                                spe.putString("Email", dto.getPrimaryEmailAddress());
                                spe.putString("PhoneNumber", dto.getPhoneNumber());
                                spe.apply();

                                if(dto.getStatus().contains("Active")){

                                    getDepartment(dto.getDepartmentId(),dto);


                                }else if(dto.getStatus().equalsIgnoreCase("Disabled")){
                                    Toast.makeText( LoginScreen.this, "Your Account is Disabled", Toast.LENGTH_SHORT).show();
                                }else{

                                }



                            }else{
                                if (progressDialog != null&&progressDialog.isShowing())
                                    progressDialog.dismiss();
                                Toast.makeText( LoginScreen.this, "Login credentials are wrong..", Toast.LENGTH_SHORT).show();

                            }
                        }else {
                            if (progressDialog!=null)
                                progressDialog.dismiss();
                            Toast.makeText( LoginScreen.this, "Login failed due to status code:"+statusCode, Toast.LENGTH_SHORT).show();
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

    public void getDepartment(final int id,final Employee dto){

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {

                final DepartmentApi subCategoryAPI = Util.getClient().create( DepartmentApi.class);
                Call<Departments> getProf = subCategoryAPI.getDepartmentById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<Departments>() {

                    @Override
                    public void onResponse(Call<Departments> call, Response<Departments> response) {

                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {
                            System.out.println("Inside api");

                            try {
                                getCompany(response.body().getOrganizationId(),dto);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Intent i = new Intent( LoginScreen.this, InternalServerErrorScreen.class);

                                startActivity(i);
                            }


                        }else{

                            Intent i = new Intent( LoginScreen.this, AdminNewMainScreen.class);
                            i.putExtra("Profile",dto);
                            startActivity(i);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<Departments> call, Throwable t) {

                    }
                });

            }

        });
    }

    public void getCompany(final int id,final Employee dto) {

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {

                final OrganizationApi subCategoryAPI = Util.getClient().create( OrganizationApi.class);
                Call<ArrayList< Organization >> getProf = subCategoryAPI.getOrganizationById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList< Organization >>() {

                    @Override
                    public void onResponse( Call<ArrayList< Organization >> call, Response<ArrayList< Organization >> response) {

                        if (response.code() == 200||response.code() == 201||response.code() == 204&&response.body().size()!=0)
                        {

                            Organization organization = response.body().get(0);
                            System.out.println("Inside api");
                            PreferenceHandler.getInstance( LoginScreen.this).setCompanyId(organization.getOrganizationId());
                            PreferenceHandler.getInstance( LoginScreen.this).setCompanyName(organization.getOrganizationName());

                            if(PreferenceHandler.getInstance( LoginScreen.this).getUserRoleUniqueID()==2){
                                Intent i = new Intent( LoginScreen.this, AdminNewMainScreen.class);
                                //Intent i = new Intent(LoginScreen.this, DashBoardEmployee.class);
                                i.putExtra("Profile",dto);

                                startActivity(i);
                                finish();
                            }else{
                                //Intent i = new Intent(LoginScreen.this, DashBoardAdmin.class);
                                Intent i = new Intent( LoginScreen.this, EmployeeNewMainScreen.class);
                                i.putExtra("Profile",dto);
                                i.putExtra("Organization",organization);
                                startActivity(i);
                                finish();
                            }



                        }else{

                            if(PreferenceHandler.getInstance( LoginScreen.this).getUserRoleUniqueID()==2){
                                Intent i = new Intent( LoginScreen.this, AdminNewMainScreen.class);
                                //Intent i = new Intent(LoginScreen.this, DashBoardEmployee.class);
                                i.putExtra("Profile",dto);
                                startActivity(i);
                                finish();
                            }else{
                                //Intent i = new Intent(LoginScreen.this, DashBoardAdmin.class);
                                Intent i = new Intent( LoginScreen.this, EmployeeNewMainScreen.class);
                                i.putExtra("Profile",dto);
                                startActivity(i);
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onFailure( Call<ArrayList< Organization >> call, Throwable t) {

                    }
                });

            }

        });
    }
}
