package com.stc.namada.me.validator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShieldedController {
    private static final String COMMAND_SHIELDED_TRANSFER = "namada client transfer --source stccapital --target znam1qpfdu7edr3pe9dv0y2ul68hnn8m2dlfdeygj46yznjefas0zs370jdqg2737p75l72uumwsmtr9pw --token naan --amount 1";
    @GetMapping("/stc/namada/shielded-transfer")
    String test() {
        String rs = "nonono";
        try {
            // Execute the command
            Process process = Runtime.getRuntime().exec(COMMAND_SHIELDED_TRANSFER);

            // Create a writer to write input to the process
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

            // Read the successOutput of the command
            String line;

            // Read the error (if any) from the command
            // Get the successOutput and error streams
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder errorOutput = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }

            if(StringUtils.isNotBlank(errorOutput)) {
                return rs = errorOutput.toString();
            }

            StringBuilder successOutput = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = reader.readLine()) != null) {
                successOutput.append(line).append("\n");
            }

            if(StringUtils.isNotBlank(successOutput)) {
                String password = "Ptc686grt09@123456"; // Replace this with actual password
                writer.write(password);
                writer.newLine();
                writer.flush();
            }

            // Wait for the command to finish
            process.waitFor();

            // Close the reader and writer
            reader.close();
            writer.close();

            return rs = successOutput.toString();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error executing command: " + e.getMessage());
        }
        return rs;
    }
}
