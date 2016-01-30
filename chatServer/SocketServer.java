package chat;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by runyyf on 2016-01-26.
 */
public class SocketServer implements Runnable{
    /*
   * socket连接状态
   * */
    private  boolean socketState = true;
    Socket socket = null;
    static ArrayList<DistributionData> dataArrayList = new ArrayList<DistributionData>();

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
        String  netContent= null;

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(new HeartBeat(socket)).start();

            int firstTime = 0 ;
            String targetIp = null;
            String content  = null;
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

                /*
                 *现在协议头不进行加密,为ip地址
                 * */
                if (firstTime == 0){
                    targetIp = doRead(in);
                    firstTime++;
                }

                netContent = doRead(in);
                if (netContent!=null){

                    DistributionData data = new DistributionData();
                    data.sendStatus = 1 ;
                    data.sourceIp = socket.getInetAddress().toString();
                    data.targetIp = targetIp;
                    data.content = netContent;
                    dataArrayList.add(data);

                }
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

    public String doRead(BufferedReader in) {
        String text = null;
        try {
            text = in.readLine();
            if (text != null){
                System.out.println("receive the string: "+text);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text;
    }

    public void doWrite(OutputStreamWriter out,String content){
        try {
            out.write(content+"\n");
            out.flush();
            this.setSocketState(true);

        } catch (IOException e) {
            this.setSocketState(false);
        }

    }

    public String checkMessage(String ip){

        if (dataArrayList.size() == 0){
            return null;
        }

        String message = null;

        for (int i = 0 ;i<dataArrayList.size();i++){
            if (dataArrayList.get(i).targetIp.equals(ip.substring(1))){
                message = dataArrayList.get(i).content;
                dataArrayList.remove(i);

                break;
            }
        }

        return message;
    }
    public class HeartBeat  implements Runnable {

        Socket socket = null;

        public HeartBeat(Socket socket){
            //System.out.println("heartbeat...."+socket.getInetAddress());
            this.socket = socket;
        }

        public void run(){

            try {
                OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
                String content;
                boolean ret = false;
                while (true){
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    content = checkMessage(socket.getInetAddress().toString());
                    if (content!=null){
                         doWrite(out,content);
                    }
                    else {
                         doWrite(out, "heart !!!!");
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}

