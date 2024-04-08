package com.stc.namada.me.validator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShieldedController {
    private static final String COMMAND_SHIELDED_TRANSFER = "bash namada-shielded-transfer.sh $NAMADA_WALLET_PASSWORD $TOKEN $AMOUNT $SOURCE $TARGET";

    @GetMapping("/stc/namada/shielded-transfer")
    String test(@RequestParam String source, @RequestParam String target, @RequestParam String token,
                @RequestParam String amount) {

        String rs = "Transferring has failed";
        try {
            String homeDirectory = System.getProperty("user.home");
            String[] envp = new String[]{"NAMADA_WALLET_PASSWORD=Ptc686grt09@123456", "TOKEN=" + token, "AMOUNT=" + amount, "SOURCE=" + source, "TARGET=" + target};
            Process process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "cd " + homeDirectory + " && export NAMADA_WALLET_PASSWORD; export TOKEN; export AMOUNT; export SOURCE; export TARGET; " + COMMAND_SHIELDED_TRANSFER}, envp);

            // Read the output of the command
            BufferedReader readerOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader readerError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder output = new StringBuilder();
            StringBuilder errorOutput = new StringBuilder();

            // Read standard output
            String lineOutput;
            while ((lineOutput = readerOutput.readLine()) != null) {
                output.append(lineOutput).append("\n");
            }

            // Read standard error
            String lineError;
            while ((lineError = readerError.readLine()) != null) {
                errorOutput.append(lineError).append("\n");
            }

            // Wait for the command to finish
            int exitCode = process.waitFor();

            // Close the readers
            readerOutput.close();
            readerError.close();

            if (exitCode == 0) {
                rs = output.toString();
            } else {
                // If there's an error, log it
                System.err.println("Error executing command: " + errorOutput.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error executing command: " + e.getMessage());
        }
        System.out.println("Final result: " + rs);
        return rs;
    }
}
