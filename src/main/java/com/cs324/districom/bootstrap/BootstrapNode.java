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

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class BootstrapNode {

    public static final int RMI_PORT = 1099;

    public static final String SERVICE_NAME =
            "BootstrapService";

    public static void main(String[] args) {

        try {

            Registry registry =
                    LocateRegistry.createRegistry(
                            RMI_PORT
                    );

            BootstrapService bootstrapService =
                    new BootstrapServiceImpl();

            registry.rebind(
                    SERVICE_NAME,
                    bootstrapService
            );

            System.out.println(
                    "======================================"
            );

            System.out.println(
                    "BOOTSTRAP NODE STARTED"
            );

            System.out.println(
                    "======================================"
            );

            System.out.println(
                    "Port: "
                            + RMI_PORT
            );

            System.out.println(
                    "Waiting for workers..."
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}