package com.diaosichengxuyuan.network.protocal.modbus;

import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.master.ModbusTcpMasterConfig;
import com.digitalpetri.modbus.requests.ReadHoldingRegistersRequest;
import com.digitalpetri.modbus.responses.ReadHoldingRegistersResponse;
import io.netty.buffer.ByteBuf;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author liuhaipeng
 * @date 2018/11/18
 */
public class ModbusTcpClient {
    private final ModbusTcpMaster master;

    public ModbusTcpClient() {
        master = new ModbusTcpMaster(new ModbusTcpMasterConfig.Builder("localhost")
            .setPort(50200).build());
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        new ModbusTcpClient().read(1, 101, 2);
    }

    public void read(int unitId, int address, int quality) throws InterruptedException, ExecutionException {
        CompletableFuture<ReadHoldingRegistersResponse> future = master.sendRequest(
            new ReadHoldingRegistersRequest(address, quality), unitId);
        ReadHoldingRegistersResponse response = future.get();
        ByteBuf byteBuf = response.getRegisters();
        float value = byteBuf.readFloat();
        System.out.println("时间" + System.currentTimeMillis() + " 读取地址" + address + "的值" + value);
    }
}
