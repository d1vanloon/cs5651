package server;

import javax.swing.*;

import client.Client;
import port.PortScanner;

import java.io.*;
import java.net.*; 
import java.util.ArrayList;
import java.awt.*; 
import java.awt.event.*;
/**
 *
 * @author jordanross1992
 */
public class Server implements Runnable, ActionListener {
    private JFrame jFrame;
    private ServerSocket serverSocket; 
    private Socket clientSocket;
    private ObjectInputStream ois; 
    private ObjectOutputStream oos; 
    private JTextArea jta; 
    private JScrollPane jScroll; 
    private JTextField jtfInput; 
    private JButton btnSend;
    public static final int TCP_UPLOAD_PORT = 8080;
    public static final int TCP_DOWNLOAD_PORT = 8000; 
    
    public static ArrayList<Socket> connectionArray = new ArrayList<Socket>();
    public static ArrayList<String> currentUsers = new ArrayList<String>();
    
    public Server(){
    	//instantiate all the private instance fields 
        jFrame = new JFrame("Server");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLayout(new FlowLayout());
        jFrame.setSize(300,320);
        Thread myThread = new Thread(this);
        myThread.start();
        jta = new JTextArea(15,15);
        jta.setEditable(false);
        jta.setLineWrap(true);
        jScroll = new JScrollPane(jta);
        jtfInput = new JTextField(15);
        //ActionListener is defined below 
        jtfInput.addActionListener(this);
        btnSend = new JButton("Send");
        btnSend.addActionListener(this);
        //incorporate all the GUI objects into the Frame
        jFrame.getContentPane().add(jScroll);
        jFrame.getContentPane().add(jtfInput);
        jFrame.getContentPane().add(btnSend);
        jFrame.setVisible(true);
        
        
        setupConnectionMonitoring();
    }
    //Runnable method the creates a new server socket for the client to connect
    //to. 
    @Override
    public void run(){
        try{
        	ArrayList<Thread> threads = new ArrayList<Thread>();
        	 serverSocket = new ServerSocket(4444);
             clientSocket = serverSocket.accept();
      	   oos = new ObjectOutputStream(clientSocket.getOutputStream());
           ois = new ObjectInputStream(clientSocket.getInputStream());              

    
           
           while(true){
        	   Object input = ois.readObject(); 
               jta.setText(jta.getText() + "client says: " + (String)input + "\n");
               
           }
           
        }
        catch(IOException e){
            e.printStackTrace();
        }
        catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        
    }
    //Action Listener Method 
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getActionCommand().equals("Send") || e.getSource() instanceof JTextField){
            try{
                oos.writeObject(jtfInput.getText());
                jta.setText(jta.getText() + "you say: " + jtfInput.getText() + "\n");   
            }
            catch(IOException ae){
                ae.printStackTrace();
            }
            
        }
    }
    
    /**
     * Starts the threads that monitor the connection quality.
     */
    public void setupConnectionMonitoring() {

        // Listen for TCP upload requests
        new Thread() {
            public void run() {
                try {
                    spawnConnectionHandler(new ServerSocket(Server.TCP_UPLOAD_PORT));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        // Listen for TCP download requests
        new Thread() {
            public void run() {
                try {
                    spawnConnectionHandler(new ServerSocket(Server.TCP_DOWNLOAD_PORT));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * Spawns a new connection handler for the given socket.
     * 
     * @param listen
     *            the socket for the connection
     */
    public void spawnConnectionHandler(ServerSocket listen) {
        try {
            while (true) {
                System.out
                        .println("Server listening for new TCP connections on port: "
                                + listen.getLocalPort());
                Socket client = listen.accept();
                new ServerConnectionHandler(client);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 
     * 
     * @param args the command line arguments
     */
    //main method
    public static void main(String[] args) {
        // TODO code application logic here
    	SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
            	new Server();
            }
        });
        
    }

}



