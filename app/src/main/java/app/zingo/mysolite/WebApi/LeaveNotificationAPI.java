package app.zingo.mysolite.WebApi;

import java.util.ArrayList;

import app.zingo.mysolite.model.LeaveNotificationManagers;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by ZingoHotels Tech on 26-10-2018.
 */

public interface LeaveNotificationAPI {


    @POST("LeaveNotificationManagers")
    Call<LeaveNotificationManagers> saveLeave ( @Body LeaveNotificationManagers details );

    @POST("LeaveNotificationManagers/SendLeaveDetailsNotification")
    Call<ArrayList<String>> sendLeaveNotification ( @Body LeaveNotificationManagers details );

}
