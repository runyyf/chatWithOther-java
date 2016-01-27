package chat;

import java.io.*;
import java.net.Socket;

/**
 * Created by runyyf on 2016-01-26.
 */
public class SocketServer implements Runnable{
    /*
   * socket连接状态
   * */
    private  boolean socketState = true;
    Socket socket = null;

    public SocketServer(Socket accept){
        System.out.println("Create a new Thread ......."+accept.getInetAddress());
        this.socket = accept;
    }

    public boolean isSocketState() {
        return socketState;
    }

    public void setSocketState(boolean socketState) {
        this.socketState = socketState;
    }

    public void run(){
        boolean ret = true ;

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Thread heartBeatThread = new Thread(new HeartBeat(socket));
            heartBeatThread.start();

            while (true){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!isSocketState()){
                    System.out.println("心跳断开，socket close");
                    break;
                }

                doRead(in);
                //if (!doRead(in)){
                //    break;
                //}

            }
        } catch (IOException e) {
                e.printStackTrace();
        }finally {
            try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public boolean doRead(BufferedReader in) {
        String text;
        try {
            text = in.readLine();
            if (text != null){
                System.out.println("receive the string: "+text);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean doWrite(OutputStream out){
        try {
            out.write("welcome to client!!".getBytes());
            out.flush();
        } catch (IOException e) {
            try {
                System.out.println("send error !! close the out");
                out.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            return false;
        }
        return true;
    }

    public class HeartBeat  implements Runnable {

        Socket socket = null;

        public HeartBeat(Socket socket){
            System.out.println("heartbeat...."+socket.getInetAddress());
            this.socket = socket;
        }

        public void run(){

            try {
                PrintWriter out = new PrintWriter(new BufferedWriter
                        (new OutputStreamWriter(socket.getOutputStream())), true);

                while (true){
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    out.print("hello connect");
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }


    }
}

