package client;

import java.net.Socket;

/**
 * Handles connections based on the type and port.
 *
 */
public class ClientConnectionHandler {

    private Socket client;

    /**
     * Creates a new ConnectionHandler.
     * 
     * @param client
     *            the connection to the client
     */
    public ClientConnectionHandler(Socket client) {
        this.client = client;
        run();
    }

    /**
     * Runs the appropriate connection handler based on the port.
     */
    public void run() {
        switch (client.getLocalPort()) {
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
