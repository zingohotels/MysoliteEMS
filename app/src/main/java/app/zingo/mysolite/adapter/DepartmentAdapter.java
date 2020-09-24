package app.zingo.mysolite.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import app.zingo.mysolite.model.Departments;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.ThreadExecuter;
import app.zingo.mysolite.utils.Util;
import app.zingo.mysolite.WebApi.EmployeeApi;
import app.zingo.mysolite.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ZingoHotels Tech on 27-09-2018.
 */

public class DepartmentAdapter extends RecyclerView.Adapter< DepartmentAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Departments> departments;

    public DepartmentAdapter(Context context, ArrayList<Departments> departments)
    {
        this.context = context;
        this.departments = departments;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_department_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Departments department = departments.get(position);

        if(department!=null){

            holder.mDepartmentName.setText(""+department.getDepartmentName());


            holder.mShowList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(holder.mDepEmpList.getVisibility()==View.VISIBLE){
                        holder.mDepEmpList.setVisibility(View.GONE);
                    }else if(holder.mDepEmpList.getVisibility()==View.GONE){
                        holder.mDepEmpList.setVisibility(View.VISIBLE);
                        holder.mDepEmpList.removeAllViews();
                        getEmpByDep(department.getDepartmentId(),holder.mDepEmpList);
                    }

                }
            });


        }


    }

    @Override
    public int getItemCount() {
        return departments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mDepartmentName;
        CardView mLayout;
        RecyclerView mDepEmpList;
        RelativeLayout mShowList;

        public ViewHolder(View itemView) {
            super(itemView);

            mDepartmentName = itemView.findViewById(R.id.department_name);
            mLayout = itemView.findViewById(R.id.department);
            mDepEmpList = itemView.findViewById(R.id.department_emp_list);
            mShowList = itemView.findViewById(R.id.shw_emp_list);

        }
    }

    private void getEmpByDep(final int deptId,final RecyclerView mEmpList){


        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Loading Employees");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create( EmployeeApi.class);
                Call<ArrayList<Employee>> call = apiService.getEmployeesByDepId(deptId);

                call.enqueue(new Callback<ArrayList<Employee>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                        int statusCode = response.code();

                        if (progressDialog != null&&progressDialog.isShowing())
                            progressDialog.dismiss();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                            ArrayList<Employee> list = response.body();


                            if (list !=null && list.size()!=0) {

                                ArrayList<Employee> employees = new ArrayList<>();
                                for(int i=0;i<list.size();i++){

                                    if(list.get(i).getEmployeeId()!= PreferenceHandler.getInstance(context).getUserId()){

                                        employees.add(list.get(i));

                                    }
                                }

                                if(employees!=null&&employees.size()!=0){
                                    Collections.sort(employees, Employee.compareEmployee);

                                    EmployeeUpdateAdapter adapter = new EmployeeUpdateAdapter(context, employees,"Update");
                                    mEmpList.setAdapter(adapter);

                                }else{
                                    Toast.makeText(context,"No Employees added",Toast.LENGTH_LONG).show();
                                }


                                //}

                            }else{
                                Toast.makeText(context,"No Employees added",Toast.LENGTH_LONG).show();
                            }

                        }else {


                            Toast.makeText(context, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog != null&&progressDialog.isShowing())
                            progressDialog.dismiss();

                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }
}

