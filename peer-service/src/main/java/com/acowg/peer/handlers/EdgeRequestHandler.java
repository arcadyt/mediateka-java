package com.acowg.peer.handlers;

import com.acowg.proto.peer_edge.PeerEdge;
import io.grpc.stub.StreamObserver;

public interface EdgeRequestHandler<T, R> {
    void handleRequest(String requestId, T request, StreamObserver<PeerEdge.PeerMessage> responseObserver);
}