package com.acowg.peer.events;

import com.acowg.shared.models.enums.CategoryType;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.context.ApplicationEvent;

import java.util.Set;

@Value
@EqualsAndHashCode(callSuper = false)
public class ScrapeResultEvent extends ApplicationEvent {
    String categoryRoot;
    CategoryType category;
    Set<ScrapedFile> scrapedFiles;

    public ScrapeResultEvent(Object source, String categoryRoot, CategoryType category, Set<ScrapedFile> scrapedFiles) {
        super(source);
        this.categoryRoot = categoryRoot;
        this.category = category;
        this.scrapedFiles = scrapedFiles;
    }
}
