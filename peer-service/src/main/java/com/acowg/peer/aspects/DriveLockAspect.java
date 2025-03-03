package com.acowg.peer.aspects;

import com.acowg.peer.services.locks.Drive;
import com.acowg.peer.services.locks.IDriveLockManager;
import com.acowg.peer.utils.PathUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Semaphore;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class DriveLockAspect {

    private final IDriveLockManager driveLockManager;

    @Around("@annotation(com.acowg.peer.services.locks.RequiresDriveLock)")
    public Object lockDrivesAroundExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        Set<String> drivesToLock = findDriveParametersToLock(joinPoint);
        return executeWithLocks(joinPoint, drivesToLock);
    }

    private Set<String> findDriveParametersToLock(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();

        Set<String> drives = new HashSet<>();

        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(Drive.class)) {
                Object arg = args[i];
                try {
                    Path path = convertToPath(arg);
                    String drive = PathUtils.extractRootIdentifier(path);
                    drives.add(drive);
                } catch (IllegalArgumentException e) {
                    log.warn("Couldn't extract drive from parameter: {}", arg);
                }
            }
        }

        validateDrives(drives);
        return drives;
    }

    private void validateDrives(Set<String> drives) {
        if (drives.isEmpty()) {
            throw new IllegalStateException("No drives to lock determined from method parameters");
        }
    }

    private Object executeWithLocks(ProceedingJoinPoint joinPoint, Set<String> drivesToLock) throws Throwable {
        List<Semaphore> acquiredLocks = new ArrayList<>();

        try {
            acquireLocks(drivesToLock, acquiredLocks);
            return joinPoint.proceed();
        } finally {
            releaseLocks(acquiredLocks);
        }
    }

    private void acquireLocks(Set<String> drivesToLock, List<Semaphore> acquiredLocks) throws InterruptedException {
        for (String drive : drivesToLock) {
            Semaphore lock = driveLockManager.getLockForDrive(drive);
            if (lock == null) {
                releaseLocks(acquiredLocks); // Release any acquired locks
                throw new IllegalStateException("No lock found for drive: " + drive);
            }
            lock.acquire();
            acquiredLocks.add(lock);
            log.debug("Acquired lock for drive: {}", drive);
        }
    }

    private void releaseLocks(List<Semaphore> acquiredLocks) {
        acquiredLocks.forEach(lock -> {
            lock.release();
            log.debug("Released lock for drive");
        });
    }

    private Path convertToPath(Object obj) {
        if (obj instanceof Path) {
            return (Path) obj;
        } else if (obj instanceof File) {
            return ((File) obj).toPath();
        } else if (obj instanceof String) {
            return Paths.get((String) obj);
        } else if (obj instanceof Collection) {
            // Handle collections - just use the first path element
            Collection<?> collection = (Collection<?>) obj;
            if (!collection.isEmpty()) {
                return convertToPath(collection.iterator().next());
            }
        }

        throw new IllegalArgumentException(
                "Cannot convert parameter of type " + obj.getClass().getName() + " to Path");
    }
}