package com.xiaoxin.project_android.entity;

public class Statistics  {
    private final String studentName;
    private final String studentAccount;
    private final Integer successCount;
    private final Integer failedCount;
    private final Integer absentCount;
    private final Integer leaveCount;

    public Statistics(String studentName, String studentAccount, Integer successCount, Integer failedCount, Integer absentCount, Integer leaveCount) {
        this.studentName = studentName;
        this.studentAccount = studentAccount;
        this.successCount = successCount;
        this.failedCount = failedCount;
        this.absentCount = absentCount;
        this.leaveCount = leaveCount;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentAccount() {
        return studentAccount;
    }

    public Integer getAbsentCount() {
        return absentCount;
    }

    public Integer getFailedCount() {
        return failedCount;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public Integer getLeaveCount() {
        return leaveCount;
    }
}
