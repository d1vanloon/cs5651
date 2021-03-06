package port;

import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class PortScanner extends Frame {

    static boolean insufficientData = false;
    Socket SOCK;
    int PORT_START;
    int PORT_END;
    int portStart;
    int portEnd;
    String TARGET;

    Frame main = new Frame("Port Scanner");
    JLabel lTitle = new JLabel("Port Scanner", JLabel.CENTER);
    JLabel lTarget = new JLabel("Target: ");
    JLabel lPortStart = new JLabel("Starting Port: ");
    JLabel lPortEnd = new JLabel("Ending Port: ");
    JLabel lStatus = new JLabel("Status: ", JLabel.CENTER);

    JTextField tfTarget = new JTextField(15);
    JTextField tfPortStart = new JTextField(15);
    JTextField tfPortEnd = new JTextField(15);

    JTextArea message = new JTextArea();
    JScrollPane scroll = new JScrollPane();

    JButton scan = new JButton("Scan");
    JButton reset = new JButton("RESET");
    JButton showOpenPorts = new JButton("Ports Open");
    
    JFrame openPorts = new JFrame("Open Ports");
    JTextArea openPortList = new JTextArea();
    

    // constructor
    public PortScanner(String host) {
        buildGUI(host);
    }

    // method to build the graphical uyser interface
    public void buildGUI(String host) {
    	
    	TARGET = host;
        main.setLayout(new GridLayout(5, 1));
        main.setSize(300, 320);
        main.setResizable(false);
       
        openPorts.setSize(300,320);
        openPorts.setResizable(false);
        openPorts.setVisible(false); 
        openPorts.add(openPortList);
        
        Panel upperPanel = new Panel(new GridLayout(3, 3));

        lTitle.setFont(new Font("Impact", Font.BOLD, 20));
        main.add(lTitle);

        lPortStart.setForeground(Color.blue);
        upperPanel.add(lPortStart);

        tfPortStart.setForeground(Color.blue);
        tfPortStart.setFocusable(true);
        upperPanel.add(tfPortStart);

        lPortEnd.setForeground(Color.blue);
        upperPanel.add(lPortEnd);

        tfPortEnd.setForeground(Color.blue);
        tfPortEnd.setFocusable(true);

        upperPanel.add(tfPortEnd);

        main.add(upperPanel);

        Panel lowerPanel = new Panel();
        showOpenPorts.setEnabled(false);
        JLabel space = new JLabel();
        lowerPanel.add(scan);
        lowerPanel.add(space);
        lowerPanel.add(reset);
        lowerPanel.add(space);
        lowerPanel.add(showOpenPorts);
        
        main.add(lowerPanel);

        scroll.setViewportView(message);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        main.add(scroll);

        lStatus.setForeground(Color.blue);
        main.add(lStatus);

        addEventHandlers();

        main.setVisible(true);
        main.repaint();
    }

    // adding the event handlers to our buttons
    public void addEventHandlers() {

        // action listener for the main window frame
        main.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(1);
            }
        });
        // action listener for the scan button
        scan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scanAction();
            }
        });
        // reset button action listener
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetAction();
            }
        });
        showOpenPorts.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openPorts.setVisible(true);
            }
        });
    }

    public void scanAction() {
        if (tfPortStart.getText().equals("")) {
            message.setText("Enter a STARTING PORT");
            return;
        } else if (tfPortEnd.getText().equals("")) {
            message.setText("Enter a END PORT");
            return;
        }

        insufficientData = false;

        message.setText("");

        Thread t1 = new Thread() {
            public void run() {
                scan.setEnabled(false);
                reset.setText("Stop");

                //TARGET = tfTarget.getText();
                PORT_START = Integer.parseInt(tfPortStart.getText());
                PORT_END = Integer.parseInt(tfPortEnd.getText());

                for (int i = PORT_START; i <= PORT_END; i++) {
                    lStatus.setText("Port: " + i + " is being tested.");
                    if (insufficientData)
                        break;
                    try {
                        SOCK = new Socket(TARGET, i);
                        message.append("Port: " + i + " is open\n");
                        openPortList.append("Port: " + i + " is open\n");
                        
                        SOCK.close();
                    } catch (Exception x) {
                        message.append("Port: " + i + " is closed\n");
                        continue;
                    }
                }
                JOptionPane.showMessageDialog(null, "Port Scan Complete");
                scan.setEnabled(true);
                showOpenPorts.setEnabled(true);
                reset.setText("RESET");
                lStatus.setText("Press Scan to Start");
            }
        };
        t1.start();
    }// end scanAction

    public void resetAction() {
        message.setText("");
        openPortList.setText("");
        tfTarget.setText("");
        tfPortStart.setText("");
        tfPortEnd.setText("");

    }

    

}
