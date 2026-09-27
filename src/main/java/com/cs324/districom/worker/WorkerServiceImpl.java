package com.cs324.districom.worker;

import com.cs324.districom.common.CoordinatorMessage;
import com.cs324.districom.common.ElectionResult;
import com.cs324.districom.common.Job;
import com.cs324.districom.common.JobResult;
import com.cs324.districom.common.WorkerInfo;
import com.cs324.districom.common.WorkerService;
import com.cs324.districom.util.PrimeUtil;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkerServiceImpl
        extends UnicastRemoteObject
        implements WorkerService {

  private final int workerId;

    private final int workerPort;

    private final boolean active;

    /*
     * Job Allocation Counter (JAC)
     */
   private final AtomicInteger jac;

    /*
     * Required by assignment.
     */
    private final String leaderman = "cs324";

    private final List<WorkerInfo> neighbours;

    /*
     * Elections already processed.
     */
    private final List<String> processedElections;

    /*
     * Coordinator announcements already processed.
     */
    private final List<String> processedCoordinators;

    /*
     * Current coordinator.
     */
    private int coordinatorId;
    /*
 * Thread pool for concurrent job execution.
 */
    private final ExecutorService executor;

    public WorkerServiceImpl(int workerId, int workerPort)
            throws RemoteException {

        super();

        this.workerId = workerId;
        this.workerPort = workerPort;
        this.active = true;
        this.jac =
        new AtomicInteger(
                0
        );
        this.coordinatorId = -1;

        this.neighbours = new ArrayList<>();
        this.processedElections = new ArrayList<>();
        this.processedCoordinators = new ArrayList<>();
        this.executor = Executors.newFixedThreadPool(
                 10);
    }

    @Override
    public int getWorkerId()
            throws RemoteException {

        return workerId;
    }

    @Override
    public boolean isActive()
            throws RemoteException {

        return active;
    }

    @Override
    public int getJAC()
            throws RemoteException {

        return jac.get();
    }

 public void incrementJAC() {

    jac.incrementAndGet();
}

public boolean coordinatorTermExpired() {

    return jac.get() >= 5;
}
public void resetJAC() {

    jac.set(
            0
    );
}

public int getCoordinatorId() {

    return coordinatorId;
}

public int getWorkerPort() {

    return workerPort;
}

    @Override
    public synchronized void addNeighbour(
            WorkerInfo workerInfo)
            throws RemoteException {

        if (workerInfo.getWorkerId()
                == workerId) {

            return;
        }

        for (WorkerInfo neighbour
                : neighbours) {

            if (neighbour.getWorkerId()
                    == workerInfo.getWorkerId()) {

                return;
            }
        }

        neighbours.add(workerInfo);

        System.out.println(
                "Worker "
                        + workerId
                        + " added neighbour -> "
                        + workerInfo
        );
    }

    @Override
    public synchronized List<WorkerInfo>
    getNeighbours()
            throws RemoteException {

        return new ArrayList<>(
                neighbours
        );
    }

    @Override
    public synchronized ElectionResult receiveElection(
            String electionId,
            int senderId)
            throws RemoteException {

        /*
         * Ignore duplicate elections.
         */
        if (processedElections.contains(
                electionId
        )) {

            System.out.println(
                    "Worker "
                            + workerId
                            + " ignored duplicate election."
            );

            return new ElectionResult(
                workerId,
                jac.get()
                );
        }

        processedElections.add(
                electionId
        );

        System.out.println(
                "Worker "
                        + workerId
                        + " received election "
                        + electionId
        );

        ElectionResult bestResult =
        new ElectionResult(
                workerId,
                jac.get()
        );
        /*
         * Forward election to neighbours.
         */
        for (WorkerInfo neighbourInfo
                : neighbours) {

            if (neighbourInfo.getWorkerId()
                    == senderId) {

                continue;
            }

            try {

                Registry registry =
                        LocateRegistry.getRegistry(
                                neighbourInfo.getWorkerHost(),
                                neighbourInfo.getWorkerPort()
                        );

                WorkerService neighbour =
                        (WorkerService)
                                registry.lookup(
                                        "WorkerService"
                                );

                ElectionResult result =
                        neighbour.receiveElection(
                                electionId,
                                workerId
                        );

                /*
                 * Lowest JAC wins.
                 */
                if (result.getJAC()
                        < bestResult.getJAC()) {

                    bestResult = result;
                }

                /*
                 * Same JAC?
                 * Highest worker ID wins.
                 */
                else if (
                        result.getJAC()
                                == bestResult.getJAC()
                                &&
                                result.getWorkerId()
                                        > bestResult.getWorkerId()
                ) {

                    bestResult = result;
                }

            } catch (Exception e) {

                System.out.println(
                        "Worker "
                                + workerId
                                + " failed to contact Worker "
                                + neighbourInfo.getWorkerId()
                );
            }
        }

        return bestResult;
    }

    @Override
    public synchronized void receiveCoordinator(
            CoordinatorMessage message)
            throws RemoteException {

        /*
         * Ignore duplicate coordinator messages.
         */
        if (processedCoordinators.contains(
                message.getElectionId()
        )) {

            return;
        }

        processedCoordinators.add(
                message.getElectionId()
        );

        coordinatorId =
                message.getCoordinatorId();

        System.out.println(
                "Worker "
                        + workerId
                        + " accepted coordinator -> Worker "
                        + coordinatorId
        );

        /*
         * Forward coordinator message.
         */
        for (WorkerInfo neighbourInfo
                : neighbours) {

            try {

                Registry registry =
                        LocateRegistry.getRegistry(
                                neighbourInfo.getWorkerHost(),
                                neighbourInfo.getWorkerPort()
                        );

                WorkerService neighbour =
                        (WorkerService)
                                registry.lookup(
                                        "WorkerService"
                                );

                neighbour.receiveCoordinator(
                        message
                );

            } catch (Exception e) {

                System.out.println(
                        "Worker "
                                + workerId
                                + " failed to contact Worker "
                                + neighbourInfo.getWorkerId()
                );
            }
        }
    }

 @Override
    public JobResult executeJob(
            Job job)
            throws RemoteException {

        try {

            Future<JobResult> future =
                    executor.submit(() -> {

                        long result = 0;

                        switch (job.getJobType()) {

                            case MAX:

                                int max =
                                        Integer.MIN_VALUE;

                                for (Integer number
                                        : job.getNumbers()) {

                                    if (number > max) {

                                        max = number;
                                    }
                                }

                                result = max;
                                break;

                            case PRIMECOUNT:

                                int count = 0;

                                for (Integer number
                                        : job.getNumbers()) {

                                    if (PrimeUtil.isPrime(
                                            number
                                    )) {

                                        count++;
                                    }
                                }

                                result = count;
                                break;

                            case PRIMESUM:

                                long sum = 0;

                                for (int i =
                                     job.getStart();
                                     i <= job.getEnd();
                                     i++) {

                                    if (PrimeUtil.isPrime(
                                            i
                                    )) {

                                        sum += i;
                                    }
                                }

                                result = sum;
                                break;
                        }

                        System.out.println(
                                "Worker "
                                        + workerId
                                        + " executed "
                                        + job.getJobType()
                                        + " -> Result = "
                                        + result
                        );

                        return new JobResult(
                                job.getJobId(),
                                result
                        );
                    });

            return future.get();

        } catch (Exception e) {

            throw new RemoteException(
                    "Job execution failed",
                    e
            );
        }
}
    public String getLeaderman() {

    return leaderman;
}

    public ElectionResult startElection()
            throws RemoteException {

        String electionId =
                java.util.UUID.randomUUID()
                        .toString();

        return receiveElection(
                electionId,
                -1
        );
    }
    @Override
    public String toString() {

        return "WorkerServiceImpl{"
                + "workerId=" + workerId
                + ", active=" + active
                + ", jac=" + jac
                + ", coordinatorId=" + coordinatorId
                + '}';
    }
}