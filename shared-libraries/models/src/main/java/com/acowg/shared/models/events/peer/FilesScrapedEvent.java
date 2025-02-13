package com.acowg.shared.models.events.peer;

import com.acowg.shared.models.basics.HasPeerId;
import com.acowg.shared.models.basics.file.FileBasicDetails;
import com.acowg.shared.models.events.AEvent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collection;

@Getter
@Builder(builderClassName = "Bob")
@EqualsAndHashCode
public class FilesScrapedEvent extends AEvent implements HasPeerId {
    @NotBlank
    private final String peerId;
    @NotEmpty
    private final Collection<FileBasicDetails> files;
}
