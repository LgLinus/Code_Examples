import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;


/**
 * Class responsible of handling information sent and retrieved by the server
 * 
 * @author Linus Granath
 * 
 */
public class ServerCommunication {

	private Sender sender;
	private Receiver receiver;
	private int sendPort = 8085;
	private int receivePort = 8086;
	private Controller controller;
	ServerSocket sendSocket;
	Socket socket = null;

	ServerSocket serverSocket = null;
	Socket clientSocket = null;
	BufferedReader bufferedReader = null;

	public ServerCommunication(Controller controller) {
		this.controller = controller;
		receiver = new Receiver();
		receiver.start();
		sender = new Sender();
		sender.start();

	}

	private class Sender extends Thread {

		public void run() {
			ServerSocket sendServSocket = null;
			try {
				sendServSocket = new ServerSocket(sendPort);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			while (true) {
				if (Controller.dataBaseName != null) {

					// socket = new Socket(ip, sendPort);

					try {
						socket = sendServSocket.accept();
						new SendToPi(socket);
						socket.setSoTimeout(10000);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
			}
		}
	}

	private class SendToPi extends Thread {
		Socket socket = null;

		public SendToPi(Socket socket) {
			this.socket = socket;
			start();
		}

		public void run(){


			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			PrintWriter outp = null;

			try {
				outp = new PrintWriter(socket.getOutputStream(), true);
				String ret[] = controller.retreiveData();
				for (int i = 0; i < ret.length; i++) {
					outp.println(ret[i]);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				outp.println("end"); }// Tell the game we have sent all
				catch(IOException e){// the info
					
				}
//				break;
			}
		}
	

	private class Receiver extends Thread {

		public void run() {
			try {
				serverSocket = new ServerSocket(receivePort);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			while (true) {
				try {
					if (clientSocket == null) {
						clientSocket = serverSocket.accept();
						clientSocket.setSoTimeout(60 * 1000 * 120);
						
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (clientSocket != null) {
						bufferedReader = new BufferedReader(
								new InputStreamReader(
										clientSocket.getInputStream()));
						String inputLine = bufferedReader.readLine();
						if (inputLine != null) {
							if (inputLine.contains("INSERT INTO")) {
								controller.receivedInfo(inputLine);
							} else if (inputLine
									.contains("CREATE TABLE IF NOT EXISTS")) {
								controller.setDataBaseName(inputLine);
							}

						} else if (bufferedReader.readLine() == null) {
							clientSocket.close();
							clientSocket = null;
						}
					}
				} catch (SocketException e) {
					try {
						clientSocket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					clientSocket = null;
					e.printStackTrace();
				} catch (IOException e) {
					clientSocket = null;
					e.printStackTrace();
				}
			}
		}
	}
}
