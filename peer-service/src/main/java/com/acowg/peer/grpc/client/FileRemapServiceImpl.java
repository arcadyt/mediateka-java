package com.acowg.peer.grpc.client;

import com.acowg.peer_edge_proto.FileRemap;
import com.acowg.peer_edge_proto.FileRemapServiceGrpc;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class FileRemapServiceImpl extends FileRemapServiceGrpc.FileRemapServiceImplBase {

    @Override
    public void remapFile(FileRemap.FileRemapRequest request, StreamObserver<Empty> responseObserver) {
        // Perform the remapping logic
        performRemapping(request.getOldCatalogUuid(), request.getNewCatalogUuid());

        // Send an empty response to acknowledge completion
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    private void performRemapping(String oldCatalogUuid, String newCatalogUuid) {
        // Logic to remap the catalog UUID
        System.out.println("Remapping " + oldCatalogUuid + " to " + newCatalogUuid);
    }
}