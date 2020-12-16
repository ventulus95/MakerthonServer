package com.makerthon.kangwonServer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.net.Socket;

public class SocketTest {

    @Value("${socket.hostname}")
    private String kkk;

    @Test
    public void 소캣통신_예제() throws IOException {
        Socket socket = new Socket(kkk, 81);
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
