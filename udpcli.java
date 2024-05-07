import java.io.*;
import java.net.*;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class udpcli {
    public static void main(String[] args) throws IOException {

        if (args.length != 3) {
            System.out.println();
            System.out
                    .println("Syntax error. Correct format is: udpcli ip_server_address server_port_number operation");
            System.out.println("Example: udpcli 127.0.0.1 7777 1+1");
            System.out.println("Operator must be: +, -, x or /");
            System.out.println();
            return;
        }

        InetAddress ip = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);
        String operation = args[2];
        byte[] bytesend = operation.getBytes();
        byte[] bytereceive = new byte[9999];

        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(10000);
        DatagramPacket packetreceive = new DatagramPacket(bytereceive, bytereceive.length);
        DatagramPacket packetsend = new DatagramPacket(bytesend, bytesend.length, ip, port);
        socket.send(packetsend);

        try {
            socket.receive(packetreceive);

        } catch (SocketTimeoutException exc) {
            System.out.println();
            System.out.println("Waiting time is over. Try again.");
            System.out.println();
        }

        String receivedstring = new String(packetreceive.getData());

        System.out.println();
        System.out.println("Result: " + receivedstring);
        System.out.println();
        socket.close();
    }

}
