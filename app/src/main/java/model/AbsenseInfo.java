package model;

public class AbsenseInfo {
    private String courseName;
    private String teacherName;
    private String place;
    private String signInTime;
    private String signType;


    public AbsenseInfo(){
        super();
    }

    public AbsenseInfo(String courseName, String teacherName, String place, String signInTime, String signType) {
        this.courseName = courseName;
        this.teacherName = teacherName;
        this.place = place;
        this.signInTime = signInTime;
        this.signType = signType;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getSignInTime() {
        return signInTime;
    }

    public void setSignInTime(String signInTime) {
        this.signInTime = signInTime;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    @Override
    public String toString() {
        return "AbsenseInfo{" +
                "courseName='" + courseName + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", place='" + place + '\'' +
                ", signInTime='" + signInTime + '\'' +
                ", signType='" + signType + '\'' +
                '}';
    }
}
