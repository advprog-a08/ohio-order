package id.ac.ui.cs.advprog.ohioorder.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.h2.table.Table;
import org.springframework.stereotype.Service;
import table_session.TableSessionOuterClass;
import table_session.TableSessionServiceGrpc;

@Service
public class TableSessionGrpcClient {

    private final TableSessionServiceGrpc.TableSessionServiceBlockingStub stub;

    public TableSessionGrpcClient() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        stub = TableSessionServiceGrpc.newBlockingStub(channel);
    }

    public TableSessionOuterClass.TableSessionResponse createTableSession(String tableId, String orderId) {
        TableSessionOuterClass.TableIdRequest request = TableSessionOuterClass.TableIdRequest.newBuilder()
                .setTableId(tableId)
                .setOrderId(orderId)
                .build();
        return stub.createTableSession(request);
    }

    public TableSessionOuterClass.TableSessionResponse verifyTableSession(String sessionId) {
        TableSessionOuterClass.SessionIdRequest request = TableSessionOuterClass.SessionIdRequest.newBuilder()
                .setSessionId(sessionId)
                .build();
        return stub.verifyTableSession(request);
    }

    public TableSessionOuterClass.TableSessionResponse deactivateTableSession(String sessionId) {
        TableSessionOuterClass.IsActiveRequest request = TableSessionOuterClass.IsActiveRequest.newBuilder()
                .setId(sessionId)
                .setValue(false)
                .build();
        return stub.setIsActiveToTableSession(request);
    }

    public TableSessionOuterClass.TableSessionResponse setCheckoutIdToTableSession(String sessionId, String checkoutId) {
        TableSessionOuterClass.CheckoutIdRequest request = TableSessionOuterClass.CheckoutIdRequest.newBuilder()
                .setId(sessionId)
                .setCheckoutId(checkoutId)
                .build();
        return stub.setCheckoutIdToTableSession(request);
    }
}
