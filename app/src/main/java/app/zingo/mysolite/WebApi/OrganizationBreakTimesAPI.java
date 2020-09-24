package app.zingo.mysolite.WebApi;

import java.util.ArrayList;

import app.zingo.mysolite.model.OrganizationBreakTimes;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface OrganizationBreakTimesAPI {

    @POST("OrganizationBreakTimes")
    Call<OrganizationBreakTimes> addBreaks ( @Body OrganizationBreakTimes details );

    @GET("OrganizationBreakTimes/GetOrganizationBreakTimesByOrganizationId/{OrganizationId}")
    Call<ArrayList<OrganizationBreakTimes>> getBreaksByOrgId ( @Path ("OrganizationId") int OrganizationId );
}
