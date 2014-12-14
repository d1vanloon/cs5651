package server;

import java.net.Socket;

/**
 * Handles connections based on the type and port.
 *
 */
public class ServerConnectionHandler {

    private Socket client;
    /**
     * Creates a new ConnectionHandler.
     * 
     * @param client
     *            the connection to the client
     */
    public ServerConnectionHandler(Socket client) {
        this.client = client;
        run();
    }

    /**
     * Runs the appropriate connection handler based on the port.
     */
    public void run() {
        switch (client.getLocalPort()) {
            case Server.TCP_UPLOAD_PORT:
                testTcpUpload();
                break;
            case Server.TCP_DOWNLOAD_PORT:
                testTcpDownload();
                break;
        }
    }

    private void testTcpUpload() {
        new ServerHandleTCP(client, Server.TCP_UPLOAD_PORT);
    }

    private void testTcpDownload() {
        new ServerHandleTCP(client, Server.TCP_DOWNLOAD_PORT);
    }
}
