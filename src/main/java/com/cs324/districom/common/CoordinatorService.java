/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.cs324.districom.common;

/**
 *
 * @author tulol
 */
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CoordinatorService extends Remote {

    JobResult submitJob(
            Job job
    ) throws RemoteException;
}