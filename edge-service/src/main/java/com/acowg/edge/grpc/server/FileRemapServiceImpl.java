package com.acowg.edge.grpc.server;

import com.acowg.peer_edge_proto.FileRemap;
import com.acowg.peer_edge_proto.FileRemapServiceGrpc;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class FileRemapServiceImpl extends FileRemapServiceGrpc.FileRemapServiceImplBase {

    private final FileRemapServiceGrpc.FileRemapServiceBlockingStub peerServiceStub;

    public FileRemapServiceImpl() {
        // Initialize the gRPC channel to the Peer Service
        ManagedChannel channel = ManagedChannelBuilder.forAddress("peer-service", 9090)
                .usePlaintext() // For testing only; use TLS in production
                .build();
        this.peerServiceStub = FileRemapServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public void remapFile(FileRemap.FileRemapRequest request, StreamObserver<Empty> responseObserver) {
        // Call the Peer Service to perform the remapping
        peerServiceStub.remapFile(request);

        // Send an empty response to acknowledge receipt of the request
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}