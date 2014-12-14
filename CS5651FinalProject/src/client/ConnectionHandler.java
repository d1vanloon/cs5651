package client;

import java.net.Socket;

import server.HandleTCP;

/**
 * Handles connections based on the type and port.
 *
 */
public class ConnectionHandler {

    private Socket client;

    /**
     * Creates a new ConnectionHandler.
     * 
     * @param client
     *            the connection to the client
     */
    public ConnectionHandler(Socket client) {
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
        new HandleTCP(client, Client.TCP_UPLOAD_PORT);
    }

    private void testTcpDownload() {
        new HandleTCP(client, Client.TCP_DOWNLOAD_PORT);
    }
}
