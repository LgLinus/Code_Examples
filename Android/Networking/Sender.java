package com.controlforconsol;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.StrictMode;

/**
 * A thread that connects to the server and sends messages
 * 
 * @author LgLinuss
 * 
 */
public class Sender extends Thread {
	Controller main = null;
	private boolean finished = true;
	private String sendInfo;

	public Sender(Controller controller) {
		this.main = controller;
	}

	/**
	 * Stop the thread
	 */
	public void stopMe() {
		finished = true;
	}

	public void stopThread() {
		finished = true;
		this.interrupt();
	}

	/**
	 * Runs when the thread is started
	 */
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {

			}
			while (!finished) {
				if (main.getSocket() == null) {
					try {
						InetAddress serverAddress = InetAddress.getByName(main
								.getServerIP());
						Socket socket = new Socket();
						socket.connect(new InetSocketAddress(serverAddress,
								main.getServerPort()), 3000);
						main.setSocket(socket); // Try and establish connection
												// to the server

						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						main.sendIp(); // Send the devices ip to the server, so
										// the server can reach the device
					} catch (UnknownHostException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				@SuppressWarnings("unused")
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
						.detectAll().penaltyLog().build();

				PrintWriter outp = null;
				try {
					if (main.getSocket() != null) {
						outp = new PrintWriter(main.getSocket()
								.getOutputStream(), true); // Allows us to send
															// information
						outp.println(sendInfo); // Skicka värdet
						stopMe(); // Stop the thread so we don't resend the
									// information
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Start the thread
	 */
	public void startThread() {
		finished = false;
	}

	/**
	 * Called when we want to send a value
	 * 
	 * @param button
	 *            information to send
	 */
	public void send(String button) {
		sendInfo = button;
		finished = false;
	}

}
