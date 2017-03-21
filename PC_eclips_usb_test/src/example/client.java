package example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class client {
	public static void main(String[] args) throws Exception, IOException {
		while (true) {
			// 基于TCP的客户端程序
			//来个手机
//			String host = "127.0.0.1";
//			Socket s = new Socket(host, 8000);
//			//控制台录入
//			System.out.println("请输入：");
//			Scanner sc = new Scanner(System.in);
//			String sss = sc.nextLine();
//			//打电话
//			OutputStream os = s.getOutputStream();
//			os.write(sss.getBytes());
//			//获取serverIP地址
//			String serverIP = s.getInetAddress().getHostAddress();
//			//接电话
//			InputStream is = s.getInputStream();
//			byte b[] = new byte[20];
//			is.read(b);
//			System.out.println(serverIP + "说： " + new String(b).trim());
//			//关机
//			s.close();
			
	    	
			System.out.println("请输入命令：");
			Scanner sc = new Scanner(System.in);
			String msg = sc.nextLine();
			
	    	Runtime.getRuntime().exec("adb shell am broadcast -a NotifyServiceStop");
	    	Runtime.getRuntime().exec("adb forward tcp:8000 tcp:9000");
	    	Runtime.getRuntime().exec("adb shell am broadcast -a NotifyServiceStart");
			
			  Socket socket = new Socket("127.0.0.1", 8000);
		        DataInputStream dis = new DataInputStream(socket.getInputStream());
		        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		        dos.writeUTF(msg);
		        InputStream is = socket.getInputStream();
				byte b[] = new byte[1024];
				is.read(b);
				
				System.out.println("----"+new String(b,"utf-8").trim());
		        socket.close();
		}
	}

}
