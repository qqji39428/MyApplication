package com.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MyClass {

    private static int serverport = 8763;
    private static ServerSocket serverSocket = null;

    // 儲存每個client
    private static ArrayList<Socket> players = new ArrayList<>();

    // 程式進入
    public static void main(String[] args) {

        try {
            serverSocket = new ServerSocket(serverport);
            System.out.println("Server is start.");

            // Server運作中時
            while (!serverSocket.isClosed()) {
                // 顯示等待連接
                System.out.println("Wait new clinet connect");

                // 等待接受客戶端連接
                waitNewPlayer();
            }

        } catch (IOException e) {
            System.out.println("Server Socket ERROR"+"\n"+e.toString());
        }

    }

    // 等待客戶端連接
    public static void waitNewPlayer() {
        try {
            Socket socket = serverSocket.accept();

            // 呼叫創造新的使用者
            createNewPlayer(socket);
        } catch (IOException e) {

        }

    }

    // 創造新的使用者
    public static void createNewPlayer(final Socket socket) {

        // 以新的執行緒來執行
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 增加新的使用者
                    players.add(socket);

                    // 取得網路串流
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));

                    // 當Socket已連接時連續執行
                    while (socket.isConnected()) {
                        // 取得網路串流的訊息
                        String msg = br.readLine();

                        // 輸出訊息
                        System.out.println(msg);

                        // 廣播訊息給其它的客戶端
                        castMsg(msg);
                    }

                } catch (IOException e) {

                }

                // 移除客戶端
                players.remove(socket);
            }
        });

        // 啟動執行緒
        t.start();
    }

    // 廣播訊息給其它的客戶端
    public static void castMsg(String Msg) {
        // 創造socket陣列
        Socket[] ps = new Socket[players.size()];

        // 將players轉換成陣列存入ps
        players.toArray(ps);

        // 走訪ps中的每一個元素
        for (Socket socket : ps) {
            try {
                // 創造網路輸出串流
                BufferedWriter bw;
                bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                // 寫入訊息到串流
                bw.write(Msg + "\n");

                // 立即發送
                bw.flush();
            } catch (IOException e) {

            }
        }
    }
}
