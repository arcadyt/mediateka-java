package com.acowg.peer.services.mediatype;

import com.acowg.peer.entities.MediaEntity;
import com.acowg.shared.models.enums.MediaType;
import lombok.NonNull;

public interface IMediaTypeResolver {
    MediaType resolveMediaType(@NonNull MediaEntity mediaEntity);

    boolean isVideo(@NonNull MediaEntity mediaEntity);

    boolean isAudio(@NonNull MediaEntity mediaEntity);
}
