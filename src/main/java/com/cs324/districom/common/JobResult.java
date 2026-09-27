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

public class JobResult implements Serializable {

    private final String jobId;

    private final long result;

    public JobResult(
            String jobId,
            long result) {

        this.jobId = jobId;
        this.result = result;
    }

    public String getJobId() {
        return jobId;
    }

    public long getResult() {
        return result;
    }

    @Override
    public String toString() {

        return "JobResult{" +
                "jobId='" + jobId + '\'' +
                ", result=" + result +
                '}';
    }
}
