package com.acowg.edge.grpc;

import io.grpc.Context;

public class PeerContext {
    public static final Context.Key<String> PEER_NAME_KEY = Context.key("peer-name");
}