package app.zingo.mysolite.ui.NewAdminDesigns;
import android.app.ProgressDialog;

import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import app.zingo.mysolite.Custom.MyEditText;
import app.zingo.mysolite.Custom.MyTextView;
import app.zingo.mysolite.R;
import app.zingo.mysolite.WebApi.EmployeeApi;
import app.zingo.mysolite.model.Designations;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.utils.ProgressBarUtil;
import app.zingo.mysolite.utils.Util;
import app.zingo.mysolite.utils.ValidationConst;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static app.zingo.mysolite.utils.AppUtil.isEmpty;

public class RetailerCreation extends AppCompatActivity {
    MyEditText mName,mPrimaryEmail,mSecondaryEmail, mMobile,mPassword,mConfirm,mGST;//,mDob,mDoj
    MyEditText mAddress;
    NestedScrollView mEmployeeLayout;
    RadioButton mMale,mFemale,mOthers;
    MyTextView mCreate;
    CheckBox mShowPwd;
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    ProgressBarUtil progressBarUtil;
    String PhoneNumber;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        try{
            setContentView ( R.layout.activity_retailer_creation );
            progressBarUtil = new ProgressBarUtil ( RetailerCreation.this );
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
            mAddress = findViewById(R.id.address);
            mMale = findViewById(R.id.founder_male);
            mFemale = findViewById(R.id.founder_female);
            mOthers = findViewById(R.id.founder_other);
            mCreate = findViewById(R.id.createFounder);
            mShowPwd = findViewById(R.id.show_hide_password);

            mCreate.setOnClickListener( view -> {
                try{
                    validate();
                }catch (Exception e){
                    e.printStackTrace();
                }
            } );

            mShowPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged( CompoundButton button, boolean isChecked) {
                    // If it is checked then show password else hide password
                    if (isChecked) {
                        mShowPwd.setText("Hide Password");// change checkbox text
                        mPassword.setInputType( InputType.TYPE_CLASS_TEXT);
                        mPassword.setTransformationMethod( HideReturnsTransformationMethod.getInstance());// show password
                        mConfirm.setInputType(InputType.TYPE_CLASS_TEXT);
                        mConfirm.setTransformationMethod(HideReturnsTransformationMethod.getInstance());// show password
                    } else {
                        mShowPwd.setText("Show Password");// change checkbox text
                        mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        mPassword.setTransformationMethod( PasswordTransformationMethod.getInstance());// hide password
                        mConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
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
        }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(primary).matches()) {
            Toast.makeText(this, "Provide valid Email Address", Toast.LENGTH_SHORT).show();
        }else if(!isEmpty ( mSecondaryEmail)&&! Patterns.EMAIL_ADDRESS.matcher( Objects.requireNonNull ( mSecondaryEmail.getText ( ) ).toString()).matches()){
            Toast.makeText(this, "Provide valid secondry Email Address", Toast.LENGTH_SHORT).show();
        }else if(mPrimaryEmail.getText().toString().equals (mSecondaryEmail.getText().toString())){
            Toast.makeText(this, "Both email address are same", Toast.LENGTH_SHORT).show();
        }
        /*else if(gst.isEmpty()){
           Toast.makeText(this, "GST Number is required", Toast.LENGTH_SHORT).show();
        }*/
        else if(mobile.isEmpty()){
            Toast.makeText(this, "Mobile is required", Toast.LENGTH_SHORT).show();
        }else if( mobile.length ( ) < 10 ){
            Toast.makeText(this, "Provide valid Mobile Number", Toast.LENGTH_SHORT).show();
        }else if(password.isEmpty()){
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
        }else if(confirm.isEmpty()){
            Toast.makeText(this, "Confirm Password is required", Toast.LENGTH_SHORT).show();
        }else if(!password.isEmpty()&&!confirm.isEmpty()&&!password.equals(confirm)){
            Toast.makeText(this, "Confirm password should be same as Password", Toast.LENGTH_SHORT).show();
        }/*else if(!mMale.isChecked()&&!mFemale.isChecked()&&!mOthers.isChecked()){

            Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show();

        }*/else{
            Employee employee = new Employee();
            employee.setEmployeeName(name);

            if ( gst != null && ! gst.isEmpty ( ) ) {
                employee.setBattery ( gst );
            }else{
                employee.setBattery ( "" );
            }

            if(!address.isEmpty()){
                employee.setAddress(address);
            }

            if(mMale.isChecked()){
                employee.setGender("Male");
            }else if(mFemale.isChecked()){
                employee.setGender("Female");
            }else if(mOthers.isChecked()){
                employee.setGender("Others");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            try {
                //Date fdate = sdf.parse(dob);
                String from1 = simpleDateFormat.format(new Date ());
                employee.setDateOfBirth(from1);
                // fdate = sdf.parse(doj);
                from1 = simpleDateFormat.format(new Date());
                employee.setDateOfJoining(from1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            employee.setPrimaryEmailAddress(primary);
            if(address!=null&&!address.isEmpty()){
                employee.setAlternateEmailAddress(secondary);
            }
            employee.setPhoneNumber(mobile);
            employee.setPassword(password);
            employee.setDepartmentId(1);
            employee.setStatus("Active");
            employee.setUserRoleId(8);
            Designations designations = new Designations();
            designations.setDesignationTitle("Retailer");
            designations.setDescription("Retailer");
            employee.setDesignation ( designations );

            checkUserByEmailId(employee);
        }
    }


    private void checkUserByEmailId(final Employee userProfile){
        userProfile.setEmail(userProfile.getPrimaryEmailAddress());
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Please wait..");
        dialog.show();
        EmployeeApi apiService = Util.getClient().create(EmployeeApi.class);
        Call <ArrayList<Employee>> call = apiService.getUserByEmail(userProfile);
        call.enqueue(new Callback <ArrayList<Employee>> () {
            @Override
            public void onResponse(Call<ArrayList<Employee>> call, Response <ArrayList<Employee>> response) {
                int statusCode = response.code();
                if(dialog != null) {
                    dialog.dismiss();
                }
                if(statusCode == 200 || statusCode == 204) {
                    System.out.println ( "Suree Email working" );
                    ArrayList<Employee> responseProfile = response.body();
                    if(responseProfile != null && responseProfile.size()!=0 ) {
                        mPrimaryEmail.setError("Email Exists");
                        Toast.makeText(RetailerCreation.this, "Email already Exists", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        checkUserByPhone(userProfile);
                    }
                }
                else {
                    System.out.println ( "Suree Email not working" );
                    Toast.makeText(RetailerCreation.this,response.message(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {
                if(dialog != null) {
                    dialog.dismiss();
                }
                Log.e("TAG", t.toString());
            }
        });
    }

    private void checkUserByPhone(final Employee userProfile){
        EmployeeApi apiService = Util.getClient().create(EmployeeApi.class);
        Call<ArrayList<Employee>> call = apiService.getUserByPhone(userProfile.getPhoneNumber());
        call.enqueue(new Callback<ArrayList<Employee>>() {
            @Override
            public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                int statusCode = response.code();
                if(statusCode == 200 || statusCode == 204) {
                    System.out.println ( "Suree Phone working" );
                    ArrayList<Employee> responseProfile = response.body();
                    if(responseProfile != null && responseProfile.size()!=0 ) {
                        mMobile.setError("Number Already Exists");
                        Toast.makeText(RetailerCreation.this, "Mobile already Exists", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        try {
                            addEmployee (userProfile);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    System.out.println ( "Suree Phone not working" );
                    Toast.makeText(RetailerCreation.this,response.message(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {
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
        Call<Employee> call = apiService.addEmployee(employee);
        call.enqueue(new Callback<Employee>() {
            @Override
            public void onResponse(Call<Employee> call, Response<Employee> response) {
                try {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {
                        Employee dto = response.body();
                        if(dto!=null){
                           RetailerCreation.this.finish ();
                        }
                    }else {
                        Toast.makeText(RetailerCreation.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText( RetailerCreation.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        int id = item.getItemId();
        if ( id == android.R.id.home ) {
            RetailerCreation.this.finish ( );
        }
        return super.onOptionsItemSelected(item);
    }
}
