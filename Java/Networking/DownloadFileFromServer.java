//Remember to change package to match your local setup!
package PI;

import java.net.*;
import java.io.*;

import javax.swing.JOptionPane;

/**
 * This is the finalized class for downloading a game of choice from the server (derived from the testclass
 * "PiToServer"). There is no GUI here since the main GUI of the game console should handle all that. Instead,
 * when you want to get a game from the server, you create an instance of this class and enclose the gamename
 * to be downloaded (and launched afterwards from a different class) as a String parameter.
 * There will be no upload option in this class. Uploading TO the server will be handled in a separate class.
 * All the parameters that are to be changed from a programmer's POV are declared in the beginning of the class.
 * 
 * If you want to get the LIST OF FILES - use "getGameList" method.
 * If you want to download a FILE (not necessarily a game/jar-file) - use "getGame" method.
 * 
 * @author Skalis (TA) with assistance of ML.
 *
 */
public class GetDataFromServer {
    private final static String serverIP = "localhost"; // The IP address of the server. Use "localhost" to test locally
	private final static int gameDownloadPort = 3248; // the port used when downloading a game
    private final static int gameChoicePort = 3247; // the port used for the selection choice (different type of connection)
    // Make sure that the server-app uses the same ports as specified above!
//    private final static String fromPath = "C:/temp/from/"; // the "server" path (for future potential use)
    protected final static String toPath = "C:/temp/to/"; // this is where the dl'd files are put (windows path)
    protected final static String requestList = "gamelist"; // the string to send when we only want to fetch the list of games
    private String fileOutput; // The file to be downloaded
    private byte[] aByte = new byte[1];
    private int bytesRead;
    private String[] gameList;
    Socket clientSocket = null;
    InputStream is = null;
	ObjectInputStream input; //140505 ML

    // Opens a "printWriter socket" to send the filename to the server, letting it know which file we want to download,
    // or if we simply wants to get a list of games on the server.
	private void requestData(String choice) {
		Socket socket;
		PrintWriter printWriter;
		try	{
			socket = new Socket(serverIP, gameChoicePort);
			printWriter = new PrintWriter(socket.getOutputStream(),true);
			printWriter.println(choice);
			socket.close();
		}
			catch(Exception e) {
				System.out.println(e);
    	}
	}
	
    /**
     * Starts the download in a separate thread.
     * @param choice The filename of the game to download
     * @throws InterruptedException 
     */
    public void getGame(String choice) throws InterruptedException {
		requestData(choice);
		fileOutput = toPath+choice;
        Thread thread = new Thread(new DownloadGame()); // run the download in a separate thread
        thread.start();
        thread.join();
    }
    
	/**
	 * This method returns a String array of the filenames of all the games available 
	 * for download on the server
	 * @return A string array with the filenames of all the games available for download 
	 * @author Mattias Larsson
	 * 
	 */
	public String[] getGameList() {
		try {
			requestData(requestList);	//sends a request for a list of all games
			Thread thread = new Thread(new RecieveGamelist());
			thread.start();
			thread.join();	//waits for the thread to die before continuing
		} catch (InterruptedException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return gameList;
	}
	
	/**
	 * Inner class that connects and downloads the selected game from server, using bytestream.
	 * @author Skalis
	 *
	 */
    private class DownloadGame implements Runnable {
        public void run() {
            try {
                clientSocket = new Socket(serverIP , gameDownloadPort);
                is = clientSocket.getInputStream();
            } catch (IOException ex) {
                // Do exception handling
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            if (is != null) {
                FileOutputStream fos = null;
                BufferedOutputStream bos = null;
                try {
                    fos = new FileOutputStream(fileOutput);
                    bos = new BufferedOutputStream(fos);
                    bytesRead = is.read(aByte, 0, aByte.length);
                    
                    do { // Start fetching the bytes, byte for byte
                            baos.write(aByte);
                            bytesRead = is.read(aByte);
                    } while (bytesRead != -1);

                    bos.write(baos.toByteArray());
                    bos.flush();
                    bos.close();
                    clientSocket.close();
                } catch (IOException ex) {
                    // Do exception handling
                }
            }
        }
    }
    
	/**
	 * Inner class that connects to the server and recieves a String array with the filenames
	 * of all the files located on the server 
	 * @author Mattias Larsson
	 */
	private class RecieveGamelist implements Runnable {	
		public void run() {
			try {
				clientSocket = new Socket(serverIP,gameDownloadPort);
				input = new ObjectInputStream(clientSocket.getInputStream()); 
				gameList = (String[]) input.readObject(); 
				input.close();
				clientSocket.close();
				
			} catch (IOException e) {
				System.out.println(e);
			} catch (ClassNotFoundException e2) {
				System.out.println(e2);
			}
		}
	}
 	
}
