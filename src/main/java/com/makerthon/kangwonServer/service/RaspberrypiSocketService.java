package com.makerthon.kangwonServer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Socket;

@Service
public class RaspberrypiSocketService {

    @Value("${socket.hostname}")
    private String HostName;
    @Value("${socket.port}")
    private int Port;

    public void engineStart() throws IOException {
        Socket socket = new Socket(HostName, Port);
        System.out.println("socket 연결");
        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();
        String tetst = "true";
        outputStream.write(tetst.getBytes());
        PrintWriter printWriter = new PrintWriter(outputStream, true);
        System.out.println("메세지 전송됨.");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        System.out.println(reader.readLine());
        System.out.println("메세지 받음.");
        socket.close();
        System.out.println("socket 접속 종료.");
    }
}
