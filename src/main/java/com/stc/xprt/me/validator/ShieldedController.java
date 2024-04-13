//package com.stc.xprt.me.validator;
//
//import lombok.AccessLevel;
//import lombok.experimental.FieldDefaults;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.InputStreamReader;
//
//@RestController
//@FieldDefaults(level = AccessLevel.PRIVATE)
//public class ShieldedController {
//
//    @GetMapping("/stc/namada/shielded-transfer")
//    String test(@RequestParam String source, @RequestParam String target, @RequestParam String token,
//                @RequestParam String amount) {
//
//        String rs = "Transferring has failed";
//        try {
//            String homeDirectory = System.getProperty("user.home");
//            System.out.println(homeDirectory);
//
//            StringBuilder transferCommand = new StringBuilder();
//            transferCommand.append("export SOURCE=").append("stccapital");
//            transferCommand.append(" && export TARGET=").append(target);
//            transferCommand.append(" && export TOKEN=").append(token);
//            transferCommand.append(" && export AMOUNT=").append(amount);
//            transferCommand.append(" && export NAMADA_WALLET_PASSWORD=Ptc686grt09@123456");
//            transferCommand.append(
//                    " && bash namada-shielded-transfer.sh $NAMADA_WALLET_PASSWORD $TOKEN $AMOUNT $SOURCE $TARGET");
//
//
//            ProcessBuilder processBuilder = new ProcessBuilder();
//            processBuilder.directory(new File(homeDirectory));
//            processBuilder.command("bash", "-c", transferCommand.toString());
//            Process process = processBuilder.start();
//
//            // Read the output of the command
//            BufferedReader readerOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            BufferedReader readerError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//            StringBuilder output = new StringBuilder();
//            StringBuilder errorOutput = new StringBuilder();
//
//            // Read standard output
//            String lineOutput;
//            while ((lineOutput = readerOutput.readLine()) != null) {
//                System.out.println(lineOutput);
//                output.append(lineOutput).append("\n");
//            }
//
//            // Read standard error
//            String lineError;
//            while ((lineError = readerError.readLine()) != null) {
//                errorOutput.append(lineError).append("\n");
//            }
//
//            // Wait for the command to finish
//            int exitCode = process.waitFor();
//
//            // Close the readers
//            readerOutput.close();
//            readerError.close();
//
//            if (exitCode == 0) {
//                rs = output.toString();
//            } else {
//                // If there's an error, log it
//                System.err.println("Error executing command: " + errorOutput.toString());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("Error executing command: " + e.getMessage());
//            return e.getMessage();
//        }
//        System.out.println("Final result: " + rs);
//        return rs;
//    }
//}
