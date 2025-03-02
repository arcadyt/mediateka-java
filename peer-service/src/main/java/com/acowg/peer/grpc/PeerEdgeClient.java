package com.acowg.peer.grpc;

import com.acowg.peer.handlers.FileDeleteRequestHandler;
import com.acowg.peer.handlers.FileHashRequestHandler;
import com.acowg.peer.handlers.ScreenshotCaptureRequestHandler;
import com.acowg.proto.peer_edge.PeerEdge;
import com.acowg.proto.peer_edge.PeerEdgeServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PeerEdgeClient {

    private final PeerConfig peerConfig;
    private final FileDeleteRequestHandler fileDeleteHandler;
    private final FileHashRequestHandler fileHashHandler;
    private final ScreenshotCaptureRequestHandler screenshotHandler;

    private ManagedChannel channel;
    private PeerEdgeServiceGrpc.PeerEdgeServiceStub peerEdgeServiceStub;
    private StreamObserver<PeerEdge.PeerMessage> clientStream;

    @PostConstruct
    public void initializeConnection() {
        String edgeAddress = peerConfig.getEdgeServiceAddress();

        // Create a channel to the edge service
        channel = ManagedChannelBuilder.forTarget(edgeAddress)
                .usePlaintext() // For testing only; use TLS in production
                .build();

        // Create an async stub
        peerEdgeServiceStub = PeerEdgeServiceGrpc.newStub(channel);

        // Initialize the bidirectional stream
        clientStream = peerEdgeServiceStub.message(new StreamObserver<>() {
            @Override
            public void onNext(PeerEdge.EdgeMessage message) {
                String requestId = message.getRequestId();

                try {
                    if (message.hasRegistrationResponse()) {
                        // Handle registration response
                        PeerEdge.PeerRegistrationResponse response = message.getRegistrationResponse();
                        log.info("Received registration response: {}", response);

                    } else if (message.hasFileDeleteRequest()) {
                        // Handle file deletion request
                        fileDeleteHandler.handleRequest(requestId, message.getFileDeleteRequest(), clientStream);

                    } else if (message.hasFileHashRequest()) {
                        // Handle file hash request
                        fileHashHandler.handleRequest(requestId, message.getFileHashRequest(), clientStream);

                    } else if (message.hasScreenshotCaptureRequest()) {
                        // Handle screenshot capture request
                        screenshotHandler.handleRequest(requestId, message.getScreenshotCaptureRequest(), clientStream);

                    } else if (message.hasFileRemapRequest()) {
                        // Handle file remap request if implemented
                        log.warn("File remap request received but handler not implemented");

                    } else if (message.hasFileOfferResponse()) {
                        // Handle file offer response
                        log.info("Received file offer response: {}", message.getFileOfferResponse());
                    }
                } catch (Exception e) {
                    log.error("Error handling edge message: {}", e.getMessage(), e);
                }
            }

            @Override
            public void onError(Throwable t) {
                log.error("Server stream error: {}", t.getMessage());
                // Attempt to reconnect
                scheduleReconnect();
            }

            @Override
            public void onCompleted() {
                log.info("Server stream completed");
                // Attempt to reconnect
                scheduleReconnect();
            }
        });

        // Send a registration request
        sendRegistrationRequest();
    }

    private void sendRegistrationRequest() {
        String requestId = UUID.randomUUID().toString();
        PeerEdge.PeerMessage request = PeerEdge.PeerMessage.newBuilder()
                .setRequestId(requestId)
                .setRegistrationRequest(PeerEdge.PeerRegistrationRequest.newBuilder()
                        .setPeerName(peerConfig.getPeerName())
                        .build())
                .build();
        clientStream.onNext(request);
        log.info("Sent registration request with ID: {}", requestId);
    }

    private void scheduleReconnect() {
        // For simplicity, we'll just log the need to reconnect here
        // In a real implementation, you'd use a scheduler to retry with exponential backoff
        log.warn("Connection to edge service lost. Manual reconnection needed.");
    }

    // Example: Send a file offer request to the edge
    public void sendFileOffer(String relativePath, long sizeBytes) {
        String requestId = UUID.randomUUID().toString();
        PeerEdge.PeerMessage request = PeerEdge.PeerMessage.newBuilder()
                .setRequestId(requestId)
                .setFileOfferRequest(PeerEdge.FileOfferRequest.newBuilder()
                        .setPeerLuid(peerConfig.getPeerName())
                        .setRelativePath(relativePath)
                        .setSizeBytes(sizeBytes)
                        .build())
                .build();
        clientStream.onNext(request);
        log.info("Sent file offer for path: {}, size: {} bytes", relativePath, sizeBytes);
    }

    // Example: Send a file hash response to the edge directly
    public void sendFileHashResponse(String catalogUuid, Map<String, String> hashes) {
        String requestId = UUID.randomUUID().toString();
        PeerEdge.PeerMessage request = PeerEdge.PeerMessage.newBuilder()
                .setRequestId(requestId)
                .setFileHashResponse(PeerEdge.FileHashResponse.newBuilder()
                        .setCatalogUuid(catalogUuid)
                        .putAllHashes(hashes)
                        .build())
                .build();
        clientStream.onNext(request);
        log.info("Sent file hash response for catalog UUID: {}", catalogUuid);
    }

    @PreDestroy
    public void shutdown() {
        if (clientStream != null) {
            try {
                clientStream.onCompleted();
            } catch (Exception e) {
                log.warn("Error completing client stream: {}", e.getMessage());
            }
        }

        if (channel != null) {
            channel.shutdown();
        }

        log.info("PeerEdgeClient shut down");
    }
}