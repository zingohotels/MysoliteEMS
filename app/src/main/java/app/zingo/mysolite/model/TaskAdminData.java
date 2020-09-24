package app.zingo.mysolite.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TaskAdminData implements Serializable {

    @SerializedName("Employee")
    public Employee employee;

    @SerializedName("Tasks")
    app.zingo.mysolite.model.Tasks Tasks;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public app.zingo.mysolite.model.Tasks getTasks() {
        return Tasks;
    }

    public void setTasks(app.zingo.mysolite.model.Tasks tasks) {
        Tasks = tasks;
    }
}
