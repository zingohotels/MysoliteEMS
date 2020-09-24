package app.zingo.mysolite.ui.NewAdminDesigns;

import android.app.ProgressDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import app.zingo.mysolite.WebApi.StockOrders;
import app.zingo.mysolite.R;
import app.zingo.mysolite.model.StockOrdersModel;
import app.zingo.mysolite.utils.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockOrdersScreen extends AppCompatActivity {

    RecyclerView mList;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );

        try{
            setContentView ( R.layout.activity_stock_orders_screen );

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Stock Orders");

            mList = (RecyclerView)findViewById(R.id.stock_options_type_list);

        }catch ( Exception e ){
            e.printStackTrace ();
        }

    }

    public void getStockOrders() {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        StockOrders apiService = Util.getClient().create(StockOrders.class);

        Call < ArrayList < StockOrdersModel > > call = apiService.getStockOrder();

        call.enqueue(new Callback <ArrayList<StockOrdersModel>> () {
            @Override
            public void onResponse(Call<ArrayList<StockOrdersModel>> call, Response <ArrayList<StockOrdersModel>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||statusCode==204) {


                        ArrayList<StockOrdersModel> stockCategoryModelsList = response.body();
                        ArrayList<StockOrdersModel> stockCategoryModelsListOrg = new ArrayList <> (  );
                        if(stockCategoryModelsList != null && stockCategoryModelsList.size()!=0 )
                        {

                            for ( StockOrdersModel so: stockCategoryModelsList) {

                               //if(so.getO)

                            }
                         /*   StockCategoryListAdapter adapter = new StockCategoryListAdapter(StockOrdersScreen.this,stockCategoryModelsList);
                            mList.setAdapter(adapter);*/

                        }
                        else
                        {


                        }






                    }else {
                        Toast.makeText(StockOrdersScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<ArrayList<StockOrdersModel>> call, Throwable t) {

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
