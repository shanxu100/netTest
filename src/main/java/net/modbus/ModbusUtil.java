package net.modbus;

import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.*;
import net.wimpi.modbus.net.TCPMasterConnection;

import java.net.InetAddress;


public class ModbusUtil {
    /**
     * 查询Function 为Input Status的寄存器
     *
     * @param ip
     * @param address
     * @param slaveId
     * @return
     */
    public static int readDigitalInput(String ip, int port, int address, int slaveId) {
        int data = 0;

        try {
            InetAddress addr = InetAddress.getByName(ip);

            // 建立连接
            TCPMasterConnection con = new TCPMasterConnection(addr);

            con.setPort(port);

            con.connect();

            // 第一个参数是寄存器的地址，第二个参数时读取多少个
            ReadInputDiscretesRequest req = new ReadInputDiscretesRequest(address, 1);

            // 这里设置的Slave Id, 读取的时候这个很重要
            req.setUnitID(slaveId);

            ModbusTCPTransaction trans = new ModbusTCPTransaction(con);

            trans.setRequest(req);

            // 执行查询
            trans.execute();

            // 得到结果
            ReadInputDiscretesResponse res = (ReadInputDiscretesResponse) trans.getResponse();

            if (res.getDiscretes().getBit(0)) {
                data = 1;
            }

            // 关闭连接
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public static int readInputRegister(String ip, int port, int address, int slaveId) {
        int data = 0;

        try {
            InetAddress addr = InetAddress.getByName(ip);
            TCPMasterConnection con = new TCPMasterConnection(addr);

//            Modbus.DEFAULT_PORT;
            con.setPort(port);
            con.connect();

            //这里重点说明下，这个地址和数量一定要对应起来
            ReadInputRegistersRequest req = new ReadInputRegistersRequest(address, 1);

            //这个SlaveId一定要正确
            req.setUnitID(slaveId);

            ModbusTCPTransaction trans = new ModbusTCPTransaction(con);

            trans.setRequest(req);

            trans.execute();

            ReadInputRegistersResponse res = (ReadInputRegistersResponse) trans.getResponse();

            data = res.getRegisterValue(0);

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public static int readDigitalOutput(String ip, int port, int address, int slaveId) {
        int data = 0;
        try {
            InetAddress addr = InetAddress.getByName(ip);

            TCPMasterConnection con = new TCPMasterConnection(addr);
            con.setPort(port);
            con.connect();

            ReadCoilsRequest req = new ReadCoilsRequest(address, 1);

            req.setUnitID(slaveId);

            ModbusTCPTransaction trans = new ModbusTCPTransaction(con);

            trans.setRequest(req);

            trans.execute();

            ReadCoilsResponse res = ((ReadCoilsResponse) trans.getResponse());

            if (res.getCoils().getBit(0)) {
                data = 1;
            }

            con.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return data;
    }

    public static int readRegister(String ip, int port, int address, int slaveId) {
        int data = 0;
        try {
            InetAddress addr = InetAddress.getByName(ip);

            TCPMasterConnection con = new TCPMasterConnection(addr);

            con.setPort(port);
            con.connect();
            //  指定寄存器个数，一次读n个
            ReadMultipleRegistersRequest req = new ReadMultipleRegistersRequest(address, 1);
            req.setUnitID(slaveId);

            ModbusTCPTransaction trans = new ModbusTCPTransaction(con);

            trans.setRequest(req);

            trans.execute();

            ReadMultipleRegistersResponse res = (ReadMultipleRegistersResponse) trans.getResponse();

            data = res.getRegisterValue(0);
            //都多个数据的结果
            res.getRegisters();

            System.out.println(res.getRegisterValue(0)+"=============="+res.getRegisterValue(1));
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    /**
     * 写入数据到真机，数据类型是RE
     *
     * @param ip
     * @param port
     * @param slaveId
     * @param address
     * @param value
     */
    public static void writeRegister(String ip, int port, int slaveId,
                                     int address, int value) {

        try {
            InetAddress addr = InetAddress.getByName(ip);

            TCPMasterConnection connection = new TCPMasterConnection(addr);
            connection.setPort(port);
            connection.connect();

            ModbusTCPTransaction trans = new ModbusTCPTransaction(connection);

            UnityRegister register = new UnityRegister(value);

            WriteSingleRegisterRequest req = new WriteSingleRegisterRequest(
                    address, register);

            req.setUnitID(slaveId);
            trans.setRequest(req);

            System.out.println("ModbusSlave: FC" + req.getFunctionCode()
                    + " ref=" + req.getReference() + " value="
                    + register.getValue());
            trans.execute();

            connection.close();
        } catch (Exception ex) {
            System.out.println("Error in code");
            ex.printStackTrace();
        }
    }

    /**
     * 写入数据到真机的DO类型的寄存器上面
     *
     * @param ip
     * @param port
     * @param slaveId
     * @param address
     * @param value
     */
    public static void writeDigitalOutput(String ip, int port, int slaveId,
                                          int address, int value) {

        try {
            InetAddress addr = InetAddress.getByName(ip);

            TCPMasterConnection connection = new TCPMasterConnection(addr);
            connection.setPort(port);
            connection.connect();

            ModbusTCPTransaction trans = new ModbusTCPTransaction(connection);

            boolean val = true;

            if (value == 0) {
                val = false;
            }

            WriteCoilRequest req = new WriteCoilRequest(address, val);

            req.setUnitID(slaveId);
            trans.setRequest(req);

            trans.execute();
            connection.close();
        } catch (Exception ex) {
            System.out.println("writeDigitalOutput Error in code");
            ex.printStackTrace();
        }
    }

}
