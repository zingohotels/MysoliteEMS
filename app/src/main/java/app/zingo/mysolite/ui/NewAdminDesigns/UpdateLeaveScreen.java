package app.zingo.mysolite.ui.NewAdminDesigns;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import app.zingo.mysolite.model.Leaves;
import app.zingo.mysolite.utils.NetworkUtil;
import app.zingo.mysolite.utils.ProgressBarUtil;
import app.zingo.mysolite.utils.Util;
import app.zingo.mysolite.WebApi.LeaveAPI;
import app.zingo.mysolite.R;
import app.zingo.mysolite.utils.ValidationClass;
import app.zingo.mysolite.utils.ValidationConst;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateLeaveScreen extends ValidationClass {
    private TextInputEditText mFrom,mTo,mLeaveComment;
    private Spinner mLeaveType,mLeaveStatus;
    private Leaves leavess;
   // private String[] leaveTypes,leaveStauses;
    private int leaveId;
    private ProgressBarUtil progressBarUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_leave_screen);
        Objects.requireNonNull ( getSupportActionBar ( ) ).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Leave Details");
        initViews();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            leavess = (Leaves)bundle.getSerializable("Leaves");
            leaveId = bundle.getInt("LeaveId");
        }

        if(leavess!=null){
            setData(leavess);
        }else if(leaveId!=0){
            getLeaveDetails(leaveId);
        }
    }
    private void initViews ( ) {
        try{
            progressBarUtil = new ProgressBarUtil ( this );
            mLeaveType = findViewById(R.id.leave_type_spinner);
            mLeaveStatus = findViewById(R.id.leave_status_spinner);
            mFrom = findViewById(R.id.from_date);
            mTo = findViewById(R.id.to_date);
            mLeaveComment = findViewById(R.id.leave_comment);
            TextView mApply = findViewById ( R.id.apply_leave );
            /*leaveTypes = getResources().getStringArray(R.array.leave_type);
            leaveStauses = getResources().getStringArray(R.array.leave_status);*/

            mFrom.setOnClickListener( view -> openDatePicker(mFrom) );

            mTo.setOnClickListener( view -> openDatePicker(mTo) );

            mApply.setOnClickListener ( view -> {
                if(isLeaveVlidated ( UpdateLeaveScreen.this, mFrom, mTo ,mLeaveComment)){
                    if ( NetworkUtil.checkInternetConnection ( UpdateLeaveScreen.this ) ) {
                        setLeaveModel ( );
                    } else {
                        noInternetConnection ();
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressLint ("SimpleDateFormat")
    public void setData( Leaves dto){
        String froms = dto.getFromDate();
        String tos = dto.getToDate();
        if(froms.contains("T")){
            String[] dojs = froms.split ( "T" );
            try {
                @SuppressLint ("SimpleDateFormat") Date afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                if ( afromDate != null ) {
                    froms = new SimpleDateFormat("MMM dd,yyyy").format(afromDate);
                }
                mFrom.setText(froms);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(tos.contains("T")){
            String[] dojs = tos.split ( "T" );
            try {
                Date afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                if ( afromDate != null ) {
                    tos = new SimpleDateFormat("MMM dd,yyyy").format(afromDate);
                }
                mTo.setText(tos);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(dto.getLeaveType().equalsIgnoreCase("Paid")){
            mLeaveType.setSelection(0);
        }else{
            mLeaveType.setSelection(1);
        }

        if(dto.getStatus().equalsIgnoreCase("Pending")){
            mLeaveStatus.setSelection(0);
        }else  if(dto.getStatus().equalsIgnoreCase("Approved")){
            mLeaveStatus.setSelection(1);
        }else{
            mLeaveStatus.setSelection(2);
        }
        mLeaveComment.setText(dto.getLeaveComment());

    }

    public void openDatePicker(final TextInputEditText tv) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear  = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay   = c.get(Calendar.DAY_OF_MONTH);
        //launch datepicker modal
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

    private void setLeaveModel ( ) {
        try{
            String leaveType= mLeaveType.getSelectedItem().toString();
            String leaveStatus= mLeaveStatus.getSelectedItem().toString();
            Leaves leaves = leavess;
            leaves.setLeaveComment( Objects.requireNonNull ( mLeaveComment.getText ( ) ).toString ());
            @SuppressLint ("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("MMM dd,yyyy");
            @SuppressLint ("SimpleDateFormat") SimpleDateFormat dfs = new SimpleDateFormat("MM/dd/yyyy");

            Date fromDate = df.parse(Objects.requireNonNull ( mFrom.getText ( ) ).toString ());
            Date toDate = df.parse(Objects.requireNonNull ( mTo.getText ( ) ).toString ());
            if ( fromDate != null ) {
                leaves.setFromDate(dfs.format(fromDate));
            }
            if ( toDate != null ) {
                leaves.setToDate(dfs.format(toDate));
            }
            leaves.setStatus(leaveStatus);
            leaves.setLeaveType(leaveType);
            int diffs = (int)dateCal(Objects.requireNonNull ( mFrom.getText ( ) ).toString (),Objects.requireNonNull ( mTo.getText ( ) ).toString ());
            leaves.setNoOfDays(diffs);
            leaves.setApprovedDate(dfs.format(new Date()));

            try {
                updateLeaves(leaves);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void updateLeaves(final Leaves leaves) {
        progressBarUtil.showProgress ( "Updating..." );
        LeaveAPI apiService = Util.getClient().create(LeaveAPI.class);
        Call<Leaves> call = apiService.updateLeaves(leaves.getLeaveId(),leaves);
        call.enqueue(new Callback<Leaves>() {
            @Override
            public void onResponse(@NonNull Call<Leaves> call, @NonNull Response<Leaves> response) {
                try {
                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201|| statusCode == 204) {
                        progressBarUtil.hideProgress ();
                        ShowToast ( ValidationConst.LEAVE_UPDATED_SUCESSFULLY );
                        UpdateLeaveScreen.this.finish();

                    }else {
                        progressBarUtil.hideProgress ();
                       ShowToast ( ValidationConst.FAILES_DUE_TO+statusCode );
                    }
                }
                catch (Exception ex) {
                    progressBarUtil.hideProgress ();
                    noInternetConnection ();
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure( @NonNull Call<Leaves> call, @NonNull Throwable t) {
                progressBarUtil.hideProgress ();
                noInternetConnection ();
                Log.e("TAG", t.toString());
            }
        });
    }

    public long dateCal(String start,String end){
        @SuppressLint ("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");
        Date fd,td;
        try {
            fd = sdf.parse(""+start);
            td = sdf.parse(""+end);
            long diff = 0;
            if ( td != null ) {
                if ( fd != null ) {
                    diff = td.getTime() - fd.getTime();
                }
            }
            long Hours = diff / (60 * 60 * 1000) % 24;
            long Minutes = diff / (60 * 1000) % 60;
            long diffDays = diff / (24 * 60 * 60 * 1000);
            System.out.println("Diff "+diff);
            System.out.println("Hours "+Hours);
            System.out.println("Minutes "+Minutes);
          /*  long diffDays = diff / (24 * 60 * 60 * 1000);
            long Hours = diff / (60 * 60 * 1000) % 24;
            long Minutes = diff / (60 * 1000) % 60;
            long Seconds = diff / 1000 % 60;*/

            return  diffDays;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void getLeaveDetails(final int leaveId){
        progressBarUtil.showProgress ( "Loading..." );
        LeaveAPI apiService = Util.getClient().create(LeaveAPI.class);
        Call<Leaves> call = apiService.getLeaveById(leaveId);
        call.enqueue(new Callback<Leaves>() {
            @Override
            public void onResponse(@NonNull Call<Leaves> call, @NonNull Response<Leaves> response) {
                int statusCode = response.code();
                if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {
                    progressBarUtil.hideProgress ();
                    leavess = response.body();
                    if(leavess!=null){
                        setData(leavess);
                    }else{
                        ShowToast ( ValidationConst.SOMETHING_WENT_WRONG+statusCode );
                    }

                }else {
                    progressBarUtil.hideProgress ();
                   ShowToast ( ValidationConst.FAILES_DUE_TO+statusCode );
                }
            }

            @Override
            public void onFailure(@NonNull Call<Leaves> call ,@NonNull  Throwable t) {
                // Log error here since request failed
                progressBarUtil.hideProgress ();
                noInternetConnection ();
                Log.e("TAG", t.toString());
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if ( id == android.R.id.home ) {
           /* if ( leaveId != 0 ) {
                Intent splash = new Intent ( UpdateLeaveScreen.this , SplashScreen.class );
                startActivity ( splash );
                UpdateLeaveScreen.this.finish ( );
            }*/
                UpdateLeaveScreen.this.finish ( );

        }
        return super.onOptionsItemSelected(item);
    }
}
