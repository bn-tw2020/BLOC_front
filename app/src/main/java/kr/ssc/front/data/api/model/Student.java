package kr.ssc.front.data.api.model;

import com.google.gson.annotations.SerializedName;

public class Student {
    @SerializedName("name")
    private String name;
    @SerializedName("studentId")
    private String student_id;
    @SerializedName("university")
    private String university;
    @SerializedName("department")
    private String department;
    @SerializedName("expireDate")
    private String expireDate;
    @SerializedName("holder_id")
    private String holder_id;
    @SerializedName("status")
    private String status;

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", student_id='" + student_id + '\'' +
                ", university='" + university + '\'' +
                ", department='" + department + '\'' +
                ", expireDate='" + expireDate + '\'' +
                ", holder_id='" + holder_id + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
