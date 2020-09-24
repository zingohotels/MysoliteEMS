package app.zingo.mysolite.ui.NewAdminDesigns;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import app.zingo.mysolite.adapter.TeamMembersAdapter;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.ThreadExecuter;
import app.zingo.mysolite.utils.Util;
import app.zingo.mysolite.WebApi.EmployeeApi;
import app.zingo.mysolite.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamMembersList extends AppCompatActivity {

    RecyclerView mTeamList;
    LinearLayout mNoEmpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_team_members_list);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Team Details");

            mTeamList = findViewById(R.id.team_list);
            mNoEmpl = findViewById(R.id.noEmployeeUpdate);

            getProfiles();


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void getProfiles(){


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Team members");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create( EmployeeApi.class);
                Call<ArrayList< Employee >> call = apiService.getEmployeesByOrgId(PreferenceHandler.getInstance( TeamMembersList.this).getCompanyId());

                call.enqueue(new Callback<ArrayList< Employee >>() {
                    @Override
                    public void onResponse( Call<ArrayList< Employee >> call, Response<ArrayList< Employee >> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog!=null)
                                progressDialog.dismiss();
                            ArrayList< Employee > list = response.body();
                            ArrayList< Employee > teamlist = new ArrayList<>();


                            if (list !=null && list.size()!=0) {

                                for ( Employee emp:list) {

                                    if(PreferenceHandler.getInstance( TeamMembersList.this).getUserRoleUniqueID()==2){

                                        if(emp.getEmployeeId()!= PreferenceHandler.getInstance( TeamMembersList.this).getUserId()){
                                            teamlist.add(emp);
                                        }

                                    }else{

                                        if(emp.getManagerId()== PreferenceHandler.getInstance( TeamMembersList.this).getUserId()){
                                            teamlist.add(emp);
                                        }

                                    }



                                }

                                if(teamlist!=null&&teamlist.size()!=0){

                                    Collections.sort(teamlist, Employee.compareEmployee);
                                    mNoEmpl.setVisibility(View.GONE);
                                    mTeamList.setVisibility(View.VISIBLE);
                                    TeamMembersAdapter adapter = new TeamMembersAdapter( TeamMembersList.this, teamlist);
                                    mTeamList.setAdapter(adapter);

                                }else{
                                    Toast.makeText( TeamMembersList.this, "You don't have any Team", Toast.LENGTH_SHORT).show();
                                    mNoEmpl.setVisibility(View.VISIBLE);
                                    mTeamList.setVisibility(View.GONE);
                                }



                                //}

                            }else{
                                Toast.makeText( TeamMembersList.this,"No Employees added",Toast.LENGTH_LONG).show();
                                mNoEmpl.setVisibility(View.VISIBLE);
                                mTeamList.setVisibility(View.GONE);

                            }

                        }else {


                            Toast.makeText( TeamMembersList.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                            mNoEmpl.setVisibility(View.VISIBLE);
                            mTeamList.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure( Call<ArrayList< Employee >> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog!=null)
                            progressDialog.dismiss();
                        Log.e("TAG", t.toString());
                        mNoEmpl.setVisibility(View.VISIBLE);
                        mTeamList.setVisibility(View.GONE);
                    }
                });
            }


        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                TeamMembersList.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
