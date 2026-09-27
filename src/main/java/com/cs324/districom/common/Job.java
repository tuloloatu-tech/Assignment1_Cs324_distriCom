/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cs324.districom.common;

/**
 *
 * @author tulol
 */
import java.io.Serializable;
import java.util.List;

public class Job implements Serializable {

    private final String jobId;

    private final JobType jobType;

    private List<Integer> numbers;

    private int start;

    private int end;

    public Job(
            String jobId,
            JobType jobType) {

        this.jobId = jobId;
        this.jobType = jobType;
    }

    public String getJobId() {
        return jobId;
    }

    public JobType getJobType() {
        return jobType;
    }

    public List<Integer> getNumbers() {
        return numbers;
    }

    public void setNumbers(
            List<Integer> numbers) {

        this.numbers = numbers;
    }

    public int getStart() {
        return start;
    }

    public void setStart(
            int start) {

        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(
            int end) {

        this.end = end;
    }

    @Override
    public String toString() {

        return "Job{" +
                "jobId='" + jobId + '\'' +
                ", jobType=" + jobType +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
