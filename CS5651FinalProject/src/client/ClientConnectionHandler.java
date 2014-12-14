package client;

import java.net.Socket;

/**
 * Handles connections based on the type and port.
 *
 */
public class ClientConnectionHandler {

    private Socket client;
    private int whichPort;

    /**
     * Creates a new ConnectionHandler.
     * 
     * @param client
     *            the connection to the client
     * @param whichPort
     *            the port to use
     */
    public ClientConnectionHandler(Socket client, int whichPort) {
        this.client = client;
        this.whichPort = whichPort;
        run();
    }

    /**
     * Runs the appropriate connection handler based on the port.
     */
    public void run() {
        switch (whichPort) {
            case Client.TCP_UPLOAD_PORT:
                testTcpUpload();
                break;
            case Client.TCP_DOWNLOAD_PORT:
                testTcpDownload();
                break;
        }
    }

    private void testTcpUpload() {
        new ClientHandleTCP(client, Client.TCP_UPLOAD_PORT);
    }

    private void testTcpDownload() {
        new ClientHandleTCP(client, Client.TCP_DOWNLOAD_PORT);
    }
}
