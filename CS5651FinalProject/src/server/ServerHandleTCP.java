package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

/**
 * Handles TCP connections.
 * 
 */
public class ServerHandleTCP extends Thread {

    private final static int BYTES_IN_MEGABYTES = 1048576;

    private Socket client;
    private final int whichPort;
    BufferedReader is;
    DataOutputStream os;
    InputStream ins;

    /**
     * Creates a new TCP handler.
     * 
     * @param client
     *            the connection to the client
     * @param whichPort
     *            which port to use
     */
    public ServerHandleTCP(Socket client, int whichPort) {
        this.client = client;
        this.whichPort = whichPort;
        try {

            // Build the necessary streams from the client.
            ins = client.getInputStream();
            is = new BufferedReader(new InputStreamReader(ins));
            os = new DataOutputStream(client.getOutputStream());
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
            if (whichPort == Server.TCP_UPLOAD_PORT)
                receiveData(ins);
            else if (whichPort == Server.TCP_DOWNLOAD_PORT)
            sendData(os);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends random data to the client to test the download bandwidth.
     * 
     * @param output
     *            the output stream to use
     * @throws IOException
     *             if an error is encountered when closing the connection
     */
    private void sendData(DataOutputStream output) throws IOException {
        double currentBytes = 0.0;
        int i = 0;
        //System.out.println("Trying to write 100 MB to the client via TCP...");
        try {
            // Create a 1MB buffer and fill it with random data
            ByteBuffer byteBuffer = ByteBuffer.allocate(BYTES_IN_MEGABYTES);
            byte[] byteArray = new byte[BYTES_IN_MEGABYTES];
            new Random().nextBytes(byteArray);
            byteBuffer.put(byteArray);
            byteBuffer.flip();

            // Write 100 MB to the output stream
            while (currentBytes <= (BYTES_IN_MEGABYTES * 100)) {
                output.write(byteBuffer.array(), 0, BYTES_IN_MEGABYTES);
                byteBuffer.clear();
                byteBuffer.put(byteArray);
                byteBuffer.flip();
                i++;
                currentBytes = (i * BYTES_IN_MEGABYTES);
            }
            //System.out.println("Finished writing 100 MB via TCP.");
            output.writeBytes("Content-Type: random/bytes\r\n\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            output.close();
        }
    }

    /**
     * Receives data from the client to test the upload bandwidth.
     * 
     * @param ins
     *            the input stream to use
     */
    private void receiveData(InputStream ins) {
        DataInputStream input = new DataInputStream(ins);
        //System.out
        //        .println("Waiting for the client to upload as much data as it'd like via TCP...");
        // Prepare to receive data
        byte[] byteArray = new byte[BYTES_IN_MEGABYTES];
        ByteBuffer byteBuffer = ByteBuffer.allocate(BYTES_IN_MEGABYTES);
        try {
            // Continuously receive data from the client, while it is available
            while (input.read() != -1) {
                input.read(byteArray);
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
        }
    }
}
