/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cs324.districom.bootstrap;

/**
 *
 * @author tulol
 */
import com.cs324.districom.common.BootstrapService;
import com.cs324.districom.common.WorkerInfo;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class BootstrapServiceImpl
        extends UnicastRemoteObject
        implements BootstrapService {

    private final List<WorkerInfo> activeWorkers;

    public BootstrapServiceImpl()
            throws RemoteException {

        activeWorkers = new ArrayList<>();
    }

    @Override
    public synchronized void registerWorker(
            int workerId,
            String workerHost,
            int workerPort)
            throws RemoteException {

        for (WorkerInfo worker :
                activeWorkers) {

            if (worker.getWorkerId()
                    == workerId) {

                System.out.println(
                        "Worker "
                                + workerId
                                + " already registered."
                );

                return;
            }
        }

        WorkerInfo worker =
                new WorkerInfo(
                        workerId,
                        workerHost,
                        workerPort
                );

        activeWorkers.add(worker);

        System.out.println(
                "Worker registered: "
                        + worker
        );
    }

    @Override
    public synchronized void unregisterWorker(
            int workerId)
            throws RemoteException {

        activeWorkers.removeIf(
                worker ->
                        worker.getWorkerId()
                                == workerId
        );

        System.out.println(
                "Worker "
                        + workerId
                        + " unregistered."
        );
    }

    @Override
    public synchronized List<WorkerInfo>
    getActiveWorkers()
            throws RemoteException {

        return new ArrayList<>(
                activeWorkers
        );
    }
}