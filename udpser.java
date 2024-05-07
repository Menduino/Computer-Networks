import java.io.*;
import java.net.*;

public class udpser {

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.out.println("Syntax error. Correct format is: udpser port_number secret");
            System.out.println("Example: udpser 666 5");
            return;
        }
        DatagramSocket socket = new DatagramSocket(Integer.parseInt(args[0]));
        DatagramPacket packetsend = null;
        DatagramPacket packetreceived = null;

        while (true) {

            byte[] bytereceived = new byte[9999];
            packetreceived = new DatagramPacket(bytereceived, bytereceived.length);
            socket.receive(packetreceived);
            String math = new String(packetreceived.getData());
            String result = calculator(math, Integer.parseInt(args[1]));
            byte[] bytesend = result.getBytes();

            packetsend = new DatagramPacket(bytesend, bytesend.length, packetreceived.getSocketAddress());
            socket.send(packetsend);
        }
    }

    private static String calculator(String math, int secret) {
        math.replaceAll(" ", "");
        int num1, num2;
        char operator;
        int out;
        int i = 0;

        while (true) {
            if (!(Character.isDigit(math.charAt(i)))) {
                break;
            }
            i++;
        }
        operator = math.charAt(i);
        num1 = Integer.parseInt(math.substring(0, i));
        i++;
        int n = i;
        while (true) {
            if (!(Character.isDigit(math.charAt(i)))) {
                break;
            }
            i++;
        }

        num2 = Integer.parseInt(math.substring(n, i));

        if (num1 <= 255 & num1 >= 0 & num2 <= 255 & num2 >= 0) {
            switch (operator) {

                case '+':
                    out = num1 + num2 + secret;
                    break;

                case '-':
                    out = num1 - num2 + secret;
                    break;

                case 'x':
                    out = num1 * num2 + secret;
                    break;

                case '/':
                    out = num1 / num2 + secret;
                    break;

                default:
                    return ("You entered a wrong operator. The expected format is +, -, x or /");
            }
            return Integer.toString(out);

        } else {
            return ("You entered a wrong operand. They must be in the range [0,255]");
        }

    }

}
