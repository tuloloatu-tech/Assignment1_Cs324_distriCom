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
import java.util.List;

public interface BootstrapService extends Remote {

    void registerWorker(
            int workerId,
            String workerHost,
            int workerPort
    ) throws RemoteException;

    void unregisterWorker(
            int workerId
    ) throws RemoteException;

    List<WorkerInfo> getActiveWorkers()
            throws RemoteException;
}
