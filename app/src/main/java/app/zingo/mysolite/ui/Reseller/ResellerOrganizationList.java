package app.zingo.mysolite.ui.Reseller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import app.zingo.mysolite.adapter.ResellerOrganizationListAdapter;
import app.zingo.mysolite.model.Organization;
import app.zingo.mysolite.ui.landing.InternalServerErrorScreen;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.ThreadExecuter;
import app.zingo.mysolite.utils.Util;
import app.zingo.mysolite.WebApi.ResellerAPI;
import app.zingo.mysolite.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResellerOrganizationList extends AppCompatActivity {


    RecyclerView mOrganizationList;
    LinearLayout mNoRecord;

    private int mResellerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_reseller_organization_list);

            mOrganizationList = findViewById(R.id.organization_list);
            mNoRecord = findViewById(R.id.noRecordFound);
            mOrganizationList.setLayoutManager(new LinearLayoutManager( ResellerOrganizationList.this));

            mResellerId = PreferenceHandler.getInstance( ResellerOrganizationList.this).getResellerUserId();


            if(mResellerId!=0){
                getOrganizations(mResellerId);
            }else{
                mNoRecord.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }




        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void getOrganizations(final int mResellerId){


        final ProgressDialog progressDialog = new ProgressDialog( ResellerOrganizationList.this);
        progressDialog.setTitle("Loading Details..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                ResellerAPI apiService = Util.getClient().create( ResellerAPI.class);
                Call<ArrayList< Organization >> call = apiService.getOrganizationBySellerId(mResellerId);

                call.enqueue(new Callback<ArrayList< Organization >>() {
                    @Override
                    public void onResponse( Call<ArrayList< Organization >> call, Response<ArrayList< Organization >> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog!=null)
                                progressDialog.dismiss();
                            ArrayList< Organization > list = response.body();




                            if (list !=null && list.size()!=0) {

                                mNoRecord.setVisibility(View.GONE);

                                ResellerOrganizationListAdapter adapter = new ResellerOrganizationListAdapter( ResellerOrganizationList.this,list);
                                mOrganizationList.setAdapter(adapter);


                            }else{
                                mNoRecord.setVisibility(View.VISIBLE);

                                Toast.makeText( ResellerOrganizationList.this, "No Organizations under you ", Toast.LENGTH_SHORT).show();

                            }

                        }else {

                            if (progressDialog!=null)
                                progressDialog.dismiss();

                            if(statusCode==500){
                                Intent error = new Intent( ResellerOrganizationList.this, InternalServerErrorScreen.class);
                                startActivity(error);
                            }else{
                                mNoRecord.setVisibility(View.VISIBLE);
                                Toast.makeText( ResellerOrganizationList.this,  "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                            }


                        }
                    }

                    @Override
                    public void onFailure( Call<ArrayList< Organization >> call, Throwable t) {
                        // Log error here since request failed
                       /* if (progressDialog!=null)
                            progressDialog.dismiss();*/
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }
}
