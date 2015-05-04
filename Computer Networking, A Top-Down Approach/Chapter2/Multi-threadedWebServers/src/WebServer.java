import java.io.*;
import java.net.*;

public class WebServer {
	public static void main(String argv[]){
		int i = 0 , PORT = 8090;
		ServerSocket server = null;
		Socket client = null;
		try{
			server = new ServerSocket(PORT);
			System.out.println("Web Server is listening on port " + server.getLocalPort());
			while(true){
				client = server.accept();
				new ConnectionThread(client , i).start();
				i++;
			}
		}catch(Exception e){System.out.println(e);}
	}
}
