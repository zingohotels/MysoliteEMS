package app.zingo.mysolite.ui.NewAdminDesigns;

import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.ArrayList;

import app.zingo.mysolite.adapter.ShiftAdapter;
import app.zingo.mysolite.model.WorkingDay;
import app.zingo.mysolite.ui.Company.WorkingDaysScreen;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.Util;
import app.zingo.mysolite.WebApi.OrganizationTimingsAPI;
import app.zingo.mysolite.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShiftScreenList extends AppCompatActivity {

    LinearLayout mNoShifts;
    RecyclerView mShiftsList;
    FloatingActionButton mAddShifts;
    ImageView mLoader;

    int orgId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_shift_screen_list);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Shift List");

            Bundle bun = getIntent().getExtras();

            if(bun!=null){

                orgId = bun.getInt("OrganizationId",0);
            }

            mNoShifts = findViewById(R.id.noShiftFound);
            mShiftsList = findViewById(R.id.shift_list_data);
            mAddShifts = findViewById(R.id.add_shift_float);
            mLoader = findViewById(R.id.spin_loader);

            Glide.with(this)
                    .load(R.drawable.spin)
                    .into(new GlideDrawableImageViewTarget(mLoader));

            if(orgId!=0){

                getShiftTimings(orgId);

            }else{
                getShiftTimings(PreferenceHandler.getInstance( ShiftScreenList.this).getCompanyId());
            }

            mAddShifts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent branch = new Intent( ShiftScreenList.this, WorkingDaysScreen.class);
                    branch.putExtra("OrganizationId",orgId);
                    startActivity(branch);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void getShiftTimings(final int id) {

        final OrganizationTimingsAPI orgApi = Util.getClient().create(OrganizationTimingsAPI.class);
        Call<ArrayList< WorkingDay >> getProf = orgApi.getOrganizationTimingByOrgId(id);
        //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

        getProf.enqueue(new Callback<ArrayList< WorkingDay >>() {

            @Override
            public void onResponse( Call<ArrayList< WorkingDay >> call, Response<ArrayList< WorkingDay >> response) {



                if (response.code() == 200||response.code() == 201||response.code() == 204)
                {
                    mLoader.setVisibility(View.GONE);

                    ArrayList< WorkingDay > branches = response.body();

                    if(branches!=null&&branches.size()!=0){

                        mLoader.setVisibility(View.GONE);
                        mNoShifts.setVisibility(View.GONE);

                        mShiftsList.removeAllViews();

                        ShiftAdapter adapter = new ShiftAdapter( ShiftScreenList.this,branches);
                        mShiftsList.setAdapter(adapter);


                    }


                }else{

                    mLoader.setVisibility(View.GONE);

                    Toast.makeText( ShiftScreenList.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure( Call<ArrayList< WorkingDay >> call, Throwable t) {

                mLoader.setVisibility(View.GONE);


                Toast.makeText( ShiftScreenList.this, "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(orgId!=0){

            getShiftTimings(orgId);

        }else{
            getShiftTimings(PreferenceHandler.getInstance( ShiftScreenList.this).getCompanyId());
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                ShiftScreenList.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
