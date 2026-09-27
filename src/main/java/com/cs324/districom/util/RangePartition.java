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

public class RangePartition {

    private final int start;

    private final int end;

    public RangePartition(
            int start,
            int end) {

        this.start = start;
        this.end = end;
    }

    public int getStart() {

        return start;
    }

    public int getEnd() {

        return end;
    }

    public static List<RangePartition> splitRange(
            int start,
            int end,
            int workers) {

        List<RangePartition> partitions =
                new ArrayList<>();

        int total =
                end - start + 1;

        int chunkSize =
                (int) Math.ceil(
                        (double) total
                                / workers
                );

        int current =
                start;

        while (current <= end) {

            int partitionEnd =
                    Math.min(
                            current + chunkSize - 1,
                            end
                    );

            partitions.add(
                    new RangePartition(
                            current,
                            partitionEnd
                    )
            );

            current =
                    partitionEnd + 1;
        }

        return partitions;
    }

    @Override
    public String toString() {

        return "[" +
                start +
                " - " +
                end +
                "]";
    }
}