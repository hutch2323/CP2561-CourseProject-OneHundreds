import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {
        String hostName = "localhost";
        int portNumber = 8009;

        try (
                Socket clientSocket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            String userInput, fromServer;
            while ((fromServer = in.readLine()) != null) {
                if (fromServer.equals("Good")) {
                    System.out.println("breaking");
                    break;
                }
                System.out.println("Server: " + fromServer);

                userInput = stdIn.readLine();
                if (userInput != null) {
                    System.out.println("Client: " + userInput);
                    out.println(userInput);
                }
            }

            while ((fromServer = in.readLine()) != null){
                System.out.println("Server: " + fromServer);
            }

        } catch (UnknownHostException e) {
            System.err.println("Unable to find host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Unable to retrieve I/O for connection to " + hostName);
            System.exit(1);
        }
    }
}
