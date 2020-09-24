package app.zingo.mysolite.WebApi;


import java.util.ArrayList;

import app.zingo.mysolite.model.Expenses;
import app.zingo.mysolite.model.LiveTracking;
import app.zingo.mysolite.model.LoginDetails;
import app.zingo.mysolite.model.Meetings;
import app.zingo.mysolite.model.Tasks;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface MultpleAPI {

    @POST("LoginDetails/GetLoginDetailsByEmployeeIdAndLoginDate")
    Observable<ArrayList<LoginDetails>> getLoginByEmployeeIdAndDate ( @Body LoginDetails details );

    @POST("LiveTrackingDetailsAsync/GetliveTrackingDetailsByEmployeeIdAndDate")
    Observable<ArrayList<LiveTracking>> getLiveTrackingByEmployeeIdAndDate ( @Body LiveTracking details );

    @POST("Meetings/GetMeetingsDetailsByEmployeeIdAndLoginDate")
    Observable<ArrayList< Meetings >> getMeetingsByEmployeeIdAndDate( @Body Meetings details );

    @GET("Tasks/GetTasksByEmployeeId/{EmployeeId}")
    Observable<ArrayList<Tasks>> getTasksByEmployeeId ( @Path ("EmployeeId") int EmployeeId );

    @GET("Expenses/GetExpensesByOrganizationIdAndEmployeeId/{OrganizationId}/{EmployeeId}")
    Observable<ArrayList<Expenses>> getExpenseByEmployeeIdAndOrganizationId ( @Path ("OrganizationId") int OrganizationId , @Path ("EmployeeId") int EmployeeId );

}
