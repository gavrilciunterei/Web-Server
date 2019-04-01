import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class WebRequest extends Thread {
	private final String HTML_ENCODING = "UTF-8";
	private final String HTML_INDEX = "index.html";
	private final String HTML_PATH = ".";

	private Socket client;

	public WebRequest(Socket socket) {
		client = socket;
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), HTML_ENCODING), true);

			boolean ok = false;
			while (!ok) {
				String line = in.readLine();
				System.out.println(line); // Muestro la peticion
				String[] tokens = line.split(" "); // Separa hasta el espacio, GET + RUTA
				if (tokens.length >= 2 && tokens[0].equals("GET")) { // SI ES MAYOR A 2 (GET+RUTA) Y SI EL PRIMERO ES UN
																		// GET
					outHtml(out, tokens[1]); // LLAMA AL METODO ENVIANDOLE EL PrintWriter Y LA RUTA
					ok = true;
				} else {
					out.println("400 Petici√≥n Incorrecta");
				}
			}

			out.close();

			boolean empty = false;
			while (!empty) {
				String str = in.readLine();
				empty = str == null || str.trim().isEmpty();
			}

			in.close();

		} catch (Exception e) {
		}
	}

	private void outHtml(PrintWriter out, String path) {

		// EMPIEZA EN: /pag/css/design.css HTTP/1.1/

		// SI LA STRING EMPIEZA CON / SE LA QUITA
		if (path.startsWith("/")) {
			path = path.substring(1);
		}

		// SI ACABA EN / o SI EST¡ VACIO LE PONE INDEX.HTML
		if (path.endsWith("/") || path.equals("")) {
			path += HTML_INDEX;
		}

		// PONE ./RUTA
		path = HTML_PATH + "/" + path;

		try {
			File file = new File(path);
			DataOutputStream out2 = new DataOutputStream(client.getOutputStream());

			if (file.exists()) {

				int numOfBytes = (int) file.length();
				FileInputStream inFile = new FileInputStream(path);
				byte[] fileInBytes = new byte[numOfBytes];
				inFile.read(fileInBytes);
				out2.writeBytes("HTTP/1.0 200 Document Follows\r\n");
				out2.writeBytes(tipo(path));
				out2.writeBytes("Content-Length:" + numOfBytes + "\r\n");
				out2.writeBytes("\r\n");
				out2.write(fileInBytes, 0, numOfBytes);
				inFile.close();
				
			} else {

				String statusLine = "HTTP/1.1 404 Not found\r\n";
				String contentType = "Content-type:" + tipo(path) + "\r\n";
				String entityBody = "<HTML><HEAD><TITLE>404 Not Found</TITLE></HEAD>"
						+ "<BODY><center><h1>404 Not Found</h1></center></BODY></HTML>";

				out2.writeBytes(statusLine);
				out2.writeBytes(contentType);
				out2.writeBytes("\r\n");
				out2.writeBytes(entityBody);
			}
			out2.close();
		} catch (Exception e) {
		}

	}

	private String tipo(String path) {
		
	
		if (path.endsWith(".html"))
			return ("Content-Type: text/html\r\n");
		else if (path.endsWith(".css"))
			return ("Content-Type: text/css\r\n");
		else if (path.endsWith(".jpg"))
			return ("Content-Type: image/jpeg\r\n");
		else if (path.endsWith(".gif"))
			return ("Content-Type: image/gif\r\n");
		else if (path.endsWith(".png"))
			return ("Content-Type: image/png\r\n");
		else if (path.endsWith(".mp4"))
			return ("Content-Type: video/mp4\r\n");
		else if (path.endsWith(".js"))
			return ("Content-Type: text/javascript\r\n");
		return null;
	}

}
