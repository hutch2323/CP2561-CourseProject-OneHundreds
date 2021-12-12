import java.io.*;
import java.util.*;
import java.net.*;


public class ServerClientSocket2 {
    Socket socket;
    PrintWriter out;
    BufferedReader in;
    OutputStream outputStream;
    ObjectOutputStream objectOutput;
    InputStream inputStream;
    ObjectInputStream objectInput;

    public ServerClientSocket2(Socket socket) throws IOException {
        this.socket = socket;

        socketSetup();
    }

    public void socketSetup() throws IOException {
        this.out = new PrintWriter(this.socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.outputStream = this.socket.getOutputStream();
        this.objectOutput = new ObjectOutputStream(this.outputStream);
        this.inputStream = this.socket.getInputStream();
        this.objectInput = new ObjectInputStream(this.inputStream);
    }
}
