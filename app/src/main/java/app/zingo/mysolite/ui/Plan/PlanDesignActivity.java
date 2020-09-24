package app.zingo.mysolite.ui.Plan;

import android.os.Bundle;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import app.zingo.mysolite.WebApi.OrganizationApi;
import app.zingo.mysolite.adapter.PlanDesignAdapter;
import app.zingo.mysolite.model.Organization;
import app.zingo.mysolite.model.PlanDesign;
import app.zingo.mysolite.model.PlanFeatures;
import app.zingo.mysolite.R;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanDesignActivity extends AppCompatActivity {

    ViewPager slidePager;
    Button basic_buy;
    int[] layouts = {R.layout.basic_plan_design, R.layout.advance_plan_design};
    LinearLayout dots;
    ImageView[] dot;

    int currentPage = 0,start = 0,end = 0;
    Timer timer;
    final long DELAY_MS = 2000;
    final long PERIOD_MS = 7000;

    ArrayList< PlanDesign > planDesigns;
    Organization organization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_plan_design);

            slidePager = findViewById(R.id.pager_image_support);

            planDesigns = new ArrayList<>();

            PlanDesign basicDesign = new PlanDesign ();
            basicDesign.setName("Premium");
            basicDesign.setRupees("10000");
            basicDesign.setColor("#62DDAE");
            basicDesign.setDrawable(R.drawable.theme1);
            ArrayList< PlanFeatures > basicFeatures = new ArrayList<>();
            PlanFeatures basics = new PlanFeatures ();
            basics.setFeature("Attendance Management");
            basicFeatures.add(basics);
            basics = new PlanFeatures ();
            basics.setFeature("Leave Management");
            basicFeatures.add(basics);
            basics = new PlanFeatures ();
            basics.setFeature("Live Tracking");
            basicFeatures.add(basics);
            basics = new PlanFeatures ();
            basics.setFeature("Task Management");
            basicFeatures.add(basics);
            basics = new PlanFeatures ();
            basics.setFeature("Salary Management");
            basicFeatures.add(basics);
            basicDesign.setFeatures(basicFeatures);

            planDesigns.add(basicDesign);

            PlanDesign advanceDesign = new PlanDesign ();
            advanceDesign.setName("Advance Halfly");
            advanceDesign.setRupees("80");
            advanceDesign.setColor("#56A8FE");
            advanceDesign.setDrawable(R.drawable.theme2);
            ArrayList< PlanFeatures > advanceFeatures = new ArrayList<>();
            PlanFeatures advances = new PlanFeatures ();
            advances.setFeature("Basic plan");
            advanceFeatures.add(advances);
            advances = new PlanFeatures ();
            advances.setFeature("Live Tracking");
            advanceFeatures.add(advances);
            advances = new PlanFeatures ();
            advances.setFeature("Task Management");
            advanceFeatures.add(advances);
            advances = new PlanFeatures ();
            advances.setFeature("Expenses Management");
            advanceFeatures.add(advances);
            advanceDesign.setFeatures(advanceFeatures);
            planDesigns.add(advanceDesign);

            advanceDesign = new PlanDesign ();
            advanceDesign.setName("Advance Yearly");
            advanceDesign.setRupees("72");
            advanceDesign.setColor("#56A8FE");
            advanceDesign.setDrawable(R.drawable.theme1);
            advanceFeatures = new ArrayList<>();
            advances = new PlanFeatures ();
            advances.setFeature("Basic plan");
            advanceFeatures.add(advances);
            advances = new PlanFeatures ();
            advances.setFeature("Live Tracking");
            advanceFeatures.add(advances);
            advances = new PlanFeatures ();
            advances.setFeature("Task Management");
            advanceFeatures.add(advances);
            advances = new PlanFeatures ();
            advances.setFeature("Expenses Management");
            advanceFeatures.add(advances);
            advanceDesign.setFeatures(advanceFeatures);
            planDesigns.add(advanceDesign);


            getCompany( PreferenceHandler.getInstance ( PlanDesignActivity.this ).getCompanyId () );


            final Handler handler = new Handler();
            final Runnable Update = new Runnable() {
                public void run() {
                    if (currentPage == layouts.length && start == 0) {
                        currentPage = --currentPage;
                        start = 1;
                        end = 0;
                    }else if(currentPage < layouts.length && currentPage != 0 && end == 0&& start == 1){
                        currentPage = --currentPage;
                    }else if(currentPage == 0 && end == 0 && start == 1){
                        currentPage = 0;
                        end = 1;
                        start = 0;
                    }else if(currentPage <= layouts.length&& start == 0){

                        currentPage = ++currentPage;
                    }else if(currentPage == 0&& start == 0){

                        currentPage = ++currentPage;
                    }else{

                    }
                    slidePager.setCurrentItem(currentPage, true);
                }
            };

            timer = new Timer(); // This will create a new Thread
            timer .schedule(new TimerTask() { // task to be scheduled

                @Override
                public void run() {
                    handler.post(Update);
                }
            }, DELAY_MS, PERIOD_MS);
            dots = findViewById(R.id.dots_layout);

            createDot(0);

            slidePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }
                @Override
                public void onPageSelected(int position) {
                    createDot(position);
                }
                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createDot(int current){
        if(dots != null){
            dots.removeAllViews();
        }
        dot = new ImageView[layouts.length];
        for (int i =0;i<layouts.length;i++){
            dot[i] = new ImageView(this);
            if(i==current){
                dot[i].setImageDrawable( ContextCompat.getDrawable(this, R.drawable.active_dots));
            }else {
                dot[i].setImageDrawable( ContextCompat.getDrawable(this, R.drawable.inactive_dots));
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4,0,4,0);
            dots.addView(dot[i],params);
        }
    }

    public void getCompany(final int id) {



        final OrganizationApi subCategoryAPI = Util.getClient().create( OrganizationApi.class);
        Call <ArrayList< Organization >> getProf = subCategoryAPI.getOrganizationById(id);
        //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

        getProf.enqueue(new Callback <ArrayList< Organization >> () {

            @Override
            public void onResponse( Call<ArrayList< Organization >> call, Response <ArrayList< Organization >> response) {

                if (response.code() == 200||response.code() == 201||response.code() == 204)
                {
                    organization = response.body().get(0);

                    if(organization!=null){

                       /* appType = organization.getAppType();
                        planType = organization.getPlanType();
                        startDate = organization.getLicenseStartDate();
                        endDate = organization.getLicenseEndDate();*/

                        PlanDesignAdapter slider = new PlanDesignAdapter( PlanDesignActivity.this,planDesigns,organization);
                        slidePager.setAdapter(slider);
                        slidePager.setClipToPadding(false);
                        slidePager.setPadding(40,0,40,0);


                    }else{
                        Toast.makeText( PlanDesignActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }


                }else{

                    Toast.makeText( PlanDesignActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure( Call<ArrayList< Organization >> call, Throwable t) {

                Toast.makeText( PlanDesignActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        });

    }

}
