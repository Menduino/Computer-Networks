import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class tcpmpcli {
    public static void main(String[] args) throws IOException {

        if (args.length != 2 && args.length != 3) {
            System.out.println("Syntax error");
            System.out.println("Correct syntax: tcpmpcli ip_server_address server_port_number -u(optional)");
            System.out.println("Example: tcpmpcli 127.0.0.1 6666 -u");
            System.exit(0);
        }
        boolean flag = false;

        if (args.length == 3) {
            if (args[2].equals("-u"))
                flag = true;
        }

        if (flag) {

            int port = Integer.parseInt(args[1]);
            InetSocketAddress ip = new InetSocketAddress(InetAddress.getByName(args[0]), port);
            byte[] bytereceive = new byte[128];
            String addr = args[0];

            DatagramSocket socket = new DatagramSocket();

            DatagramPacket packetreceive = new DatagramPacket(bytereceive, bytereceive.length);
            DatagramPacket packetsend = null;

            BufferedReader buff = new BufferedReader(new InputStreamReader(System.in));

            try {
                while (true) {
                    System.out.println();
                    System.out.println("Enter arithmetic operation in infix notation.");
                    System.out.println("Examples: 10 + 5 , 10 - 5 , 10 * 5 , 10 / 5 , 10 % 5 , 10 ! ");
                    System.out.print("Operation: ");
                    String inline = buff.readLine();

                    if (inline.equals("quit")) {
                        break;
                    }

                    String[] split = inline.split("\\s+");
                    int type = 0;
                    int length = 0;
                    int split0 = 0;
                    int split1 = 0;
                    int split2 = 0;

                    if (split.length == 3) {

                        switch (split[1]) {
                            case "+":
                                type = 1;
                                break;
                            case "-":
                                type = 2;
                                break;
                            case "*":
                                type = 3;
                                break;
                            case "/":
                                type = 4;
                                break;
                            case "%":
                                type = 5;
                                break;
                            default:
                                System.out.println("Wrong format.");
                                System.out.println("Examples: 10 + 5 , 10 - 5 , 10 * 5 , 10 / 5 , 10 % 5 , 10 ! ");
                                System.out.println("Valid operators are: +, -, *, /, % and !");
                                continue;
                        }

                        split1 = Integer.parseInt(split[0]);
                        split2 = Integer.parseInt(split[2]);
                        length = 2;

                    } else if (split.length == 2 && split[1].equals("!")) {

                        type = 6;
                        split0 = Integer.parseInt(split[0]);
                        length = 1;

                    } else {
                        System.out.println("Invalid input format. Please check that the format is correct.");
                        System.out.println("Examples: 10 + 5 , 10 - 5 , 10 * 5 , 10 / 5 , 10 % 5 , 10 ! ");
                        continue;
                    }

                    if (length == 2) {
                        byte[] op2 = new byte[] { (byte) type, (byte) length, (byte) split1, (byte) split2 };
                        packetsend = new DatagramPacket(op2, op2.length, ip);
                    }

                    if (length == 1) {
                        byte[] op1 = new byte[] { (byte) type, (byte) length, (byte) split0 };
                        packetsend = new DatagramPacket(op1, op1.length, ip);
                    }
                    socket.send(packetsend);

                    socket.receive(packetreceive);

                    byte[] bytes = packetreceive.getData();
                    int lengthin = (int) bytes[1];
                    int errorin = (int) bytes[2];
                    if (lengthin == 10) {
                        byte[] originalArray = bytes;
                        byte[] newArray = new byte[8];
                        System.arraycopy(originalArray, 4, newArray, 0, newArray.length);

                        long acc = (ByteBuffer.wrap(newArray)).getLong();

                        System.out.println("Value of accumulator: " + acc);

                    } else if (errorin == 16) {

                        byte[] originalArray = bytes;

                        byte[] newArray = new byte[8];
                        System.arraycopy(originalArray, 4, newArray, 0, newArray.length);

                        long acc = (ByteBuffer.wrap(newArray)).getLong();

                        System.out.println("Value of accumulator: " + acc);

                        byte[] originalArray2 = bytes;

                        byte[] newArray2 = new byte[bytes[13] + 1];
                        System.arraycopy(originalArray2, 13, newArray2, 0, newArray2.length);

                        String acc2 = new String(newArray2);

                        System.out.println(acc2);

                    } else if (errorin == 11) {

                        byte[] originalArray = bytes;

                        byte[] newArray = new byte[8];
                        System.arraycopy(originalArray, 6 + bytes[3], newArray, 0, newArray.length);

                        long acc = (ByteBuffer.wrap(newArray)).getLong();

                        System.out.println("Value of accumulator: " + acc);

                        byte[] originalArray2 = bytes;

                        byte[] newArray2 = new byte[bytes[3]];
                        System.arraycopy(originalArray2, 4, newArray2, 0, newArray2.length);

                        String acc2 = new String(newArray2);

                        System.out.println(acc2);
                    }

                }
            } catch (Exception m) {
            }

        } else {
            try {
                int port = Integer.parseInt(args[1]);
                String addr = args[0];

                Socket socket = new Socket();

                try {
                    socket.connect(new InetSocketAddress(addr, port), 15000);
                } catch (SocketTimeoutException exc) {
                    System.out.println("Waiting time is over. Try again.");
                    System.exit(0);
                }

                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());

                BufferedReader buff = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    System.out.println();
                    System.out.println("Enter arithmetic operation in infix notation.");
                    System.out.println("Examples: 10 + 5 , 10 - 5 , 10 * 5 , 10 / 5 , 10 % 5 , 10 ! ");
                    System.out.print("Operation: ");
                    String inline = buff.readLine();

                    if (inline.equals("quit")) {
                        break;
                    }

                    String[] split = inline.split("\\s+");
                    int type = 0;
                    int length = 0;
                    int split0 = 0;
                    int split1 = 0;
                    int split2 = 0;

                    if (split.length == 3) {

                        switch (split[1]) {
                            case "+":
                                type = 1;
                                break;
                            case "-":
                                type = 2;
                                break;
                            case "*":
                                type = 3;
                                break;
                            case "/":
                                type = 4;
                                break;
                            case "%":
                                type = 5;
                                break;
                            default:
                                System.out.println("Wrong format.");
                                System.out.println("Examples: 10 + 5 , 10 - 5 , 10 * 5 , 10 / 5 , 10 % 5 , 10 ! ");
                                System.out.println("Valid operators are: +, -, *, /, % and !");
                                continue;
                        }

                        split1 = Integer.parseInt(split[0]);
                        split2 = Integer.parseInt(split[2]);
                        length = 2;

                    } else if (split.length == 2 && split[1].equals("!")) {

                        type = 6;
                        split0 = Integer.parseInt(split[0]);
                        length = 1;

                    } else {
                        System.out.println("Invalid input format. Please check that the format is correct.");
                        System.out.println("Examples: 10 + 5 , 10 - 5 , 10 * 5 , 10 / 5 , 10 % 5 , 10 ! ");
                        continue;
                    }

                    if (length == 2) {
                        byte[] op2 = new byte[] { (byte) type, (byte) length, (byte) split1, (byte) split2 };
                        output.write(op2);

                    }

                    if (length == 1) {
                        byte[] op1 = new byte[] { (byte) type, (byte) length, (byte) split0 };
                        output.write(op1);

                    }

                    byte[] bytes = new byte[128];
                    input.read(bytes);
                    int lengthin = (int) bytes[1];
                    int errorin = (int) bytes[2];
                    if (lengthin == 10) {
                        byte[] originalArray = bytes;
                        byte[] newArray = new byte[8];
                        System.arraycopy(originalArray, 4, newArray, 0, newArray.length);

                        long acc = (ByteBuffer.wrap(newArray)).getLong();

                        System.out.println("Value of accumulator: " + acc);

                    } else if (errorin == 16) {

                        byte[] originalArray = bytes;

                        byte[] newArray = new byte[8];
                        System.arraycopy(originalArray, 4, newArray, 0, newArray.length);

                        long acc = (ByteBuffer.wrap(newArray)).getLong();

                        System.out.println("Value of accumulator: " + acc);

                        byte[] originalArray2 = bytes;

                        byte[] newArray2 = new byte[bytes[13] + 1];
                        System.arraycopy(originalArray2, 13, newArray2, 0, newArray2.length);

                        String acc2 = new String(newArray2);

                        System.out.println(acc2);

                    } else if (errorin == 11) {

                        byte[] originalArray = bytes;

                        byte[] newArray = new byte[8];
                        System.arraycopy(originalArray, 6 + bytes[3], newArray, 0, newArray.length);

                        long acc = (ByteBuffer.wrap(newArray)).getLong();

                        System.out.println("Value of accumulator: " + acc);

                        byte[] originalArray2 = bytes;

                        byte[] newArray2 = new byte[bytes[3]];
                        System.arraycopy(originalArray2, 4, newArray2, 0, newArray2.length);

                        String acc2 = new String(newArray2);

                        System.out.println(acc2);
                    }

                }

                input.close();
                output.close();
                socket.close();
            } catch (Exception m) {
            }
        }
    }
}
