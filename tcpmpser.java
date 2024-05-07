import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.util.Set;

public class tcpmpser {

    public static void main(String args[]) throws Exception {
        if (args.length != 1) {
            System.out.println("Syntax error");
            System.out.println("Correct format : tcpmpser port_number ");
            System.out.println("Example: tcpmpser  6666");
            System.exit(0);
        }

        DatagramChannel channel = DatagramChannel.open();
        channel.bind(new InetSocketAddress(Integer.parseInt(args[0])));
        Selector selector = Selector.open();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
        channel.keyFor(selector).attach(Long.valueOf(0));
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(Integer.parseInt(args[0])));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            try {
                selector.select();

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = keys.iterator();
                SelectionKey key = keyIterator.next();

                if (key.isAcceptable()) {

                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                    SocketChannel clientChannel = serverSocketChannel.accept();
                    if (clientChannel == null) {
                        key = keyIterator.next();
                    } else {
                        clientChannel.configureBlocking(false);
                        SelectionKey keyclient = clientChannel.register(selector, SelectionKey.OP_READ);
                        keyclient.attach(Long.valueOf(0));

                    }
                }

                if (key.isReadable()) {
                    SelectableChannel clientcha = key.channel();
                    if (clientcha instanceof SocketChannel) {
                        SocketChannel clientChannel = (SocketChannel) clientcha;
                        ByteBuffer buffer = ByteBuffer.allocate(4);
                        int bytesRead = clientChannel.read(buffer);

                        if (bytesRead <= 0) {
                            clientChannel.close();
                            key.cancel();
                            continue;
                        }
                        long accumulator = (long) key.attachment();
                        try {
                            byte[] inputbytes = buffer.array();

                            ByteBuffer send;

                            long[] result;

                            System.out.println("IP & Port:" + clientChannel.getRemoteAddress() + ", Operation:");
                            result = Calculator(inputbytes, accumulator);
                            accumulator = result[0];
                            if (result[1] == 0) {
                                send = ByteBuffer.allocate(12);
                                send.put((byte) 10);
                                send.put((byte) 10);
                                send.put((byte) 16);
                                send.put((byte) 8);
                                send.putLong(result[0]);
                                send.flip();
                                clientChannel.write(send);
                                key.attach(Long.valueOf(accumulator));
                            } else {

                                if (result[2] == 1) {
                                    String error = "Can not divide by 0";// 19
                                    send = ByteBuffer.allocate(33); // 19+10+4
                                    send.put((byte) 10);// 10
                                    send.put((byte) 31);// 19+10+2
                                    send.put((byte) 11);// 11
                                    send.put((byte) 19);// 19
                                    send.put(error.getBytes());
                                    send.put((byte) 16);// 16
                                    send.put((byte) 8);// 8
                                    send.putLong(result[0]);
                                    send.flip();
                                    clientChannel.write(send);

                                    System.out.println("Can not divide by 0");

                                }

                                if (result[2] == 2) {
                                    String error = "Can not perform the factorial of negative number";
                                    send = ByteBuffer.allocate(62);
                                    send.put((byte) 10);// 10
                                    send.put((byte) 60);// 60
                                    send.put((byte) 11);// 11
                                    send.put((byte) 48);// 48
                                    send.put(error.getBytes());
                                    send.put((byte) 16);// 16
                                    send.put((byte) 8);// 8
                                    send.putLong(result[0]);
                                    send.flip();

                                    clientChannel.write(send);

                                    System.out.println("Can not perform the factorial of negative number");

                                }

                                if (result[2] == 3) {
                                    String error = "Overflow error";
                                    send = ByteBuffer.allocate(28);
                                    send.put((byte) 10);
                                    send.put((byte) 26);
                                    send.put((byte) 11);
                                    send.put((byte) 14);
                                    send.put(error.getBytes());
                                    send.put((byte) 16);
                                    send.put((byte) 8);
                                    send.putLong(result[0]);
                                    send.flip();
                                    clientChannel.write(send);

                                    System.out.println("Overflow error");
                                }
                                key.attach(Long.valueOf(accumulator));

                            }

                        } catch (Exception m) {
                            m.printStackTrace();
                            System.out.println("Error 3");

                        }

                    }

                    else {
                        DatagramChannel clientChannel = (DatagramChannel) clientcha;
                        ByteBuffer buffer = ByteBuffer.allocate(4);
                        SocketAddress add = clientChannel.receive(buffer);

                        if (buffer.array()[0] == 0) {
                            keyIterator.remove();
                            continue;
                        }
                        long accumulator = (long) key.attachment();

                        byte[] inputbytes = buffer.array();

                        ByteBuffer send;

                        long[] result;

                        System.out.println(
                                "IP & Port:" + add + ", Operation:");

                        result = Calculator(inputbytes, accumulator);
                        accumulator = result[0];
                        if (result[1] == 0) {
                            send = ByteBuffer.allocate(12);
                            send.put((byte) 10);
                            send.put((byte) 10);
                            send.put((byte) 16);
                            send.put((byte) 8);
                            send.putLong(result[0]);
                            send.flip();
                            clientChannel.send(send, add);
                            key.attach(Long.valueOf(accumulator));

                        } else {

                            if (result[2] == 1) {
                                String error = "Can not divide by 0";// 19
                                send = ByteBuffer.allocate(33); // 19+10+4
                                send.put((byte) 10);// 10
                                send.put((byte) 31);// 19+10+2
                                send.put((byte) 11);// 11
                                send.put((byte) 19);// 19
                                send.put(error.getBytes());
                                send.put((byte) 16);// 16
                                send.put((byte) 8);// 8
                                send.putLong(result[0]);
                                send.flip();
                                clientChannel.send(send, add);

                                System.out.println("Can not divide by 0");

                            }

                            if (result[2] == 2) {
                                String error = "Can not perform the factorial of negative number";
                                send = ByteBuffer.allocate(62);
                                send.put((byte) 10);// 10
                                send.put((byte) 60);// 60
                                send.put((byte) 11);// 11
                                send.put((byte) 48);// 48
                                send.put(error.getBytes());
                                send.put((byte) 16);// 16
                                send.put((byte) 8);// 8
                                send.putLong(result[0]);
                                send.flip();

                                clientChannel.send(send, add);

                                System.out.println("Can not perform the factorial of negative number");

                            }

                            if (result[2] == 3) {
                                String error = "Overflow error";
                                send = ByteBuffer.allocate(28);
                                send.put((byte) 10);
                                send.put((byte) 26);
                                send.put((byte) 11);
                                send.put((byte) 14);
                                send.put(error.getBytes());
                                send.put((byte) 16);
                                send.put((byte) 8);
                                send.putLong(result[0]);
                                send.flip();
                                clientChannel.send(send, add);

                                System.out.println("Overflow error");
                            }
                            key.attach(Long.valueOf(accumulator));

                        }

                    }

                }
                keyIterator.remove();
            } catch (Exception m) {
            }
        }

    }

    private static long[] Calculator(byte[] input, long accumulator) {

        long length;
        long operator1;
        long operator2;
        int operationtype;
        long type = 0;
        long result2;

        long result = 0;
        long mistake = 0;
        operator1 = (long) (input[2]);
        length = (long) (input[1]);
        operationtype = (int) (input[0]);
        try {
            if (length == 2)
                operator2 = Byte.valueOf(input[3]).intValue();
            else
                operator2 = 0;
            switch (operationtype) {
                case 1:

                    result = Math.addExact(operator1, operator2);
                    System.out.println(operator1 + "+" + operator2 + "= " + result);
                    break;
                case 2:

                    result = Math.subtractExact(operator1, operator2);
                    System.out.println(operator1 + "-" + operator2 + "= " + result);
                    break;
                case 3:

                    result = Math.multiplyExact(operator1, operator2);
                    System.out.println(operator1 + "*" + operator2 + "= " + result);
                    break;
                case 4:
                    type = 1;
                    if (operator2 != 0) {
                        result = operator1 / operator2;
                        System.out.println(operator1 + "/" + operator2 + "= " + result);
                    } else {
                        result = 0;
                        mistake = 1;
                    }
                    break;
                case 5:
                    type = 1;
                    if (operator2 != 0) {
                        result = operator1 % operator2;
                        System.out.println(operator1 + "%" + operator2 + "= " + result);
                    } else {
                        result = 0;
                        mistake = 1;
                    }

                    break;

                case 6:
                    type = 2;
                    if (operator1 < 0) {
                        mistake = 1;
                        result = 0;
                    } else {
                        result = operator1;
                        for (int i = 1; i < operator1; i++) {

                            result = Math.multiplyExact(i, result);
                        }

                        System.out.println(operator1 + "!" + "= " + result);
                    }
                    break;
                default:
                    result = 0;
                    break;
            }
        } catch (ArithmeticException overflow) {

            mistake = 1;
            type = 3;
            result = 0;
        }

        try {
            result2 = Math.addExact(result, accumulator);
        } catch (ArithmeticException max) {

            result2 = Long.MAX_VALUE;
        }

        return new long[] { result2, mistake, type };
    }

}