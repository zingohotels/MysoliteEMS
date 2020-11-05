package app.zingo.mysolite.WebApi;

import java.util.ArrayList;

import app.zingo.mysolite.model.Tasks;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by ZingoHotels Tech on 05-01-2019.
 */

public interface TasksAPI {

    @POST("Tasks")
    Call<Tasks> addTasks ( @Body Tasks details );

    @GET("Tasks")
    Call<ArrayList<Tasks>> getTasks ( );

    @PUT("Tasks/{id}")
    Call<Tasks> updateTasks ( @Path ("id") int id , @Body Tasks details );

    @DELETE("Tasks/{id}")
    Call<Tasks> deleteTasks ( @Path ("id") int id );

    @GET("Tasks/GetTasksByEmployeeId/{EmployeeId}")
    Call<ArrayList<Tasks>> getTasksByEmployeeId ( @Path ("EmployeeId") int EmployeeId );

    @GET("Tasks/GetTasksByEmployeeIdAndStatus/{EmployeeId}/{Status}")
    Call<ArrayList<Tasks>> getTasksByEmployeeIdStatus ( @Path ("EmployeeId") int EmployeeId , @Path ("Status") String Status );



    //RxJava

    @GET("Tasks/GetTasksByEmployeeId/{EmployeeId}")
    Observable <ArrayList<Tasks>> getTasksByEmployeeIdRx ( @Path ("EmployeeId") int EmployeeId );

    @GET("Tasks")
    Observable <ArrayList<Tasks>> getTasksRx ( );
}
