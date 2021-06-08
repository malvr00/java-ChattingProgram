package chttingClientSocket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChttingClientSocket {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		chatClient chatClient1 = new chatClient("Java Chatting");
		chatClient1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		chatClient1.initForm();
		chatClient1.pack();
		chatClient1.setVisible(true);
	}

}
class chatClient extends JFrame{
	public JTextArea showText, showUser;	// 재사용 위하여 PUBLIC으로
	private JTextField ServerIp, PortNo, UserName, MessageBox;
	private JButton StartBt, StopBt, SendBt;
	private JPanel pan1, pan2, pan21,pan22,pan23;
// Client Socket
	private Socket clientSocket = null;
// Socket 입출력
	private PrintStream stdout = null;
	public BufferedReader stdIn = null;		// 입력 RecieveThread에서 재사용 하기 위해 PUBLIC 으로

	public chatClient() {}
	public chatClient(final String str) {
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
		showText.setLineWrap(true);	// 자동줄바꿈
		JScrollPane scrollPane1 = new JScrollPane(showText);
		scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pan1.add("North",scrollPane1);
		pan1.add("Center", MessageBox);
		
		// Panel 21 Server 접속창
		ServerIp = new JTextField("localhost", 10);
		PortNo = new JTextField("1234",10);
		UserName = new JTextField("손님", 10);
		StartBt = new JButton("Server Start");
		StopBt = new JButton("Server Stop");
		
		// Panel 22 User 접속현황 창
		showUser = new JTextArea(10, 20);
		
		// Panel 23 강퇴 및 메세지 보내기
		SendBt = new JButton("Send");
		
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
	
		add("East",pan2);
		add("Center",pan1);
		
		// Event Listener 추가
		StartBt.addActionListener(new ChatActionListenerHandle());
		StopBt.addActionListener(new ChatActionListenerHandle());
		SendBt.addActionListener(new ChatSendActionListenerHandle());
		MessageBox.addActionListener(new ChatSendActionListenerHandle());
	} // initForm End
	
// StartBt, StopBt Event Handle
	class ChatActionListenerHandle implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			try {
				if(event.getActionCommand().equals("Server Start")) {
				// Chatting Server 접속
					int port = Integer.parseInt(PortNo.getText());
					clientSocket = new Socket("localhost",port);
					if(clientSocket.isConnected()) {
						showText.append("localhost 포트번호 "+port+"에 접속하였습니다.\n");
						// Io 연결
						stdout = new PrintStream(clientSocket.getOutputStream(),true);
						stdIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					
						stdout.println("Chatting"); 			// Server 접속자 체크 Message 전송
						stdout.println(UserName.getText());		// Server에 닉네임 전송
						
					// ReceiveThread Start
						ReceiveThread trd = new ReceiveThread(chatClient.this);
						trd.start();
						
					}else {
						clientSocket.close();
					}
				// Component의 사용가능 여부 지정.
					StartBt.setEnabled(false);
					StopBt.setEnabled(true);
					SendBt.setEnabled(true);
				}else {
				// Chatting Server 종료
					showText.append("서버에 나갔습니다.\n");
					clientSocket.close();
					
				// Component의 사용가능 여부 지정.
					StartBt.setEnabled(true);
					StopBt.setEnabled(false);
					SendBt.setEnabled(false);
				 }
			}catch(UnknownHostException e) {
				showText.append("Server를 찾을수 없습니다.\n");
			}catch(IOException e) {
				showText.append("Server와 연결실패. \n");
			}
		}
	} // ChatActionListenerHandle End
	
// MessageBox, SendBt Event Handle
	class ChatSendActionListenerHandle implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			String strMsg;
			try {
				strMsg = MessageBox.getText();
			// 사용자 이름변경
				if(strMsg.regionMatches(0, "/r", 0, 2)){
					UserName.setText(strMsg.substring(2).trim());
				}
				stdout.println(strMsg);
				MessageBox.setText("");
			}catch(Exception e) {
				showText.append("Server와 연결 실패.\n");
			}
		}
	} // ChatSendActionListenerHandle End
	
}

// ============================ Receive Thread class =================
class ReceiveThread extends Thread {
	private chatClient c;
	private BufferedReader stdIn = null;
	private String strMsg;
	public ReceiveThread() {}
	public ReceiveThread(chatClient c) {
		this.c = c;
		this.stdIn = c.stdIn;
	}
	public void run() {
		try {
			while((strMsg = stdIn.readLine()) != null) {
				c.showText.append(strMsg + "\n");
			}
		}catch(Exception e) {
			c.showText.append("Server와 연결이 끊어졌습니다.\n");
		}
	}
}