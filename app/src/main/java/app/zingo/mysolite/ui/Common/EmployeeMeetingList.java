package app.zingo.mysolite.ui.Common;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import app.zingo.mysolite.adapter.EmployeeMeetingAdapter;
import app.zingo.mysolite.model.Meetings;
import app.zingo.mysolite.utils.ThreadExecuter;
import app.zingo.mysolite.utils.Util;
import app.zingo.mysolite.WebApi.MeetingsAPI;
import app.zingo.mysolite.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeeMeetingList extends AppCompatActivity {

    RecyclerView mMeetingList;
    int employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_employee_meeting_list);

            mMeetingList = findViewById(R.id.meeting_list);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                employeeId = bundle.getInt("EmployeeId");
            }

            if(employeeId!=0){

                getMeetingDetails(employeeId);
            }


        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void getMeetingDetails(final int employeeId){


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Details..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                MeetingsAPI apiService = Util.getClient().create( MeetingsAPI.class);
                Call<ArrayList< Meetings >> call = apiService.getMeetingsByEmployeeId(employeeId);

                call.enqueue(new Callback<ArrayList< Meetings >>() {
                    @Override
                    public void onResponse( Call<ArrayList< Meetings >> call, Response<ArrayList< Meetings >> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog!=null)
                                progressDialog.dismiss();


                            ArrayList< Meetings > list = response.body();


                            if (list !=null && list.size()!=0) {

                                EmployeeMeetingAdapter adapter = new EmployeeMeetingAdapter( EmployeeMeetingList.this,list);
                                mMeetingList.setAdapter(adapter);

                            }else{

                                Toast.makeText( EmployeeMeetingList.this, "No Meetings", Toast.LENGTH_SHORT).show();
                            }

                        }else {


                            Toast.makeText( EmployeeMeetingList.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure( Call<ArrayList< Meetings >> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog!=null)
                            progressDialog.dismiss();
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

}
