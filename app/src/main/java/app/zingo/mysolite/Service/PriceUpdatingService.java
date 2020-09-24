package app.zingo.mysolite.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import app.zingo.mysolite.WebApi.EmployeeApi;
import app.zingo.mysolite.WebApi.GeneralNotificationAPI;
import app.zingo.mysolite.WebApi.OrganizationApi;
import app.zingo.mysolite.WebApi.SendEmailAPI;
import app.zingo.mysolite.WebApi.StockItemPricingsApi;
import app.zingo.mysolite.model.EmailData;
import app.zingo.mysolite.model.Employee;
import app.zingo.mysolite.model.GeneralNotification;
import app.zingo.mysolite.model.LiveTracking;
import app.zingo.mysolite.model.LoginDetails;
import app.zingo.mysolite.model.Meetings;
import app.zingo.mysolite.model.Organization;
import app.zingo.mysolite.model.ReportDataModel;
import app.zingo.mysolite.model.StockItemPricingModel;
import app.zingo.mysolite.model.StockPriceUpdateData;
import app.zingo.mysolite.utils.Constants;
import app.zingo.mysolite.utils.PreferenceHandler;
import app.zingo.mysolite.utils.ThreadExecuter;
import app.zingo.mysolite.utils.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PriceUpdatingService extends Service {


    ArrayList < StockPriceUpdateData > priceUpdateData;
    ArrayList < StockItemPricingModel > stockItemPricingModels;

    @Override
    public IBinder onBind( Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

        try {

            Bundle b=intent.getExtras();

            if(b!=null){

                priceUpdateData = (ArrayList< StockPriceUpdateData > ) b.getSerializable ( "PriceUpdateData" );
                stockItemPricingModels = (ArrayList< StockItemPricingModel > ) b.getSerializable ( "PriceUpdate" );

            }

            if(priceUpdateData!=null&&priceUpdateData.size ()!=0){

                Gson gson = new Gson();
                String json = gson.toJson ( priceUpdateData );
                GeneralNotification gm = new GeneralNotification ();
                gm.setOrganizationId ( PreferenceHandler.getInstance ( PriceUpdatingService.this ).getCompanyId () );
                gm.setSenderId( Constants.O_SENDER_ID);
                gm.setServerId( Constants.O_SERVER_ID);
                gm.setComments (""+new SimpleDateFormat ( "MMM dd,yyyy hh:mm a" , Locale.US).format ( new Date (  ) ));
                gm.setTitle("Stock Price Update");
                gm.setMessage(""+json);
                //

                getEmployees (gm);
                saveStockOrderNm(gm);

            }

            if(stockItemPricingModels!=null&&stockItemPricingModels.size ()!=0){

                for ( StockItemPricingModel sp: stockItemPricingModels) {
                    updateStockItemPrice(sp);


                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // sendEmail();

        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

    }

    public void updateStockItemPrice(final StockItemPricingModel scm) {


        StockItemPricingsApi apiService = Util.getClient().create( StockItemPricingsApi.class);

        Call< StockItemPricingModel > call = apiService.updateStockItemPricingById (scm.getStockItemPricingId (),scm);

        call.enqueue(new Callback< StockItemPricingModel >() {
            @Override
            public void onResponse( Call< StockItemPricingModel > call, Response< StockItemPricingModel > response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||statusCode==204) {




                    }else {

                    }
                }
                catch (Exception ex)
                {

                    ex.printStackTrace();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure( Call< StockItemPricingModel > call, Throwable t) {


                //     Toast.makeText(CustomerCreation.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    private void getEmployees(final GeneralNotification gm){


        EmployeeApi apiService = Util.getClient().create( EmployeeApi.class);
        Call<ArrayList<Employee>> call = apiService.getEmployeesByOrgId(PreferenceHandler.getInstance ( PriceUpdatingService.this ).getCompanyId ());

        call.enqueue(new Callback<ArrayList<Employee>>() {
            @Override
            public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                int statusCode = response.code();
                if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                    ArrayList<Employee> list = response.body();

                    if (list !=null && list.size()!=0) {


                        for(int i=0;i<list.size();i++){

                            if(list.get(i).getUserRoleId()!=2){

                                gm.setEmployeeId ( list.get ( i ).getEmployeeId () );
                                sendStockPriceUpdateNM(gm);

                            }

                        }
                        //}

                    }else{

                    }

                }else {


                }
            }

            @Override
            public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {

                Log.e("TAG", t.toString());
            }
        });
    }

    public void sendStockPriceUpdateNM(final GeneralNotification scm) {

        GeneralNotificationAPI apiService = Util.getClient().create(GeneralNotificationAPI.class);

        Call <ArrayList<String>> call = apiService.sendGeneralNotification ( scm);

        call.enqueue(new Callback <ArrayList<String>> () {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response <ArrayList<String>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201|| statusCode == 202) {



                    }else {

                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();


                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure( Call < ArrayList<String> > call, Throwable t) {

            }
        });



    }

    public void saveStockOrderNm(final GeneralNotification scm) {


        GeneralNotificationAPI apiService = Util.getClient().create(GeneralNotificationAPI.class);

        Call <GeneralNotification> call = apiService.saveGeneralNotification ( scm);

        call.enqueue(new Callback <GeneralNotification> () {
            @Override
            public void onResponse(Call<GeneralNotification> call, Response <GeneralNotification> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201|| statusCode == 202) {





                    }
                }
                catch (Exception ex)
                {


                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure( Call <GeneralNotification> call, Throwable t) {


            }
        });



    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}