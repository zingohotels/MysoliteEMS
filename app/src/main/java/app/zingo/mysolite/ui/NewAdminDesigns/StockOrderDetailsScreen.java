package app.zingo.mysolite.ui.NewAdminDesigns;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import app.zingo.mysolite.Custom.MyTextView;
import app.zingo.mysolite.adapter.StockOrderDetailsAdapter;
import app.zingo.mysolite.R;
import app.zingo.mysolite.model.StockOrderDetailsModel;
import app.zingo.mysolite.model.StockOrdersModel;

public class StockOrderDetailsScreen extends AppCompatActivity {

    StockOrdersModel stockOrders;

    MyTextView mGuestName,mTotalPrice,mTotalQuantity,mOrderBy,mDate,mMobile,mAddress;
    RecyclerView mList;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        try{
            setContentView ( R.layout.activity_stock_order_details_screen );

            mGuestName = (MyTextView)findViewById( R.id.guest_name);
            mTotalPrice = (MyTextView)findViewById( R.id.total_price);
            mTotalQuantity = (MyTextView)findViewById( R.id.total_quantity);
            mOrderBy = (MyTextView)findViewById( R.id.order_by);
            mDate = (MyTextView)findViewById( R.id.date);
            mMobile = (MyTextView)findViewById( R.id.mobile);
            mAddress = (MyTextView)findViewById( R.id.address);
            mList = (RecyclerView)findViewById(R.id.stock_orders_list);

            Bundle bundle = getIntent ().getExtras ();

            if(bundle!=null){

                stockOrders = ( StockOrdersModel )bundle.getSerializable ( "StockOrders" );
            }

            if(stockOrders!=null){


                if(stockOrders.get_stockOrderPersonInfo ()!=null&&stockOrders.get_stockOrderPersonInfo ().size ()!=0){

                    mGuestName.setText ( "Customer:"+stockOrders.get_stockOrderPersonInfo ().get ( 0 ).getPersonName () );
                    String mobile = stockOrders.get_stockOrderPersonInfo ().get ( 0 ).getMobile ();
                    String address = stockOrders.get_stockOrderPersonInfo ().get ( 0 ).getShippingAddress ();

                    if(mobile!=null&&!mobile.isEmpty ()){
                        mMobile.setText ( mobile );
                    }else{
                        mMobile.setVisibility ( View.GONE );
                    }

                    if(address!=null&&!address.isEmpty ()){
                        mAddress.setText ( address );
                    }else{
                        mAddress.setVisibility ( View.GONE );


                    }

                }

                int quantity = 0;
                for( StockOrderDetailsModel sodm: stockOrders.getStockOrderDetailsList ()){

                    quantity = quantity+sodm.getQuantity ();
                }

                if(stockOrders.getUserName ()!=null&&!stockOrders.getUserName ().isEmpty ()){
                    mOrderBy.setText ( "Order by:"+stockOrders.getUserName () );
                }

                if(stockOrders.getOrderDate ()!=null&&!stockOrders.getOrderDate ().isEmpty ()){
                    mDate.setText ( "Order Date:"+stockOrders.getOrderDate () );
                }

                StockOrderDetailsAdapter itemsAdapter = new StockOrderDetailsAdapter ( StockOrderDetailsScreen.this,stockOrders.getStockOrderDetailsList ());
                mList.setAdapter (itemsAdapter);
                mTotalPrice.setText ("Total Amount ₹"+ stockOrders.getTotalAmount ());
                mTotalQuantity.setText ("Quantity "+quantity);
            }
        }catch ( Exception e ){
            e.printStackTrace ();
        }

    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        int id = item.getItemId ();
        if(id==android.R.id.home){
            StockOrderDetailsScreen.this.finish ();
        }
        return super.onOptionsItemSelected (item);
    }

}
