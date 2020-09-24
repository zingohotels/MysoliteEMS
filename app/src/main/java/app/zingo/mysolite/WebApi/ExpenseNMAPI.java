package app.zingo.mysolite.WebApi;

import java.util.ArrayList;

import app.zingo.mysolite.model.ExpensesNotificationManagers;
import app.zingo.mysolite.model.GeneralNotification;
import app.zingo.mysolite.model.LeaveNotificationManagers;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ExpenseNMAPI {

    @POST ("ExpensesNotificationManagers")
    Call < ExpensesNotificationManagers > saveExpensesNotificationManagers( @Body ExpensesNotificationManagers details);

    @POST("EMgmt/SendExpensesMangerToEmployeeId")
    Call< ArrayList <String> > sendExpenseNM ( @Body ExpensesNotificationManagers details );

}
