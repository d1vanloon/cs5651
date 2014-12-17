package client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

/**
 * Handles TCP connections.
 * 
 */
public class ClientHandleTCP extends Thread {

    public final static int BYTES_IN_MEGABYTES = 1048576;

    private Socket server;
    private final int whichPort;
    BufferedReader is;
    DataOutputStream os;
    InputStream ins;
    private final Client client;

    /**
     * Creates a new TCP handler.
     * 
     * @param server
     *            the connection to the server
     * @param whichPort
     *            which port to use
     */
    public ClientHandleTCP(Socket server, int whichPort, final Client client) {
        //System.out.println("Handling TCP connection on port " + whichPort + "...");
        
        this.server = server;
        this.whichPort = whichPort;
        this.client = client;
        try {

            // Build the necessary streams from the client.
            ins = server.getInputStream();
            is = new BufferedReader(new InputStreamReader(ins));
            os = new DataOutputStream(server.getOutputStream());
        } catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        this.start();
    }

    /**
     * Tests the connection. Tests upload or download based on the port number.
     */
    public void run() {
        try {
            if (whichPort == Client.TCP_UPLOAD_PORT)
                sendData(os);
            else if (whichPort == Client.TCP_DOWNLOAD_PORT)
                receiveData(ins);
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends random data to the server to test the download bandwidth.
     * 
     * @param output
     *            the output stream to use
     * @throws IOException
     *             if an error is encountered when closing the connection
     */
    private void sendData(DataOutputStream output) throws IOException {
        double currentBytes = 0.0;
        int i = 0;
        //System.out.println("Trying to write 2 MB to the server via TCP...");
        try {
            // Create a 1MB buffer and fill it with random data
            ByteBuffer byteBuffer = ByteBuffer.allocate(BYTES_IN_MEGABYTES);
            byte[] byteArray = new byte[BYTES_IN_MEGABYTES];
            new Random().nextBytes(byteArray);
            byteBuffer.put(byteArray);
            byteBuffer.flip();

            // Write 1 MB to the output stream
            while (currentBytes <= (BYTES_IN_MEGABYTES * 1)) {
                output.write(byteBuffer.array(), 0, BYTES_IN_MEGABYTES);
                byteBuffer.clear();
                byteBuffer.put(byteArray);
                byteBuffer.flip();
                i++;
                currentBytes = (i * BYTES_IN_MEGABYTES);
            }
            //System.out.println("Finished writing 1 MB via TCP.");
            output.writeBytes("Content-Type: random/bytes\r\n\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            output.close();
            this.client.reportAndRetestUploadBandwidth(new Date(), currentBytes);
        }
    }

    /**
     * Receives data from the server to test the upload bandwidth.
     * 
     * @param ins
     *            the input stream to use
     */
    private void receiveData(InputStream ins) {
        DataInputStream input = new DataInputStream(ins);
        //System.out
        //        .println("Waiting for the server to send as much data as it'd like via TCP...");
        double bytesReceived = 0.0;
        // Prepare to receive data
        byte[] byteArray = new byte[BYTES_IN_MEGABYTES];
        ByteBuffer byteBuffer = ByteBuffer.allocate(BYTES_IN_MEGABYTES);
        try {
            // Continuously receive data from the client, while it is available
            while (input.read() != -1) {
                bytesReceived += input.read(byteArray);
                byteBuffer.put(byteArray);
                Arrays.fill(byteArray, (byte) 0);
                byteBuffer.clear();
            }
            //System.out
            //        .println("Finished receiving data from the client via TCP.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            this.client.reportAndRetestDownloadBandwidth(new Date(), bytesReceived);
        }
    }
}
