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
			// ����TCP�Ŀͻ��˳���
			//�����ֻ�
//			String host = "127.0.0.1";
//			Socket s = new Socket(host, 8000);
//			//����̨¼��
//			System.out.println("�����룺");
//			Scanner sc = new Scanner(System.in);
//			String sss = sc.nextLine();
//			//��绰
//			OutputStream os = s.getOutputStream();
//			os.write(sss.getBytes());
//			//��ȡserverIP��ַ
//			String serverIP = s.getInetAddress().getHostAddress();
//			//�ӵ绰
//			InputStream is = s.getInputStream();
//			byte b[] = new byte[20];
//			is.read(b);
//			System.out.println(serverIP + "˵�� " + new String(b).trim());
//			//�ػ�
//			s.close();
			
	    	
			System.out.println("���������");
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
