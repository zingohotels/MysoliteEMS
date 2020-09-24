package app.zingo.mysolite.ui.NewAdminDesigns;

import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import app.zingo.mysolite.WebApi.MeetingsAPI;
import app.zingo.mysolite.adapter.MeetingDetailAdapter;
import app.zingo.mysolite.R;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.model.Meetings;
import app.zingo.mysolite.ui.Employee.EmployeeListScreen;
import app.zingo.mysolite.utils.Common;
import app.zingo.mysolite.utils.ProgressBarUtil;
import app.zingo.mysolite.utils.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowAllMeetings extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton refresh;
    private LinearLayout noRecord_found;
    private ProgressBarUtil progressBarUtil;
    private ArrayList < Meetings > meetingsArrayList;
    private MeetingDetailAdapter adapter;
    private final String TAG = "ShowAllMeetings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView ( R.layout.activity_show_all_meetings);
        getSupportActionBar ().setHomeButtonEnabled (true);
        getSupportActionBar ().setDisplayHomeAsUpEnabled (true);
        setTitle ("MeetingList ");
        progressBarUtil = new ProgressBarUtil (this);
        meetingsArrayList = new ArrayList <> ();
        recyclerView = findViewById (R.id.targetList);
        refresh = findViewById (R.id.refresh);
        noRecord_found = findViewById (R.id.norecord_found);
        if(Common.employeeDetals!=null&&Common.employeeDetals.size ()!=0){
            for( Employee list: Common.employeeDetals){
                if(list!=null&&list.getEmployeeId ()!=0){
                    networkCall(list.getEmployeeId ());
                }
            }
        }

        refresh.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( ShowAllMeetings.this, EmployeeListScreen.class);
                intent.putExtra("Type","meetingsList");
                startActivity(intent);
            }
        });
    }

    private void networkCall(int employeeId) {
        progressBarUtil.showProgress ("Please wait...");
        MeetingsAPI apiService = Util.getClient ().create (MeetingsAPI.class);
        Call <ArrayList< Meetings >> call = apiService.getMeetingsByEmployeeId (employeeId);
        call.enqueue (new Callback <ArrayList< Meetings >> () {
            @Override
            public void onResponse( Call <ArrayList< Meetings >> call, Response <ArrayList< Meetings >> response) {
                int statusCode = response.code ();
                if(statusCode==200||statusCode==201||statusCode==204){
                    Log.d (TAG,"Suree: "+statusCode);
                    System.out.println("Suree: "+statusCode);
                    if(response.body ()!=null){
                        meetingsArrayList.addAll (response.body ());
                        noRecord_found.setVisibility (View.INVISIBLE);
                        progressBarUtil.hideProgress ();
                    }
                    else{
                        noRecord_found.setVisibility ( View.VISIBLE);
                    }

                    adapter = new MeetingDetailAdapter ( ShowAllMeetings.this,meetingsArrayList);
                    recyclerView.setAdapter (adapter);

                }
            }

            @Override
            public void onFailure( Call < ArrayList < Meetings > > call, Throwable t) {
                progressBarUtil.hideProgress ();
                Log.d (TAG,"Suree: "+t.getMessage ());
                System.out.println("Suree: "+t.getMessage ());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        int id = item.getItemId ();
        if(id==android.R.id.home){
            ShowAllMeetings.this.finish ();
        }
        return super.onOptionsItemSelected (item);
    }
}
