package app.zingo.mysolite.WebApi;

import app.zingo.mysolite.model.EmailData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by ZingoHotels Tech on 03-01-2019.
 */

public interface SendEmailAPI {

    @POST("Operation/SendInvoice")
    Call<String> sendEmail ( @Body EmailData emailData );
}
