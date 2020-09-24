package app.zingo.mysolite.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import app.zingo.mysolite.Custom.MyRegulerText;
import app.zingo.mysolite.model.Organization;
import app.zingo.mysolite.model.PlanDesign;
import app.zingo.mysolite.model.PlanIntentData;
import app.zingo.mysolite.ui.NewAdminDesigns.PlanSubscribtionScreen;
import app.zingo.mysolite.R;
import app.zingo.mysolite.ui.Plan.PlanHundred;

public class PlanDesignAdapter extends PagerAdapter {

    //Activity activity;

    Context context;
    ArrayList< PlanDesign > planDesigns;
    Organization organization;
    private LayoutInflater inflater;


    public PlanDesignAdapter( Context context, ArrayList< PlanDesign > planDesigns, Organization organization )
    {
        this.context = context;
        this.planDesigns = planDesigns;
        this.organization = organization;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        return planDesigns.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = inflater.inflate(R.layout.basic_plan_design, container, false);

        FrameLayout backFram = (FrameLayout) itemView.findViewById(R.id.frame_lay);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.back_theme);
        MyRegulerText planName = ( MyRegulerText ) itemView.findViewById(R.id.name_plan);
        MyRegulerText planPrice = ( MyRegulerText ) itemView.findViewById(R.id.plan_price);
        MyRegulerText buy = ( MyRegulerText ) itemView.findViewById(R.id.button_buy_basic);
        RecyclerView featureList = (RecyclerView) itemView.findViewById(R.id.feature_list);

        container.addView(itemView);

        PlanDesign design = planDesigns.get(position);

        if(design!=null){

            planName.setText(""+design.getName());
            planPrice.setText(""+design.getRupees());


            final  String planNames  = design.getName();

            backFram.setBackgroundColor(Color.parseColor(""+design.getColor()));
            imageView.setImageResource(design.getDrawable());
            buy.setBackgroundColor(Color.parseColor(""+design.getColor()));

            if(design.getFeatures()!=null&&design.getFeatures().size()!=0){

                PlanFeaturesRecAdapter adapter = new PlanFeaturesRecAdapter (context,design.getFeatures());
                featureList.setAdapter(adapter);
            }


            //listening to image click
            buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(planNames!=null&&planNames.equalsIgnoreCase("Premium")){



                        Calendar c = Calendar.getInstance();
                        c.setTime(new Date ());

                        c.add(Calendar.DATE, 365);


                        // convert calendar to date
                        Date additionalDate = c.getTime();

                        Bundle bundlePlan = new Bundle();
                        PlanIntentData planData = new PlanIntentData ();
                        planData.setPassStartDate(""+new SimpleDateFormat ("MM/dd/yyyy").format(new Date()));
                        planData.setViewStartDate(""+new SimpleDateFormat("MMM dd,yyyy").format(new Date()));
                        planData.setPlanType("Advance,6");
                        planData.setPlanId(3);
                        planData.setPassEndDate(""+new SimpleDateFormat("MM/dd/yyyy").format(additionalDate));
                        planData.setViewEndDate(""+new SimpleDateFormat("MMM dd,yyyy").format(additionalDate));
                        planData.setPlanName("Advance");
                        planData.setAmount(80 * 3);
                        bundlePlan.putSerializable("PlanIntent",planData);
                        bundlePlan.putSerializable("Organization",organization);


                        Intent plan  = new Intent(context, PlanHundred.class);
                        plan.putExtras(bundlePlan);
                        ((Activity)context).startActivity(plan);




                    }else if(planNames!=null&&planNames.equalsIgnoreCase("Advance Yearly")){

                        Calendar c = Calendar.getInstance();
                        c.setTime(new Date());

                        c.add(Calendar.DATE, 365);


                        // convert calendar to date
                        Date additionalDate = c.getTime();
                        PlanIntentData planData = new PlanIntentData ();
                        Bundle bundlePlan = new Bundle();
                        planData.setPassStartDate(""+new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                        planData.setViewStartDate(""+new SimpleDateFormat("MMM dd,yyyy").format(new Date()));
                        planData.setPlanType("Advance,8");
                        planData.setPlanId(3);
                        planData.setPassEndDate(""+new SimpleDateFormat("MM/dd/yyyy").format(additionalDate));
                        planData.setViewEndDate(""+new SimpleDateFormat("MMM dd,yyyy").format(additionalDate));
                        planData.setPlanName("Advance");
                        planData.setAmount(2.4 * 365);
                        bundlePlan.putSerializable("PlanIntent",planData);
                        bundlePlan.putSerializable("Organization",organization);


                        Intent plan  = new Intent(context, PlanSubscribtionScreen.class);
                        plan.putExtras(bundlePlan);
                        ((Activity)context).startActivity(plan);

                    }else if(planNames!=null&&planNames.equalsIgnoreCase("Advance Halfly")){



                        Calendar c = Calendar.getInstance();
                        c.setTime(new Date());

                        c.add(Calendar.DATE, 180);


                        // convert calendar to date
                        Date additionalDate = c.getTime();
                        PlanIntentData planData = new PlanIntentData ();
                        Bundle bundlePlan = new Bundle();
                        planData.setPassStartDate(""+new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                        planData.setViewStartDate(""+new SimpleDateFormat("MMM dd,yyyy").format(new Date()));
                        planData.setPlanType("Advance,7");
                        planData.setPlanId(3);
                        planData.setPassEndDate(""+new SimpleDateFormat("MM/dd/yyyy").format(additionalDate));
                        planData.setViewEndDate(""+new SimpleDateFormat("MMM dd,yyyy").format(additionalDate));
                        planData.setPlanName("Advance");
                        planData.setAmount(80 * 6);
                        bundlePlan.putSerializable("PlanIntent",planData);
                        bundlePlan.putSerializable("Organization",organization);


                        Intent plan  = new Intent(context, PlanSubscribtionScreen.class);
                        plan.putExtras(bundlePlan);
                        ((Activity)context).startActivity(plan);

                    }


                    }
            });

        }






        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}