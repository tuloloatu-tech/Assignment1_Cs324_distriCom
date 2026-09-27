/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cs324.districom.client;

/**
 *
 * @author tulol
 */

import com.cs324.districom.common.CoordinatorService;
import com.cs324.districom.common.Job;
import com.cs324.districom.common.JobResult;
import com.cs324.districom.common.JobType;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;

public class CoordinatorTest {

    public static void main(String[] args) {

        try {

            Registry registry =
                    LocateRegistry.getRegistry(
                            "localhost",
                            2001
                    );

            CoordinatorService coordinator =
                    (CoordinatorService)
                            registry.lookup(
                                    "CoordinatorService"
                            );

            /*
             * TEST 1 : MAX
             */
            System.out.println();
            System.out.println("==================================");
            System.out.println("TESTING MAX");
            System.out.println("==================================");

            Job maxJob =
                    new Job(
                            "MAX-1",
                            JobType.MAX
                    );

            maxJob.setNumbers(
                    Arrays.asList(
                            5,
                            7,
                            2,
                            11,
                            9
                    )
            );

            JobResult maxResult =
                    coordinator.submitJob(
                            maxJob
                    );

            System.out.println(
                    "MAX Result = "
                            + maxResult.getResult()
            );

            /*
             * TEST 2 : PRIMECOUNT
             */
            System.out.println();
            System.out.println("==================================");
            System.out.println("TESTING PRIMECOUNT");
            System.out.println("==================================");

            Job countJob =
                    new Job(
                            "COUNT-1",
                            JobType.PRIMECOUNT
                    );

            countJob.setNumbers(
                    Arrays.asList(
                            2,
                            3,
                            4,
                            5,
                            6,
                            7
                    )
            );

            JobResult countResult =
                    coordinator.submitJob(
                            countJob
                    );

            System.out.println(
                    "PRIMECOUNT Result = "
                            + countResult.getResult()
            );

            /*
             * TEST 3 : PRIMESUM
             */
            System.out.println();
            System.out.println("==================================");
            System.out.println("TESTING PRIMESUM");
            System.out.println("==================================");

            Job sumJob =
                    new Job(
                            "SUM-1",
                            JobType.PRIMESUM
                    );

            sumJob.setStart(1);
            sumJob.setEnd(10);

            JobResult sumResult =
                    coordinator.submitJob(
                            sumJob
                    );

            System.out.println(
                    "PRIMESUM Result = "
                            + sumResult.getResult()
            );

            System.out.println();
            System.out.println("==================================");
            System.out.println("ALL TESTS COMPLETE");
            System.out.println("==================================");

        } catch (Exception e) {

            System.err.println(
                    "Coordinator test failed."
            );

            e.printStackTrace();
        }
    }
}