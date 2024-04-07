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
            // Execute the command
            String[] envp = new String[]{"TOKEN=" + token, "AMOUNT=" + amount, "SOURCE=" + source, "TARGET=" + target};

            // Execute the command
            Process process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "export TOKEN; export AMOUNT; export SOURCE; export TARGET; " + COMMAND_SHIELDED_TRANSFER}, envp);

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
