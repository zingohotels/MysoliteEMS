package app.zingo.mysolite.WebApi;

import app.zingo.mysolite.model.ActivityLogs;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ActivityLogsAPI {

    @POST("ActivityLogs")
    Call< ActivityLogs > addActivityLogs( @Body ActivityLogs details );


}
