package example;

import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class test {

	private JFrame frame;
	private JButton btnNewButton;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					test window = new test();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public test() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("\u83B7\u53D6\u624B\u673A\u4F4D\u7F6E\u4FE1\u606F");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		btnNewButton = new JButton("\u83B7\u53D6\u4F4D\u7F6E\u4FE1\u606F");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				textField.setText("");
				try {				
			    	Runtime.getRuntime().exec("adb shell am broadcast -a NotifyServiceStop");
			    	Runtime.getRuntime().exec("adb forward tcp:8000 tcp:9000");
			    	Runtime.getRuntime().exec("adb shell am broadcast -a NotifyServiceStart");
					
					  Socket socket = new Socket("127.0.0.1", 8000);
				        DataInputStream dis = new DataInputStream(socket.getInputStream());
				        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				        
							dos.writeUTF("aaa");
						
				        InputStream is = socket.getInputStream();
						byte b[] = new byte[1024];
						is.read(b);
						
						System.out.println("----"+new String(b,"utf-8").trim());
						textField.setText(new String(b,"utf-8").trim());
				        socket.close();
			        } catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		});
		btnNewButton.setBounds(100, 152, 214, 23);
		frame.getContentPane().add(btnNewButton);
		
		textField = new JTextField();
		textField.setText("\u4FE1\u606F");
		textField.setBounds(74, 45, 260, 64);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
	}
}
