package app.zingo.mysolite.ui.NewAdminDesigns;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.zingo.mysolite.adapter.TaskEmployeeListAdapter;
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
 * A simple {@link Fragment} subclass.
 */
public class TaskListFragment extends Fragment {

    final String TAG = "Employer Task Employee";
    View layout;
    LinearLayout mNoEmployee;
    private static TaskEmployeeListAdapter mAdapter;
    static Context mContext;

    private static List<Employee> mEmployeeList = new ArrayList();
    private static List<Employee> searchList = new ArrayList();
    private Map<String, String> countMap = new HashMap();

    private RecyclerView mEmployeeRecyclerView;

    ArrayList<Employee> employeesList;

    public TaskListFragment() {
        // Required empty public constructor
    }

    public static TaskListFragment getInstance() {
        return new TaskListFragment ();
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        // GAUtil.trackScreen(getActivity(), "Employer Dashboard");
    }


    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        super.onCreateView(layoutInflater, viewGroup, savedInstanceState);
        try{
            this.layout = layoutInflater.inflate(R.layout.fragment_task_list, viewGroup, false);
            mEmployeeRecyclerView = this.layout.findViewById(R.id.listEmployees);
            mNoEmployee = this.layout.findViewById(R.id.noRecordFound);
            mEmployeeRecyclerView.setVisibility(View.VISIBLE);
            mEmployeeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mContext = getContext();

            getEmployees();

            return this.layout;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    private void getEmployees(){


        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading Employees");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create( EmployeeApi.class);
                Call<ArrayList<Employee>> call = apiService.getEmployeesByOrgId(PreferenceHandler.getInstance(getActivity()).getCompanyId());

                call.enqueue(new Callback<ArrayList<Employee>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog!=null)
                                progressDialog.dismiss();
                            ArrayList<Employee> list = response.body();


                            if (list !=null && list.size()!=0) {

                                employeesList= new ArrayList<>();
                                for(int i=0;i<list.size();i++){

                                    if(list.get(i).getUserRoleId()!=2){


                                        employeesList.add(list.get(i));


                                    }
                                }


                                if(employeesList!=null&&employeesList.size()!=0){
                                    mNoEmployee.setVisibility(View.GONE);
                                    Collections.sort(employeesList, Employee.compareEmployee);
                                    mAdapter = new TaskEmployeeListAdapter(getActivity(), employeesList);
                                    mEmployeeRecyclerView.setAdapter(mAdapter);
                                }else{

                                    mNoEmployee.setVisibility(View.VISIBLE);
                                }

                                //}

                            }else{
                                mNoEmployee.setVisibility(View.VISIBLE);
                            }

                        }else {
                            mNoEmployee.setVisibility(View.VISIBLE);

                            Toast.makeText(getActivity(), "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog!=null)
                            progressDialog.dismiss();
                        mNoEmployee.setVisibility(View.VISIBLE);
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }



}
