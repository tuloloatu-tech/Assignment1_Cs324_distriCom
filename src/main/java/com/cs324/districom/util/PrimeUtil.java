/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cs324.districom.util;

/**
 *
 * @author tulol
 */

public class PrimeUtil {

    public static boolean isPrime(
            int number) {

        if (number < 2) {
            return false;
        }

        for (int i = 2;
             i <= Math.sqrt(number);
             i++) {

            if (number % i == 0) {
                return false;
            }
        }

        return true;
    }
}

