package com.acowg.edge.grpc;

import com.acowg.edge.config.EdgeConfig;
import com.acowg.proto.peer_edge.PeerEdge;
import com.acowg.proto.peer_edge.PeerEdge.*;
import com.acowg.proto.peer_edge.PeerEdgeServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class PeerEdgeServiceImpl extends PeerEdgeServiceGrpc.PeerEdgeServiceImplBase {

    private final Map<String, StreamObserver<EdgeMessage>> peerRegistry = new ConcurrentHashMap<>();
    private final EdgeConfig edgeConfig;

    @Override
    public StreamObserver<PeerMessage> message(StreamObserver<EdgeMessage> responseObserver) {
        return new StreamObserver<>() {
            private String peerName;

            @Override
            public void onNext(PeerMessage message) {
                String requestId = message.getRequestId();
                if (message.hasRegistrationRequest()) {
                    // Handle peer registration
                    peerName = message.getRegistrationRequest().getPeerName();
                    peerRegistry.put(peerName, responseObserver);

                    PeerEdge.EdgeMessage response = EdgeMessage.newBuilder()
                            .setRequestId(requestId)
                            .setRegistrationResponse(
                                    PeerRegistrationResponse.newBuilder()
                                            .setPeerName(peerName)
                                            .setEdgeName(edgeConfig.getName())
                                            .setSuccess(true)
                                            .build()
                            )
                            .build();
                    responseObserver.onNext(response);
                } else if (message.hasFileDeleteResponse()) {
                    // Handle file deletion response
                    FileDeleteResponse fileDeleteResponse = message.getFileDeleteResponse();
                    log.info("Received file delete response from peer: {}, catalog: {}, success: {}",
                            peerName, fileDeleteResponse.getCatalogUuid(), fileDeleteResponse.getSuccess());
                } else if (message.hasScreenshotCaptureResponse()) {
                    // Handle screenshot capture response
                    ScreenshotCaptureResponse screenshotResponse = message.getScreenshotCaptureResponse();
                    if (screenshotResponse.hasScreenshot()) {
                        log.info("Received screenshot from peer: {}, catalog: {}, frame: {}",
                                peerName, screenshotResponse.getCatalogUuid(),
                                screenshotResponse.getScreenshot().getFrameNumberInVideo());
                    } else {
                        log.warn("Screenshot capture failed for peer: {}, catalog: {}, error: {}",
                                peerName, screenshotResponse.getCatalogUuid(),
                                screenshotResponse.getErrorMessage());
                    }
                }
                // Add more handlers as needed
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

    // Helper method to send a message to a specific peer
    private void sendToPeer(String peerName, EdgeMessage message) {
        StreamObserver<EdgeMessage> peerStream = peerRegistry.get(peerName);
        if (peerStream != null) {
            peerStream.onNext(message);
        } else {
            log.warn("Peer not found: {}", peerName);
        }
    }

    // Example: Send a screenshot capture request to a peer
    public void requestScreenshot(String peerName, String requestId, ScreenshotCaptureRequest request) {
        EdgeMessage message = EdgeMessage.newBuilder()
                .setRequestId(requestId)
                .setScreenshotCaptureRequest(request)
                .build();
        sendToPeer(peerName, message);
    }

    // Example: Send a file delete request to a peer
    public void requestFileDeletion(String peerName, String requestId, FileDeleteRequest request) {
        EdgeMessage message = EdgeMessage.newBuilder()
                .setRequestId(requestId)
                .setFileDeleteRequest(request)
                .build();
        sendToPeer(peerName, message);
    }
}