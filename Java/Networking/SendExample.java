package PI;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

/**
 * A class to send something to the client
 * 
 * @author Linus Granath
 * 
 */
public class Sender extends Thread {
	boolean finished = false;
	private Socket socket;
	private ServerSocket servSocket;
	public int SERVERGET = 8081;
	public String serverAddress = null;
	private ArrayList<Socket> sendSockets = new ArrayList<Socket>();
	private Controller controller;
	byte[] content;

	public Sender(Controller controller) {
		this.controller = controller;
		start();
	}

	@Override
	public void run() {
		try {
			servSocket = new ServerSocket(SERVERGET);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		while (true) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			try {
				System.out.println("Waiting");
				socket = servSocket.accept();
				sendSockets.add(socket);

				System.out.println("ADDED");
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// while (!finished) {

			// PrintWriter outp = null;

			// stopMe();
			// }

		}

	}

	public void stopMe() {
		finished = true;
	}

	public void send() {
		for (int i = 0; i < this.sendSockets.size(); i++) {
			new SendToAndroidDevice(sendSockets.get(i), "Player, 5");

			System.out.println("Send to " + i);

		}
	}

	private class SendToAndroidDevice extends Thread {
		Socket sendSocket = null;
		String message;

		public SendToAndroidDevice(Socket socket, String message) {
			this.sendSocket = socket;
			this.message = message;
			start();
		}

		public SendToAndroidDevice(Socket socket, byte[] contentSend) {
			this.sendSocket = socket;
			content = contentSend;
			message = "layout";
			start();
		}

		public void run() {
			System.out.println("send please 1");
			if (message.contains("Player")) {
				PrintWriter outp = null;
				try {
					outp = new PrintWriter(sendSocket.getOutputStream(), true);
					outp.println(message);
					if (outp.checkError()) {
						sendSockets.remove(sendSocket);
						System.out.println("REMOVED SOCKET");
					}
					System.out.println("SENDDEVICE");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			System.out.println("send please 5");
			if (message.equals("layout")) {	
				PrintWriter outp = null;
				try {
					outp = new PrintWriter(sendSocket.getOutputStream(), true);
					outp.println(message);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println("send please 5");
				File file = new File(Values.PATHLAYOUT);
				byte[] content = new byte[(int) file.length()];
				FileInputStream fileInStream = null;
				try {
					fileInStream = new FileInputStream(file);
				} catch (FileNotFoundException ex) {
					JOptionPane.showMessageDialog(null, "didn't find file");
					System.out.println(ex);
				}
				BufferedInputStream bis = new BufferedInputStream(fileInStream);

				try {
					BufferedOutputStream outToClient = new BufferedOutputStream(
							sendSocket.getOutputStream());
					System.out.println("Sending " + file);
					bis.read(content, 0, content.length);
					outToClient.write(content, 0, content.length);
					outToClient.flush();
					outToClient.close();
					content = null; // 0428 TA
					System.gc(); // 0428 TA
					return;
				} catch (IOException ex) {
					System.out.println(ex);
					// client app was terminated during transfer
				}
			}
		}
	}

	public void send(byte[] content) {
		for (int i = 0; i < this.sendSockets.size(); i++) {
			new SendToAndroidDevice(sendSockets.get(i), content);

			System.out.println("Send to " + i);

		}
	}

}
