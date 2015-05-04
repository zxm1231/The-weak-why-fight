import java.io.*;
import java.net.*;

public class ConnectionThread extends Thread {
	Socket client;
	int counter;
	public ConnectionThread(Socket client1 , int counter1){
		client = client1;
		counter = counter1;
	}
	public void run(){
		try{
			String destIP = client.getInetAddress().toString();
			int destPort = client.getPort();
			System.out.println("Connection " + counter + " : connected to " + destIP + " on port " + destPort); 
			PrintStream outStream = new PrintStream(client.getOutputStream());
			DataInputStream inStream = new DataInputStream(client.getInputStream());
			String message = inStream.readLine();
			System.out.println("Received: " + message);
			if(getquest(message)){
				String filename = getfilename(message);
				File file = new File(filename);
				if(file.exists()){
					System.out.println(filename + " requested.");
					outStream.println("HTTP/1.0 200 OK");
					outStream.println("MIME_version:1.0");
					outStream.println("Content_Type:text/html");
					int len = (int) file.length();
					outStream.println("Content_Length:" + len);
					outStream.println();
					sendfile(outStream , file);
					outStream.flush();
				}
				else{
					String notfound = "<html><head><title>Not Found</title></head><body><h1>Error 404-file not found</h1></body></html>";
					outStream.println("HTTP/1.0 404 not found");
					outStream.println("Content_Type:text/html");
					outStream.println("Content_Length:" + notfound.length() + 2);
					outStream.println("");
					outStream.println(notfound);
					outStream.flush();
				}
			}
			for(int x = 1 ; x < 10000000 ; x++);
			client.close();
		}catch(IOException e){
			System.out.println("Exception:" + e);
		}
	}
	boolean getquest(String str){
		if(str.length() > 3){
			if(str.substring(0 , 3).equalsIgnoreCase("GET"))return true;
		}
		return false;
	}
	String getfilename(String str){
		String sf = str.substring(str.indexOf(' ') + 1);
		sf = sf.substring(0 , sf.indexOf(' ' ));
		try{
			if(sf.charAt(0) == '/'){
				sf = sf.substring(1);
			}
		}catch(StringIndexOutOfBoundsException e){
			System.out.println("Exception:" + e);
		}
		if(sf.equals(""))sf = "index.html";
		return sf;
	}
	void sendfile(PrintStream outStream , File file){
		try{
			DataInputStream inStream = new DataInputStream(new FileInputStream(file));
			int len = (int) file.length();
			byte buf[] = new byte[len];
			inStream.readFully(buf);
			outStream.write(buf , 0 , len);
			outStream.flush();
			inStream.close();
		} catch(Exception e){
			System.out.println("Error retireving file.");
			System.exit(1);
		}
	}
}
