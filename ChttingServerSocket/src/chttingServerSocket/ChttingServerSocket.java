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
		
		
	// Panel 1 제작
		showText = new JTextArea(20, 40);
		MessageBox = new JTextField(40);
		
		pan1.setLayout(new BorderLayout());
		JScrollPane scrollPane1 = new JScrollPane(showText);
		scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pan1.add("North",scrollPane1);
		pan1.add("Center", MessageBox);
		
		// Panel 21 Server 접속창
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
		
		// Panel 22 User 접속현황 창
		showUser = new JTextArea(10, 20);
		
		// Panel 23 강퇴 및 메세지 보내기
		SendBt = new JButton("Send");
		ExpulsionBt = new JButton("강퇴");
		
		// Panel 2 제작	
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
		
		pan22.add("North",new Label("접속자"));
		pan22.add("Center", showUser);
		
		pan23.add("North", ExpulsionBt);
		pan23.add("Center",SendBt);
		
		pan2.add("North", pan21);
		pan2.add("Center", pan22);
		pan2.add("South", pan23);
		
	// Component의 사용가능 여부 지정.
		showText.setEditable(false);
		showUser.setEditable(false);
		StartBt.setEnabled(true);
		StopBt.setEnabled(false);
		SendBt.setEnabled(false);
		ExpulsionBt.setEnabled(false);
	
		add("East",pan2);
		add("Center",pan1);
		
		// Event Listener 추가
		StartBt.addActionListener(new ChatActionListenerHandle());
		StopBt.addActionListener(new ChatActionListenerHandle());
		SendBt.addActionListener(new ChatSendActionListenerHandle());
		MessageBox.addActionListener(new ChatSendActionListenerHandle());
	} // initForm End
	
// 모든 접속자에게 전송
	public void broadcast(String msg) {
		for(int i=0; i<vClient.size(); i++) {
			ServerReceiveThread trd = ((ServerReceiveThread)vClient.elementAt(i));
			trd.socketOut.println(msg);
		}
		showText.append(msg + "\n");	// server 화면에 출력										
		showText.setCaretPosition(showText.getDocument().getLength()); // 스크롤바 제일 아래로 내리기
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
				showText.append("Message 전송오류");
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
				//broadcast("Server를 종료합니다.");
				showText.append("Server를 종료합니다. \n");
				listening = false;
			// Component의 사용가능 여부 지정.
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
	
// Accept Class 생성
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
				showText.append("Server 생성 오류");
				System.err.println(e);
				return;
			}
		// Component의 사용가능 여부 지정.
			StartBt.setEnabled(false);
			StopBt.setEnabled(true);
			SendBt.setEnabled(true);
			ExpulsionBt.setEnabled(true);
		// Focus를 SendBt으로
			MessageBox.requestFocus();
			showText.append(port + "에서 접속자를 기다립니다. \n");
			try {
				while(listening) {
					clientSocket = serverSocket.accept();
				// Outer reference를 보냄
					trd = new ServerReceiveThread(chatServer.this);
					trd.start();
				// 접속자정보 저장
					vClient.addElement(trd);
				}
				serverSocket.close();
				showText.append("Server를 종료합니다.");
			}catch(IOException e) {}
		} // Thread run End
	} // ServerAcceptThread End
} // chatServer End

// ================================= 별도의 class로 만들어 처리 ===================================
class ServerReceiveThread extends Thread{
	private Socket clientSocket = null;
	private BufferedReader socketIn;		// 보낸 Message 입력
	public PrintWriter socketOut;			// Message 출력
	private String strUserName = "NoName";  // User Name 저장
	private String strMsg; 					// Buffer에 있는 Message 담는 곳
	
	chatServer chatting;
	public ServerReceiveThread(chatServer c) {
		chatting = c;
		this.clientSocket = c.clientSocket;
	}
	public void removeClient() {
		chatting.broadcast("[" + strUserName + "] 님이 퇴장하셨습니다.");
		chatting.vClient.removeElement(this);
	}
// 접속자 목록 전송 ("/u" 처리 함수)
	public void SendUser() {
		int cnt = chatting.vClient.size() + 1;
		socketOut.println("< ==== 현재 접속자 + " + cnt + "명  명단 ==== >");
		
		for(int i = 0; i<chatting.vClient.size(); i++) {
			ServerReceiveThread trd = ((ServerReceiveThread)chatting.vClient.elementAt(i));
			socketOut.println("\t" + trd.strUserName);
		}
	}
	public void run() {
		try {
			chatting.showText.append("Client : " + clientSocket.toString() + "+에서 접속하였습니다.");
			socketOut = new PrintWriter(clientSocket.getOutputStream(),true);
			socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
			socketOut.println("ChatServer"); 	// Server 확인 Message 보냄
			strMsg = socketIn.readLine();		// 접속한 Client가 맞는지 확인 Message 받음
			
			if(strMsg.equals("Chatting")) {
				socketOut.println("<단축키> /h(도움말), /u(접속자목록), /r 이름 (변경할 이름)");
				socketOut.println("먼저 이름을 정해주세요!");
				strUserName = socketIn.readLine();
				chatting.broadcast("[" + strUserName + "] 님이 접속하셨습니다.");
				
				while((strMsg = socketIn.readLine()) != null) {
				// 단축키 처리
					if(strMsg.equals("/h")) {
						socketOut.println("<단축키> /h(도움말), /u(접속자목록), /r 이름 (변경할 이름)");
					}
					else if(strMsg.equals("/u")) {
						SendUser();
					}
					else if(strMsg.regionMatches(0, "/r", 0, 2)) {
						String new_name = strMsg.substring(2).trim();
						chatting.broadcast("접속자" + strUserName + "님의 이름이" + new_name + "으로 변경되었습니다.");
						strUserName = new_name;
					}else {
						chatting.broadcast("[" + strUserName + "] : " + strMsg + "\n" );
					}
				} // while End
			}else
				socketOut.println("잘못된 클라이언트 입니다.");
			socketOut.close();
			socketIn.close();
			clientSocket.close();
			removeClient();
		}catch(IOException e) {
			removeClient();
			chatting.showText.append(" " + strUserName + " 의 접속이 끊겼습니다.");
		}
	} // ServerReceiveThread run Method End
} // ServerReceiveThread Class End