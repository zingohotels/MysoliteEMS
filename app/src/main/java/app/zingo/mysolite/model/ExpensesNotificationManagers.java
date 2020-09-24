package app.zingo.mysolite.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ExpensesNotificationManagers implements Serializable {

    @SerializedName ( "ExpensesNotificationManagerId" )
    private int ExpensesNotificationManagerId;

    @SerializedName("SenderId")
    public String SenderId;

    @SerializedName("ServerId")
    public String ServerId;

    @SerializedName ( "ExpenseTitle" )
    private String ExpenseTitle;

    @SerializedName ( "Amount" )
    private double Amount;

    @SerializedName ( "date" )
    private String date;

    @SerializedName ( "Description" )
    private String Description;

    @SerializedName ( "employee" )
    private Employee employee;

    @SerializedName ( "EmployeeId" )
    private int EmployeeId;

    @SerializedName ( "ManagerId" )
    private int ManagerId;

    @SerializedName ( "ImageUrl" )
    private String ImageUrl;

    @SerializedName ( "Status" )
    private String Status;

    @SerializedName ( "Location" )
    private String Location;


    @SerializedName ( "Longititude" )
    private String Longititude;

    @SerializedName ( "latitude" )
    private String latitude;

    @SerializedName ( "OrganizationId" )
    private int OrganizationId;

    @SerializedName ( "title" )
    private String title;

    @SerializedName ( "Message" )
    private String Message;


    public int getExpensesNotificationManagerId ( ) {
        return ExpensesNotificationManagerId;
    }

    public void setExpensesNotificationManagerId ( int expensesNotificationManagerId ) {
        ExpensesNotificationManagerId = expensesNotificationManagerId;
    }

    public String getExpenseTitle ( ) {
        return ExpenseTitle;
    }

    public void setExpenseTitle ( String expenseTitle ) {
        ExpenseTitle = expenseTitle;
    }

    public double getAmount ( ) {
        return Amount;
    }

    public void setAmount ( double amount ) {
        Amount = amount;
    }

    public String getDate ( ) {
        return date;
    }

    public void setDate ( String date ) {
        this.date = date;
    }

    public String getDescription ( ) {
        return Description;
    }

    public void setDescription ( String description ) {
        Description = description;
    }

    public Employee getEmployee ( ) {
        return employee;
    }

    public void setEmployee ( Employee employee ) {
        this.employee = employee;
    }

    public int getEmployeeId ( ) {
        return EmployeeId;
    }

    public void setEmployeeId ( int employeeId ) {
        EmployeeId = employeeId;
    }

    public int getManagerId ( ) {
        return ManagerId;
    }

    public void setManagerId ( int managerId ) {
        ManagerId = managerId;
    }

    public String getImageUrl ( ) {
        return ImageUrl;
    }

    public void setImageUrl ( String imageUrl ) {
        ImageUrl = imageUrl;
    }

    public String getStatus ( ) {
        return Status;
    }

    public void setStatus ( String status ) {
        Status = status;
    }

    public String getLocation ( ) {
        return Location;
    }

    public void setLocation ( String location ) {
        Location = location;
    }

    public String getLongititude ( ) {
        return Longititude;
    }

    public void setLongititude ( String longititude ) {
        Longititude = longititude;
    }

    public String getLatitude ( ) {
        return latitude;
    }

    public void setLatitude ( String latitude ) {
        this.latitude = latitude;
    }

    public int getOrganizationId ( ) {
        return OrganizationId;
    }

    public void setOrganizationId ( int organizationId ) {
        OrganizationId = organizationId;
    }

    public String getTitle ( ) {
        return title;
    }

    public void setTitle ( String title ) {
        this.title = title;
    }

    public String getMessage ( ) {
        return Message;
    }

    public void setMessage ( String message ) {
        Message = message;
    }

    public String getSenderId ( ) {
        return SenderId;
    }

    public void setSenderId ( String senderId ) {
        SenderId = senderId;
    }

    public String getServerId ( ) {
        return ServerId;
    }

    public void setServerId ( String serverId ) {
        ServerId = serverId;
    }
}
