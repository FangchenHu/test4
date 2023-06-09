import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

public class ChatClient {
    private JFrame frame;           // 窗口对象
    private JTextArea textArea;     // 显示消息的文本区域
    private JTextField textField;  // 输入消息的文本框
    private JButton sendButton;     // 发送消息的按钮
    private JButton sendFileButton; // 发送文件的按钮
    private JButton sendImageButton; // 发送图片的按钮
    private Socket socket;          // Socket对象，用于与服务器通信
    private BufferedWriter writer; // 写入器，用于发送消息
    private BufferedReader reader;  // 读取器，用于接收消息
    private String username;        // 用户名

    public static void main(String[] args) {
        // 使用EventQueue.invokeLater()方法，将初始化界面的任务放到事件分发线程中执行，以避免线程安全问题
        EventQueue.invokeLater(() -> {
            try {
                ChatClient client = new ChatClient();
                client.showLoginDialog(); // 显示登录对话框
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ChatClient() {
        initComponents();  // 初始化界面组件
        initConnection();   // 初始化与服务器的连接
    }

    private void initComponents() {
        frame = new JFrame("Chat Client");  // 创建窗口对象
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // 设置窗口关闭时的操作
        frame.setSize(700, 500);  // 设置窗口大小
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));  // 使用BoxLayout布局管理器

        // 添加textArea
        textArea = new JTextArea();  // 创建文本区域对象
        textArea.setEditable(true);  // 设置文本区域可编辑
        JScrollPane scrollPane = new JScrollPane(textArea);  // 创建滚动窗格对象
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);  // 将滚动窗格左对齐
        Dimension d = new Dimension(Short.MAX_VALUE, 7 * frame.getHeight() / 10);  // 设置滚动窗格的最大尺寸
        scrollPane.setMaximumSize(d);
        frame.add(scrollPane);  // 将滚动窗格添加到窗口中

        // 添加buttonPanel
        JPanel buttonPanel = new JPanel();  // 创建按钮面板对象
        sendFileButton = new JButton("发送文件");  // 创建“发送文件”按钮对象
        sendFileButton.addActionListener(e -> sendFile());  // 为“发送文件”按钮添加事件监听器
        sendImageButton = new JButton("发送图片");  // 创建“发送图片”按钮对象
        sendImageButton.addActionListener(e -> sendImage());  // 为“发送图片”按钮添加事件监听器
        buttonPanel.add(sendFileButton);  // 将“发送文件”按钮添加到按钮面板中
        buttonPanel.add(sendImageButton);  // 将“发送图片”按钮添加到按钮面板中
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);  // 将按钮面板左对齐
        d = new Dimension(Short.MAX_VALUE, frame.getHeight() / 10);  // 设置按钮面板的最大尺寸
        buttonPanel.setMaximumSize(d);
        frame.add(buttonPanel);  // 将按钮面板添加到窗口中

        // 添加inputPanel
        JPanel inputPanel = new JPanel(new BorderLayout());  // 创建输入面板对象
        textField = new JTextField();  // 创建文本框对象
        textField.addActionListener(e -> sendMessage());  // 为文本框添加事件监听器
        inputPanel.add(textField, BorderLayout.CENTER);  // 将文本框添加到输入面板中

        sendButton = new JButton("发送");  // 创建“发送”按钮对象
        sendButton.addActionListener(e -> sendMessage());  // 为“发送”按钮添加事件监听器
        inputPanel.add(sendButton, BorderLayout.EAST);  // 将“发送”按钮添加到输入面板中
        inputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);  // 将输入面板左对齐
        d = new Dimension(Short.MAX_VALUE, 2*frame.getHeight() / 10);  // 设置输入面板的最大尺寸
        inputPanel.setMaximumSize(d);
        frame.add(inputPanel);  // 将输入面板添加到窗口中

        // 将frame显示在屏幕中央
        frame.setLocationRelativeTo(null);  // 将窗口显示在屏幕正中央
        frame.setVisible(true);  // 显示窗口
    }

    private void initConnection() {
        try {
            socket = new Socket("localhost", 8080);  // 创建Socket对象，连接到服务器
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));  // 创建写入器对象，用于发送消息
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));  // 创建读取器对象，用于接收消息

            new Thread(() -> {  // 创建新线程，用于接收服务器发送的消息
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        textArea.append(line + "\n");  // 将接收到的消息显示在文本区域中
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showLoginDialog() {
        username = JOptionPane.showInputDialog(frame, "请输入用户名:");  // 显示输入对话框，获取用户名
        frame.setTitle("Chat Client - " + username);  // 设置窗口标题
    }

    private void sendMessage() {
        try {
            String message = textField.getText().trim();  // 获取用户输入的消息
            if (!message.isEmpty()) {  // 如果消息不为空
                writer.write(username + ": " + message + "\n");  // 发送消息
                writer.flush();  // 刷新缓冲区
                textField.setText("");  // 清空文本框
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFile() {
        // 实现文件发送功能，注意文件大小限制和安全性问题
    }

    private void sendImage() {
        // 实现图片发送功能，注意图片大小限制和安全性问题
    }
}