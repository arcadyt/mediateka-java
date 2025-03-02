package com.acowg.peer.aspects;

import com.acowg.peer.services.locks.IDriveLockManager;
import com.acowg.peer.services.locks.RequiresDriveLock;
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
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class DriveLockAspect {

    private final IDriveLockManager driveLockManager;

    @Around("@annotation(com.acowg.peer.services.locks.RequiresDriveLock)")
    public Object lockDrivesAroundExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        RequiresDriveLock annotation = extractAnnotation(joinPoint);
        Set<String> drivesToLock = determineDrivesToLock(joinPoint, annotation);

        return executeWithLocks(joinPoint, drivesToLock);
    }

    private RequiresDriveLock extractAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(RequiresDriveLock.class);
    }

    private Set<String> determineDrivesToLock(ProceedingJoinPoint joinPoint, RequiresDriveLock annotation) {
        if (!annotation.driveParamName().isEmpty()) {
            return extractDrivesFromParam(joinPoint, annotation.driveParamName());
        } else {
            return extractDrivesFromPaths(joinPoint, annotation.pathParamName());
        }
    }

    private Set<String> extractDrivesFromParam(ProceedingJoinPoint joinPoint, String paramName) {
        Object driveParam = findNamedParameter(joinPoint, paramName);
        Set<String> drives = new HashSet<>();

        if (driveParam instanceof String) {
            drives.add((String) driveParam);
        } else if (driveParam instanceof Collection) {
            ((Collection<?>) driveParam).forEach(drive -> {
                if (drive instanceof String) {
                    drives.add((String) drive);
                }
            });
        }

        validateDrives(drives);
        return drives;
    }

    private Set<String> extractDrivesFromPaths(ProceedingJoinPoint joinPoint, String paramName) {
        Object pathParam = paramName.isEmpty()
                ? findFirstPathParameter(joinPoint)
                : findNamedParameter(joinPoint, paramName);

        if (pathParam == null) {
            throw new IllegalArgumentException("No valid path parameter found");
        }

        Set<String> drives;
        if (pathParam instanceof Path || pathParam instanceof File || pathParam instanceof String) {
            // Single path
            Path path = convertToPath(pathParam);
            drives = Collections.singleton(PathUtils.extractRootIdentifier(path));
        } else if (pathParam instanceof Collection) {
            // Collection of paths
            drives = extractDrivesFromPathCollection((Collection<?>) pathParam);
        } else {
            drives = Collections.emptySet();
        }

        validateDrives(drives);
        return drives;
    }

    private Set<String> extractDrivesFromPathCollection(Collection<?> paths) {
        return paths.stream()
                .filter(Objects::nonNull)
                .map(item -> {
                    try {
                        Path path = convertToPath(item);
                        return PathUtils.extractRootIdentifier(path);
                    } catch (IllegalArgumentException e) {
                        log.warn("Couldn't extract drive from collection item: {}", item);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
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

    private Object findNamedParameter(ProceedingJoinPoint joinPoint, String paramName) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i].equals(paramName)) {
                return args[i];
            }
        }

        throw new IllegalArgumentException("No parameter named '" + paramName + "' found");
    }

    private Object findFirstPathParameter(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Parameter[] parameters = signature.getMethod().getParameters();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameters.length; i++) {
            Class<?> type = parameters[i].getType();
            if (Path.class.isAssignableFrom(type) ||
                    File.class.isAssignableFrom(type) ||
                    String.class.isAssignableFrom(type) ||
                    Collection.class.isAssignableFrom(type)) {
                return args[i];
            }
        }

        return null;
    }

    private Path convertToPath(Object obj) {
        if (obj instanceof Path) {
            return (Path) obj;
        } else if (obj instanceof File) {
            return ((File) obj).toPath();
        } else if (obj instanceof String) {
            return Paths.get((String) obj);
        } else {
            throw new IllegalArgumentException(
                    "Cannot convert parameter of type " + obj.getClass().getName() + " to Path");
        }
    }
}