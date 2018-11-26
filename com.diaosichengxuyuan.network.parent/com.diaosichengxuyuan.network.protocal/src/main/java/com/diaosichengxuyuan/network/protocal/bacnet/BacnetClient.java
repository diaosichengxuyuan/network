package com.diaosichengxuyuan.network.protocal.bacnet;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.ResponseConsumer;
import com.serotonin.bacnet4j.apdu.AckAPDU;
import com.serotonin.bacnet4j.event.DeviceEventAdapter;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkUtils;
import com.serotonin.bacnet4j.service.acknowledgement.AcknowledgementService;
import com.serotonin.bacnet4j.service.confirmed.ReadPropertyRequest;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;

/**
 * @author liuhaipeng
 * @date 2018/9/26
 */
public class BacnetClient {

    public static void main(String[] args) throws Exception {
        IpNetwork ipNetwork = new IpNetworkBuilder().withBroadcast("localhost", 16).withLocalNetworkNumber(1).withPort(
            2222).build();
        DefaultTransport transport = new DefaultTransport(ipNetwork);
        LocalDevice localDevice = new LocalDevice(1, transport);
        localDevice.getEventHandler().addListener(new DeviceEventAdapter() {
            @Override
            public void iAmReceived(final RemoteDevice d) {
                System.out.println("收到：" + d);
            }
        });

        localDevice.initialize();

        Address address = new Address(1, IpNetworkUtils.toOctetString("remote-address", 47808));
        ObjectIdentifier objectIdentifier = new ObjectIdentifier(ObjectType.device, 1);
        ReadPropertyRequest readPropertyRequest = new ReadPropertyRequest(objectIdentifier,
            PropertyIdentifier.propertyList);
        localDevice.send(address, readPropertyRequest, new ResponseConsumer() {
            @Override
            public void success(AcknowledgementService ack) {
                System.out.println("成功响应" + ack);
            }

            @Override
            public void fail(AckAPDU ack) {
                System.out.println("失败响应：" + ack);
            }

            @Override
            public void ex(BACnetException e) {
                System.out.println("异常响应：" + e);
            }
        });
    }

}
