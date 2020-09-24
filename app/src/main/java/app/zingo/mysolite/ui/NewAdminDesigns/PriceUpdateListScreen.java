package app.zingo.mysolite.ui.NewAdminDesigns;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import app.zingo.mysolite.Custom.MyTextView;
import app.zingo.mysolite.Service.PriceUpdatingService;
import app.zingo.mysolite.WebApi.StockItemPricingsApi;
import app.zingo.mysolite.WebApi.StockItemsApi;
import app.zingo.mysolite.R;
import app.zingo.mysolite.model.StockItemModel;
import app.zingo.mysolite.model.StockItemPricingModel;
import app.zingo.mysolite.model.StockPriceUpdateData;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PriceUpdateListScreen extends AppCompatActivity {

    RecyclerView mItemList;
    EditText mSearch;

    ProgressDialog mProgressBar;

    ArrayList < StockItemModel > itemsArrayList;

    MyTextView mUpdate;

    double total=0;
    private ArrayList< StockItemModel > stockItemArrayList = new ArrayList <> (  );
    private ArrayList< StockPriceUpdateData > stockItemUpdateArrayList = new ArrayList <> (  );
    private ArrayList< StockItemPricingModel > stockItemPricingModels = new ArrayList <> (  );

    ItemAdapter adapter;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );

        try{

            setContentView ( R.layout.activity_price_update_list_screen );
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Price Update");


            mItemList = findViewById(R.id.item_list);
            mSearch = findViewById(R.id.search_by_name);

            mUpdate = (MyTextView ) findViewById(R.id.txttotal);
            mProgressBar = new ProgressDialog(this);
            itemsArrayList = new ArrayList<>();
            getItems();

            mSearch.addTextChangedListener(new TextWatcher () {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    searchByname(charSequence.toString().toLowerCase());

                }

                @Override
                public void afterTextChanged(final Editable editable) {


                }
            });

            mUpdate.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick ( View v ) {

                    Intent intent=new Intent( PriceUpdateListScreen.this, PriceUpdatingService.class);
                    Bundle b=new Bundle();
                    b.putSerializable ("PriceUpdateData", stockItemUpdateArrayList);
                    b.putSerializable ("PriceUpdate", stockItemPricingModels);
                    intent.putExtras(b);
                    startService(intent);
                    Toast.makeText ( PriceUpdateListScreen.this , "Updating..." , Toast.LENGTH_SHORT ).show ( );
                    PriceUpdateListScreen.this.finish ();

                }
            } );

        }catch ( Exception e ){
            e.printStackTrace ();
        }

    }

    public void show(String message) {

        mProgressBar.setCancelable(false);
        mProgressBar.setMessage(message);
        mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressBar.show();


    }

    private void searchByname(String s) {

        ArrayList<StockItemModel> filteredList = new ArrayList<>();

        try{
            for(int i=0;i<itemsArrayList.size();i++)
            {

                String hotelName = "";

                if(itemsArrayList.get(i).getStockItemName ()!=null){
                    hotelName= itemsArrayList.get(i).getStockItemName();
                }

                if(hotelName.toLowerCase().contains(s.toLowerCase()))
                {
                    filteredList.add(itemsArrayList.get(i));
                }


            }

            adapter = new ItemAdapter ( PriceUpdateListScreen.this,filteredList);
            mItemList.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }catch (Exception e){
            e.printStackTrace();

        }




    }

    public void getItems() {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        StockItemsApi apiService = Util.getClient().create(StockItemsApi.class);

        Call < ArrayList < StockItemModel > > call = apiService.getStockItemsByOrganizationId ( PreferenceHandler.getInstance ( PriceUpdateListScreen.this ).getCompanyId () );

        call.enqueue(new Callback <ArrayList<StockItemModel>> () {
            @Override
            public void onResponse(Call<ArrayList<StockItemModel>> call, Response <ArrayList<StockItemModel>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||statusCode==204) {

                        itemsArrayList = response.body ();

                        if(itemsArrayList!=null&&itemsArrayList.size ()!=0){

                            //getStockItemPricings(itemsArrayList);
                            adapter = new ItemAdapter ( PriceUpdateListScreen.this, itemsArrayList);
                            mItemList.setAdapter(adapter);

                        }



                    }else {
                        Toast.makeText( PriceUpdateListScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<ArrayList<StockItemModel>> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                //Toast.makeText(CustomerCreation.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }
    public void getStockItemPricings(final ArrayList<StockItemModel> stockItemsArrayList) {


        StockItemPricingsApi apiService = Util.getClient().create( StockItemPricingsApi.class);

        Call < ArrayList < StockItemPricingModel > > call = apiService.getStockItemPricing ( );

        call.enqueue(new Callback <ArrayList<StockItemPricingModel>> () {
            @Override
            public void onResponse(Call<ArrayList<StockItemPricingModel>> call, Response <ArrayList<StockItemPricingModel>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {


                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||statusCode==204) {

                        ArrayList<StockItemPricingModel> stockItemPricingsArrayList = response.body ();

                        ArrayList<StockItemModel> itemsArrayList = new ArrayList <> (  );

                        if(stockItemPricingsArrayList!=null&&stockItemPricingsArrayList.size ()!=0){

                            for(StockItemModel stockItemModel:stockItemsArrayList){
                                ArrayList<StockItemPricingModel> stockItemPricings = new ArrayList <> ();
                                for (StockItemPricingModel stockItemPricingModel:stockItemPricingsArrayList){
                                    if(stockItemModel.getStockItemId () == stockItemPricingModel.getStockItemId ()){
                                        stockItemPricings.add (stockItemPricingModel);
                                    }
                                }
                                stockItemModel.setStockItemPricingList (stockItemPricings);
                                itemsArrayList.add (stockItemModel);
                            }


                            adapter = new ItemAdapter ( PriceUpdateListScreen.this, itemsArrayList);
                            mItemList.setAdapter(adapter);

                        }else{
                            ItemAdapter adapter = new ItemAdapter ( PriceUpdateListScreen.this, itemsArrayList);
                            mItemList.setAdapter(adapter);
                        }



                    }else {
                        Toast.makeText( PriceUpdateListScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex)
                {


                    ex.printStackTrace();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure(Call<ArrayList<StockItemPricingModel>> call, Throwable t) {


                //Toast.makeText(CustomerCreation.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public class ItemAdapter extends RecyclerView.Adapter< ItemAdapter.ViewHolder>{

        private Context context;
        private ArrayList<StockItemModel> list;
        public ItemAdapter(Context context, ArrayList<StockItemModel> list) {

            this.context = context;
            this.list = list;

        }

        @Override
        public ItemAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
            try{
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_price_update_adapter, parent, false);
                ItemAdapter.ViewHolder viewHolder = new ItemAdapter.ViewHolder (v);
                return viewHolder;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }

        @Override
        public void onBindViewHolder( final ItemAdapter.ViewHolder holder, int position) {

            try {
                final StockItemModel dto = list.get(position);
              if(dto!=null){

                  holder.item_name.setText ( ""+dto.getStockItemName () );

                  if(dto.getStockItemPricingList ()!=null&&dto.getStockItemPricingList ().size ()!=0){

                      holder.p_rate.setText ( ""+dto.getStockItemPricingList ().get ( 0).getSellingPrice () );
                      holder.n_rate.setText ( ""+dto.getStockItemPricingList ().get ( 0).getSellingPrice () );

                      holder.n_rate.addTextChangedListener(new TextWatcher() {
                          @Override
                          public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                          }

                          @Override
                          public void onTextChanged(CharSequence s, int start, int before, int count) {

                              priceUpdate(dto,dto.getStockItemPricingList ().get ( 0 ),holder.n_rate,holder.p_rate);

                          }

                          @Override
                          public void afterTextChanged(Editable s) {

                          }
                      });
                  }

              }


            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public long getItemId ( int position ) {
            return position;
        }

        @Override
        public int getItemViewType ( int position ) {
            return position;
        }

        class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

            public TextView item_name;
            public EditText p_rate,n_rate;
//

            public ViewHolder(View itemView) {
                super(itemView);
                itemView.setClickable(true);
                try{
                    item_name =itemView.findViewById(R.id.item_name);
                    p_rate =itemView.findViewById(R.id.p_rate);
                    n_rate =itemView.findViewById(R.id.n_rate);

                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        }

    }

    public void priceUpdate(final StockItemModel dto,final StockItemPricingModel sip,final EditText price,final EditText oldRate){

        String  sellRate = price.getText().toString();

        StockPriceUpdateData spu = new StockPriceUpdateData ();
        spu.setItemId ( dto.getStockItemId () );
        spu.setItemName ( dto.getStockItemName () );
        spu.setOldPrice ( oldRate.getText ().toString () );

        if(mUpdate.getVisibility ()==View.GONE){
            mUpdate.setVisibility ( View.VISIBLE );
        }

        if(sellRate.isEmpty()){

            sip.setSellingPrice ( 0 );
            spu.setNewPrice (""+0);
        }else{

            double priceRate = Double.parseDouble (sellRate);
            sip.setSellingPrice ( priceRate );
            spu.setNewPrice (""+priceRate);
        }


        boolean value = false;
        for(int i =0;i<stockItemUpdateArrayList.size ();i++){
            if(stockItemUpdateArrayList.get ( i ).getItemId ()==spu.getItemId ()){
                stockItemUpdateArrayList.set(i,spu);
                value = true;
                break;

            }


        }

        if(!value){

            stockItemUpdateArrayList.add ( spu );
        }


        if(stockItemPricingModels.contains ( sip )){

            int index = stockItemPricingModels.indexOf ( sip );

            stockItemPricingModels.set(index,sip);
        }else{

            stockItemPricingModels.add ( sip );

        }


    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        int id = item.getItemId ();
        if(id==android.R.id.home){
            PriceUpdateListScreen.this.finish ();
        }
        return super.onOptionsItemSelected (item);
    }

}
