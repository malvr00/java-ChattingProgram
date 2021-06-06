package chttingServerSocket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Vector;

public class ChttingServerSocket {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		chatServer chatServer1 = new chatServer("Java Chatting");
		chatServer1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		chatServer1.initForm();
		chatServer1.pack();
		chatServer1.setVisible(true);
	}

}

class chatServer extends JFrame{
	public JTextArea showText, showUser;
	private JTextField ServerIp, PortNo, UserName, MessageBox;
	private JButton StartBt, StopBt, SendBt, ExpulsionBt;
	private JPanel pan1, pan2, pan21,pan22,pan23;
	
	private ServerSocket serverSocket = null;
	public Socket clientSocket = null;
	Vector <ServerReceiveThread> vClient;
	boolean listening;
	
	public chatServer() {}
	public chatServer(final String str) {
		super(str);
	}
	public void  initForm() {
		pan1 = new JPanel();
		pan2 = new JPanel();
		pan21 = new JPanel();
		pan22 = new JPanel();
		pan23 = new JPanel();
		
		
	// Panel 1 ����
		showText = new JTextArea(20, 40);
		MessageBox = new JTextField(40);
		
		pan1.setLayout(new BorderLayout());
		JScrollPane scrollPane1 = new JScrollPane(showText);
		scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pan1.add("North",scrollPane1);
		pan1.add("Center", MessageBox);
		
		// Panel 21 Server ����â
		InetAddress inet = null;
		try {
			inet = InetAddress.getLocalHost();
		}catch(UnknownHostException e) {
			e.printStackTrace();
		}
		ServerIp = new JTextField(inet.getHostAddress(), 10);
		PortNo = new JTextField("1234",10);
		UserName = new JTextField("Server", 10);
		StartBt = new JButton("Server Start");
		StopBt = new JButton("Server Stop");
		
		// Panel 22 User ������Ȳ â
		showUser = new JTextArea(10, 20);
		
		// Panel 23 ���� �� �޼��� ������
		SendBt = new JButton("Send");
		ExpulsionBt = new JButton("����");
		
		// Panel 2 ����	
		pan2.setLayout(new BorderLayout());
		pan21.setLayout(new GridLayout(4,2));
		pan22.setLayout(new BorderLayout());
		pan23.setLayout(new BorderLayout());
		
		pan21.add(new Label(" Server Ip"));
		pan21.add(ServerIp);
		pan21.add(new Label(" Port No"));
		pan21.add(PortNo);
		pan21.add(new Label(" Name"));
		pan21.add(UserName);
		pan21.add(StartBt);
		pan21.add(StopBt);
		
		pan22.add("North",new Label("������"));
		pan22.add("Center", showUser);
		
		pan23.add("North", ExpulsionBt);
		pan23.add("Center",SendBt);
		
		pan2.add("North", pan21);
		pan2.add("Center", pan22);
		pan2.add("South", pan23);
		
	// Component�� ��밡�� ���� ����.
		showText.setEditable(false);
		showUser.setEditable(false);
		StartBt.setEnabled(true);
		StopBt.setEnabled(false);
		SendBt.setEnabled(false);
		ExpulsionBt.setEnabled(false);
	
		add("East",pan2);
		add("Center",pan1);
		
		// Event Listener �߰�
		StartBt.addActionListener(new ChatActionListenerHandle());
		StopBt.addActionListener(new ChatActionListenerHandle());
		SendBt.addActionListener(new ChatSendActionListenerHandle());
		MessageBox.addActionListener(new ChatSendActionListenerHandle());
	} // initForm End
	
// ��� �����ڿ��� ����
	public void broadcast(String msg) {
		for(int i=0; i<vClient.size(); i++) {
			ServerReceiveThread trd = ((ServerReceiveThread)vClient.elementAt(i));
			trd.socketOut.println(msg);
		}
		showText.append(msg + "\n");	// server ȭ�鿡 ���										
		showText.setCaretPosition(showText.getDocument().getLength()); // ��ũ�ѹ� ���� �Ʒ��� ������
	} // broadcast End
	
// MessageBox, SendBt Event Handle
	class ChatSendActionListenerHandle implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			String strMsg;
			try {
				strMsg = MessageBox.getText();
				if(!strMsg.isEmpty()) {
					broadcast("[Server] : " + strMsg);
					MessageBox.setText("");
					MessageBox.requestFocus();					
				}
			}catch(Exception e) {
				showText.append("Message ���ۿ���");
			}
		}
	} // ChatSendActionListenerHandle End
	
// StartBt, StopBt Event Handle
	class ChatActionListenerHandle implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			if(event.getActionCommand().equals("Server Start")) {
				listening = true;
				ServerAcceptThread accept = new ServerAcceptThread();
				accept.start();
			}else {
				//broadcast("Server�� �����մϴ�.");
				showText.append("Server�� �����մϴ�. \n");
				listening = false;
			// Component�� ��밡�� ���� ����.
				StartBt.setEnabled(true);
				StopBt.setEnabled(false);
				SendBt.setEnabled(false);
				ExpulsionBt.setEnabled(false);
				try {
					serverSocket.close();
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	} // ChatActionListenerHandle End
	
// Accept Class ����
	class ServerAcceptThread extends Thread{
		private ServerReceiveThread trd;
		public ServerAcceptThread() {}
		public void run() {
		// Server Start
			int port = Integer.parseInt(PortNo.getText());
			showText.append("Server Start \n");
			try {
				serverSocket = new ServerSocket(port);
			}catch(IOException e) {
				showText.append("Server ���� ����");
				System.err.println(e);
				return;
			}
		// Component�� ��밡�� ���� ����.
			StartBt.setEnabled(false);
			StopBt.setEnabled(true);
			SendBt.setEnabled(true);
			ExpulsionBt.setEnabled(true);
		// Focus�� SendBt����
			MessageBox.requestFocus();
			showText.append(port + "���� �����ڸ� ��ٸ��ϴ�. \n");
			try {
				while(listening) {
					clientSocket = serverSocket.accept();
				// Outer reference�� ����
					trd = new ServerReceiveThread(chatServer.this);
					trd.start();
				// ���������� ����
					vClient.addElement(trd);
				}
				serverSocket.close();
				showText.append("Server�� �����մϴ�.");
			}catch(IOException e) {}
		} // Thread run End
	} // ServerAcceptThread End
} // chatServer End

// ================================= ������ class�� ����� ó�� ===================================
class ServerReceiveThread extends Thread{
	private Socket clientSocket = null;
	private BufferedReader socketIn;		// ���� Message �Է�
	public PrintWriter socketOut;			// Message ���
	private String strUserName = "NoName";  // User Name ����
	private String strMsg; 					// Buffer�� �ִ� Message ��� ��
	
	chatServer chatting;
	public ServerReceiveThread(chatServer c) {
		chatting = c;
		this.clientSocket = c.clientSocket;
	}
	public void removeClient() {
		chatting.broadcast("[" + strUserName + "] ���� �����ϼ̽��ϴ�.");
		chatting.vClient.removeElement(this);
	}
// ������ ��� ���� ("/u" ó�� �Լ�)
	public void SendUser() {
		int cnt = chatting.vClient.size() + 1;
		socketOut.println("< ==== ���� ������ + " + cnt + "��  ��� ==== >");
		
		for(int i = 0; i<chatting.vClient.size(); i++) {
			ServerReceiveThread trd = ((ServerReceiveThread)chatting.vClient.elementAt(i));
			socketOut.println("\t" + trd.strUserName);
		}
	}
	public void run() {
		try {
			chatting.showText.append("Client : " + clientSocket.toString() + "+���� �����Ͽ����ϴ�.");
			socketOut = new PrintWriter(clientSocket.getOutputStream(),true);
			socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
			socketOut.println("ChatServer"); 	// Server Ȯ�� Message ����
			strMsg = socketIn.readLine();		// ������ Client�� �´��� Ȯ�� Message ����
			
			if(strMsg.equals("Chatting")) {
				socketOut.println("<����Ű> /h(����), /u(�����ڸ��), /r �̸� (������ �̸�)");
				socketOut.println("���� �̸��� �����ּ���!");
				strUserName = socketIn.readLine();
				chatting.broadcast("[" + strUserName + "] ���� �����ϼ̽��ϴ�.");
				
				while((strMsg = socketIn.readLine()) != null) {
				// ����Ű ó��
					if(strMsg.equals("/h")) {
						socketOut.println("<����Ű> /h(����), /u(�����ڸ��), /r �̸� (������ �̸�)");
					}
					else if(strMsg.equals("/u")) {
						SendUser();
					}
					else if(strMsg.regionMatches(0, "/r", 0, 2)) {
						String new_name = strMsg.substring(2).trim();
						chatting.broadcast("������" + strUserName + "���� �̸���" + new_name + "���� ����Ǿ����ϴ�.");
						strUserName = new_name;
					}else {
						chatting.broadcast("[" + strUserName + "] : " + strMsg + "\n" );
					}
				} // while End
			}else
				socketOut.println("�߸��� Ŭ���̾�Ʈ �Դϴ�.");
			socketOut.close();
			socketIn.close();
			clientSocket.close();
			removeClient();
		}catch(IOException e) {
			removeClient();
			chatting.showText.append(" " + strUserName + " �� ������ ������ϴ�.");
		}
	} // ServerReceiveThread run Method End
} // ServerReceiveThread Class End