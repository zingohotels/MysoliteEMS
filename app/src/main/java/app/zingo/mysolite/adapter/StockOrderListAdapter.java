package app.zingo.mysolite.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import app.zingo.mysolite.Custom.MyTextView;
import app.zingo.mysolite.R;
import app.zingo.mysolite.WebApi.GeneralNotificationAPI;
import app.zingo.mysolite.model.GeneralNotification;
import app.zingo.mysolite.model.StockOrderDetailsModel;
import app.zingo.mysolite.model.StockOrdersModel;
import app.zingo.mysolite.ui.NewAdminDesigns.StockOrderDetailsScreen;
import app.zingo.mysolite.utils.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockOrderListAdapter extends RecyclerView.Adapter<StockOrderListAdapter.ViewHolder> {

    Context context;
    ArrayList < StockOrdersModel > stockOrdersModelArrayList;

    public StockOrderListAdapter(Context context,  ArrayList < StockOrdersModel > stockOrdersModelArrayList)
    {
        this.context = context;
        this.stockOrdersModelArrayList = stockOrdersModelArrayList;
    }
    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate( R.layout.stock_order_list_adapter,parent,false);
        return new ViewHolder (view);
    }

    @Override
    public void onBindViewHolder( final ViewHolder holder, final int position) {

        final StockOrdersModel stockOrdersModel = stockOrdersModelArrayList.get(position);

        if(stockOrdersModel!=null){


            holder.mGuestName.setText ( "Customer:"+stockOrdersModel.get_stockOrderPersonInfo ().get ( 0 ).getPersonName () );

            int quantity = 0;
            for( StockOrderDetailsModel sodm: stockOrdersModel.getStockOrderDetailsList ()){

                quantity = quantity+sodm.getQuantity ();
            }
            holder.mTotalPrice.setText ("Total Amount ₹"+ stockOrdersModel.getTotalAmount ());
            holder.mTotalQuantity.setText ("Quantity "+quantity);

            holder.mStock.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick ( View v ) {
                    showAlertDialog(stockOrdersModel);
                }
            } );


        }else{

        }





    }

    private void showAlertDialog(final StockOrdersModel stockOrdersModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.dialog_title);
        builder.setCancelable(true);

        builder.setPositiveButton(
                "View",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent next = new Intent ( context, StockOrderDetailsScreen.class );
                        Bundle bundle = new Bundle (  );
                        bundle.putSerializable ( "StockOrders",stockOrdersModel );
                        next.putExtras ( bundle );
                        (( Activity )context).startActivity ( next );
                        dialog.cancel();
                    }
                });

        builder.setNegativeButton(
                "Delete",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DeleteOrders(stockOrdersModel.getGeneralNotificationManagerId ());
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder.create();
        alert11.show();
    }

    private void DeleteOrders(int getGeneralNotificationManagerId) {
        GeneralNotificationAPI apiService = Util.getClient().create( GeneralNotificationAPI.class);
        Call <GeneralNotification> call = apiService.deleteNotification(getGeneralNotificationManagerId);
        call.enqueue (new Callback <GeneralNotification> () {
            @Override
            public void onResponse(Call <GeneralNotification> call, Response <GeneralNotification> response) {
                int statusCode = response.code();
                if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {
                    Intent refresh = new Intent(context,context.getClass());
                    refresh.putExtra ( "Type","Stock Orders" );
                    context.startActivity(refresh);
                    ((Activity)context).finish();

                }else {

                    Toast.makeText(context, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call <GeneralNotification> call, Throwable t) {
                Toast.makeText (context,"Please check your internet connection and try again",Toast.LENGTH_SHORT).show ();
            }
        });
    }

    @Override
    public int getItemCount() {
        return stockOrdersModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        MyTextView mGuestName,mTotalPrice,mTotalQuantity;
        LinearLayout mStock;
        //CardView mCategory;
        public ViewHolder( View itemView) {
            super(itemView);
            mGuestName = (MyTextView)itemView.findViewById( R.id.guest_name);
            mTotalPrice = (MyTextView)itemView.findViewById( R.id.total_price);
            mTotalQuantity = (MyTextView)itemView.findViewById( R.id.total_quantity);
            mStock = (LinearLayout ) itemView.findViewById( R.id.layout_main);
           // mCategory = (CardView) itemView.findViewById(R.id.category);

        }
    }

}