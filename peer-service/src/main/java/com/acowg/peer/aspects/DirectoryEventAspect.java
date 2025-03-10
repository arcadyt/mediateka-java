package com.acowg.peer.aspects;

import com.acowg.peer.entities.DirectoryEntity;
import com.acowg.peer.events.DirectoriesChangeEvent;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DirectoryEventAspect {
    
    private final ApplicationEventPublisher eventPublisher;
    
    public DirectoryEventAspect(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    @AfterReturning(
        pointcut = "execution(* com.acowg.peer.services.catalog.DirectoryService.createDirectory(..))", 
        returning = "result")
    public void afterDirectoryCreation(JoinPoint joinPoint, Object result) {
        if (result instanceof DirectoryEntity) {
            DirectoryEntity directory = (DirectoryEntity) result;
            eventPublisher.publishEvent(
                new DirectoriesChangeEvent(this, directory, DirectoriesChangeEvent.ChangeType.CREATED));
        }
    }
    
    @AfterReturning(
        pointcut = "execution(* com.acowg.peer.services.catalog.DirectoryService.updateDirectory(..))",
        returning = "result")
    public void afterDirectoryUpdate(JoinPoint joinPoint, Object result) {
        if (result instanceof DirectoryEntity) {
            DirectoryEntity directory = (DirectoryEntity) result;
            eventPublisher.publishEvent(
                new DirectoriesChangeEvent(this, directory, DirectoriesChangeEvent.ChangeType.UPDATED));
        }
    }
    
    @AfterReturning(
        pointcut = "execution(* com.acowg.peer.services.catalog.DirectoryService.deleteDirectory(..))")
    public void afterDirectoryDeletion(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof DirectoryEntity) {
            DirectoryEntity directory = (DirectoryEntity) args[0];
            eventPublisher.publishEvent(
                new DirectoriesChangeEvent(this, directory, DirectoriesChangeEvent.ChangeType.DELETED));
        }
    }
}