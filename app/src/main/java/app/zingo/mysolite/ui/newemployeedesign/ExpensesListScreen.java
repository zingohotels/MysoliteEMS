package app.zingo.mysolite.ui.newemployeedesign;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import app.zingo.mysolite.R;

public class ExpensesListScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_expenses_list_screen);

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
