package app.zingo.mysolite.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import app.zingo.mysolite.R;

public class FAQScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_faqscreen);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Join Us");



        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
