package app.zingo.mysolite.ui.NewAdminDesigns;

import android.app.ProgressDialog;

import androidx.multidex.BuildConfig;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import app.zingo.mysolite.R;
import app.zingo.mysolite.WebApi.StockCategoriesApi;
import app.zingo.mysolite.adapter.StockCategoriesGridAdapter;
import app.zingo.mysolite.model.StockCategoryModel;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetailerHomeScreen extends AppCompatActivity {
    private ProgressBar mProgressBar;
    GridView mCategoryGrid;
    DrawerLayout drawer;
    ArrayList< StockCategoryModel > categoriesArrayList = new ArrayList <> (  );

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_retailer_home_screen );

        try{

            setContentView ( R.layout.activity_retailer_home_screen );

            getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);



            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            drawer = ( DrawerLayout ) findViewById(R.id.drawer_layout);
         /*   ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();*/


            mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
            mCategoryGrid = ( GridView ) findViewById(R.id.stock_category_grid);


            getStockCategories();




        }catch(Exception e){
            e.printStackTrace ();
        }
    }


    public void getStockCategories() {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        int companyId = PreferenceHandler.getInstance ( RetailerHomeScreen.this ).getCompanyId ();
        if(PreferenceHandler.getInstance ( RetailerHomeScreen.this ).getCompanyId ()!=0){
            companyId = PreferenceHandler.getInstance ( RetailerHomeScreen.this ).getCompanyId ();
        }

        StockCategoriesApi apiService = Util.getClient().create(StockCategoriesApi.class);

        Call < ArrayList< StockCategoryModel > > call = apiService.getStockCategoryByOrganizationId ( companyId );

        call.enqueue(new Callback <ArrayList<StockCategoryModel>> () {
            @Override
            public void onResponse(Call<ArrayList<StockCategoryModel>> call, Response <ArrayList<StockCategoryModel>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||statusCode==204) {

                        categoriesArrayList= response.body ();

                        if(categoriesArrayList!=null&&categoriesArrayList.size ()!=0){

                            StockCategoriesGridAdapter adapter = new StockCategoriesGridAdapter (RetailerHomeScreen.this,categoriesArrayList);
                            mCategoryGrid.setAdapter(adapter);

                        }


                    }else {
                        Toast.makeText(RetailerHomeScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex)
                {

                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure(Call<ArrayList<StockCategoryModel>> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                //Toast.makeText(CustomerCreation.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }
}
