/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.cs324.districom.common;

/**
 *
 * @author tulol
 */
import com.cs324.districom.common.Job;
import com.cs324.districom.common.JobResult;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface WorkerService extends Remote {

    int getWorkerId()
            throws RemoteException;

    boolean isActive()
            throws RemoteException;

    int getJAC()
            throws RemoteException;

    void addNeighbour(
            WorkerInfo workerInfo
    ) throws RemoteException;

    List<WorkerInfo> getNeighbours()
            throws RemoteException;

    ElectionResult receiveElection(
            String electionId,
            int senderId
    ) throws RemoteException;

    void receiveCoordinator(
            CoordinatorMessage message
    ) throws RemoteException;

   JobResult executeJob(
        Job job
        ) throws RemoteException;

   ElectionResult startElection()
           throws RemoteException;
}

