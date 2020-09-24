package app.zingo.mysolite.ui.NewAdminDesigns;

import android.app.ProgressDialog;

import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import app.zingo.mysolite.Custom.MyEditText;
import app.zingo.mysolite.Custom.MyTextView;
import app.zingo.mysolite.R;
import app.zingo.mysolite.WebApi.EmployeeApi;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.utils.ProgressBarUtil;
import app.zingo.mysolite.utils.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateRetailer extends AppCompatActivity {

    MyEditText mName,mPrimaryEmail,mSecondaryEmail,
            mMobile,mPassword,mConfirm,mGST;//,mDob,mDoj
    MyEditText mAddress;
    NestedScrollView mEmployeeLayout;
    RadioButton mMale,mFemale,mOthers;
    MyTextView mCreate;


    CheckBox mShowPwd;

    Switch mActive;

    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    ProgressBarUtil progressBarUtil;

    String PhoneNumber;

    Employee employee;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );


        try{

            setContentView ( R.layout.activity_update_retailer );

            progressBarUtil = new ProgressBarUtil ( UpdateRetailer.this );

            Objects.requireNonNull ( getSupportActionBar ( ) ).setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Retailer");
            mName = findViewById(R.id.name);

            mEmployeeLayout = findViewById(R.id.employee_detail_layout);

            mPrimaryEmail = findViewById(R.id.email);
            mSecondaryEmail = findViewById(R.id.semail);
            mMobile = findViewById(R.id.mobile);
            mGST = findViewById(R.id.gst);
            mPassword = findViewById(R.id.password);
            mConfirm = findViewById(R.id.confirmpwd);
            mActive = findViewById(R.id.active_employee);

            mAddress = findViewById(R.id.address);

            mMale = findViewById(R.id.founder_male);
            mFemale = findViewById(R.id.founder_female);
            mOthers = findViewById(R.id.founder_other);

            mCreate = findViewById(R.id.createFounder);

            mShowPwd = findViewById(R.id.show_hide_password);




            Bundle bundle = getIntent ().getExtras ();

            if(bundle!=null){

                employee = (Employee)bundle.getSerializable ( "Employee" );

            }

            if(employee!=null){
              /*
                RadioButton mMale,mFemale,mOthers;*/

                String gender = employee.getGender();
                mName.setText ( ""+employee.getEmployeeName () );
                mPrimaryEmail.setText(""+employee.getPrimaryEmailAddress());
                mSecondaryEmail.setText(""+employee.getAlternateEmailAddress());
                mMobile.setText(""+employee.getPhoneNumber());
                mAddress.setText(""+employee.getAddress());
                mPassword.setText(""+employee.getPassword());
                mConfirm.setText(""+employee.getPassword());
                mGST.setText ( ""+employee.getBattery () );

                if(gender!=null&&!gender.isEmpty()){

                    if(gender.equalsIgnoreCase("Male")){

                        mMale.setChecked(true);

                    }else if(gender.equalsIgnoreCase("Female")){

                        mFemale.setChecked(true);
                    }else {

                        mOthers.setChecked(true);
                    }
                }

                String statusEmp = employee.getStatus();

                if(statusEmp!=null&&statusEmp.equalsIgnoreCase("Active")){

                    mActive.setChecked(true);

                }else{

                    mActive.setChecked(false);
                }

            }




            mCreate.setOnClickListener( view -> {

                try{

                    validate();

                }catch (Exception e){
                    e.printStackTrace();
                }

            } );



            mShowPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged( CompoundButton button,
                                              boolean isChecked) {

                    // If it is checked then show password else hide password
                    if (isChecked) {

                        mShowPwd.setText("Hide Password");// change checkbox text

                        mPassword.setInputType( InputType.TYPE_CLASS_TEXT);
                        mPassword.setTransformationMethod( HideReturnsTransformationMethod.getInstance());// show password

                        mConfirm.setInputType(InputType.TYPE_CLASS_TEXT);
                        mConfirm.setTransformationMethod(HideReturnsTransformationMethod.getInstance());// show password
                    } else {
                        mShowPwd.setText("Show Password");// change checkbox text

                        mPassword.setInputType(InputType.TYPE_CLASS_TEXT
                                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        mPassword.setTransformationMethod( PasswordTransformationMethod.getInstance());// hide password

                        mConfirm.setInputType(InputType.TYPE_CLASS_TEXT
                                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        mConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());// hide password

                    }

                }
            });



        }catch ( Exception e){
            e.printStackTrace ();
        }

    }

    public void validate(){

        String name = mName.getText().toString();
        String gst = mGST.getText ().toString ();
        String primary = mPrimaryEmail.getText().toString();
        String secondary = mSecondaryEmail.getText().toString();
        String mobile = mMobile.getText().toString();
        String password = mPassword.getText().toString();
        String confirm = mConfirm.getText().toString();
        String address = mAddress.getText().toString();

        if(name.isEmpty()){

            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();

        }else if(primary.isEmpty()){

            Toast.makeText(this, "Primary Email is required", Toast.LENGTH_SHORT).show();

        }else if(gst.isEmpty()){

            Toast.makeText(this, "GST Number is required", Toast.LENGTH_SHORT).show();

        }else if(mobile.isEmpty()){

            Toast.makeText(this, "Mobile is required", Toast.LENGTH_SHORT).show();

        }else if(password.isEmpty()){

            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();

        }else if(confirm.isEmpty()){

            Toast.makeText(this, "Confirm Password is required", Toast.LENGTH_SHORT).show();

        }else if(!password.isEmpty()&&!confirm.isEmpty()&&!password.equals(confirm)){

            Toast.makeText(this, "Confirm password should be same as Password", Toast.LENGTH_SHORT).show();
        }else if(!mMale.isChecked()&&!mFemale.isChecked()&&!mOthers.isChecked()){

            Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show();

        }else{




            Employee employees =employee;
            employees.setEmployeeName(name);
            employees.setBattery ( gst );
            if(!address.isEmpty()){
                employees.setAddress(address);

            }

            if(mActive.isChecked()){

                employee.setStatus("Active");
            }else{
                employee.setStatus("Deactive");
            }

            if(mMale.isChecked()){
                employees.setGender("Male");
            }else if(mFemale.isChecked()){

                employees.setGender("Female");
            }else if(mOthers.isChecked()){

                employees.setGender("Others");
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            try {
                //Date fdate = sdf.parse(dob);

                String from1 = simpleDateFormat.format(new Date ());
                employees.setDateOfBirth(from1);

                // fdate = sdf.parse(doj);

                from1 = simpleDateFormat.format(new Date());
                employees.setDateOfJoining(from1);



            } catch (Exception e) {
                e.printStackTrace();
            }

            employees.setPrimaryEmailAddress(primary);
            if(address!=null&&!address.isEmpty()){
                employees.setAlternateEmailAddress(secondary);
            }

            employees.setPhoneNumber(mobile);
            employees.setPassword(password);
            employees.setDepartmentId(1);
            checkUserByEmailId(employees);
        }
    }


    private void checkUserByEmailId(final Employee userProfile){


        userProfile.setEmail(userProfile.getPrimaryEmailAddress());


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Please wait..");
        dialog.show();





        EmployeeApi apiService =
                Util.getClient().create( EmployeeApi.class);

        Call<ArrayList< Employee >> call = apiService.getUserByEmail(userProfile);

        call.enqueue(new Callback<ArrayList< Employee >>() {
            @Override
            public void onResponse( Call<ArrayList< Employee >> call, Response<ArrayList< Employee >> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                int statusCode = response.code();

                if(dialog != null)
                {
                    dialog.dismiss();
                }

                if(statusCode == 200 || statusCode == 204)
                {

                    ArrayList< Employee > responseProfile = response.body();
                    if(responseProfile != null && responseProfile.size()!=0 )
                    {


                        boolean check = false;
                        for ( Employee empl:responseProfile) {

                            if(empl.getEmployeeId()!=userProfile.getEmployeeId()){

                                check = true;
                                break;
                            }

                        }

                        if(check){
                            mPrimaryEmail.setError("Email Exists");
                            Toast.makeText( UpdateRetailer.this, "Email already Exists", Toast.LENGTH_SHORT).show();
                        }else{
                            checkUserByPhone(userProfile);
                        }




                    }
                    else
                    {
                        checkUserByPhone(userProfile);
                    }
                }
                else
                {

                    Toast.makeText( UpdateRetailer.this,response.message(),Toast.LENGTH_SHORT).show();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure( Call<ArrayList< Employee >> call, Throwable t) {
                // Log error here since request failed

                if(dialog != null)
                {
                    dialog.dismiss();
                }

                Log.e("TAG", t.toString());
            }
        });
    }

    private void checkUserByPhone(final Employee userProfile){
        EmployeeApi apiService = Util.getClient().create( EmployeeApi.class);
        Call<ArrayList< Employee >> call = apiService.getUserByPhone(userProfile.getPhoneNumber());
        call.enqueue(new Callback<ArrayList< Employee >>() {
            @Override
            public void onResponse( Call<ArrayList< Employee >> call, Response<ArrayList< Employee >> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                int statusCode = response.code();

                if(statusCode == 200 || statusCode == 204)
                {
                    ArrayList< Employee > responseProfile = response.body();
                    if(responseProfile != null && responseProfile.size()!=0 )
                    {
                        boolean check = false;
                        for ( Employee empl:responseProfile) {

                            if(empl.getEmployeeId()!=userProfile.getEmployeeId()){

                                check = true;
                            }

                        }

                        if(check){
                            mMobile.setError("Number Already Exists");
                            Toast.makeText( UpdateRetailer.this, "Mobile already Exists", Toast.LENGTH_SHORT).show();
                        }else{

                            addEmployee (userProfile);
                        }


                    }
                    else
                    {

                        try {



                            addEmployee (userProfile);
                            //addDesignations(designations,userProfile);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                else
                {

                    Toast.makeText( UpdateRetailer.this,response.message(),Toast.LENGTH_SHORT).show();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure( Call<ArrayList< Employee >> call, Throwable t) {
                // Log error here since request failed

                Log.e("TAG", t.toString());
            }
        });
    }

    public void addEmployee(final Employee employee) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();
        EmployeeApi apiService = Util.getClient().create(EmployeeApi.class);
        Call <Employee> call = apiService.updateEmployee (employee.getEmployeeId (),employee);
        call.enqueue(new Callback<Employee>() {
            @Override
            public void onResponse(Call<Employee> call, Response<Employee> response) {
                try {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201|| statusCode == 204) {
                       UpdateRetailer.this.finish ();
                    }else {
                        Toast.makeText(UpdateRetailer.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex) {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Employee> call, Throwable t) {
                if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText( UpdateRetailer.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        int id = item.getItemId();
        if ( id == android.R.id.home ) {
            UpdateRetailer.this.finish ( );
        }
        return super.onOptionsItemSelected(item);
    }
}
