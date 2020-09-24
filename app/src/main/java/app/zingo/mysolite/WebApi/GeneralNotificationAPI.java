package app.zingo.mysolite.WebApi;

import java.util.ArrayList;

import app.zingo.mysolite.model.GeneralNotification;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GeneralNotificationAPI {


    @POST("GeneralNotificationManagers/SendGeneralNotificationToEmployeeId")
    Call<ArrayList<String>> sendGeneralNotification( @Body GeneralNotification details );

    @GET ("GeneralNotificationManagers")
    Call < ArrayList< GeneralNotification > > getGeneralNotification ( );

    @POST ("GeneralNotificationManagers")
    Call < GeneralNotification > saveGeneralNotification( @Body GeneralNotification details);

    @DELETE ("GeneralNotificationManagers/{id}")
    Call < GeneralNotification> deleteNotification( @Path ("id") int id );

}
