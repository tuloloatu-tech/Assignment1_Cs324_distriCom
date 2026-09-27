package com.cs324.districom.worker;

import com.cs324.districom.common.BootstrapService;
import com.cs324.districom.common.CoordinatorMessage;
import com.cs324.districom.common.ElectionResult;
import com.cs324.districom.common.WorkerInfo;
import com.cs324.districom.common.WorkerService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class WorkerNode {

    private static final String BOOTSTRAP_HOST =
            "localhost";

    private static final int BOOTSTRAP_PORT =
            1099;

    private static final String BOOTSTRAP_SERVICE =
            "BootstrapService";

    private static final String WORKER_SERVICE =
            "WorkerService";

    private static final String COORDINATOR_SERVICE =
            "CoordinatorService";

    public static void main(String[] args) {

        try {

            /*
             * Example:
             * WorkerNode 1 2001
             */
            if (args.length != 2) {

                System.out.println();
                System.out.println(
                        "Usage: WorkerNode <workerId> <workerPort>"
                );
                System.out.println(
                        "Example: WorkerNode 1 2001"
                );

                return;
            }

            int workerId;
            int workerPort;

            try {

                workerId =
                        Integer.parseInt(
                                args[0]
                        );

                workerPort =
                        Integer.parseInt(
                                args[1]
                        );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Worker ID and Port must be numbers."
                );

                return;
            }

            /*
             * Create worker registry.
             */
            Registry workerRegistry =
                    LocateRegistry.createRegistry(
                            workerPort
                    );

            /*
             * Create worker service.
             */
            WorkerServiceImpl worker =
                    new WorkerServiceImpl(
                            workerId,
                            workerPort
                    );

            /*
             * Create coordinator service.
             */
            CoordinatorServiceImpl coordinator =
                    new CoordinatorServiceImpl(
                            worker
                    );

            /*
             * Register services.
             */
            workerRegistry.rebind(
                    WORKER_SERVICE,
                    worker
            );

            workerRegistry.rebind(
                    COORDINATOR_SERVICE,
                    coordinator
            );

            /*
             * Connect to Bootstrap.
             */
            Registry bootstrapRegistry =
                    LocateRegistry.getRegistry(
                            BOOTSTRAP_HOST,
                            BOOTSTRAP_PORT
                    );

            BootstrapService bootstrap =
                    (BootstrapService)
                            bootstrapRegistry.lookup(
                                    BOOTSTRAP_SERVICE
                            );

            /*
             * Register worker.
             */
            bootstrap.registerWorker(
                    workerId,
                    BOOTSTRAP_HOST,
                    workerPort
            );

            /*
             * Get active workers.
             */
            List<WorkerInfo> activeWorkers =
                    bootstrap.getActiveWorkers();

            /*
             * Find candidate neighbours.
             */
            List<WorkerInfo> possibleNeighbours =
                    new ArrayList<>();

            for (WorkerInfo info
                    : activeWorkers) {

                if (info.getWorkerId()
                        != workerId) {

                    possibleNeighbours.add(
                            info
                    );
                }
            }

            /*
             * Create random neighbour connection.
             */
            if (!possibleNeighbours.isEmpty()) {

                Random random =
                        new Random();

                WorkerInfo selectedNeighbour =
                        possibleNeighbours.get(
                                random.nextInt(
                                        possibleNeighbours.size()
                                )
                        );

                Registry neighbourRegistry =
                        LocateRegistry.getRegistry(
                                selectedNeighbour.getWorkerHost(),
                                selectedNeighbour.getWorkerPort()
                        );

                WorkerService neighbour =
                        (WorkerService)
                                neighbourRegistry.lookup(
                                        WORKER_SERVICE
                                );

                worker.addNeighbour(
                        selectedNeighbour
                );

                WorkerInfo myInfo =
                        new WorkerInfo(
                                workerId,
                                BOOTSTRAP_HOST,
                                workerPort
                        );

                neighbour.addNeighbour(
                        myInfo
                );

                System.out.println();
                System.out.println(
                        "Neighbour connection created:"
                );

                System.out.println(
                        "Worker "
                                + workerId
                                + " <--> Worker "
                                + selectedNeighbour.getWorkerId()
                );

            } else {

                System.out.println();
                System.out.println(
                        "No neighbours available yet."
                );
            }

            System.out.println();
            System.out.println(
                    "=================================="
            );
            System.out.println(
                    "WORKER STARTED"
            );
            System.out.println(
                    "=================================="
            );

            System.out.println(
                    "Worker ID : "
                            + workerId
            );

            System.out.println(
                    "Worker Port : "
                            + workerPort
            );

            System.out.println(
                    "JAC : "
                            + worker.getJAC()
            );

            System.out.println();
            System.out.println(
                    "Active Workers"
            );

            System.out.println(
                    "----------------------------------"
            );

            for (WorkerInfo info
                    : activeWorkers) {

                System.out.println(
                        info
                );
            }

            System.out.println(
                    "----------------------------------"
            );

            /*
             * Manual election trigger.
             */
            if (workerId == 1) {

                System.out.println();
                System.out.println(
                        "Press ENTER to start election..."
                );

                System.in.read();

                String electionId =
                        UUID.randomUUID()
                                .toString();

                ElectionResult winner =
                        worker.receiveElection(
                                electionId,
                                -1
                        );

                System.out.println();
                System.out.println(
                        "=================================="
                );

                System.out.println(
                        "ELECTION COMPLETE"
                );

                System.out.println(
                        "=================================="
                );

                System.out.println(
                        "Winner Worker ID : "
                                + winner.getWorkerId()
                );

                System.out.println(
                        "Winner JAC : "
                                + winner.getJAC()
                );

                CoordinatorMessage coordinatorMessage =
                        new CoordinatorMessage(
                                electionId,
                                winner.getWorkerId()
                        );

                worker.receiveCoordinator(
                        coordinatorMessage
                );

                System.out.println(
                        "Coordinator announced."
                );
            }

            System.out.println();
            System.out.println(
                    "Worker is running..."
            );

            synchronized (WorkerNode.class) {

                WorkerNode.class.wait();
            }

        } catch (Exception e) {

            System.err.println(
                    "Worker failed to start."
            );

            e.printStackTrace();
        }
    }
}