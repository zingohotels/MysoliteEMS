package app.zingo.mysolite.ui.Common;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.zingo.mysolite.adapter.ReportCustomDataAdapter;
import app.zingo.mysolite.model.ReportDataEmployee;
import app.zingo.mysolite.R;

public class ReportBulkDataDisplayScreen extends AppCompatActivity {

    ArrayList< ReportDataEmployee > reportDataEmployeeArrayList ;

    RecyclerView mReportList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_report_bulk_data_display_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Report Management");

            mReportList = findViewById(R.id.report_list);
            mReportList.setNestedScrollingEnabled(false);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                reportDataEmployeeArrayList = (ArrayList< ReportDataEmployee >)bundle.getSerializable("Data");

            }

            if(reportDataEmployeeArrayList!=null&&reportDataEmployeeArrayList.size()!=0){

                setData(reportDataEmployeeArrayList);

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void setData(ArrayList< ReportDataEmployee > reportDataEmployees){

        ReportCustomDataAdapter adapter = new ReportCustomDataAdapter( ReportBulkDataDisplayScreen.this,reportDataEmployees);
        mReportList.setAdapter(adapter);


    }
}
