package com.acowg.edge.grpc;

import com.acowg.proto.peer_edge.PeerEdge.*;
import com.acowg.proto.peer_edge.PeerEdgeServiceGrpc;
import com.google.protobuf.Empty;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@GrpcService
public class PeerEdgeServiceImpl extends PeerEdgeServiceGrpc.PeerEdgeServiceImplBase {

    private final Map<String, StreamObserver<PeerRegistrationResponse>> peerRegistry = new ConcurrentHashMap<>();

    private String getPeerNameOrError(StreamObserver<?> responseObserver) {
        String peerName = PeerContext.PEER_NAME_KEY.get();
        if (peerName == null) {
            sendError(responseObserver, "Peer not authenticated");
            return null;
        }
        return peerName;
    }

    private void sendError(StreamObserver<?> responseObserver, String errorMessage) {
        log.error(errorMessage);
        responseObserver.onError(new RuntimeException(errorMessage));
    }

    private StreamObserver<PeerRegistrationResponse> getPeerStreamOrError(String peerName, StreamObserver<?> responseObserver) {
        StreamObserver<PeerRegistrationResponse> peerStream = peerRegistry.get(peerName);
        if (peerStream == null) {
            sendError(responseObserver, "Peer not found: " + peerName);
            return null;
        }
        return peerStream;
    }

    @Override
    public StreamObserver<PeerRegistrationRequest> registerPeer(StreamObserver<PeerRegistrationResponse> responseObserver) {
        return new StreamObserver<>() {
            private String peerName;

            @Override
            public void onNext(PeerRegistrationRequest request) {
                peerName = request.getPeerName();
                peerRegistry.put(peerName, responseObserver);

                // Store the peer ID in the gRPC context
                Context.current().withValue(PeerContext.PEER_NAME_KEY, peerName).run(() -> {
                    log.info("Peer registered: {}", peerName);

                    PeerRegistrationResponse response = PeerRegistrationResponse.newBuilder()
                            .setPeerName(peerName)
                            .setEdgeName("edge-1") // Replace with actual edge name
                            .setSuccess(true)
                            .build();
                    responseObserver.onNext(response);
                });
            }

            @Override
            public void onError(Throwable t) {
                if (peerName != null) {
                    peerRegistry.remove(peerName);
                    log.warn("Peer disconnected (error): {}", peerName);
                }
            }

            @Override
            public void onCompleted() {
                if (peerName != null) {
                    peerRegistry.remove(peerName);
                    log.info("Peer disconnected: {}", peerName);
                }
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void deleteFile(FileDeleteRequest request, StreamObserver<FileDeleteResponse> responseObserver) {
        String peerName = getPeerNameOrError(responseObserver);
        if (peerName == null) return;

        StreamObserver<PeerRegistrationResponse> peerStream = getPeerStreamOrError(peerName, responseObserver);
        if (peerStream == null) return;

        log.info("Requesting file deletion from peer: {}, catalog: {}", peerName, request.getCatalogUuid());

        FileDeleteResponse response = FileDeleteResponse.newBuilder()
                .setCatalogUuid(request.getCatalogUuid())
                .setSuccess(true)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void requestHashCalculation(FileHashRequest request, StreamObserver<Empty> responseObserver) {
        String peerName = getPeerNameOrError(responseObserver);
        if (peerName == null) return;

        StreamObserver<PeerRegistrationResponse> peerStream = getPeerStreamOrError(peerName, responseObserver);
        if (peerStream == null) return;

        log.info("Requesting hash calculation from peer: {}, catalog: {}", peerName, request.getCatalogUuid());
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void sendHashResult(FileHashResponse request, StreamObserver<Empty> responseObserver) {
        String peerName = getPeerNameOrError(responseObserver);
        if (peerName == null) return;

        log.info("Received hash result from peer: {}, catalog: {}", peerName, request.getCatalogUuid());
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<FileOfferRequest> offerFile(StreamObserver<FileOfferResponse> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(FileOfferRequest request) {
                String peerName = getPeerNameOrError(responseObserver);
                if (peerName == null) return;

                log.info("Received file offer from peer: {}, luid: {}", peerName, request.getPeerLuid());

                FileOfferResponse response = FileOfferResponse.newBuilder()
                        .setPeerLuid(request.getPeerLuid())
                        .setCatalogUuid("catalog-" + request.getPeerLuid())
                        .build();
                responseObserver.onNext(response);
            }

            @Override
            public void onError(Throwable t) {
                log.warn("Error in file offer stream: {}", t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void remapFile(FileRemapRequest request, StreamObserver<FileRemapResponse> responseObserver) {
        String peerName = getPeerNameOrError(responseObserver);
        if (peerName == null) return;

        StreamObserver<PeerRegistrationResponse> peerStream = getPeerStreamOrError(peerName, responseObserver);
        if (peerStream == null) return;

        log.info("Requesting file remapping from peer: {}, old catalog: {}, new catalog: {}", peerName, request.getOldCatalogUuid(), request.getNewCatalogUuid());

        FileRemapResponse response = FileRemapResponse.newBuilder()
                .setNewCatalogUuid(request.getNewCatalogUuid())
                .setSuccess(true)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void requestScreenshotCapture(ScreenshotCaptureRequest request, StreamObserver<Empty> responseObserver) {
        String peerName = getPeerNameOrError(responseObserver);
        if (peerName == null) return;

        StreamObserver<PeerRegistrationResponse> peerStream = getPeerStreamOrError(peerName, responseObserver);
        if (peerStream == null) return;

        log.info("Requesting screenshot capture from peer: {}, catalog: {}", peerName, request.getCatalogUuid());
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<ScreenshotCaptureResponse> sendScreenshots(StreamObserver<Empty> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(ScreenshotCaptureResponse request) {
                String peerName = getPeerNameOrError(responseObserver);
                if (peerName == null) return;

                log.info("Received screenshot from peer: {}, catalog: {}", peerName, request.getCatalogUuid());
            }

            @Override
            public void onError(Throwable t) {
                log.warn("Error in screenshot stream", t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(Empty.getDefaultInstance());
                responseObserver.onCompleted();
            }
        };
    }
}
