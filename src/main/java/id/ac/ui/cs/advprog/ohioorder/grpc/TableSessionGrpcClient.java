package id.ac.ui.cs.advprog.ohioorder.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import table_session.TableSessionOuterClass;
import table_session.TableSessionServiceGrpc;

@Service
public class TableSessionGrpcClient {

    private final TableSessionServiceGrpc.TableSessionServiceBlockingStub stub;

    public TableSessionGrpcClient(
            @Value("${grpc.host}") String host,
            @Value("${grpc.port}") int port
    ) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
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

    public TableSessionOuterClass.TableSessionResponse unsetCheckoutIdToTableSession(String sessionId) {
        TableSessionOuterClass.CheckoutIdRequest request = TableSessionOuterClass.CheckoutIdRequest.newBuilder()
                .setId(sessionId)
                .clearCheckoutId()
                .build();
        return stub.setCheckoutIdToTableSession(request);
    }

    public TableSessionOuterClass.TableSessionResponse setCheckoutIdToTableSession(String sessionId, String checkoutId) {
        TableSessionOuterClass.CheckoutIdRequest request = TableSessionOuterClass.CheckoutIdRequest.newBuilder()
                .setId(sessionId)
                .setCheckoutId(checkoutId)
                .build();
        return stub.setCheckoutIdToTableSession(request);
    }
}
