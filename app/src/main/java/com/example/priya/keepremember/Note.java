package com.example.priya.keepremember;

/**
 * Created by Priyank Verma on 5/17/2017.
 */

public class Note {

    String subject, priority,update_time, status, id;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Note{" +
                "subject='" + subject + '\'' +
                ", priority='" + priority + '\'' +
                ", update_time='" + update_time + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
