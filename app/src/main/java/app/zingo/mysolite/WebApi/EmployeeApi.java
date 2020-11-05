package app.zingo.mysolite.WebApi;
import java.util.ArrayList;
import app.zingo.mysolite.model.Employee;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by ZingoHotels Tech on 27-09-2018.
 */

public interface EmployeeApi {

    @POST("Employees")
    Call<Employee> addEmployee ( @Body Employee details );

    @PUT("Employees/{id}")
    Call<Employee> updateEmployee ( @Path ("id") int id , @Body Employee details );

    @GET("Employees/GetEmployeeByPhoneNumber/{PhoneNumber}")
    Call<ArrayList<Employee>> getUserByPhone ( @Path ("PhoneNumber") String phone );

    @POST("Employees/GetEmployeeByEmail")
    Call<ArrayList<Employee>> getUserByEmail ( @Body Employee userProfile );

    @GET("Employees")
    Call<ArrayList<Employee>> getEmployees ( );

    //@GET("Employees/GetEmployeesByOrganizationIdCustom/{OrganizationId}")
    @GET("Employees/GetEmployeesByOrganizationId/{OrganizationId}")
    Call<ArrayList<Employee>> getEmployeesByOrgId ( @Path ("OrganizationId") int id );

    @GET("Employees/GetEmployeeByDepartmentId/{DepartmentId}")
    Call<ArrayList<Employee>> getEmployeesByDepId ( @Path ("DepartmentId") int id );


    @POST("Employees/GetEmployeeByEmailAndPassword")
    Call<ArrayList<Employee>> getEmployeeforLogin ( @Body Employee body );

    //@POST("Employees/GetEmployeeByEmailAndPasswordCustomApi")
    @POST("Employees/GetEmployeeByEmailAndPassword")
    Call<ArrayList<Employee>> getEmployeeforLoginCustomApi ( @Body Employee body );

    @GET("Employees/{id}")
    Call<ArrayList<Employee>> getProfileById ( @Path ("id") int id );

    @DELETE("Employees/{id}")
    Call<Employee> deletEmployee ( @Path ("id") int id );

    /*RxJava*/
    @GET("Employees/GetEmployeesByOrganizationId/{OrganizationId}")
    Observable <ArrayList<Employee>> getEmployeesByOrgIdRx ( @Path ("OrganizationId") int id );
}
