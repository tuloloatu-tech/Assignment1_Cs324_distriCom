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

public class ElectionResult implements Serializable {

    private final int workerId;
    private final int jac;

    public ElectionResult(
            int workerId,
            int jac) {

        this.workerId = workerId;
        this.jac = jac;
    }

    public int getWorkerId() {
        return workerId;
    }

    public int getJAC() {
        return jac;
    }

    @Override
    public String toString() {

        return "Worker="
                + workerId
                + ", JAC="
                + jac;
    }
}