package com.diaosichengxuyuan.network.protocal.modbus;

import com.digitalpetri.modbus.requests.ReadHoldingRegistersRequest;
import com.digitalpetri.modbus.responses.ReadHoldingRegistersResponse;
import com.digitalpetri.modbus.slave.ModbusTcpSlave;
import com.digitalpetri.modbus.slave.ModbusTcpSlaveConfig;
import com.digitalpetri.modbus.slave.ServiceRequestHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.ExecutionException;

/**
 * @author liuhaipeng
 * @date 2018/11/18
 */
public class ModbusTcpServer {
    private final ModbusTcpSlave slave = new ModbusTcpSlave(new ModbusTcpSlaveConfig.Builder().build());

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new ModbusTcpServer().start();
        System.out.println("启动成功");
    }

    public void start() throws ExecutionException, InterruptedException {
        slave.setRequestHandler(new ServiceRequestHandler() {
            @Override
            public void onReadHoldingRegisters(
                ServiceRequest<ReadHoldingRegistersRequest, ReadHoldingRegistersResponse> service) {

                ReadHoldingRegistersRequest request = service.getRequest();
                int quantity = request.getQuantity();
                int address = request.getAddress();
                ByteBuf byteBuf = Unpooled.buffer();
                float data = 76.54F;
                for(int i = 0; i < quantity; i += 2) {
                    byteBuf.writeFloat(data);
                }
                System.out.println(String
                    .format("时间%s 接收到地址%s要读取%s个线圈的数据 给出数据：%s", System.currentTimeMillis(), address, quantity, data));
                service.sendResponse(new ReadHoldingRegistersResponse(byteBuf));
                ReferenceCountUtil.release(request);
            }
        });
        slave.bind("localhost", 50200).get();
    }
}
