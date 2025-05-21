package id.ac.ui.cs.advprog.ohioorder.grpc;

import admin.AdminOuterClass.AdminResponse;
import admin.AdminOuterClass.CreateAdminRequest;

import admin.AdminServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Service;

@Service
public class AdminGrpcClient {

    private final AdminServiceGrpc.AdminServiceBlockingStub adminStub;

    public AdminGrpcClient() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        adminStub = AdminServiceGrpc.newBlockingStub(channel);
    }

    public AdminResponse createAdmin(String email, String password) {
        CreateAdminRequest request = CreateAdminRequest.newBuilder()
                .setEmail(email)
                .setPassword(password)
                .build();
        return adminStub.createAdmin(request);
    }
}
