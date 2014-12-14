package client;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author jordanross1992
 */
public class Client implements Runnable, ActionListener {
    private JFrame jFrame;

    private Socket Socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private JTextArea jta;
    private JScrollPane jScroll;
    private JTextField jtfInput;
    private JButton btnSend;
    public static final int TCP_UPLOAD_PORT = 8080;
    public static final int TCP_DOWNLOAD_PORT = 8000;
    private final String serverAddress;

    public Client(final String serverAddress) {
        this.serverAddress = serverAddress;

        // formatting the window
        jFrame = new JFrame("Client");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLayout(new FlowLayout());
        jFrame.setSize(300, 320);

        // Create a thread for this client
        Thread myThread = new Thread(this);
        myThread.start();

        jta = new JTextArea(15, 15);
        jta.setEditable(false);
        jta.setLineWrap(true);
        jScroll = new JScrollPane(jta);
        jtfInput = new JTextField(15);

        // ActionListener for the send button
        jtfInput.addActionListener(this);
        btnSend = new JButton("Send");
        btnSend.addActionListener(this);
        // Add the separate gui objects to the Frame
        jFrame.getContentPane().add(jScroll);
        jFrame.getContentPane().add(jtfInput);
        jFrame.getContentPane().add(btnSend);

        jFrame.setVisible(true);

    }

    // run method the creates the socket and writes to and from it
    @Override
    public void run() {
        try {
            this.testConnection();
            
            Socket = new Socket(this.serverAddress, 4444);
            oos = new ObjectOutputStream(Socket.getOutputStream());
            ois = new ObjectInputStream(Socket.getInputStream());
            // while this socket is available read the input from the server
            while (true) {
                Object input = ois.readObject();
                jta.setText(jta.getText() + "server says: " + (String) input
                        + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private void testConnection() {
        System.out.println("Testing connection to server...");
        
        // Test TCP upload
        new Thread() {
            public void run() {
                try {
                    Socket connection = new Socket(serverAddress, TCP_UPLOAD_PORT);
                    System.out.println("Testing upload connection...");
                    new ClientConnectionHandler(connection, TCP_UPLOAD_PORT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        // Test TCP download
        new Thread() {
            public void run() {
                try {
                    Socket connection = new Socket(serverAddress, TCP_DOWNLOAD_PORT);
                    System.out.println("Testing download connection...");
                    new ClientConnectionHandler(connection, TCP_DOWNLOAD_PORT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    // ActionListener
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Send")
                || e.getSource() instanceof JTextField) {
            try {
                oos.writeObject(jtfInput.getText());
                jta.setText(jta.getText() + "you say: " + jtfInput.getText()
                        + "\n");
            } catch (IOException ae) {
                ae.printStackTrace();
            }

        }

    }

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Client("localhost");
            }
        });
    }

}