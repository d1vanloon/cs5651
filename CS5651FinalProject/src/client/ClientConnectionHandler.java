package client;

import java.net.Socket;

/**
 * Handles connections based on the type and port.
 *
 */
public class ClientConnectionHandler {

    private Socket server;
    private int whichPort;
    private final Client client;

    /**
     * Creates a new ConnectionHandler.
     * 
     * @param client
     *            the connection to the client
     * @param whichPort
     *            the port to use
     */
    public ClientConnectionHandler(Socket server, int whichPort, final Client client) {
        this.server = server;
        this.whichPort = whichPort;
        this.client = client;
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
        new ClientHandleTCP(server, Client.TCP_UPLOAD_PORT, this.client);
    }

    private void testTcpDownload() {
        new ClientHandleTCP(server, Client.TCP_DOWNLOAD_PORT, this.client);
    }
}
