package SocketTest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by runyyf on 2016-01-22.
 */
public class SimpleChatClient {
    JTextArea  incoming;
    JTextField outgoing ;
    JTextField friendName;
    BufferedReader reader;
    PrintWriter writer;
    Socket sock;

    public void go(){
        JFrame frame = new JFrame("simple chat client");
        JPanel mainPanel = new JPanel();

        incoming = new JTextArea(15,30);
        incoming.setLineWrap(true);
        incoming.setWrapStyleWord(true);
        incoming.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(incoming);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        outgoing = new JTextField();
        outgoing.setColumns(20);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new SendButtonListener());

        friendName = new JTextField();
        friendName.setColumns(8);
        friendName.setText("192.9.100.177");

        mainPanel.add(friendName);
        mainPanel.add(scrollPane);
        mainPanel.add(outgoing);
        mainPanel.add(sendButton);

        setUpNetworking();

        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();

        //Thread serverThread = new Thread(new ServiceTest());
        //serverThread.start();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(BorderLayout.CENTER,mainPanel);
        frame.setSize(400,500);
        frame.setVisible(true);


    }

    private void setUpNetworking(){

        try {
            sock = new Socket("192.9.100.177",30041);
            InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(streamReader);
            writer = new PrintWriter(sock.getOutputStream());
            System.out.println("netWorking established !!! ");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public class SendButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            writer.println(outgoing.getText());
            writer.flush();

            incoming.append("localHost : "+"\n");
            incoming.append(outgoing.getText()+"\n");

            outgoing.setText("");
            outgoing.requestFocus();

        }
    }

    public class IncomingReader implements Runnable{
        public void run(){
            String message;

            try {
                while ((message = reader.readLine()) != null){
                    System.out.println("read  "+message);
                    incoming.append("192.9.100.177 :"+"\n");
                    incoming.append(message+" \n ");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public class ServiceTest implements Runnable{
        public void run(){
            try {
                String message;
                ServerSocket serverSocket = new ServerSocket(30041);
                Socket socket = serverSocket.accept();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while ((message = bufferedReader.readLine()) != null){
                    System.out.println("bufferedReader  "+message);
                    incoming.append(message+" \n ");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        SimpleChatClient ex= new SimpleChatClient();
        ex.go();
    }
}
