package com.controlforconsol;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

import android.content.Context;
import android.os.StrictMode;

/**
 * A thread that can receive information from the console
 * 
 * @author LgLinuss
 * 
 */
public class Receive extends Thread {
	Controller controller = null;
	private BufferedReader bufferedReader;
	private Socket clientSocket = null;
	private InputStream is = null;
	private int bytesRead;
	private byte[] aByte = new byte[1];

	public Receive(Controller controller) {
		this.controller = controller;
		this.start();
	}

	/**
	 * Run the thread
	 */
	@Override
	public void run() {
		@SuppressWarnings("unused")
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.detectAll().penaltyLog().build();
		try {

			while (true) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				if (controller.getReceiveClientSocket() == null) {
					InetAddress serverAddress = InetAddress
							.getByName(controller.getServerIP());
					clientSocket = new Socket(serverAddress,
							controller.SERVERRECEIVEPORT);
					controller.setReceiveClientSocket(clientSocket);
					try {
						bufferedReader = new BufferedReader(
								new InputStreamReader(controller
										.getReceiveClientSocket()
										.getInputStream()));
					} catch (IOException e) {
					}
				} else {

					String inputLine = null;
					if ((inputLine = bufferedReader.readLine()) != null) {
						if (inputLine.contains("Player")) {
							String[] split = inputLine.split(",");
							controller.changePlayerText(split[1]);
						} else if (inputLine.equals("layout")) {

							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							is = controller.getReceiveClientSocket()
									.getInputStream();
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							if (is != null) {
								FileOutputStream fos = null;
								BufferedOutputStream bos = null;
								try {
									fos = controller
											.getGUI()
											.getApplicationContext()
											.openFileOutput("layout.txt",
													Context.MODE_PRIVATE);
									bos = new BufferedOutputStream(fos);
									bytesRead = is.read(aByte, 0, aByte.length);
									do { // Start fetching the bytes, byte for
											// byte
										baos.write(aByte);
										bytesRead = is.read(aByte);
									} while (bytesRead != -1);
									controller.readFile();
									bos.write(baos.toByteArray());
									bos.flush();
									bos.close();
									break;
								} catch (IOException ex) {
								}
							}
						}

					} else {
						clientSocket.close();
						controller.setReceiveClientSocket(null);
					}

				}
			}
		} catch (IOException e) {
		}
	}

	/**
	 * Stop the thread
	 */
	public void remove() {
		try {
			if (controller.getReceiveClientSocket() != null)
				controller.getReceiveClientSocket().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		controller.setReceiveClientSocket(null);
		stopThread();
	}

	/**
	 * Stop the thread
	 */
	public void stopThread() {
		this.interrupt();
	}

}
