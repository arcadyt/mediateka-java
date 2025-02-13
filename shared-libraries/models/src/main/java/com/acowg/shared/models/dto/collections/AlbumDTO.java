package com.acowg.shared.models.dto.collections;

import com.acowg.shared.models.dto.AudioDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AlbumDTO extends MediaCollectionDTO {
    private List<AudioDTO> tracks;
}
