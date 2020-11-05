package app.zingo.mysolite.ui.Employee;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import androidx.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import app.zingo.mysolite.adapter.DepartmentSpinnerAdapter;
import app.zingo.mysolite.adapter.ManagerSpinnerAdapter;
import app.zingo.mysolite.adapter.ShiftSpinnerAdapter;
import app.zingo.mysolite.Custom.MyEditText;
import app.zingo.mysolite.model.Departments;
import app.zingo.mysolite.model.Designations;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.model.WorkingDay;
import app.zingo.mysolite.utils.NetworkUtil;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.ProgressBarUtil;
import app.zingo.mysolite.utils.Util;
import app.zingo.mysolite.WebApi.DepartmentApi;
import app.zingo.mysolite.WebApi.EmployeeApi;
import app.zingo.mysolite.WebApi.OrganizationTimingsAPI;
import app.zingo.mysolite.R;
import app.zingo.mysolite.utils.ValidationClass;
import app.zingo.mysolite.utils.ValidationConst;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEmployeeScreen extends ValidationClass {
    private TextInputEditText mName,mPrimaryEmail,mSecondaryEmail, mMobile,mPassword,mConfirm,mDesignation,mSalary,mAddress;
    private MyEditText mDob,mDoj;
    private Spinner mDepartment,mtoReport,mShift;
    private RadioButton mMale,mFemale,mOthers;
    private CheckBox mLocationCondition,mCheckTime;
    private Switch mAdmin;
    private ArrayList<Departments> departmentData;
    private ArrayList<Employee> employees;
    private ArrayList< WorkingDay > workingDays;
    private String current = "";
    private String ddmmyyyy = "DDMMYYYY";
    private Calendar cal = Calendar.getInstance();
    private int orgId;
    private ProgressBarUtil progressBarUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_employee_screen);
        Objects.requireNonNull ( getSupportActionBar ( ) ).setHomeButtonEnabled(true);
        Objects.requireNonNull ( getSupportActionBar ( ) ).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull ( getSupportActionBar ( ) ).setTitle("Create Employee");
        Bundle bun = getIntent().getExtras();
        if(bun!=null){
            orgId = bun.getInt("BranchId",0);
        }

        initViews();
    }
    private void initViews ( ) {
        try{
            progressBarUtil = new ProgressBarUtil ( this );
            mAdmin = findViewById(R.id.admin_switch);
            mName = findViewById(R.id.name);
            mDob = findViewById(R.id.dob);
            mDoj = findViewById(R.id.doj);
            mDesignation = findViewById(R.id.designation);
            mSalary = findViewById(R.id.salary);
            mPrimaryEmail = findViewById(R.id.email);
            mSecondaryEmail = findViewById(R.id.semail);
            mMobile = findViewById(R.id.mobile);
            mPassword = findViewById(R.id.password);
            mConfirm = findViewById(R.id.confirmpwd);
            mDepartment = findViewById(R.id.android_material_design_spinner);
            mtoReport = findViewById(R.id.managers_list);
            mShift = findViewById(R.id.shift_list);
            mAddress = findViewById(R.id.address);
            mLocationCondition = findViewById(R.id.location_condition);
            mCheckTime = findViewById(R.id.time_condition);
            mMale = findViewById(R.id.founder_male);
            mFemale = findViewById(R.id.founder_female);
            mOthers = findViewById(R.id.founder_other);
            TextView mCreate = findViewById ( R.id.createFounder );
            mAdmin.setTextOff(getResources ().getString ( R.string.no ));
            mAdmin.setTextOn(getResources ().getString ( R.string.yes));

            mDob.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @SuppressLint ("DefaultLocale")
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().equals(current)) {
                        String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                        String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                        int cl = clean.length();
                        int sel = cl;
                        for (int i = 2; i <= cl && i < 6; i += 2) {
                            sel++;
                        }
                        //Fix for pressing delete next to a forward slash
                        if (clean.equals(cleanC)) sel--;

                        if (clean.length() < 8){
                            clean = clean + ddmmyyyy.substring(clean.length());
                        }else{
                            //This part makes sure that when we finish entering numbers
                            //the date is correct, fixing it otherwise
                            int day  = Integer.parseInt(clean.substring(0,2));
                            int mon  = Integer.parseInt(clean.substring(2,4));
                            int year = Integer.parseInt(clean.substring(4,8));

                            @SuppressLint ("SimpleDateFormat") String currentYear = new SimpleDateFormat("yyyy").format(new Date());

                            int years = Integer.parseInt(currentYear);

                            mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                            cal.set(Calendar.MONTH, mon-1);
                            year = (year<1900)?1900:(year>years)?years:year;
                            cal.set(Calendar.YEAR, year);
                            // ^ first set year for the line below to work correctly
                            //with leap years - otherwise, date e.g. 29/02/2012
                            //would be automatically corrected to 28/02/2012

                            day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                            clean = String.format("%02d%02d%02d",day, mon, year);

                        }

                        clean = String.format("%s/%s/%s", clean.substring(0, 2),
                                clean.substring(2, 4),
                                clean.substring(4, 8));

                        sel = sel < 0 ? 0 : sel;
                        current = clean;
                        mDob.setText(current);
                        mDob.setSelection(sel < current.length() ? sel : current.length());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            mDoj.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @SuppressLint ("DefaultLocale")
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().equals(current)) {
                        String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                        String cleanC = current.replaceAll("[^\\d.]|\\.", "");
                        int cl = clean.length();
                        int sel = cl;
                        for (int i = 2; i <= cl && i < 6; i += 2) {
                            sel++;
                        }
                        //Fix for pressing delete next to a forward slash
                        if (clean.equals(cleanC)) sel--;
                        if (clean.length() < 8){
                            clean = clean + ddmmyyyy.substring(clean.length());
                        }else{
                            //This part makes sure that when we finish entering numbers
                            //the date is correct, fixing it otherwise
                            int day  = Integer.parseInt(clean.substring(0,2));
                            int mon  = Integer.parseInt(clean.substring(2,4));
                            int year = Integer.parseInt(clean.substring(4,8));
                            @SuppressLint ("SimpleDateFormat") String currentYear = new SimpleDateFormat("yyyy").format(new Date());
                            int years = Integer.parseInt(currentYear);
                            mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                            cal.set(Calendar.MONTH, mon-1);
                            year = (year<1900)?1900:(year>years)?years:year;
                            cal.set(Calendar.YEAR, year);
                            // ^ first set year for the line below to work correctly
                            //with leap years - otherwise, date e.g. 29/02/2012
                            //would be automatically corrected to 28/02/2012

                            day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                            clean = String.format("%02d%02d%02d",day, mon, year);
                        }
                        clean = String.format("%s/%s/%s", clean.substring(0, 2),
                                clean.substring(2, 4),
                                clean.substring(4, 8));

                        sel = sel < 0 ? 0 : sel;
                        current = clean;
                        mDoj.setText(current);
                        mDoj.setSelection(sel < current.length() ? sel : current.length());

                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            mCreate.setOnClickListener( view -> {
                try{
                    if(isEmployeeValidated ( CreateEmployeeScreen.this, mName, mDob ,mDoj,mPrimaryEmail,mSecondaryEmail,mMobile,mDesignation,mSalary,mPassword,mConfirm,mAddress) ) {
                        if ( NetworkUtil.checkInternetConnection ( CreateEmployeeScreen.this ) ) {
                            setEmployeeModel ( );
                        } else {
                            noInternetConnection ();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            } );

            if(mLocationCondition.isChecked()){
                mLocationCondition.setText(getResources ().getString ( R.string.check_in_with_location ));
            }else{
                mLocationCondition.setText(getResources ().getString ( R.string.check_in_location ));
            }

            if(mCheckTime.isChecked()){
                mCheckTime.setText(getResources ().getString ( R.string.check_in_with_time ));
            }else{
                mCheckTime.setText(getResources ().getString ( R.string.check_in_time ));
            }
            mLocationCondition.setOnCheckedChangeListener( ( buttonView , isChecked ) -> {
                if(isChecked){
                    mLocationCondition.setText(getResources ().getString ( R.string.check_in_with_location ));
                }else{
                    mLocationCondition.setText(getResources ().getString ( R.string.check_in_location ));
                }
            } );

            mCheckTime.setOnCheckedChangeListener( ( buttonView , isChecked ) -> {
                if(isChecked){
                    mCheckTime.setText(getResources ().getString ( R.string.check_in_with_time ));
                }else{
                    mCheckTime.setText(getResources ().getString ( R.string.check_in_time ));
                }
            } );
            if(orgId!=0){
                if ( NetworkUtil.checkInternetConnection ( CreateEmployeeScreen.this ) ) {
                    getDepartment(orgId);
                    getmanagerProfile(orgId);
                    getShiftTimings(orgId);
                }else{
                    noInternetConnection ();
                }

            }else{
                if ( NetworkUtil.checkInternetConnection ( CreateEmployeeScreen.this ) ) {
                    getDepartment(PreferenceHandler.getInstance( CreateEmployeeScreen.this).getCompanyId());
                    getmanagerProfile(PreferenceHandler.getInstance( CreateEmployeeScreen.this).getCompanyId());
                    getShiftTimings(PreferenceHandler.getInstance( CreateEmployeeScreen.this).getCompanyId());
                }else{
                    noInternetConnection ();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void setEmployeeModel ( ) {
        Employee employee = new Employee();
        employee.setEmployeeName( Objects.requireNonNull ( mName.getText ( ) ).toString ());
        Objects.requireNonNull ( mAddress.getText ( ) ).toString ( );
        if( ! mAddress.getText().toString().isEmpty() ){
            employee.setAddress(Objects.requireNonNull ( mAddress.getText().toString()));
        }

        if(mLocationCondition.isChecked()){
            employee.setLocationOn(false);
        }else{
            employee.setLocationOn(true);
        }

        if(mCheckTime.isChecked()){
            employee.setDataOn(false);
        }else{
            employee.setDataOn(true);
        }
        if(mMale.isChecked()){
            employee.setGender(getResources ().getString ( R.string.male ));
        }else if(mFemale.isChecked()){
            employee.setGender(getResources ().getString ( R.string.female));
        }else if(mOthers.isChecked()){
            employee.setGender(getResources ().getString ( R.string.other ));
        }
        @SuppressLint ("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        @SuppressLint ("SimpleDateFormat")SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date fdate = sdf.parse( Objects.requireNonNull ( mDob.getText ( ) ).toString ());
            String from1 = null;
            if ( fdate != null ) {
                from1 = simpleDateFormat.format(fdate);
            }
            employee.setDateOfBirth(from1);
            fdate = sdf.parse(Objects.requireNonNull ( mDoj.getText ( ) ).toString ());
            if ( fdate != null ) {
                from1 = simpleDateFormat.format(fdate);
            }
            employee.setDateOfJoining(from1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        employee.setPrimaryEmailAddress(Objects.requireNonNull ( mPrimaryEmail.getText ( ) ).toString ());
        employee.setSalary(Double.parseDouble(Objects.requireNonNull ( mSalary.getText ( ) ).toString ()));
        if(!Objects.requireNonNull ( mSecondaryEmail.getText ( ) ).toString ().isEmpty()){
            employee.setAlternateEmailAddress(Objects.requireNonNull ( mSecondaryEmail.getText ( ) ).toString ());
        }
        employee.setPhoneNumber(Objects.requireNonNull ( mMobile.getText ( ) ).toString ());
        employee.setPassword(Objects.requireNonNull ( mPassword.getText ( ) ).toString ());
        employee.setDepartmentId(departmentData.get(mDepartment.getSelectedItemPosition()).getDepartmentId());
        if(employees!=null&&employees.size()!=0){
            employee.setManagerId(employees.get(mtoReport.getSelectedItemPosition()).getEmployeeId());
        }else{
            employee.setManagerId(0);
        }
        if(workingDays!=null&&workingDays.size()!=0){
            employee.setDeviceModel(""+workingDays.get(mShift.getSelectedItemPosition()).getOrganizationTimingId());
        }else{
            employee.setDeviceModel(""+0);
        }
        employee.setStatus("Active");
        if(mAdmin.isChecked()){
            employee.setUserRoleId(9);
        }else{
            employee.setUserRoleId(1);
        }
        Designations designations = new Designations ();
        designations.setDesignationTitle(Objects.requireNonNull ( mDesignation.getText ( ) ).toString ());
        designations.setDescription(Objects.requireNonNull ( mDesignation.getText ( ) ).toString ());
        checkUserByEmailId(employee,designations);
    }

    public void addEmployee(final Employee employee) {
        progressBarUtil.showProgress ( "Loading..." );
        EmployeeApi apiService = Util.getClient().create( EmployeeApi.class);
        Call<Employee> call = apiService.addEmployee(employee);
        call.enqueue(new Callback<Employee>() {
            @Override
            public void onResponse( @NonNull Call<Employee> call, @NonNull  Response<Employee> response) {
                try {
                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {
                        progressBarUtil.hideProgress ();
                        Employee s = response.body();
                        if(s!=null){
                            ShowToast ( ValidationConst.EMPLOYEE_ADDED_SUCCESSFULLY );
                            CreateEmployeeScreen.this.finish();
                        }
                    }else {
                        ShowToast ( ValidationConst.FAILES_DUE_TO+statusCode );
                        progressBarUtil.hideProgress ();
                    }
                }
                catch (Exception ex) {
                    progressBarUtil.hideProgress ();
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Employee> call, @NonNull Throwable t) {
                progressBarUtil.hideProgress ();
                noInternetConnection ();
                Log.e("TAG", t.toString());
            }
        });
    }

    private void checkUserByEmailId(final Employee userProfile, final Designations designations){
        userProfile.setEmail(userProfile.getPrimaryEmailAddress());
        EmployeeApi apiService = Util.getClient().create( EmployeeApi.class);
        Call<ArrayList<Employee>> call = apiService.getUserByEmail(userProfile);
        call.enqueue(new Callback<ArrayList<Employee>>() {
            @Override
            public void onResponse( @NonNull Call<ArrayList<Employee>> call,  @NonNull Response<ArrayList<Employee>> response) {
                int statusCode = response.code();
                if(statusCode == 200 || statusCode == 204) {
                    ArrayList<Employee> responseProfile = response.body();
                    if(responseProfile != null && responseProfile.size()!=0 ) {
                        mPrimaryEmail.setError("Email Exists");
                        ShowToast ( ValidationConst.EMAIL_ALREADY_EXIST );
                    }
                    else {
                        checkUserByPhone(userProfile,designations);
                    }
                }
                else {
                    ShowToast ( ValidationConst.FAILES_DUE_TO+statusCode );
                    progressBarUtil.hideProgress ();
                }
            }

            @Override
            public void onFailure( @NonNull Call<ArrayList<Employee>> call, @NonNull  Throwable t) {
                progressBarUtil.hideProgress ();
                noInternetConnection ();
                Log.e("TAG", t.toString());
            }
        });
    }

    private void checkUserByPhone(final Employee userProfile, final Designations designations){
        EmployeeApi apiService = Util.getClient().create( EmployeeApi.class);
        Call<ArrayList<Employee>> call = apiService.getUserByPhone(userProfile.getPhoneNumber());
        call.enqueue(new Callback<ArrayList<Employee>>() {
            @Override
            public void onResponse( @NonNull Call<ArrayList<Employee>> call,  @NonNull Response<ArrayList<Employee>> response) {
                int statusCode = response.code();
                if(statusCode == 200 || statusCode == 204) {
                    ArrayList<Employee> responseProfile = response.body();
                    if(responseProfile != null && responseProfile.size()!=0 ) {
                        mMobile.setError("Number Already Exists");
                        ShowToast ( ValidationConst.MOBILE_ALREADY_EXIST );
                    }
                    else {
                        try {
                            userProfile.setDesignation(designations);
                            addEmployee(userProfile);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    ShowToast ( ValidationConst.FAILES_DUE_TO+statusCode );
                }
            }

            @Override
            public void onFailure( @NonNull Call<ArrayList<Employee>> call,  @NonNull Throwable t) {
                noInternetConnection ();
                Log.e("TAG", t.toString());
            }
        });
    }

    private void getDepartment(final int id){
        DepartmentApi apiService = Util.getClient().create( DepartmentApi.class);
        Call<ArrayList<Departments>> call = apiService.getDepartmentByOrganization(id);
        call.enqueue(new Callback<ArrayList<Departments>>() {
            @Override
            public void onResponse( @NonNull Call<ArrayList<Departments>> call,  @NonNull Response<ArrayList<Departments>> response) {
                int statusCode = response.code();
                if(statusCode == 200 || statusCode == 204) {
                    ArrayList<Departments> departmentsList = response.body();
                    if(departmentsList != null && departmentsList.size()!=0 ) {
                        departmentData = new ArrayList<>();
                        for(int i=0;i<departmentsList.size();i++){
                            if(!departmentsList.get(i).getDepartmentName().equalsIgnoreCase("Founders")){
                                departmentData.add(departmentsList.get(i));
                            }
                        }

                        if(departmentData!=null&&departmentData.size()!=0){
                            DepartmentSpinnerAdapter arrayAdapter = new DepartmentSpinnerAdapter ( CreateEmployeeScreen.this, departmentData);
                            mDepartment.setAdapter(arrayAdapter);
                        }
                    }
                }
                else {
                    ShowToast ( ValidationConst.FAILES_DUE_TO+statusCode );
                }
            }

            @Override
            public void onFailure( @NonNull Call<ArrayList<Departments>> call,  @NonNull Throwable t) {
                noInternetConnection ();
                Log.e("TAG", t.toString());
            }
        });
    }

    private void getmanagerProfile(final int id){
        progressBarUtil.showProgress ( "Loading..." );
        EmployeeApi apiService = Util.getClient().create( EmployeeApi.class);
        Call<ArrayList<Employee>> call = apiService.getEmployeesByOrgId(id);
        call.enqueue(new Callback<ArrayList<Employee>>() {
            @Override
            public void onResponse( @NonNull Call<ArrayList<Employee>> call,  @NonNull Response<ArrayList<Employee>> response) {
                int statusCode = response.code();
                if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {
                    progressBarUtil.hideProgress ();
                    ArrayList<Employee> list = response.body();
                    if (list !=null && list.size()!=0) {
                        employees = list;
                        if(employees.size()!=0){
                            Collections.sort(employees, Employee.compareEmployee);
                            ManagerSpinnerAdapter arrayAdapter = new ManagerSpinnerAdapter( CreateEmployeeScreen.this, employees);
                            mtoReport.setAdapter(arrayAdapter);
                        }else {
                            ShowToast ( ValidationConst.NO_EMPLOYEE_ADDED+statusCode );
                            progressBarUtil.hideProgress ();
                        }
                    }else{
                        ShowToast ( ValidationConst.NO_EMPLOYEE_ADDED+statusCode );
                        progressBarUtil.hideProgress ();
                    }

                }else {
                    ShowToast ( ValidationConst.FAILES_DUE_TO+statusCode );
                }
            }

            @Override
            public void onFailure( @NonNull Call<ArrayList<Employee>> call,  @NonNull Throwable t) {
                noInternetConnection ();
                progressBarUtil.hideProgress ();
                Log.e("TAG", t.toString());
            }
        });
    }

    public void getShiftTimings(final int id) {
        final OrganizationTimingsAPI orgApi = Util.getClient().create(OrganizationTimingsAPI.class);
        Call<ArrayList< WorkingDay >> getProf = orgApi.getOrganizationTimingByOrgId(id);
        getProf.enqueue(new Callback<ArrayList< WorkingDay >>() {
            @Override
            public void onResponse( @NonNull  Call<ArrayList< WorkingDay >> call,  @NonNull Response<ArrayList< WorkingDay >> response) {
                int statusCode = response.code ();
                if (statusCode == 200||statusCode == 201||statusCode == 204) {
                    workingDays = response.body();
                    if(workingDays!=null&&workingDays.size()!=0){
                        ShiftSpinnerAdapter arrayAdapter = new ShiftSpinnerAdapter( CreateEmployeeScreen.this, workingDays);
                        mShift.setAdapter(arrayAdapter);
                    }
                }else{
                    ShowToast ( ValidationConst.FAILES_DUE_TO+statusCode );
                }
            }

            @Override
            public void onFailure( @NonNull  Call<ArrayList< WorkingDay >> call, @NonNull  Throwable t) {
                noInternetConnection ();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected( @NonNull MenuItem item) {
        int id = item.getItemId();
        if ( id == android.R.id.home ) {
            CreateEmployeeScreen.this.finish ( );
        }
        return super.onOptionsItemSelected(item);
    }

    /*Not in Use*/
    public void openDatePicker(final TextInputEditText tv) {
        final Calendar c = Calendar.getInstance();
        int mYear  = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay   = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                ( view , year , monthOfYear , dayOfMonth ) -> {
                    try {
                        Log.d("Date", "DATE SELECTED "+dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year,monthOfYear,dayOfMonth);
                        String date1 = (monthOfYear + 1)  + "/" + (dayOfMonth) + "/" + year;
                        @SuppressLint ("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");
                        @SuppressLint ("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                        try {
                            Date fdate = simpleDateFormat.parse(date1);
                            String from1 = null;
                            if ( fdate != null ) {
                                from1 = sdf.format(fdate);
                            }
                            tv.setText(from1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } , mYear, mMonth, mDay);
        datePickerDialog.show();
    }
}
