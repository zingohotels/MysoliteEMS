package app.zingo.mysolite.ui.newemployeedesign;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import app.zingo.mysolite.adapter.ExpenseListAdapter;
import app.zingo.mysolite.model.Expenses;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.Util;
import app.zingo.mysolite.WebApi.ExpensesApi;
import app.zingo.mysolite.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenseListScreen extends AppCompatActivity {

    RecyclerView mExpenseList;

    int employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_expense_list_screen);

            mExpenseList = findViewById(R.id.expense_lists);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                employeeId = bundle.getInt("EmployeeId");
            }



            if(employeeId!=0){
                getExpenses(employeeId);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void getExpenses(final int employeeId){


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Details..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ExpensesApi apiService = Util.getClient().create(ExpensesApi.class);
        Call<ArrayList<Expenses>> call = apiService.getExpenseByEmployeeIdAndOrganizationId(PreferenceHandler.getInstance( ExpenseListScreen.this).getCompanyId(),employeeId);

        call.enqueue(new Callback<ArrayList<Expenses>>() {
            @Override
            public void onResponse(Call<ArrayList<Expenses>> call, Response<ArrayList<Expenses>> response) {
                int statusCode = response.code();
                if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                    if (progressDialog != null&&progressDialog.isShowing())
                        progressDialog.dismiss();
                    ArrayList<Expenses> list = response.body();



                    if (list !=null && list.size()!=0) {

                        ExpenseListAdapter adapter = new ExpenseListAdapter( ExpenseListScreen.this,list);
                        mExpenseList.setAdapter(adapter);

                    }else{

                        Toast.makeText( ExpenseListScreen.this, "No Expenses ", Toast.LENGTH_SHORT).show();

                    }

                }else {

                    if (progressDialog != null&&progressDialog.isShowing())
                        progressDialog.dismiss();

                    Toast.makeText( ExpenseListScreen.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Expenses>> call, Throwable t) {
                // Log error here since request failed
                if (progressDialog != null&&progressDialog.isShowing())
                    progressDialog.dismiss();
                Log.e("TAG", t.toString());
            }
        });
    }
}
