package com.acowg.peer.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event representing the result of a scraping operation for a specific drive.
 */
@Getter
public class ScrapeResultEvent extends ApplicationEvent {

    private final String drive;
    private final ScrapeResult scrapeResult;

    /**
     * Constructs a new ScrapeResultEvent.
     *
     * @param source       The source of the event (usually the ScraperService).
     * @param drive        The drive identifier for which the scraping was performed.
     * @param scrapeResult The result of the scraping operation, containing successful results and failed files.
     */
    public ScrapeResultEvent(Object source, String drive, ScrapeResult scrapeResult) {
        super(source);
        this.drive = drive;
        this.scrapeResult = scrapeResult;
    }
}
