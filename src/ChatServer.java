import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private ServerSocket serverSocket;  // ServerSocket对象，用于监听客户端连接请求
    private List<BufferedWriter> writers = new ArrayList<>();  // 存储所有客户端的写入器对象

    public static void main(String[] args) {
        new ChatServer().start();  // 创建ChatServer对象并启动服务器
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(8080);  // 创建ServerSocket对象，监听8080端口
            System.out.println("Chat Server started.");  // 在控制台输出服务器已启动

            while (true) {
                Socket clientSocket = serverSocket.accept();  // 接受客户端连接请求，创建Socket对象
                System.out.println("Client connected: " + clientSocket.getInetAddress());  // 在控制台输出客户端连接信息
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));  // 创建写入器对象，用于向客户端发送消息
                writers.add(writer);  // 将写入器对象添加到writers列表中

                new Thread(() -> {  // 创建新线程，用于接收客户端发送的消息
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));  // 创建读取器对象，用于从客户端接收消息
                        String line;
                        while ((line = reader.readLine()) != null) {  // 循环读取客户端发送的消息
                            System.out.println("Received: " + line);  // 在控制台输出接收到的消息
                            broadcast(line);  // 将接收到的消息广播给所有客户端
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcast(String message) {
        for (BufferedWriter writer : writers) {  // 遍历writers列表中的所有写入器对象
            try {
                writer.write(message + "\n");  // 向客户端发送消息
                writer.flush();  // 刷新缓冲区，确保消息被及时发送
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}