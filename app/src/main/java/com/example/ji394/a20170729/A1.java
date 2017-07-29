package com.example.ji394.a20170729;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class A1 extends AppCompatActivity {
    public static Handler mHandler = new Handler();
    TextView textview;
    EditText Ed1;
    EditText Ed2;
    String tmp;                // 暫存文字訊息
    Socket clientSocket;    // 客戶端socket

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a1);

        // 從資源檔裡取得位址後強制轉型成文字方塊
        textview = (TextView) findViewById(R.id.textView);
        Ed1 = (EditText) findViewById(R.id.editText1);
        Ed2 = (EditText) findViewById(R.id.editText2);

        // 以新的執行緒來讀取資料
        Thread t = new Thread(readData);

        // 啟動執行緒
        t.start();

        // 從資源檔裡取得位址後強制轉型成按鈕
        Button button = (Button) findViewById(R.id.button);

        // 設定按鈕的事件
        button.setOnClickListener(new Button.OnClickListener() {
            // 當按下按鈕的時候觸發以下的方法
            public void onClick(View v) {
                // 如果已連接則
                if (clientSocket.isConnected()) {
                    BufferedWriter bw;
                    try {
                        // 取得網路輸出串流
                        bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                        // 寫入訊息
                        bw.write(Ed1.getText() + ":" + Ed2.getText() + "\n");

                        // 立即發送
                        bw.flush();
                    } catch (IOException e) {

                    }
                    // 將文字方塊清空
                    Ed2.setText("");
                }
            }
        });

    }

    // 顯示更新訊息
    private Runnable updateText = new Runnable() {
        public void run() {
            // 加入新訊息並換行
            textview.append(tmp + "\n");
        }
    };

    // 取得網路資料
    private Runnable readData = new Runnable() {
        public void run() {
            // server端的IP
            InetAddress serverIp;

            try {
                // 以內定(本機電腦端)IP為Server端
                serverIp = InetAddress.getByName("10.0.2.2");
                int serverPort = 5050;
                clientSocket = new Socket(serverIp, serverPort);

                // 取得網路輸入串流
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        clientSocket.getInputStream()));

                // 當連線後
                while (clientSocket.isConnected()) {
                    // 取得網路訊息
                    tmp = br.readLine();

                    // 如果不是空訊息則
                    if (tmp != null)
                        // 顯示新的訊息
                        mHandler.post(updateText);
                }

            } catch (IOException e) {

            }
        }
    };


}

