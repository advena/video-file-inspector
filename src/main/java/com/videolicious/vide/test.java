package com.videolicious.vide;

import java.io.IOException;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

public class test {


    public static void main(String[] args) throws IOException {
        FFprobe fFprobe = new FFprobe("/home/advena/Videos/free-solo.mp4");
        FFmpegProbeResult result = fFprobe.probe("/home/advena/Videos/out.mp4");
        result.getStreams()
            .forEach(
                fFmpegStream ->
                    System.out.println(fFmpegStream)
            );
    }
}
