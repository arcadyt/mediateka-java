package com.acowg.peer.handlers;

import com.acowg.peer.services.locks.Drive;
import com.acowg.peer.services.locks.RequiresDriveLock;
import com.acowg.peer.utils.FFmpegUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ScreenshotServiceImpl implements IScreenshotService {

    /**
     * Capture screenshots across video duration
     *
     * @param videoPath Path to the video file
     * @param quantity  Number of screenshots to capture
     * @return List of screenshot byte arrays
     */
    @RequiresDriveLock
    @Override
    public List<byte[]> captureScreenshots(@Drive Path videoPath, int quantity) throws IOException {
        // Total intervals: quantity + 2 to create even intervals, skipping first and last
        int totalIntervals = quantity + 2;

        // Use FFmpegUtils to get video duration and total frames
        int totalFrames = FFmpegUtils.getTotalFrames(videoPath);

        // Calculate frame positions for screenshots
        List<Integer> framePositions = new ArrayList<>();
        for (int i = 1; i <= quantity; i++) {
            double intervalPosition = (double) i / (totalIntervals + 1);
            int frameNumber = (int) (intervalPosition * totalFrames);
            framePositions.add(frameNumber);
        }

        // Capture all frames in one go using FFmpeg
        return FFmpegUtils.captureMultipleFrames(videoPath, framePositions);
    }

    /**
     * Capture a single frame from a video
     *
     * @param videoPath    Path to the video file
     * @param frameNumber  Frame number to capture
     * @return             Byte array containing the screenshot data
     */
    @RequiresDriveLock
    @Override
    public byte[] captureFrame(@Drive Path videoPath, int frameNumber) throws IOException {
        return FFmpegUtils.captureFrame(videoPath, frameNumber);
    }
}