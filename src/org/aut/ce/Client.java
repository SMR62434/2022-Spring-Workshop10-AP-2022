package org.aut.ce;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.username = username;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String msg = scanner.nextLine();
                if (msg.equals("#exit")) {
                    bufferedWriter.write(msg);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
                }
                bufferedWriter.write(username + ": " + msg);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void getMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msg;
                while (socket.isConnected()) {
                    try {
                        msg = bufferedReader.readLine();
                        System.out.println(msg);
                        if (msg.equals("#exit")) {
                            closeEverything(socket, bufferedReader, bufferedWriter);
                            break;
                        }
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {

        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("enter your username");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 2000);
        Client client = new Client(socket, username);
        client.getMessage();
        client.sendMessage();
    }
}
