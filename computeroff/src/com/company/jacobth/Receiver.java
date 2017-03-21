package com.company.jacobth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Receiver {

    public void run(int port) {
        try {
            DatagramSocket serverSocket = new DatagramSocket(port);
            byte[] receiveData = new byte[8];

            System.out.printf("Listening on udp:%s:%d%n",
                    InetAddress.getLocalHost().getHostAddress(), port);
            DatagramPacket receivePacket = new DatagramPacket(receiveData,
                    receiveData.length);

            while(true)
            {
                serverSocket.receive(receivePacket);
                String sentence = new String( receivePacket.getData(), 0,
                        receivePacket.getLength() );
                System.out.println("RECEIVED: " + sentence);
                System.out.println(sentence.length());

                if(sentence.equals("shutdown")) {
                    shutdown();
                }
                else if(sentence.equals("sleep")) {
                    sleep();
                }

                // now send acknowledgement packet back to sender
                InetAddress IPAddress = receivePacket.getAddress();
                String sendString = "polo";
                byte[] sendData = sendString.getBytes("UTF-8");
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                        IPAddress, receivePacket.getPort());
                serverSocket.send(sendPacket);

            }
        } catch (IOException e) {
            System.out.println(e);
        }
        // should close serverSocket in finally block
    }

    private void shutdown() {
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/c", "shutdown -s");
            builder.redirectErrorStream(true);
            Process p = builder.start();

            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;

            while (true) {
                line = r.readLine();
                if (line == null) { break; }
                System.out.println(line);
            }
        }
        catch( IOException e ) {
            System.out.println(e);
            System.exit(0);
        }
    }

    private void sleep() {
        try {
            List<String> commands = new ArrayList<String>();
            commands.add("cmd.exe");
            commands.add("/C");
            //commands.add("powercfg -hibernate off");
            commands.add("rundll32.exe powrprof.dll,SetSuspendState 0,1,0");

            ProcessBuilder builder = new ProcessBuilder(commands);
            builder.redirectErrorStream(true);
            Process p = builder.start();

            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;

            while (true) {
                line = r.readLine();
                if (line == null) { break; }
                System.out.println(line);
            }
        }
        catch( IOException e ) {
            System.out.println(e);
            System.exit(0);
        }
    }
}
