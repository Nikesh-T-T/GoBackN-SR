package com.udp.filetransfer;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;

public class Client {
    private DatagramSocket socket = null;
    private FileMetadata metadata = null;
    private String sourceFilePath = "C:/Users/I353212/Documents/GitHub/FileTransferOverUDP/42k.jpg";
    private String destinationPath = "C:/Users/I353212/Documents/GitHub/FileTransferOverUDP/src/";
    private String hostName = "localHost";

    public Client() {

    }

    public void createConnection() {
        try {

            socket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName(hostName);
            byte[] incomingData = new byte[10240000];
            metadata = getFileEvent();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(outputStream);
            os.writeObject(metadata);
            byte[] data = outputStream.toByteArray();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            System.out.println("sending File sent from client  " + sdf.format(System.currentTimeMillis()));
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, 9876);
            socket.send(sendPacket);
            System.out.println("File sent from client  " + sdf.format(System.currentTimeMillis()));
            System.exit(0);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileMetadata getFileEvent() {
        FileMetadata fileMetadata = new FileMetadata();
        String fileName = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1, sourceFilePath.length());
        String path = sourceFilePath.substring(0, sourceFilePath.lastIndexOf("/") + 1);
        fileMetadata.setDestinationDirectory(destinationPath);
        fileMetadata.setFilename(fileName);
        fileMetadata.setSourceDirectory(sourceFilePath);
        File file = new File(sourceFilePath);
        if (file.isFile()) {
            try {
                DataInputStream diStream = new DataInputStream(new FileInputStream(file));
                long len = (int) file.length();
                byte[] fileBytes = new byte[(int) len];
                int read = 0;
                int numRead = 0;
                while (read < fileBytes.length && (numRead = diStream.read(fileBytes, read, fileBytes.length - read)) >= 0) {
                    read = read + numRead;
                }
                fileMetadata.setFileSize(len);
                fileMetadata.setFileData(fileBytes);
                fileMetadata.setStatus("Success");
            } catch (Exception e) {
                e.printStackTrace();
                fileMetadata.setStatus("Error");
            }
        } else {
            System.out.println("path specified is not pointing to a file");
            fileMetadata.setStatus("Error");
        }
        return fileMetadata;
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.createConnection();
    }
}