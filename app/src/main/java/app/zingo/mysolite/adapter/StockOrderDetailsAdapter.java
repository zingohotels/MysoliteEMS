package app.zingo.mysolite.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.zingo.mysolite.R;
import app.zingo.mysolite.model.StockOrderDetailsModel;

public class StockOrderDetailsAdapter extends RecyclerView.Adapter< StockOrderDetailsAdapter.ViewHolder> {

    private Context context;
    private ArrayList < StockOrderDetailsModel > items;

    public StockOrderDetailsAdapter(Context context, ArrayList < StockOrderDetailsModel > items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from (context).inflate ( R.layout.add_cart_adapter, null);
        return new ViewHolder (itemView);
    }

    @Override
    public void onBindViewHolder( @NonNull final ViewHolder holder, final int position) {

        if(items!=null&&items.size ()!=0){

            holder.txt_price.setText ("\u20B9 " + (items.get (position).getTotalPrice ()));
            holder.txt_quantity.setText ("Quantity: "+items.get (position).getQuantity ());
            holder.txt_product_name.setText (items.get (position).getStockItem ().getStockItemName ());


            Picasso.with (context).load (items.get (position).getStockItem ().getStockItemImage ()).into (holder.img_product);


            holder.removeItem.setVisibility ( View.GONE );



        }else{

        }
    }



    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size ();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView img_product;
        private TextView txt_product_name,txt_price,txt_quantity;
        Button removeItem;


        public ViewHolder(View itemView) {
            super(itemView);

            img_product=itemView.findViewById( R.id.image_cartlist);
            txt_product_name=itemView.findViewById(R.id.cart_item_name);
            txt_price=itemView.findViewById(R.id.cart_item_price);
            txt_quantity=itemView.findViewById(R.id.cart_item_quantity);
            removeItem=itemView.findViewById(R.id.layout_removeItem);

        }

        @Override
        public void onClick(View v) {

            //iitemClickListner.onClick(v);

        }
    }
}