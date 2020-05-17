package com.udp.filetransfer;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;

public class Server {
    private DatagramSocket socket = null;
    private FileMetadata fileMetaData = null;

    public void createAndListenSocket() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            socket = new DatagramSocket(9876);
            byte[] incomingData = new byte[1024 * 1024 * 1024];
            while (true) {

                DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                socket.receive(incomingPacket);
                System.out.println("File received from client  " + sdf.format(System.currentTimeMillis()));
                byte[] data = incomingPacket.getData();
                ByteArrayInputStream in = new ByteArrayInputStream(data);
                ObjectInputStream is = new ObjectInputStream(in);
                fileMetaData = (FileMetadata) is.readObject();
                if (fileMetaData.getStatus().equalsIgnoreCase("Error")) {
                    System.out.println("Some issue happened client side");
                    System.exit(0);
                }
                createAndWriteFile(); // writing the file to hard disk
                InetAddress IPAddress = incomingPacket.getAddress();
                int port = incomingPacket.getPort();
                String reply = "Thank you for the message";
                byte[] replyBytea = reply.getBytes();
                DatagramPacket replyPacket =
                        new DatagramPacket(replyBytea, replyBytea.length, IPAddress, port);
                socket.send(replyPacket);
                Thread.sleep(3000);
                System.exit(0);

            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void createAndWriteFile() {
        String outputFile = fileMetaData.getDestinationDirectory() + fileMetaData.getFilename();
        if (!new File(fileMetaData.getDestinationDirectory()).exists()) {
            new File(fileMetaData.getDestinationDirectory()).mkdirs();
        }
        File dstFile = new File(outputFile);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(dstFile);
            fileOutputStream.write(fileMetaData.getFileData());
            fileOutputStream.flush();
            fileOutputStream.close();
            System.out.println("Output file : " + outputFile + " is successfully saved ");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Server server = new Server();
        server.createAndListenSocket();
    }
}