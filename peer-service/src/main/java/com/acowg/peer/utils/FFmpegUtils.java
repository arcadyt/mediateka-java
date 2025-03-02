package com.acowg.peer.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public final class FFmpegUtils {

    private FFmpegUtils() {
        // Private constructor to prevent instantiation
    }

    public static byte[] captureFrame(Path videoPath, int framePosition) throws IOException {
        if (!Files.exists(videoPath)) {
            throw new IOException("Video file does not exist: " + videoPath);
        }

        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-i");
        command.add(videoPath.toString());
        command.add("-ss");
        command.add(String.valueOf(framePosition));
        command.add("-frames:v");
        command.add("1");
        command.add("-q:v");
        command.add("2");
        command.add("-f");
        command.add("image2pipe"); // Output to stdout
        command.add("-"); // Output to stdout

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (var inputStream = process.getInputStream()) {
            // Read the output directly into a byte array
            byte[] frameData = inputStream.readAllBytes();

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("FFmpeg exited with code " + exitCode);
            }

            if (frameData.length == 0) {
                throw new IOException("Screenshot data was not captured");
            }

            return frameData;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("FFmpeg process interrupted", e);
        }
    }

    public static List<byte[]> captureMultipleFrames(Path videoPath, List<Integer> framePositions) throws IOException {
        if (!Files.exists(videoPath)) {
            throw new IOException("Video file does not exist: " + videoPath);
        }

        List<byte[]> frames = new ArrayList<>();
        Process process = null;

        try {
            // Start FFmpeg process
            List<String> command = new ArrayList<>();
            command.add("ffmpeg");
            command.add("-i");
            command.add(videoPath.toString());
            command.add("-f");
            command.add("image2pipe"); // Output to stdout
            command.add("-vf");
            command.add("fps=1"); // Capture 1 frame per second (adjust as needed)
            command.add("-vcodec");
            command.add("png"); // Use PNG for better quality
            command.add("-");

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();

            try (var inputStream = process.getInputStream()) {
                for (int framePosition : framePositions) {
                    // Seek to the desired frame position
                    seekToFrame(process, framePosition);

                    // Read the frame data from stdout
                    byte[] frameData = readFrameFromStream(inputStream);
                    frames.add(frameData);
                }
            }

            return frames;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    private static void seekToFrame(Process process, int framePosition) throws IOException {
        // Send a seek command to FFmpeg's stdin
        try (var outputStream = process.getOutputStream()) {
            String seekCommand = String.format("seek %d\n", framePosition);
            outputStream.write(seekCommand.getBytes());
            outputStream.flush();
        }
    }

    private static byte[] readFrameFromStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] temp = new byte[4096];
        int bytesRead;

        // Read until the end of the frame (assuming FFmpeg outputs frames as discrete chunks)
        while ((bytesRead = inputStream.read(temp)) != -1) {
            buffer.write(temp, 0, bytesRead);

            // Check if we've reached the end of the frame (e.g., by looking for a PNG end marker)
            if (isEndOfFrame(buffer.toByteArray())) {
                break;
            }
        }

        return buffer.toByteArray();
    }

    private static boolean isEndOfFrame(byte[] data) {
        // Check for the PNG end marker (IEND chunk)
        if (data.length >= 8) {
            byte[] pngEndMarker = new byte[]{
                    (byte) 0x49, (byte) 0x45, (byte) 0x4E, (byte) 0x44, // "IEND"
                    (byte) 0xAE, (byte) 0x42, (byte) 0x60, (byte) 0x82  // CRC
            };
            for (int i = 0; i < pngEndMarker.length; i++) {
                if (data[data.length - 8 + i] != pngEndMarker[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static double getVideoDuration(Path videoPath) throws IOException {
        if (!Files.exists(videoPath)) {
            throw new IOException("Video file does not exist: " + videoPath);
        }

        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-i");
        command.add(videoPath.toString());

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Duration:")) {
                    String duration = line.split("Duration:")[1].split(",")[0].trim();
                    return parseDuration(duration);
                }
            }
        }

        throw new IOException("Could not determine video duration");
    }

    private static double parseDuration(String duration) {
        String[] parts = duration.split(":");
        double hours = Double.parseDouble(parts[0]);
        double minutes = Double.parseDouble(parts[1]);
        double seconds = Double.parseDouble(parts[2]);
        return hours * 3600 + minutes * 60 + seconds;
    }

    public static int getTotalFrames(Path videoPath) throws IOException {
        if (!Files.exists(videoPath)) {
            throw new IOException("Video file does not exist: " + videoPath);
        }

        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-i");
        command.add(videoPath.toString());
        command.add("-map");
        command.add("0:v:0");
        command.add("-f");
        command.add("null");
        command.add("-");

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("frame=")) {
                    String frameCount = line.split("frame=")[1].split(" ")[0].trim();
                    return Integer.parseInt(frameCount);
                }
            }
        }

        throw new IOException("Could not determine total frames");
    }

    public static String getVideoCodec(Path videoPath) throws IOException {
        if (!Files.exists(videoPath)) {
            throw new IOException("Video file does not exist: " + videoPath);
        }

        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-i");
        command.add(videoPath.toString());

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Video:")) {
                    return line.split("Video:")[1].split(" ")[1].trim();
                }
            }
        }

        throw new IOException("Could not determine video codec");
    }

    public static String getAudioCodec(Path videoPath) throws IOException {
        if (!Files.exists(videoPath)) {
            throw new IOException("Video file does not exist: " + videoPath);
        }

        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-i");
        command.add(videoPath.toString());

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Audio:")) {
                    return line.split("Audio:")[1].split(" ")[1].trim();
                }
            }
        }

        throw new IOException("Could not determine audio codec");
    }

    public static String getVideoResolution(Path videoPath) throws IOException {
        if (!Files.exists(videoPath)) {
            throw new IOException("Video file does not exist: " + videoPath);
        }

        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-i");
        command.add(videoPath.toString());

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Video:")) {
                    Pattern pattern = Pattern.compile("\\d{2,5}x\\d{2,5}");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        return matcher.group();
                    }
                }
            }
        }

        throw new IOException("Could not determine video resolution");
    }
}