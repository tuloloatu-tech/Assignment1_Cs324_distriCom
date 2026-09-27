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

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;

public class ClientGUI extends Application {

    private ComboBox<String> jobTypeCombo;

    private TextArea inputArea;

    private TextField startField;

    private TextField endField;

    private TextField coordinatorPortField;

    private TextArea resultArea;

    @Override
    public void start(Stage stage) {

        Label title =
                new Label(
                        "DistriCom Client"
                );

        jobTypeCombo =
                new ComboBox<>();

        jobTypeCombo.getItems().addAll(
                "MAX",
                "PRIMECOUNT",
                "PRIMESUM"
        );

        jobTypeCombo.setValue(
                "MAX"
        );

        inputArea =
                new TextArea();

        inputArea.setPromptText(
                "Enter comma-separated numbers"
        );

        startField =
                new TextField();

        startField.setPromptText(
                "Range Start"
        );

        endField =
                new TextField();

        endField.setPromptText(
                "Range End"
        );

        coordinatorPortField =
                new TextField(
                        "2001"
                );

        coordinatorPortField.setPromptText(
                "Coordinator Port"
        );

        Button loadCsvButton =
                new Button(
                        "Load CSV"
                );

        Button submitButton =
                new Button(
                        "Submit Job"
                );

        resultArea =
                new TextArea();

        resultArea.setEditable(
                false
        );

        /*
         * CSV Upload
         */
        loadCsvButton.setOnAction(event -> {

            try {

                FileChooser chooser =
                        new FileChooser();

                chooser.setTitle(
                        "Choose CSV File"
                );

                File file =
                        chooser.showOpenDialog(
                                stage
                        );

                if (file != null) {

                    List<Integer> numbers =
                            CSVUtil.loadNumbers(
                                    file
                            );

                    inputArea.setText(
                            numbers.toString()
                                    .replace("[", "")
                                    .replace("]", "")
                    );

                    resultArea.appendText(
                            "CSV Loaded: "
                                    + file.getName()
                                    + "\n"
                    );
                }

            } catch (Exception e) {

                resultArea.appendText(
                        "CSV Load Failed\n"
                );
            }
        });

        /*
         * Submit Job
         */
        submitButton.setOnAction(event -> {

            new Thread(() -> {

                try {

                    Platform.runLater(() ->
                            resultArea.appendText(
                                    "Submitting Job...\n"
                            )
                    );

                    int coordinatorPort =
                            Integer.parseInt(
                                    coordinatorPortField.getText()
                            );

                    Registry registry =
                            LocateRegistry.getRegistry(
                                    "localhost",
                                    coordinatorPort
                            );

                    CoordinatorService coordinator =
                            (CoordinatorService)
                                    registry.lookup(
                                            "CoordinatorService"
                                    );

                    JobType jobType =
                            JobType.valueOf(
                                    jobTypeCombo.getValue()
                            );

                    Job job =
                            new Job(
                                    "JOB-"
                                            + System.currentTimeMillis(),
                                    jobType
                            );

                    switch (jobType) {

                        case MAX:

                        case PRIMECOUNT:

                            String[] values =
                                    inputArea.getText()
                                            .split(",");

                            List<Integer> numbers =
                                    Arrays.stream(values)
                                            .map(String::trim)
                                            .filter(v -> !v.isEmpty())
                                            .map(Integer::parseInt)
                                            .toList();

                            job.setNumbers(
                                    numbers
                            );

                            break;

                        case PRIMESUM:

                            job.setStart(
                                    Integer.parseInt(
                                            startField.getText()
                                    )
                            );

                            job.setEnd(
                                    Integer.parseInt(
                                            endField.getText()
                                    )
                            );

                            break;
                    }

                    JobResult result =
                            coordinator.submitJob(
                                    job
                            );

                    Platform.runLater(() ->
                            resultArea.appendText(
                                    "Result = "
                                            + result.getResult()
                                            + "\n"
                            )
                    );

                } catch (NumberFormatException e) {

                    Platform.runLater(() ->
                            resultArea.appendText(
                                    "Invalid Input\n"
                            )
                    );

                } catch (Exception e) {

                    Platform.runLater(() ->
                            resultArea.appendText(
                                    "Job Failed\n"
                            )
                    );

                    e.printStackTrace();
                }

            }).start();
        });

        VBox root =
                new VBox(
                        10,
                        title,

                        new Label(
                                "Job Type"
                        ),
                        jobTypeCombo,

                        new Label(
                                "Coordinator Port"
                        ),
                        coordinatorPortField,

                        new Label(
                                "Numbers (MAX / PRIMECOUNT)"
                        ),
                        inputArea,

                        new Label(
                                "PRIMESUM Start"
                        ),
                        startField,

                        new Label(
                                "PRIMESUM End"
                        ),
                        endField,

                        loadCsvButton,

                        submitButton,

                        new Label(
                                "Results"
                        ),
                        resultArea
                );

        root.setPadding(
                new Insets(
                        15
                )
        );

        Scene scene =
                new Scene(
                        root,
                        700,
                        600
                );

        stage.setTitle(
                "DistriCom Client"
        );

        stage.setScene(
                scene
        );

        stage.show();
    }

    public static void main(
            String[] args) {

        launch(args);
    }
}
