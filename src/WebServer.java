import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {

	private static final int PORT = 7777;

	public static void main(String [] args) {
		try {
			@SuppressWarnings("resource")
			ServerSocket server = new ServerSocket(PORT);
			while (true) {
				Socket socket = server.accept();
				WebRequest client = new WebRequest(socket);
				client.start();
			}
		} catch(Exception e) {
			System.out.println(e);
		}
	}
}