package cn.panda.channel;

import cn.panda.consumer.AbstractTerminatableThread;

import java.io.*;
import java.text.Normalizer;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * producer
 */
public class AttachmentProcessor {
    private final String ATTACHMENT_STORE_BASE_DIR = "D://channel";

    // producer-consumer Channel
    private final Channel<File> channel = new BlockingQueueChannel<File>(
            new ArrayBlockingQueue<File>(200));

    // producer-consumer Consumer
    private final AbstractTerminatableThread indexingThread = new
            AbstractTerminatableThread() {
                @Override
                protected void doRun() throws Exception {
                    File file = null;
                    file = channel.take();

                }
                private void indexFile(File file) throws Exception {
                    Random simuate = new Random();
                    try {
                        Thread.sleep(simuate.nextInt(100));
                    } catch (InterruptedException e) {

                    }
                }
            };
    public void init(){
        indexingThread.start();
    }
    public void shutdown(){
        indexingThread.terminate();
    }
    public void saveAttachment(InputStream in, String documentId,
                               String originalFileName) throws IOException {
    }
    private File saveAsFile(InputStream in, String documentId,
                            String originalFileName) throws IOException{
        String dirName = ATTACHMENT_STORE_BASE_DIR + documentId;
        File dir = new File(dirName);
        dir.mkdir();
        File file = new File(dirName + "/"
                + Normalizer.normalize(originalFileName, Normalizer.Form.NFC));

        // Avoid across a dirtory attack
        if(!dirName.equals(file.getCanonicalFile().getParent())){
            throw new SecurityException("Invalid originalFileName: " + originalFileName);
        }
        BufferedOutputStream bos = null;
        BufferedInputStream bis = new BufferedInputStream(in);
        byte[] buf = new byte[2048];
        int len = -1;
        try{
            bos = new BufferedOutputStream(new FileOutputStream(file));
            while((len = bis.read(buf)) > 0){
                bos.write(buf, 0, len);
            }
            bos.flush();
        }finally {
            try{
                if(bos != null){
                    bos.close();
                }
            }catch (IOException e){

            }
        }
        return file;
    }


}
