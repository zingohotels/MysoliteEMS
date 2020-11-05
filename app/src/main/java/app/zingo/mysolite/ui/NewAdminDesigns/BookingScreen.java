package app.zingo.mysolite.ui.NewAdminDesigns;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.zingo.mysolite.R;
import app.zingo.mysolite.WebApi.EmployeeApi;
import app.zingo.mysolite.WebApi.GeneralNotificationAPI;
import app.zingo.mysolite.WebApi.StockOrders;
import app.zingo.mysolite.adapter.CustomerSpinnerAdapter;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.model.GeneralNotification;
import app.zingo.mysolite.model.StockItemModel;
import app.zingo.mysolite.model.StockOrderDetailsModel;
import app.zingo.mysolite.model.StockOrderPersonInfoModel;
import app.zingo.mysolite.model.StockOrdersModel;
import app.zingo.mysolite.utils.Constants;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingScreen extends AppCompatActivity {
    EditText mAddress;
    TextInputEditText mClientName,mClientMobile,mClientMail;
    Spinner customerSpinner;
    LinearLayout ClientNameLayout,mSpinnerLay;
    TextView mPay,mQuantity,mTotal;
    ImageView mBack;
    ArrayList < StockItemModel > stockItemsList= new ArrayList <> ();
    ArrayList< StockOrderDetailsModel > stockOrderDetailsDummy = new ArrayList <> ();
    ArrayList< Employee > customerArrayList;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        try{
            setContentView ( R.layout.activity_booking_screen );
            mBack = (ImageView ) findViewById(R.id.back);
            mClientName = findViewById(R.id.client_name);
            mClientMobile = findViewById(R.id.client_contact_number);
            mClientMail = findViewById(R.id.client_contact_email);
            mAddress = (EditText)findViewById(R.id.address);
            customerSpinner = findViewById(R.id.customer_spinner_adpter);
            ClientNameLayout = findViewById(R.id.client_name_layout);
            mSpinnerLay = findViewById(R.id.spinner_lay);

            mPay = ( TextView ) findViewById(R.id.pay);
            mQuantity = ( TextView ) findViewById(R.id.pay_total_item_count);
            mTotal = ( TextView ) findViewById(R.id.pay_amount);
          /*  mName.setText ( ""+ PreferenceHandler.getInstance ( BookingScreen.this ).getUserFullName () );
            mEmail.setText ( ""+PreferenceHandler.getInstance ( BookingScreen.this ).getUserEmail () );
            mPhone.setText ( ""+PreferenceHandler.getInstance ( BookingScreen.this ).getPhoneNumber () );
            mAddress.setText ( ""+PreferenceHandler.getInstance ( BookingScreen.this ).getAddress () );*/
            getSavedCartData ( BookingScreen.this );
            getCustomers(PreferenceHandler.getInstance( BookingScreen.this).getCompanyId());
            mBack.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick ( View v ) {
                    BookingScreen.this.finish ();
                }
            });

            customerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(customerArrayList!=null && customerArrayList.size()!=0){
                        if(customerArrayList.get(position).getEmployeeName ()!=null && customerArrayList.get(position).getEmployeeName ().equalsIgnoreCase("Others")) {
                            mClientMobile.setText("");
                            mClientName.setText("");
                            mClientMail.setText("");
                            mAddress.setText ("");
                            ClientNameLayout.setVisibility(View.VISIBLE);
                        }
                        else {
                            mClientMobile.setText(""+customerArrayList.get(position).getPhoneNumber ());
                            mClientName.setText(""+customerArrayList.get(position).getEmployeeName ());
                            mClientMail.setText(""+customerArrayList.get(position).getPrimaryEmailAddress ());
                            mAddress.setText(""+customerArrayList.get(position).getAddress ());
                            ClientNameLayout.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            mPay.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick ( View v ) {
                    String name = mClientName.getText().toString();
                    String email = mClientMail.getText().toString();
                    String phone = mClientMobile.getText().toString();
                    String address = mAddress.getText().toString();
                    if(name.isEmpty()){
                        mClientName.setError("Name required");
                        mClientName.requestFocus();
                    }else if( email.isEmpty()){
                        mClientMail.setError("Email required");
                        mClientMail.requestFocus();
                    }else if( phone.isEmpty()){
                        mClientMobile.setError("Mobile number required");
                        mClientMobile.requestFocus();
                    }else if( address.isEmpty()){
                        mAddress.setError("Address required");
                        mAddress.requestFocus();
                    }else{
                        StockOrdersModel so = new StockOrdersModel ();
                        double totalAmount = 0;
                        if(stockItemsList!=null&&stockItemsList.size ()!=0){
                            ArrayList< StockOrderDetailsModel > stockOrderDetails = new ArrayList <> (  );
                            for ( StockItemModel stockItems: stockItemsList) {
                                StockOrderDetailsModel sod = new StockOrderDetailsModel ();
                                // sod.setStockItem ( null );
                                int stockItemId = stockItems.getStockItemId ();
                                int stockQuantity = stockItems.getQuantity ();
                                sod.setStockItemId ( stockItemId );
                                sod.setQuantity ( stockQuantity );

                                if(stockItems.getStockItemPricingList ()!=null&&stockItems.getStockItemPricingList ().size ()!=0){
                                    totalAmount = totalAmount+ stockItems.getQuantity ()* stockItems.getStockItemPricingList ().get ( 0 ).getSellingPrice ();
                                    sod.setTotalPrice ( stockItems.getQuantity ()* stockItems.getStockItemPricingList ().get ( 0 ).getSellingPrice ());
                                }else{
                                    sod.setTotalPrice ( 0 );
                                }
                                // sod.setStockItem ( null );
                                stockOrderDetails.add ( sod );
                              /*  sods = sod;
                                sods.setStockItem ( stockItems );
                                stockOrderDetailsDummy.add ( sods );*/
                            }
                            so.setStockOrderDetailsList ( stockOrderDetails );
                            ArrayList< StockOrderPersonInfoModel > sopList = new ArrayList <> (  );
                            StockOrderPersonInfoModel sop = new StockOrderPersonInfoModel ();
                            sop.setPersonName ( name );
                            sop.setEmail ( email );
                            sop.setMobile ( phone );
                            sop.setShippingAddress ( address );
                            sopList.add ( sop );
                            so.set_stockOrderPersonInfo ( sopList );
                            so.setTotalAmount ( totalAmount );
                            so.setOrderNumber (""+ new Date ().getTime ());
                            so.setOrganizationId ( PreferenceHandler.getInstance ( BookingScreen.this ).getCompanyId ());
                            so.setUserName ( ""+PreferenceHandler.getInstance ( BookingScreen.this ).getUserFullName ());
                            so.setUserRefId ( ""+PreferenceHandler.getInstance ( BookingScreen.this ).getUserId ());
                            addStockOrders(so);
                        }else{
                            Toast.makeText ( BookingScreen.this , "Something went wrong" , Toast.LENGTH_SHORT ).show ( );
                        }
                    }
                }
            });

        }catch ( Exception e ){
            e.printStackTrace ();
        }
    }

    public String getSavedCartData( Context context) {
        String json = null;
        try {
            File f = new File(context.getFilesDir().getPath() + "/" + "AddToCart.json");
            //check whether file exists
            System.out.println ("File path "+context.getFilesDir().getPath() + "/" + "AddToCart.json");
            FileInputStream is = new FileInputStream (f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            // return new String(buffer);
            json = new String(buffer, "UTF-8");
            parseJSON(json);
        } catch ( IOException e) {
            Log.e("TAG", "Error in Reading: " + e.getLocalizedMessage());
            return null;
        }
        return json;
    }

    private void parseJSON(String jsonString) {
        Gson gson = new Gson();
        Type type = new TypeToken < List < StockItemModel > > (){}.getType();
        stockItemsList = gson.fromJson(jsonString, type);
        double total = 0;

        for ( StockItemModel stockItems:stockItemsList ) {
            StockOrderDetailsModel sods = new StockOrderDetailsModel ();
            int stockItemId = stockItems.getStockItemId ();
            int stockQuantity = stockItems.getQuantity ();
            sods.setStockItemId ( stockItemId );
            sods.setQuantity ( stockQuantity );
            sods.setStockItem ( stockItems );

            if(stockItems.getStockItemPricingList ()!=null&&stockItems.getStockItemPricingList ().size ()!=0){
                total = total+(stockItems.getQuantity ()*stockItems.getStockItemPricingList ().get ( 0 ).getSellingPrice ());
                sods.setTotalPrice ( stockItems.getQuantity ()* stockItems.getStockItemPricingList ().get ( 0 ).getSellingPrice ());
            }else{
                total = total+0;
            }
            stockOrderDetailsDummy.add ( sods );
        }
        /// mPay.setText("TOTAL : ₹ "+total);
        mTotal.setText("₹ "+total);
        mQuantity.setText(""+stockItemsList.size ());
    }

    public void addStockOrders(final StockOrdersModel scm) {
        Gson gson = new Gson();
        String jsons = gson.toJson ( scm);
        System.out.println ("JSON "+jsons );
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();
        StockOrders apiService = Util.getClient().create(StockOrders.class);
        Call <StockOrdersModel> call = apiService.addStockOrders (scm);
        call.enqueue(new Callback <StockOrdersModel> () {
            @Override
            public void onResponse(Call<StockOrdersModel> call, Response <StockOrdersModel> response) {
                try {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {
                        StockOrdersModel s = response.body();
                        if(s!=null){
                            Toast.makeText(BookingScreen.this, "Stock Order created successfully", Toast.LENGTH_SHORT).show();
                            // BookingScreen.this.finish();

                            /*StockOrderNotificationManagers snnm = new StockOrderNotificationManagers ();
                            snnm.setStockOrder ( response.body () );
                            snnm.setStockOrderId ( response.body ().getStockOrderId () );
                            snnm.setOrganizationId ( Constants.ORG_ID);
                            snnm.setTitle ("Stock Order" );
                            snnm.setMessage ("Stock Order" );
                            snnm.setSenderId ( Constants.SENDER_ID );
                            snnm.setServerId (Constants.SERVER_ID );
                            snnm.setNotificationDateTime (new SimpleDateFormat ( "MMM dd,yyyy hh:mm a" , Locale.US).format ( new Date (  ) ) );
                            snnm.setUserId ( PreferenceHandler.getInstance ( BookingScreen.this ).getEmployeeUserId () );
                            snnm.setStockOrderId ( response.body ().getStockOrderId () );
                            sendStockOrderNm(snnm);*/
                            addToCart();
                           /* scm.setStockOrderId ( response.body ().getStockOrderId () );
                            scm.setStockOrderDetailsList (som);*/
                            response.body ().setStockOrderDetailsList ( stockOrderDetailsDummy  );
                            Gson gson = new Gson();
                            String json = gson.toJson (response.body ());
                            GeneralNotification gm = new GeneralNotification();
                            gm.setOrganizationId (PreferenceHandler.getInstance ( BookingScreen.this ).getCompanyId ());
                            gm.setEmployeeId (809);
                            gm.setSenderId( Constants.SENDER_ID);
                            gm.setServerId(Constants.SERVER_ID);
                            gm.setTitle("Stock Order");
                            gm.setReason ( ""+new SimpleDateFormat ( "MMM dd,yyyy", Locale.US ).format ( new Date() ) );
                            gm.setMessage(""+json);
                            sendStockOrderNm(gm);
                        }

                    }else {
                        Toast.makeText(BookingScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex) {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure( Call < StockOrdersModel > call, Throwable t) {
                if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                //Toast.makeText(CustomerCreation.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });
    }

    public void sendStockOrderNm(final GeneralNotification scm) {
        GeneralNotificationAPI apiService = Util.getClient().create(GeneralNotificationAPI.class);
        Call <ArrayList<String>> call = apiService.sendGeneralNotification ( scm);
        call.enqueue(new Callback <ArrayList<String>> () {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response <ArrayList<String>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try {
                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201|| statusCode == 202) {
                        saveStockOrderNm(scm);
                    }else {
                        Toast.makeText(BookingScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                        saveStockOrderNm(scm);
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    saveStockOrderNm(scm);
                }
            }

            @Override
            public void onFailure( Call < ArrayList<String> > call, Throwable t) {
                saveStockOrderNm(scm);
            }
        });
    }

    public void saveStockOrderNm(final GeneralNotification scm) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();
        GeneralNotificationAPI apiService = Util.getClient().create(GeneralNotificationAPI.class);
        Call <GeneralNotification> call = apiService.saveGeneralNotification ( scm);
        call.enqueue(new Callback <GeneralNotification> () {
            @Override
            public void onResponse(Call<GeneralNotification> call, Response <GeneralNotification> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201|| statusCode == 202) {
                        Intent main = new Intent (BookingScreen.this,RetailerHomeScreen.class );
                        startActivity ( main );
                        finishAffinity ();
                    }else {
                        Toast.makeText(BookingScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                        Intent main = new Intent ( BookingScreen.this,RetailerHomeScreen.class );
                        startActivity ( main );
                        finishAffinity ();
                    }
                }
                catch (Exception ex) {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                    Intent main = new Intent ( BookingScreen.this,RetailerHomeScreen.class );
                    startActivity ( main );
                    finishAffinity ();
                }
            }

            @Override
            public void onFailure( Call <GeneralNotification> call, Throwable t) {
                if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                //Toast.makeText(CustomerCreation.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
                Intent main = new Intent ( BookingScreen.this,RetailerHomeScreen.class );
                startActivity ( main );
                finishAffinity ();
            }
        });
    }

    public void getCustomers(final int id) {
        final ProgressDialog dialog = new ProgressDialog( BookingScreen.this);
        dialog.setMessage("Loading Details..");
        dialog.setCancelable(false);
        dialog.show();
        final EmployeeApi orgApi = Util.getClient().create( EmployeeApi.class);
        Call<ArrayList< Employee >> getProf = orgApi.getEmployeesByOrgId (id);
        getProf.enqueue(new Callback<ArrayList< Employee >>() {
            @Override
            public void onResponse( Call<ArrayList< Employee >> call, Response<ArrayList< Employee >> response) {
                if (response.code() == 200||response.code() == 201||response.code() == 204) {
                    dialog.dismiss();
                    ArrayList<Employee> employeeArrayList = response.body();
                    customerArrayList = new ArrayList <> (  );
                    if(employeeArrayList!=null&&employeeArrayList.size()!=0){
                        Employee customer = new Employee ();
                        customer.setEmployeeName ("Others");
                        customerArrayList.add(customer);
                        for(Employee e:employeeArrayList){
                            if(e.getUserRoleId ()==10){
                                customerArrayList.add ( e );
                            }
                        }
                        CustomerSpinnerAdapter adapter = new CustomerSpinnerAdapter( BookingScreen.this,customerArrayList);
                        customerSpinner.setAdapter(adapter);
                    }
                    else {
                        ClientNameLayout.setVisibility(View.VISIBLE);
                        //customerSpinner.setVisibility(View.GONE);
                    }
                }else{
                    dialog.dismiss();
                    Toast.makeText( BookingScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure( Call<ArrayList< Employee >> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText( BookingScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToCart() {
        List <StockItemModel> items = new ArrayList <> (  );
        Gson gson = new GsonBuilder ().create();
        JsonArray myCustomArray = gson.toJsonTree(items).getAsJsonArray();

        try {
            FileWriter file = new FileWriter(BookingScreen.this.getFilesDir().getPath() + "/" + "AddToCart.json");
            file.write(myCustomArray.toString ());
            file.flush();
            file.close();
            // Toast.makeText(context, "Composition saved", Toast.LENGTH_LONG).show();
        } catch ( IOException e) {
            Log.e("TAG", "Error in Writing: " + e.getLocalizedMessage());
        }
    }
}
