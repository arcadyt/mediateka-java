package com.acowg.peer.events;

import com.acowg.shared.models.enums.CategoryType;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.context.ApplicationEvent;

import java.util.Set;

@Value
@EqualsAndHashCode(callSuper = false)
public class ScrapeResultEvent extends ApplicationEvent {
    String baseDirectoryPath;
    CategoryType category;
    Set<ScrapedFile> scrapedFiles;

    public ScrapeResultEvent(Object source, String baseDirectoryPath, CategoryType category, Set<ScrapedFile> scrapedFiles) {
        super(source);
        this.baseDirectoryPath = baseDirectoryPath;
        this.category = category;
        this.scrapedFiles = scrapedFiles;
    }
}
