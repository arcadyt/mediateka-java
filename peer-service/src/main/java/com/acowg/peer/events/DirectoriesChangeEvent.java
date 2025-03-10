package com.acowg.peer.events;

import com.acowg.peer.entities.DirectoryEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DirectoriesChangeEvent extends ApplicationEvent {
    private final DirectoryEntity directory;
    private final ChangeType changeType;
    
    public enum ChangeType {
        CREATED, UPDATED, DELETED
    }
    
    public DirectoriesChangeEvent(Object source, DirectoryEntity directory, ChangeType changeType) {
        super(source);
        this.directory = directory;
        this.changeType = changeType;
    }
}