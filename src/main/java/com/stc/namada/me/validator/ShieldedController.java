package com.stc.namada.me.validator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShieldedController {
    @GetMapping("/stc/namada/shielded-transfer")
    String test() {
        String rs = "nonono";
        try {
            // Execute the command
            Process process = Runtime.getRuntime().exec("echo 'hello'");

            // Read the output of the command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                rs = output.toString();
            }

            // Wait for the command to finish
            process.waitFor();

            // Close the reader
            reader.close();

            System.out.println(output.toString());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error executing command: " + e.getMessage());
        }
        return rs;
    }
}
