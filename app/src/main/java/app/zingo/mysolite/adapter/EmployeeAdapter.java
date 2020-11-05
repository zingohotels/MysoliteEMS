package app.zingo.mysolite.adapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.model.LoginDetails;
import app.zingo.mysolite.ui.Admin.CreatePaySlip;
import app.zingo.mysolite.ui.Admin.EmployeeLiveMappingScreen;
import app.zingo.mysolite.ui.Employee.ApplyLeaveScreen;
import app.zingo.mysolite.ui.Employee.EmployeeMeetingHost;
import app.zingo.mysolite.ui.NewAdminDesigns.DailyOrdersForEmployeeActivity;
import app.zingo.mysolite.ui.NewAdminDesigns.DailyTargetsForEmployeeActivity;
import app.zingo.mysolite.ui.NewAdminDesigns.EmployeeDashBoardAdminView;
import app.zingo.mysolite.ui.NewAdminDesigns.ExpenseDashBoardAdmin;
import app.zingo.mysolite.ui.NewAdminDesigns.LeaveDashBoardAdminScreen;
import app.zingo.mysolite.ui.newemployeedesign.CreateExpensesScreen;
import app.zingo.mysolite.ui.newemployeedesign.MeetingDetailList;
import app.zingo.mysolite.utils.BaseActivity;
import app.zingo.mysolite.utils.ProgressBarUtil;
import app.zingo.mysolite.utils.Util;
import app.zingo.mysolite.WebApi.LoginDetailsAPI;
import app.zingo.mysolite.R;
import app.zingo.mysolite.utils.ValidationClass;
import app.zingo.mysolite.utils.ValidationConst;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ZingoHotels Tech on 28-09-2018.
 */

public class EmployeeAdapter extends RecyclerView.Adapter< EmployeeAdapter.ViewHolder>{
    private ProgressBarUtil progressBarUtil;
    private Context context;
    private ArrayList<Employee> list;
    private AlertDialog.Builder builder;
    private String type;
    private BaseActivity baseActivity;
    private ValidationClass validationClass;

    public EmployeeAdapter(Context context, ArrayList<Employee> list,String type) {
        this.context = context;
        this.list = list;
        this.type = type;
        this.progressBarUtil = new ProgressBarUtil ( context );
        this.baseActivity = new BaseActivity ( );
        this.validationClass = new ValidationClass ( );
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_profile_list, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint ("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Employee dto = list.get(position);
        holder.mProfileName.setText(dto.getEmployeeName());
        holder.mProfileEmail.setText(dto.getPrimaryEmailAddress());
        if(dto.getImage ()!=null){
            Picasso.get ().load (  dto.getImage ()).placeholder ( R.drawable.ic_account_circle_black ).into ( holder.profile_image );
        }
        LoginDetails loginDetails = new LoginDetails();
        loginDetails.setEmployeeId(dto.getEmployeeId());
        loginDetails.setLoginDate(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
        getLoginDetails(loginDetails,holder.mStatus,holder.mLoginId);

        holder.mProfileMain.setOnClickListener( v -> {
            if(type!=null&&type.equalsIgnoreCase("Meetings")){
                Intent intent = new Intent(context, EmployeeMeetingHost.class);
                Bundle bundle = new Bundle();
                bundle.putInt("EmployeeId",list.get(position).getEmployeeId());
                intent.putExtras(bundle);
                context.startActivity(intent);

            }else if(type!=null&&type.equalsIgnoreCase("attendance")){
                Intent intent = new Intent(context, EmployeeDashBoardAdminView.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Profile",list.get(position));
                bundle.putInt("ProfileId",list.get(position).getEmployeeId());
                intent.putExtras(bundle);
                context.startActivity(intent);

            }else if(type!=null&&type.equalsIgnoreCase("meetingsList")){
                Intent intent = new Intent(context, MeetingDetailList.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Profile",list.get(position));
                bundle.putInt("ProfileId",list.get(position).getEmployeeId());
                intent.putExtras(bundle);
                context.startActivity(intent);

            }else if(type!=null&&type.equalsIgnoreCase("Expense")){
                builder = new AlertDialog.Builder(context);
                builder.setTitle("Expense");
                builder.setMessage("What do you want to do ?");
                builder.setPositiveButton("Create Expense",
                        ( dialog , id ) -> {
                            dialog.cancel();
                            Intent dash = new Intent(context, CreateExpensesScreen.class);
                            dash.putExtra("EmployeeId", list.get(position).getEmployeeId());
                            dash.putExtra("ManagerId", list.get(position).getManagerId());
                            context.startActivity(dash);
                        } );

                builder.setNeutralButton("Cancel",
                        ( dialog , id ) -> dialog.cancel() );

                builder.setNegativeButton("View",
                        ( dialog , id ) -> {
                            dialog.cancel();
                            Intent intent = new Intent(context, ExpenseDashBoardAdmin.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("Profile",list.get(position));
                            bundle.putInt("EmployeeId",list.get(position).getEmployeeId());
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        } );
                builder.create().show();

            }else  if(type!=null&&type.equalsIgnoreCase("Salary")){
                Intent intent = new Intent(context, CreatePaySlip.class);
                Bundle bundle = new Bundle();
                bundle.putInt("EmployeeId",list.get(position).getEmployeeId());
                bundle.putSerializable("Employee",list.get(position));
                intent.putExtras(bundle);
                context.startActivity(intent);

            }else  if(type!=null&&type.equalsIgnoreCase("Live")){
                Intent intent = new Intent(context, EmployeeLiveMappingScreen.class);
                Bundle bundle = new Bundle();
                bundle.putInt("EmployeeId",list.get(position).getEmployeeId());
                bundle.putSerializable("Employee",list.get(position));
                intent.putExtras(bundle);
                context.startActivity(intent);

            }else  if(type!=null&&type.equalsIgnoreCase("Task")){
                Intent intent = new Intent(context, DailyTargetsForEmployeeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Profile",list.get(position));
                bundle.putInt("ProfileId",list.get(position).getEmployeeId());
                intent.putExtras(bundle);
                context.startActivity(intent);

            }else  if(type!=null&&type.equalsIgnoreCase("Orders")){
                Intent intent = new Intent(context, DailyOrdersForEmployeeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Profile",list.get(position));
                bundle.putInt("ProfileId",list.get(position).getEmployeeId());
                intent.putExtras(bundle);
                context.startActivity(intent);

            }else  if(type!=null&&type.equalsIgnoreCase("Leave")){

                builder = new AlertDialog.Builder(context);
                builder.setTitle("Leave");
                builder.setMessage("What do you want to do ?");
                builder.setPositiveButton("Create Leave",
                        ( dialog , id ) -> {
                            dialog.cancel();
                            Intent dash = new Intent(context, ApplyLeaveScreen.class);
                            dash.putExtra("EmployeeId", list.get(position).getEmployeeId());
                            dash.putExtra("ManagerId", list.get(position).getManagerId());
                            context.startActivity(dash);
                        } );

                builder.setNeutralButton("Cancel",
                        ( dialog , id ) -> dialog.cancel() );

                builder.setNegativeButton("View",
                        ( dialog , id ) -> {
                            dialog.cancel();
                            Intent intent = new Intent(context, LeaveDashBoardAdminScreen.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("EmployeeId",list.get(position).getEmployeeId());
                            bundle.putSerializable("Employee",list.get(position));
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        } );
                builder.create().show();

            }else  if(type!=null&&type.equalsIgnoreCase("WeekOff")){

                builder = new AlertDialog.Builder(context);
                builder.setTitle("WeekOff");
                builder.setMessage("What do you want to do ?");
                builder.setPositiveButton("Create WeekOff",
                        ( dialog , id ) -> {
                            dialog.cancel();
                            Intent dash = new Intent(context, ApplyLeaveScreen.class);
                            dash.putExtra("EmployeeId", list.get(position).getEmployeeId());
                            dash.putExtra("ManagerId", list.get(position).getManagerId());
                            context.startActivity(dash);
                        } );

                builder.setNeutralButton("Cancel",
                        ( dialog , id ) -> dialog.cancel() );

                builder.setNegativeButton("View",
                        ( dialog , id ) -> {
                            dialog.cancel();
                            Intent intent = new Intent(context, LeaveDashBoardAdminScreen.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("EmployeeId",list.get(position).getEmployeeId());
                            bundle.putSerializable("Employee",list.get(position));
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        } );
                builder.create().show();

            }else{
                Intent intent = new Intent(context, EmployeeDashBoardAdminView.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Profile",list.get(position));
                bundle.putInt("ProfileId",list.get(position).getEmployeeId());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        } );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mProfileName,mProfileEmail,mStatus,mLoginId;
        private CardView mProfileMain;
        private CircleImageView profile_image;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mStatus = itemView.findViewById(R.id.status);
            mProfileName = itemView.findViewById(R.id.profile_name_adapter);
            mProfileEmail = itemView.findViewById(R.id.profile_email_adapter);
            mLoginId = itemView.findViewById(R.id.hidden_login_id);
            profile_image = itemView.findViewById(R.id.profile_image);
            mProfileMain = itemView.findViewById(R.id.profileLayout);
        }
    }

    private void getLoginDetails(final LoginDetails loginDetails, final TextView employees,final TextView hidden){
        progressBarUtil.showProgress ( "Loading...." );
        LoginDetailsAPI apiService = Util.getClient().create(LoginDetailsAPI.class);
        Call<ArrayList<LoginDetails>> call = apiService.getLoginByEmployeeIdAndDate(loginDetails);
        call.enqueue(new Callback<ArrayList<LoginDetails>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<LoginDetails>> call, @NonNull Response<ArrayList<LoginDetails>> response) {
                int statusCode = response.code();
                if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {
                    progressBarUtil.hideProgress ();
                    ArrayList<LoginDetails> list = response.body();
                    if (list !=null && list.size()!=0) {
                        if(list.get(list.size()-1).getLogOutTime()==null||list.get(list.size()-1).getLogOutTime().isEmpty()){
                            if(list.get(0).getTotalMeeting()!=null&&!list.get(0).getTotalMeeting().isEmpty()){
                                if(list.get(0).getTotalMeeting().equalsIgnoreCase("Absent")){
                                    employees.setText(context.getResources ().getString ( R.string.absent ));
                                    employees.setBackgroundColor(Color.parseColor("#FF0000"));
                                    employees.setVisibility(View.VISIBLE);
                                }else{
                                    employees.setText(context.getResources ().getString ( R.string.present));
                                    employees.setVisibility(View.VISIBLE);
                                }
                            }else{
                                employees.setText(context.getResources ().getString ( R.string.present));
                                employees.setVisibility(View.VISIBLE);
                            }
                            hidden.setText(String.valueOf ( list.get(0).getLoginDetailsId() ));

                        }else{
                            employees.setText(context.getResources ().getString ( R.string.absent));
                            employees.setBackgroundColor(Color.parseColor("#FF0000"));
                            employees.setVisibility(View.VISIBLE);
                            hidden.setText(String.valueOf ( 0 ));
                        }

                    }else{
                        employees.setText(context.getResources ().getString ( R.string.absent));
                        employees.setBackgroundColor(Color.parseColor("#FF0000"));
                        employees.setVisibility(View.VISIBLE);
                        hidden.setText(String.valueOf ( 0 ));
                    }
                }else{
                    validationClass.ShowToast ( ValidationConst.FAILES_DUE_TO+statusCode );
                    progressBarUtil.hideProgress ();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<LoginDetails>> call, @NonNull Throwable t) {
                progressBarUtil.hideProgress ();
                baseActivity.noInternetConnection ();
                Log.e("TAG", t.toString());
            }
        });
    }

    /*Not in use*/
/*    private String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latLng.latitude, latLng.longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append(",");
                }

                result = address.getAddressLine(0);
                return result;
            }
            return result;
        } catch (IOException e) {
            Log.e("MapLocation", "Unable connect to Geocoder", e);
            return result;
        }
    }

    public void getLoginDetailsById(final int id,final String status,final TextView statusText){
        final LoginDetailsAPI subCategoryAPI = Util.getClient().create(LoginDetailsAPI.class);
        Call<LoginDetails> getProf = subCategoryAPI.getLoginById(id);
        getProf.enqueue(new Callback<LoginDetails>() {
            @Override
            public void onResponse(Call<LoginDetails> call, Response<LoginDetails> response) {
                if (response.code() == 200||response.code() == 201||response.code() == 204) {
                    System.out.println("Inside api");
                    final LoginDetails dto = response.body();
                    if(dto!=null){
                        try {
                            LoginDetails loginDetails = dto;
                            loginDetails.setTotalMeeting(status);
                            updateLogin(loginDetails,statusText);

                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginDetails> call, Throwable t) {

            }
        });
    }

    public void updateLogin(final LoginDetails loginDetails,final TextView statusText) {
        progressBarUtil.showProgress ( "Updating..." );
        LoginDetailsAPI apiService = Util.getClient().create(LoginDetailsAPI.class);
        Call<LoginDetails> call = apiService.updateLoginById(loginDetails.getLoginDetailsId(),loginDetails);
        call.enqueue(new Callback<LoginDetails>() {
            @Override
            public void onResponse(Call<LoginDetails> call, Response<LoginDetails> response) {
                try {
                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||response.code()==204) {
                        progressBarUtil.hideProgress ();
                        statusText.setText(loginDetails.getTotalMeeting()+"");
                        if(loginDetails.getTotalMeeting().equalsIgnoreCase("Present")){
                            statusText.setBackgroundColor(Color.parseColor("#00FF00"));
                        }else{
                            statusText.setBackgroundColor(Color.parseColor("#FF0000"));
                        }

                    }else {
                        validationClass.ShowToast ( ValidationConst.FAILES_DUE_TO+statusCode );
                    }
                }
                catch (Exception ex) {
                    progressBarUtil.hideProgress ();
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LoginDetails> call, Throwable t) {
                progressBarUtil.hideProgress ();
                validationClass.noInternetConnection ();
                Log.e("TAG", t.toString());
            }
        });
    }

    public void addLogin(final LoginDetails loginDetails,final TextView loginId,final TextView status) {
       progressBarUtil.showProgress ( "Loading..." );
        LoginDetailsAPI apiService = Util.getClient().create(LoginDetailsAPI.class);
        Call<LoginDetails> call = apiService.addLogin(loginDetails);
        call.enqueue(new Callback<LoginDetails>() {
            @Override
            public void onResponse(Call<LoginDetails> call, Response<LoginDetails> response) {
                try {
                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {
                        progressBarUtil.hideProgress ();
                        LoginDetails s = response.body();
                        if(s!=null){
                            status.setText("Present");
                            loginId.setText(""+s.getLoginDetailsId());
                            status.setBackgroundColor(Color.parseColor("#00FF00"));
                        }

                    }else {
                        validationClass.ShowToast ( ValidationConst.FAILES_DUE_TO+statusCode );
                    }
                }
                catch (Exception ex) {
                    progressBarUtil.hideProgress ();
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LoginDetails> call, Throwable t) {
                progressBarUtil.hideProgress ();
                validationClass.noInternetConnection ();
                Log.e("TAG", t.toString());
            }
        });
    }*/
}