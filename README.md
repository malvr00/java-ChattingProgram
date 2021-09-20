# java ChattingProgram 😀

Java Socket

## Chatting Program Server [[코드참조]](https://github.com/malvr00/java-ChattingProgram/blob/main/ChttingServerSocket/src/chttingServerSocket/ChttingServerSocket.java)
* Chatting Server 유저관리 방법으로 Vector 사용.
* Frame은 Java-Sudy에서 만든 코드 재사용 [코드참조](https://github.com/malvr00/Java-Study/blob/main/ChattingFrame/src/chattingFrame/ChattingFrame.java)
* 다중접속을 하기위한 [코드부분](https://github.com/malvr00/java-ChattingProgram/blob/main/ChttingServerSocket/src/chttingServerSocket/ChttingServerSocket.java#L172) Thread를 상속받아 별도의 Class로 만들어 관리
* Message Send [부분](https://github.com/malvr00/java-ChattingProgram/blob/main/ChttingServerSocket/src/chttingServerSocket/ChttingServerSocket.java#L121)
* Message Receive [부분](https://github.com/malvr00/java-ChattingProgram/blob/main/ChttingServerSocket/src/chttingServerSocket/ChttingServerSocket.java#L238)
* Chatting 사용자 단축키 [옵션](https://github.com/malvr00/java-ChattingProgram/blob/main/ChttingServerSocket/src/chttingServerSocket/ChttingServerSocket.java#L248)

  <img src="https://user-images.githubusercontent.com/77275513/121181769-536cec00-c89d-11eb-9b89-230fc1aace0b.png" width="450px" height="300px" title="100px" alt="RubberDuck"></img><br/>


## Chatting Program Client [[코드참조]](https://github.com/malvr00/java-ChattingProgram/blob/main/ChttingClientSocket/src/chttingClientSocket/ChttingClientSocket.java)
* Message Receive Thread로 생성 [참조](https://github.com/malvr00/java-ChattingProgram/blob/main/ChttingClientSocket/src/chttingClientSocket/ChttingClientSocket.java#L176)
* 사용자 이름변경 [참조](https://github.com/malvr00/java-ChattingProgram/blob/main/ChttingClientSocket/src/chttingClientSocket/ChttingClientSocket.java#L161)

  <img src="https://user-images.githubusercontent.com/77275513/121182448-0e958500-c89e-11eb-9717-6a051236fcf1.png" width="450px" height="300px" title="100px" alt="RubberDuck"></img><br/>


## 다중 접속 (multiple access)

   <img src="https://user-images.githubusercontent.com/77275513/121182598-397fd900-c89e-11eb-82de-cbf9f2e78c27.PNG" width="450px" height="300px" title="100px" alt="RubberDuck"></img><br/>
