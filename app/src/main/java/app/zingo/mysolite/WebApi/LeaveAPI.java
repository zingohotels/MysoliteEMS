package app.zingo.mysolite.WebApi;

import java.util.ArrayList;

import app.zingo.mysolite.model.Leaves;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by ZingoHotels Tech on 17-10-2018.
 */

public interface LeaveAPI {

    @POST("Leaves")
    Call<Leaves> addLeave ( @Body Leaves details );

    @PUT("Leaves/{id}")
    Call<Leaves> updateLeaves ( @Path ("id") int id , @Body Leaves details );

    @GET("Leaves/GetAllLeavesByEmployeeId/{EmployeeId}")
    Call<ArrayList<Leaves>> getLeavesByEmployeeId ( @Path ("EmployeeId") int id );

    @GET("Leaves/{id}")
    Call<Leaves> getLeaveById ( @Path ("id") int id );

    @GET("Leaves/GetAllLeavesByStatusandEmployeeId/{Status}/{EmployeeId}")
    Call<ArrayList<Leaves>> getLeavesByStatusAndEmployeeId ( @Path ("Status") String status , @Path ("EmployeeId") int id );


}
