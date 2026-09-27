/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cs324.districom.util;

/**
 *
 * @author tulol
 */
import java.util.ArrayList;
import java.util.List;

public class JobSplitter {

    public static List<List<Integer>> splitList(
            List<Integer> numbers,
            int workerCount) {

        List<List<Integer>> partitions =
                new ArrayList<>();

        int chunkSize =
                (int) Math.ceil(
                        (double) numbers.size()
                                / workerCount
                );

        for (int i = 0;
             i < numbers.size();
             i += chunkSize) {

            partitions.add(
                    numbers.subList(
                            i,
                            Math.min(
                                    i + chunkSize,
                                    numbers.size()
                            )
                    )
            );
        }

        return partitions;
    }

}