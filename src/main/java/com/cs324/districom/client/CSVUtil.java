/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cs324.districom.client;

/**
 *
 * @author tulol
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CSVUtil {

    public static List<Integer> loadNumbers(
            File file)
            throws Exception {

        List<Integer> numbers =
                new ArrayList<>();

        BufferedReader reader =
                new BufferedReader(
                        new FileReader(file)
                );

        String line;

        while ((line = reader.readLine()) != null) {

            line = line.trim();

            if (!line.isEmpty()) {

                numbers.add(
                        Integer.parseInt(line)
                );
            }
        }

        reader.close();

        return numbers;
    }
}