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

public class WorkerInfo implements Serializable {

    private final int workerId;
    private final String workerHost;
    private final int workerPort;

    public WorkerInfo(
            int workerId,
            String workerHost,
            int workerPort) {

        this.workerId = workerId;
        this.workerHost = workerHost;
        this.workerPort = workerPort;
    }

    public int getWorkerId() {
        return workerId;
    }

    public String getWorkerHost() {
        return workerHost;
    }

    public int getWorkerPort() {
        return workerPort;
    }

    @Override
    public String toString() {

        return "Worker ID=" + workerId
                + ", Host=" + workerHost
                + ", Port=" + workerPort;
    }
}