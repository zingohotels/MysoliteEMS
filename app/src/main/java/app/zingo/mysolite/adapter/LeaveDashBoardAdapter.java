package app.zingo.mysolite.adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import app.zingo.mysolite.Custom.MyRegulerText;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.model.Leaves;
import app.zingo.mysolite.ui.NewAdminDesigns.UpdateLeaveScreen;
import app.zingo.mysolite.ui.newemployeedesign.UpdateWeekOff;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.Util;
import app.zingo.mysolite.WebApi.EmployeeApi;
import app.zingo.mysolite.R;
import app.zingo.mysolite.utils.ValidationClass;
import app.zingo.mysolite.utils.ValidationConst;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaveDashBoardAdapter extends RecyclerView.Adapter< LeaveDashBoardAdapter.ViewHolder>{
    private final ValidationClass validationClass;
    private Context context;
    private ArrayList<Leaves> list;
    private String type;

    public LeaveDashBoardAdapter(Context context, ArrayList<Leaves> list,String type) {
        this.context = context;
        this.list = list;
        this.type = type;
        this.validationClass = new ValidationClass ( );
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_leave_dashboard, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint ({ "SetTextI18n" , "SimpleDateFormat" })
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Leaves dto = list.get(position);
        if(dto!=null){
            getEmployee(dto.getEmployeeId(),holder.mEmpName,"name");
            holder.mLeaveUpdate.setOnClickListener( view -> {
                if(type==null){
                    Intent intent = new Intent(context, UpdateLeaveScreen.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("LeaveId",list.get(position).getLeaveId());
                    bundle.putSerializable("Leaves",list.get(position));
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }else if(type.equalsIgnoreCase("WeekOff")){
                    Intent intent = new Intent(context, UpdateWeekOff.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("LeaveId",list.get(position).getLeaveId());
                    bundle.putSerializable("Leaves",list.get(position));
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });

            if(PreferenceHandler.getInstance(context).getUserRoleUniqueID()==2|| PreferenceHandler.getInstance(context).getUserRoleUniqueID()==9){
                holder.mEmpContact.setOnClickListener( v -> getEmployee(dto.getEmployeeId(),holder.mEmpName,"contact") );

            }else{
                holder.mEmpContact.setVisibility(View.GONE);
            }
            String froms = dto.getFromDate();
            String tos = dto.getToDate();
            if(froms.contains("T")){
                String[] dojs = froms.split ( "T" );
                try {
                    @SuppressLint ("SimpleDateFormat") Date afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                    if ( afromDate != null ) {
                        froms = new SimpleDateFormat("MMM dd,yyyy").format(afromDate);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if(tos.contains("T")){
                String[] dojs = tos.split ( "T" );
                try {
                    @SuppressLint ("SimpleDateFormat") Date afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                    if ( afromDate != null ) {
                        tos = new SimpleDateFormat("MMM dd,yyyy").format(afromDate);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            holder.mLeaveDuration.setText(froms+" to "+tos);
            holder.mLeaveDesc.setText(""+dto.getLeaveComment());

            if(dto.getStatus()!=null&&!dto.getStatus().isEmpty()){
                if(dto.getStatus().equalsIgnoreCase("Approved")){
                    holder.mStatusView.setBackgroundColor(Color.parseColor("#00FF00"));
                    holder.mLeaveStatus.setBackground(context.getResources().getDrawable(R.drawable.oval_green));
                    holder.mLeaveStatus.setText("A");
                }else if(dto.getStatus().equalsIgnoreCase("Rejected")){
                    holder.mStatusView.setBackgroundColor(Color.parseColor("#FF0000"));
                    holder.mLeaveStatus.setBackground(context.getResources().getDrawable(R.drawable.oval_red));
                    holder.mLeaveStatus.setText("R");
                }else if(dto.getStatus().equalsIgnoreCase("Pending")){
                    holder.mStatusView.setBackgroundColor(Color.parseColor("#FFFF00"));
                    holder.mLeaveStatus.setBackground(context.getResources().getDrawable(R.drawable.oval_yellow));
                    holder.mLeaveStatus.setText("P");
                }
            }else{
                holder.mStatusView.setBackgroundColor(Color.parseColor("#FFFF00"));
                holder.mLeaveStatus.setBackground(context.getResources().getDrawable(R.drawable.oval_yellow));
                holder.mLeaveStatus.setText("P");
            }

            if(dto.getLeaveType()!=null&&!dto.getLeaveType().isEmpty()){
                if(dto.getLeaveType().equalsIgnoreCase("Paid")){
                    holder.mLeaveType.setText("Type: "+dto.getLeaveType());
                }else if(dto.getLeaveType().equalsIgnoreCase("Un Paid")){
                    holder.mLeaveType.setText("Type: "+dto.getLeaveType());
                }else{
                    holder.mLeaveType.setText(" Type: Pending");
                }
            }else{
                holder.mLeaveType.setText("Type: Pending");
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        private MyRegulerText mEmpName,mLeaveDuration,mLeaveDesc,mLeaveType;
        private LinearLayout mLeaveUpdate,mEmpContact;
        private TextView mLeaveStatus;
        private View mStatusView;

        public ViewHolder(final View itemView) {
            super(itemView);
            itemView.setClickable(true);
            mEmpName = itemView.findViewById(R.id.from_empl);
            mLeaveDuration = itemView.findViewById(R.id.time_leave);
            mLeaveDesc = itemView.findViewById(R.id.leave_desc);
            mLeaveType = itemView.findViewById(R.id.leave_type);
            mLeaveStatus = itemView.findViewById(R.id.status_text);
            mLeaveUpdate = itemView.findViewById(R.id.leave_update);
            mEmpContact = itemView.findViewById(R.id.contact_employee);
            mStatusView = itemView.findViewById(R.id.status);
        }
    }

    private void getEmployee( final int id, final MyRegulerText textView, final String type){
        EmployeeApi apiService = Util.getClient().create( EmployeeApi.class);
        Call<ArrayList<Employee>> call = apiService.getProfileById(id);
        call.enqueue(new Callback<ArrayList<Employee>>() {
            @SuppressLint ("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ArrayList<Employee>> call, @NonNull Response<ArrayList<Employee>> response) {
                int statusCode = response.code();
                if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {
                    ArrayList<Employee> list = response.body();
                    if (list !=null && list.size()!=0) {
                        final Employee employees = list.get(0);
                        if(employees!=null){
                            try{
                                if(type.equalsIgnoreCase("name")){
                                    textView.setText(String.valueOf ( employees.getEmployeeName() ));
                                }else if(type.equalsIgnoreCase("contact")){
                                    try{
                                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
                                        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View views = inflater.inflate(R.layout.alert_contact_employee, null);

                                        builder.setView(views);
                                        final TextView mEmpName = views.findViewById(R.id.employee_name);
                                        final TextView mPhone = views.findViewById(R.id.call_employee);
                                        final TextView mEmail = views.findViewById(R.id.email_employee);
                                        final AppCompatTextView cancel = views.findViewById(R.id.cancel);
                                        final androidx.appcompat.app.AlertDialog dialogs = builder.create();
                                        dialogs.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                        dialogs.show();
                                        dialogs.setCanceledOnTouchOutside(true);
                                        mEmpName.setText(employees.getEmployeeName());
                                        mPhone.setText(employees.getPhoneNumber());
                                        mEmail.setText(employees.getPrimaryEmailAddress());
                                        mPhone.setOnClickListener( v -> {
                                            Intent intent = new Intent(Intent.ACTION_DIAL);
                                            intent.setData(Uri.parse("tel:"+employees.getPhoneNumber()));
                                            context.startActivity(intent);
                                        } );

                                        mEmail.setOnClickListener( v -> {
                                            final Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                            emailIntent.setType("plain/text");
                                            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{""+employees.getPrimaryEmailAddress()});
                                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Leave Acknowldgement/Details");
                                            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                                            context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                                        } );
                                        cancel.setOnClickListener( v -> {
                                            dialogs.dismiss ();
                                        } );

                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

                }else {
                    validationClass.ShowToast ( ValidationConst.FAILES_DUE_TO+statusCode );
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Employee>> call, @NonNull Throwable t) {
                Log.e("TAG", t.toString());
                validationClass.noInternetConnection ();
            }
        });
    }
}
