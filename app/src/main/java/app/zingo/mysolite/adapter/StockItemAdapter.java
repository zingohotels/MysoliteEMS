package app.zingo.mysolite.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import app.zingo.mysolite.R;
import app.zingo.mysolite.model.StockItemModel;
import app.zingo.mysolite.ui.NewAdminDesigns.StockItemScreen;

public class StockItemAdapter extends RecyclerView.Adapter< StockItemAdapter.ViewHolder> {

    Context context;
    ArrayList < StockItemModel > stockCategoryModelArrayList;

    public StockItemAdapter(Context context,  ArrayList < StockItemModel > stockCategoryModelArrayList)
    {
        this.context = context;
        this.stockCategoryModelArrayList = stockCategoryModelArrayList;
    }
    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate( R.layout.stock_category_adapter,parent,false);
        return new ViewHolder (view);
    }

    @Override
    public void onBindViewHolder( final ViewHolder holder, final int position) {

        final StockItemModel stockCategoryModel = stockCategoryModelArrayList.get(position);

        if(stockCategoryModel!=null){
            holder.mCategoryName.setText(stockCategoryModel.getStockItemName ());

            holder.mCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent updating = new Intent(context, StockItemScreen.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("StockItem",stockCategoryModel);
                    updating.putExtras(bundle);
                    (( Activity )context).startActivity(updating);


                }
            });

        }else{

        }





    }

    @Override
    public int getItemCount() {
        return stockCategoryModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mCategoryName;
        CardView mCategory;
        public ViewHolder( View itemView) {
            super(itemView);
            mCategoryName = (TextView)itemView.findViewById( R.id.category_name);
            mCategory = (CardView) itemView.findViewById(R.id.category);

        }
    }

}