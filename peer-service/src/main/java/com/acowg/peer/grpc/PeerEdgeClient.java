package com.acowg.peer.grpc;

import com.acowg.proto.peer_edge.PeerEdge.*;
import com.acowg.proto.peer_edge.PeerEdgeServiceGrpc;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PeerEdgeClient {

    private ManagedChannel channel;
    private PeerEdgeServiceGrpc.PeerEdgeServiceStub peerEdgeServiceStub;
    private StreamObserver<PeerMessage> clientStream;

    public void initializeConnection(String edgeAddress) {
        // Create a channel to the edge service
        channel = ManagedChannelBuilder.forTarget(edgeAddress)
                .usePlaintext() // For testing only; use TLS in production
                .build();

        // Create an async stub
        peerEdgeServiceStub = PeerEdgeServiceGrpc.newStub(channel);

        // Initialize the bidirectional stream
        clientStream = peerEdgeServiceStub.message(new StreamObserver<>() {
            @Override
            public void onNext(EdgeMessage message) {
                String requestId = message.getRequestId();
                if (message.hasRegistrationResponse()) {
                    // Handle registration response
                    PeerRegistrationResponse response = message.getRegistrationResponse();
                    log.info("Received registration response: {}", response);
                } else if (message.hasFileDeleteRequest()) {
                    // Handle file deletion request
                    FileDeleteRequest request = message.getFileDeleteRequest();
                    log.info("Received file delete request: {}", request);

                    // Process the request and send a response
                    request.getCatalogUuidsList().forEach(catalogUuid -> {
                        FileDeleteResponse response = FileDeleteResponse.newBuilder()
                                .setCatalogUuid(catalogUuid)
                                .setSuccess(true)
                                .build();
                        PeerMessage peerMessage = PeerMessage.newBuilder()
                                .setRequestId(requestId)
                                .setFileDeleteResponse(response)
                                .build();
                        clientStream.onNext(peerMessage);
                    });
                } else if (message.hasScreenshotCaptureRequest()) {
                    // Handle screenshot capture request
                    ScreenshotCaptureRequest request = message.getScreenshotCaptureRequest();
                    log.info("Received screenshot capture request: {}", request);

                    // Process the request and send a response
                    ScreenshotCaptureResponse response = ScreenshotCaptureResponse.newBuilder()
                            .setCatalogUuid(request.getCatalogUuid())
                            .setScreenshot(ScreenshotData.newBuilder()
                                    .setFrameNumberInVideo(1)
                                    .setScreenshot(ByteString.copyFrom(new byte[1024])) // Example screenshot data
                                    .build())
                            .build();
                    PeerMessage peerMessage = PeerMessage.newBuilder()
                            .setRequestId(requestId)
                            .setScreenshotCaptureResponse(response)
                            .build();
                    clientStream.onNext(peerMessage);
                }
                // Add more handlers as needed
            }

            @Override
            public void onError(Throwable t) {
                log.error("Server stream error: {}", t.getMessage());
            }

            @Override
            public void onCompleted() {
                log.info("Server stream completed");
            }
        });

        // Send a registration request
        String requestId = UUID.randomUUID().toString();
        PeerMessage request = PeerMessage.newBuilder()
                .setRequestId(requestId)
                .setRegistrationRequest(PeerRegistrationRequest.newBuilder()
                        .setPeerName("peer-1")
                        .build())
                .build();
        clientStream.onNext(request);
    }

    // Example: Send a file offer request to the edge
    public void sendFileOffer(String catalogUuid, String relativePath, long sizeBytes) {
        String requestId = UUID.randomUUID().toString();
        PeerMessage request = PeerMessage.newBuilder()
                .setRequestId(requestId)
                .setFileOfferRequest(FileOfferRequest.newBuilder()
                        .setPeerLuid("peer-1")
                        .setRelativePath(relativePath)
                        .setSizeBytes(sizeBytes)
                        .build())
                .build();
        clientStream.onNext(request);
    }

    // Example: Send a file hash response to the edge
    public void sendFileHashResponse(String catalogUuid, Map<String, String> hashes) {
        String requestId = UUID.randomUUID().toString();
        PeerMessage request = PeerMessage.newBuilder()
                .setRequestId(requestId)
                .setFileHashResponse(FileHashResponse.newBuilder()
                        .setCatalogUuid(catalogUuid)
                        .putAllHashes(hashes)
                        .build())
                .build();
        clientStream.onNext(request);
    }

    public void shutdown() {
        if (channel != null) {
            channel.shutdown();
        }
    }
}