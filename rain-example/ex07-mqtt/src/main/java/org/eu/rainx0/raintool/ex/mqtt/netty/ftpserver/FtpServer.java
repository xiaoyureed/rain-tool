package org.eu.rainx0.raintool.ex.mqtt.netty.ftpserver;

/**
 * https://github.com/ddean2009/learn-netty4?tab=readme-ov-file
 *
 *
 *
 * sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
 *             @Override
 *             public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
 *                 if (total < 0) {
 *                     log.info(future.channel() + " 传输进度: " + progress);
 *                 } else {
 *                     log.info(future.channel() + " 传输进度: " + progress + " / " + total);
 *                 }
 *             }
 *
 *             @Override
 *             public void operationComplete(ChannelProgressiveFuture future) {
 *                 log.info(future.channel() + " 传输完毕.");
 *             }
 *         });
 * @author xiaoyu
 * @time 2025/8/8 16:07
 */
public class FtpServer {
    public static void main(String[] args) {

    }
}
