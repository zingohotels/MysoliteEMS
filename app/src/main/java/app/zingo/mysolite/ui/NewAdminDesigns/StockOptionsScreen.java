package app.zingo.mysolite.ui.NewAdminDesigns;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;

import java.util.ArrayList;

import app.zingo.mysolite.adapter.NavigationAdapter;
import app.zingo.mysolite.R;
import app.zingo.mysolite.model.Navigation_Model;

public class StockOptionsScreen extends AppCompatActivity {
    String[] tv1 = {"Stock Categories","Stock SubCategories","Stock Brands(Retailer's Home Image)","Stock Items","Stock Orders","Price Update"};//
    Integer[] imagehistory = {R.drawable.stocks, R.drawable.stocks,
            R.drawable.stocks, R.drawable.stocks, R.drawable.stocks, R.drawable.stocks};//
    Integer[] image1 ={R.drawable.ic_chevron_right_black_24dp, R.drawable.ic_chevron_right_black_24dp, R.drawable.ic_chevron_right_black_24dp,
            R.drawable.ic_chevron_right_black_24dp,R.drawable.ic_chevron_right_black_24dp,R.drawable.ic_chevron_right_black_24dp};//
    private RecyclerView recyclerView;
    private NavigationAdapter navigation_adapter;
    private ArrayList < Navigation_Model > navigation_models;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        try{
            setContentView ( R.layout.activity_stock_options_screen );
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Organization Profile");
            recyclerView = findViewById(R.id.stock_options_list);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager ( StockOptionsScreen.this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator ());
            navigation_models = new ArrayList<>();

            for(int i=0;i < imagehistory.length;i++) {
                Navigation_Model ab = new Navigation_Model (tv1[i],imagehistory[i],image1[i]);
                navigation_models.add(ab);
            }
            navigation_adapter = new NavigationAdapter ( StockOptionsScreen.this,navigation_models);
            recyclerView.setAdapter(navigation_adapter);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        int id = item.getItemId ();
        if(id==android.R.id.home){
            StockOptionsScreen.this.finish ();
        }
        return super.onOptionsItemSelected (item);
    }
}
