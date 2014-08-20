package PI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Class responsible of retrieving information from an android device.
 * @author Linus Granath
 *
 */
public class AcceptConnections extends Thread {

	private Socket socket;
	private BufferedReader bufferedReader;
	private Controller controller;
	private int player;
	
	/**
	 * Construct the class with a given socket and player id. As well as an reference to our controller
	 * @param socket
	 * @param controller
	 * @param player
	 */
	public AcceptConnections(Socket socket, Controller controller, int player) {
		this.controller = controller;
		this.socket = socket;
		this.player = player;
		this.start();
	}

	/**
	 * Run the thread, check for input
	 */
	@Override
	public void run() {
		String inputLine = null;
		InputStreamReader isr = null;
		try {
			bufferedReader = new BufferedReader((isr = new InputStreamReader(
					socket.getInputStream())));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while (true && socket!=null) {
		if (bufferedReader != null && isr !=null) {
			try {
				if ((inputLine = bufferedReader.readLine()) != null) {
				} else if ((inputLine = bufferedReader.readLine()) == null) {
					socket.close();
					socket = null;
					controller.subtractConnected();
				}
			} catch (IOException e) {
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				socket = null;
				controller.subtractConnected();
				e.printStackTrace();
			}
			if(inputLine!=null){
			controller.navigate(player+","+inputLine);
			System.out.print(player+" , "+inputLine);} 

		}
		// Small delay to prevent to much CPU usage
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	}
	
	/**
	 * Return if a player is connected
	 * @return connected
	 */
	public boolean isConnected(){
		if(socket==null)
			return false;
		return !socket.isClosed();
	}
}


