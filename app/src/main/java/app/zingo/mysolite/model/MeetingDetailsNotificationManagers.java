package app.zingo.mysolite.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ZingoHotels Tech on 17-10-2018.
 */

public class MeetingDetailsNotificationManagers implements Serializable {

    @SerializedName("MeetingNotificationManagerId")
    public int MeetingNotificationManagerId;

    @SerializedName("SenderId")
    public String SenderId;

    @SerializedName("ServerId")
    public String ServerId;

    @SerializedName("Title")
    public String Title;

    @SerializedName("Message")
    public String Message;

    @SerializedName("Location")
    public String Location;

    @SerializedName("Longitude")
    public String Longitude;

    @SerializedName("Latitude")
    public String Latitude;

    @SerializedName("MeetingDate")
    public String MeetingDate;

    @SerializedName("Status")
    public String Status;

    @SerializedName("EmployeeId")
    public int EmployeeId;

    @SerializedName("ManagerId")
    public int ManagerId;

    @SerializedName("MeetingsId")
    public int MeetingsId;

    @SerializedName("MeetingPerson")
    public String MeetingPerson;

    @SerializedName("MeetingsDetails")
    public String MeetingsDetails;

    @SerializedName("MeetingComments")
    public String MeetingComments;

    public int getMeetingNotificationManagerId() {
        return MeetingNotificationManagerId;
    }

    public void setMeetingNotificationManagerId(int meetingNotificationManagerId) {
        MeetingNotificationManagerId = meetingNotificationManagerId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getMeetingDate() {
        return MeetingDate;
    }

    public void setMeetingDate(String meetingDate) {
        MeetingDate = meetingDate;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public int getEmployeeId() {
        return EmployeeId;
    }

    public void setEmployeeId(int employeeId) {
        EmployeeId = employeeId;
    }

    public int getManagerId() {
        return ManagerId;
    }

    public void setManagerId(int managerId) {
        ManagerId = managerId;
    }

    public int getMeetingsId() {
        return MeetingsId;
    }

    public void setMeetingsId(int meetingsId) {
        MeetingsId = meetingsId;
    }

    public String getMeetingPerson() {
        return MeetingPerson;
    }

    public void setMeetingPerson(String meetingPerson) {
        MeetingPerson = meetingPerson;
    }

    public String getMeetingsDetails() {
        return MeetingsDetails;
    }

    public void setMeetingsDetails(String meetingsDetails) {
        MeetingsDetails = meetingsDetails;
    }

    public String getMeetingComments() {
        return MeetingComments;
    }

    public void setMeetingComments(String meetingComments) {
        MeetingComments = meetingComments;
    }

    public String getSenderId() {
        return SenderId;
    }

    public void setSenderId(String senderId) {
        SenderId = senderId;
    }

    public String getServerId() {
        return ServerId;
    }

    public void setServerId(String serverId) {
        ServerId = serverId;
    }
}
