package app.zingo.mysolite.ui.Admin;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import app.zingo.mysolite.adapter.TaskListAdapter;
import app.zingo.mysolite.model.Tasks;
import app.zingo.mysolite.utils.ThreadExecuter;
import app.zingo.mysolite.utils.Util;
import app.zingo.mysolite.WebApi.TasksAPI;
import app.zingo.mysolite.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskListScreen extends AppCompatActivity {

    RecyclerView mTaskList;

    int employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_task_list_screen);
            mTaskList = findViewById(R.id.task_list);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                employeeId = bundle.getInt("EmployeeId");
            }



            if(employeeId!=0){
                getTasks(employeeId);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void getTasks(final int employeeId){


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Details..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                TasksAPI apiService = Util.getClient().create( TasksAPI.class);
                Call<ArrayList<Tasks>> call = apiService.getTasks();

                call.enqueue(new Callback<ArrayList<Tasks>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Tasks>> call, Response<ArrayList<Tasks>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();
                            ArrayList<Tasks> list = response.body();
                            ArrayList<Tasks> employeeTasks = new ArrayList<>();



                            if (list !=null && list.size()!=0) {


                                for (Tasks task:list) {

                                    if(task.getEmployeeId()==employeeId){

                                        employeeTasks.add(task);

                                    }

                                }

                                if(employeeTasks!=null&&employeeTasks.size()!=0){
                                    TaskListAdapter adapter = new TaskListAdapter( TaskListScreen.this,employeeTasks);
                                    mTaskList.setAdapter(adapter);
                                }else{
                                    Toast.makeText( TaskListScreen.this, "No Tasks given for this employee", Toast.LENGTH_SHORT).show();
                                }



                            }else{

                                Toast.makeText( TaskListScreen.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();

                            }

                        }else {

                            if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();

                            Toast.makeText( TaskListScreen.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Tasks>> call, Throwable t) {
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
