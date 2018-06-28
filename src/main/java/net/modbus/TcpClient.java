package net.modbus;

public class TcpClient {

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 502;

    public static void main(String[] args) {

        int data=-1;
        data = ModbusUtil.readRegister(HOST, PORT, 8, 1);
        System.out.println(data);

    }


    /**
     * 将byte以16进制的形式打印出来
     *
     * @param buffer
     * @return
     */
    public static String byte2hex(byte[] buffer) {
        String h = "";

        for (int i = 0; i < buffer.length; i++) {
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if (temp.length() == 1) {
                temp = "0" + temp;
            }
            h = h + " " + temp;
        }

        return h;
    }


}
