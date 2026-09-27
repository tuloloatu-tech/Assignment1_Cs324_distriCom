/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cs324.districom.worker;

/**
 *
 * @author tulol
 */

import com.cs324.districom.common.CoordinatorMessage;
import com.cs324.districom.common.CoordinatorService;
import com.cs324.districom.common.ElectionResult;
import com.cs324.districom.common.Job;
import com.cs324.districom.common.JobResult;
import com.cs324.districom.common.JobType;
import com.cs324.districom.common.WorkerInfo;
import com.cs324.districom.common.WorkerService;
import com.cs324.districom.util.JobSplitter;
import com.cs324.districom.util.RangePartition;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class CoordinatorServiceImpl
        extends UnicastRemoteObject
        implements CoordinatorService {

    private final WorkerServiceImpl worker;

    public CoordinatorServiceImpl(
            WorkerServiceImpl worker)
            throws RemoteException {

        super();
        this.worker = worker;
    }

   @Override
public JobResult submitJob(
        Job job)
        throws RemoteException {

    System.out.println();

    System.out.println(
            "=================================="
    );

    System.out.println(
            "COORDINATOR RECEIVED JOB"
    );

    System.out.println(
            "=================================="
    );

    System.out.println(
            "Job ID   : "
                    + job.getJobId()
    );

    System.out.println(
            "Job Type : "
                    + job.getJobType()
    );

        worker.incrementJAC();

        JobResult result;

        switch (job.getJobType()) {

            case MAX:

                result =
                        executeDistributedMax(
                                job
                        );

                break;

            case PRIMECOUNT:

                result =
                        executeDistributedPrimeCount(
                                job
                        );

                break;

            case PRIMESUM:

                result =
                        executeDistributedPrimeSum(
                                job
                        );

                break;

            default:

                result =
                        worker.executeJob(
                                job
                        );
        }

        if (worker.coordinatorTermExpired()) {

            System.out.println();

            System.out.println(
                    "=================================="
            );

            System.out.println(
                    "COORDINATOR TERM EXPIRED"
            );

            System.out.println(
                    "STARTING NEW ELECTION"
            );

            System.out.println(
                    "=================================="
            );

            ElectionResult winner =
                    worker.startElection();

            System.out.println();

            System.out.println(
                    "NEW COORDINATOR ELECTED"
            );

            System.out.println(
                    "Worker "
                            + winner.getWorkerId()
            );

            CoordinatorMessage coordinatorMessage =
                    new CoordinatorMessage(
                            java.util.UUID.randomUUID()
                                    .toString(),
                            winner.getWorkerId()
                    );

            worker.receiveCoordinator(
                    coordinatorMessage
            );

            worker.resetJAC();
        }

        return result;
}
    private List<WorkerInfo> getAvailableWorkers()
            throws RemoteException {

        List<WorkerInfo> workers =
                new ArrayList<>();

        workers.add(
                new WorkerInfo(
                    worker.getWorkerId(),
                    "localhost",
                    worker.getWorkerPort()
)
        );

        workers.addAll(
                worker.getNeighbours()
        );

        return workers;
    }

    private JobResult executeDistributedMax(
            Job job)
            throws RemoteException {

        List<WorkerInfo> workers =
                getAvailableWorkers();

        if (workers.size() <= 1) {

            return worker.executeJob(
                    job
            );
        }

        List<List<Integer>> partitions =
                JobSplitter.splitList(
                        job.getNumbers(),
                        workers.size()
                );

        long globalMax =
                Long.MIN_VALUE;

        for (int i = 0;
             i < partitions.size();
             i++) {

            WorkerInfo target =
                    workers.get(i);

            try {

                Registry registry =
                        LocateRegistry.getRegistry(
                                target.getWorkerHost(),
                                target.getWorkerPort()
                        );

                WorkerService remoteWorker =
                        (WorkerService)
                                registry.lookup(
                                        "WorkerService"
                                );

                Job subJob =
                        new Job(
                                job.getJobId()
                                        + "-MAX-"
                                        + i,
                                JobType.MAX
                        );

                subJob.setNumbers(
                        partitions.get(i)
                );

                JobResult partialResult =
                        remoteWorker.executeJob(
                                subJob
                        );

                globalMax =
                        Math.max(
                                globalMax,
                                partialResult.getResult()
                        );

                System.out.println(
                        "Worker "
                                + target.getWorkerId()
                                + " returned "
                                + partialResult.getResult()
                );

            } catch (Exception e) {

                System.out.println(
                        "Failed to contact Worker "
                                + target.getWorkerId()
                );
            }
        }

        return new JobResult(
                job.getJobId(),
                globalMax
        );
    }

    private JobResult executeDistributedPrimeCount(
            Job job)
            throws RemoteException {

        List<WorkerInfo> workers =
                getAvailableWorkers();

        if (workers.size() <= 1) {

            return worker.executeJob(
                    job
            );
        }

        List<List<Integer>> partitions =
                JobSplitter.splitList(
                        job.getNumbers(),
                        workers.size()
                );

        long totalCount = 0;

        for (int i = 0;
             i < partitions.size();
             i++) {

            WorkerInfo target =
                    workers.get(i);

            try {

                Registry registry =
                        LocateRegistry.getRegistry(
                                target.getWorkerHost(),
                                target.getWorkerPort()
                        );

                WorkerService remoteWorker =
                        (WorkerService)
                                registry.lookup(
                                        "WorkerService"
                                );

                Job subJob =
                        new Job(
                                job.getJobId()
                                        + "-COUNT-"
                                        + i,
                                JobType.PRIMECOUNT
                        );

                subJob.setNumbers(
                        partitions.get(i)
                );

                JobResult partialResult =
                        remoteWorker.executeJob(
                                subJob
                        );

                totalCount +=
                        partialResult.getResult();

                System.out.println(
                        "Worker "
                                + target.getWorkerId()
                                + " returned "
                                + partialResult.getResult()
                );

            } catch (Exception e) {

                System.out.println(
                        "Failed to contact Worker "
                                + target.getWorkerId()
                );
            }
        }

        return new JobResult(
                job.getJobId(),
                totalCount
        );
    }
    
    private JobResult executeDistributedPrimeSum(
        Job job)
        throws RemoteException {

    List<WorkerInfo> workers =
            getAvailableWorkers();

    if (workers.size() <= 1) {

        return worker.executeJob(
                job
        );
    }

    List<RangePartition> partitions =
            RangePartition.splitRange(
                    job.getStart(),
                    job.getEnd(),
                    workers.size()
            );

    long totalSum = 0;

    for (int i = 0;
         i < partitions.size();
         i++) {

        WorkerInfo target =
                workers.get(i);

        try {

            Registry registry =
                    LocateRegistry.getRegistry(
                            target.getWorkerHost(),
                            target.getWorkerPort()
                    );

            WorkerService remoteWorker =
                    (WorkerService)
                            registry.lookup(
                                    "WorkerService"
                            );

            Job subJob =
                    new Job(
                            job.getJobId()
                                    + "-SUM-"
                                    + i,
                            JobType.PRIMESUM
                    );

            subJob.setStart(
                    partitions.get(i)
                            .getStart()
            );

            subJob.setEnd(
                    partitions.get(i)
                            .getEnd()
            );

            JobResult partialResult =
                    remoteWorker.executeJob(
                            subJob
                    );

            totalSum +=
                    partialResult.getResult();

            System.out.println(
                    "Worker "
                            + target.getWorkerId()
                            + " returned "
                            + partialResult.getResult()
            );

        } catch (Exception e) {

            System.out.println(
                    "Failed to contact Worker "
                            + target.getWorkerId()
            );
        }
    }

    return new JobResult(
            job.getJobId(),
            totalSum
    );
}
}
