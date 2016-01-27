package chat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by runyyf on 2016-01-26.
 */
public class ChatServer {

    public static void main(String[] args){
        new ChatServer().go();
    }

    public void go(){
        ServerSocket serverSocket = null;
        System.out.println("SocketServer begin work!!!!");
        int num = 0 ;
        try {
            serverSocket = new ServerSocket(30041);
            while (true){
                num++;
                Socket accept = serverSocket.accept();
                new Thread(new SocketServer(accept),"client"+num).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                serverSocket.close();
                System.out.println("SocketServer has end!!!!!!!!!! ");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
