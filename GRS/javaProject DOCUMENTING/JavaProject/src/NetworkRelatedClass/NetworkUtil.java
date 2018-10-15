package NetworkRelatedClass;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * This class contains the socket through which server and client
 * is connected. It contains method to  write in the given
 * socket different objects and read from it.
 * It also can  clode connection when needed.
 */
public class NetworkUtil
{
	//server-client connection socket
	private Socket socket;
	//socket's output stream to write object
	private ObjectOutputStream oos;
	//socket;s input stream to read object
	private ObjectInputStream ois;

	/**
	 * creates a socket in the given port and createes object output and input stream
	 * of the created socket.
	 * @param s ip-address of the host
	 * @param port port number
	 */
	public NetworkUtil(String s, int port) {
		try {
			this.socket=new Socket(s,port);  
			oos=new ObjectOutputStream(socket.getOutputStream());
			ois=new ObjectInputStream(socket.getInputStream());
		} catch (Exception e) {
			System.out.println("In NetworkUtil : " + e.toString());
		}
	}

	/**
	 * creates object output and input stream of the given socket.
	 * @param s socket supplied by server or client
	 */
	public NetworkUtil(Socket s) {
		try {
			this.socket = s;
			oos=new ObjectOutputStream(socket.getOutputStream());
			ois=new ObjectInputStream(socket.getInputStream());
		} catch (Exception e) {
			System.out.println("In NetworkUtil : " + e.toString());
		}
	}

	/**
	 *
	 * @return object after reading
	 */
	public Object read() {
		Object o = null;
		try {
			o=ois.readObject();
		} catch (Exception e) {
		  //System.out.println("Reading Error in network : " + e.toString());
		}
		return o;
	}

	/**
	 *
	 * @param o write this object
	 */
	public void write(Object o) {
		try {
			oos.writeObject(o);                        
		} catch (IOException e) {
			System.out.println("Writing  Error in network : " + e.toString());
		}
	}

	/**
	 * closes the connection between server and client
	 */
	public void closeConnection() {
		try {
			ois.close();
			oos.close();
		} catch (Exception e) {
			System.out.println("Closing Error in network : "  + e.toString());
		}
	}
}

