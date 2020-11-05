package app.zingo.mysolite.utils;

import java.util.ArrayList;

import app.zingo.mysolite.WebApi.EmployeeApi;
import app.zingo.mysolite.WebApi.ExpensesApi;
import app.zingo.mysolite.WebApi.LiveTrackingAPI;
import app.zingo.mysolite.WebApi.LoginDetailsAPI;
import app.zingo.mysolite.WebApi.MeetingsAPI;
import app.zingo.mysolite.WebApi.TasksAPI;
import app.zingo.mysolite.model.Employee;

public class Common {

    public static ArrayList< Employee > employeeDetals = null;

    public static TasksAPI getTaskAPI() {
        return Util.getClient().create(TasksAPI.class);
    }
    public static LoginDetailsAPI getLoginDetailsAPI() {
        return Util.getClient().create(LoginDetailsAPI.class);
    }
    public static MeetingsAPI getMeetingsAPI ( ) {
        return Util.getClient().create(MeetingsAPI.class);
    }
    public static LiveTrackingAPI getLiveTrackingAPI ( ) {
        return Util.getClient().create(LiveTrackingAPI.class);
    }
    public static ExpensesApi getExpensesAPI ( ) {
        return Util.getClient().create(ExpensesApi.class);
    }
    public static EmployeeApi getEmployeeAPI ( ) {
        return Util.getClient().create(EmployeeApi.class);
    }
}
