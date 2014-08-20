package PI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * A class to receive something from the andriod client
 * @author Ivan
 *
 */
public class Receiver extends Thread {
	private static ServerSocket serverSocket;
	private static Socket clientSocket;
	private static BufferedReader bufferedReader;
	private static String inputLine;
	private ArrayList<AcceptConnections> connections = new ArrayList<AcceptConnections>();
	private Controller controller;
	static int Val = 0;


	public Receiver(Controller controller) {

		this.controller = controller;
		start();
	}

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(8080);
			while (true) {
				try{
					Thread.sleep(1);
				}
				catch(InterruptedException e){
					
				}
					System.out.println("test");
					clientSocket = serverSocket.accept();
					createNewConnection(clientSocket);
					clientSocket.setSoTimeout(1000*120);
					controller.addConnected();
			}
			
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	private void createNewConnection(Socket socket) {
		getPlayerNumber(socket);
		
	}

	private void getPlayerNumber(Socket socket) {
		int player = 0;
		boolean connected  = false;
		for(int i = 0; i < connections.size();i++){
			if(!connections.get(i).isConnected()){
				System.out.println("Connection is closed");
				connections.get(i).stop();
				connections.set(i, new AcceptConnections(socket, controller, i+1));
				connected = true;
				break;
			}
		}
		if(!connected) {
			System.out.println("PLAYER CONNECTED : " + (connections.size()+1));
			connections.add(new AcceptConnections(socket,controller,connections.size()+1));
		}
	}
}
