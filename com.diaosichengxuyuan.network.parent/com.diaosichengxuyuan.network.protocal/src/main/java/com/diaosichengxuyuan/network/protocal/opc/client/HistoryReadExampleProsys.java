/*
 * Copyright (c) 2018 Kevin Herron
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *   http://www.eclipse.org/org/documents/edl-v10.html.
 */

package com.diaosichengxuyuan.network.protocal.opc.client;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.eclipse.milo.opcua.stack.core.util.ConversionUtil.l;

public class HistoryReadExampleProsys implements ClientExample {

    public static void main(String[] args) throws Exception {
        HistoryReadExampleProsys example = new HistoryReadExampleProsys();

        new ClientExampleRunner(example, false).run();
    }

    @Override
    public void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception {
        client.connect().get();

        HistoryReadDetails historyReadDetails = new ReadRawModifiedDetails(
            false,
            DateTime.MIN_VALUE,
            DateTime.now(),
            uint(0),
            true
        );

        HistoryReadValueId historyReadValueId = new HistoryReadValueId(
            new NodeId(5, "Counter1"),
            null,
            QualifiedName.NULL_VALUE,
            ByteString.NULL_VALUE
        );

        List<HistoryReadValueId> nodesToRead = new ArrayList<>();
        nodesToRead.add(historyReadValueId);

        HistoryReadResponse historyReadResponse = client.historyRead(
            historyReadDetails,
            TimestampsToReturn.Both,
            false,
            nodesToRead
        ).get();


        HistoryReadResult[] historyReadResults = historyReadResponse.getResults();

        if (historyReadResults != null) {
            HistoryReadResult historyReadResult = historyReadResults[0];
            HistoryData historyData = historyReadResult.getHistoryData().decode();

            List<DataValue> dataValues = l(historyData.getDataValues());

            dataValues.forEach(v -> System.out.println("value=" + v));
        }

        future.complete(client);
    }

    @Override
    public String getEndpointUrl() {
        return "opc.tcp://localhost:53530/OPCUA/SimulationServer";
    }

}
