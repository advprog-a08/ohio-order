package id.ac.ui.cs.advprog.ohioorder.grpc;

import admin.AdminOuterClass;
import admin.AdminOuterClass.AdminResponse;
import admin.AdminOuterClass.CreateAdminRequest;

import admin.AdminServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Service;

@Service
public class AdminGrpcClient {

    private final AdminServiceGrpc.AdminServiceBlockingStub stub;

    public AdminGrpcClient() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        stub = AdminServiceGrpc.newBlockingStub(channel);
    }

    public AdminResponse verifyAdmin(String token) {
        AdminOuterClass.TokenRequest request = AdminOuterClass.TokenRequest.newBuilder()
                .setToken(token)
                .build();
        return stub.verifyAdmin(request);
    }
}
