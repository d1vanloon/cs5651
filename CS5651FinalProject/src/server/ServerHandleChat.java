package server;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.JTextArea;

import client.Client;

public class ServerHandleChat extends Thread implements Runnable {

	private final ObjectInputStream ois;
	private volatile JTextArea textArea;

	public ServerHandleChat(final ObjectInputStream ois,
			final JTextArea textArea) {
		this.ois = ois;
		this.textArea = textArea;
	}

	@Override
	public void run() {
		System.out.println("Entering run of ServerHandleChat.");
		
		while (true) {
			Object input;
			try {
				input = ois.readObject();
				System.out.println("client says: " + (String) input);
				textArea.setText(textArea.getText() +  "client says: "
						+ (String) input + "\n");
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
