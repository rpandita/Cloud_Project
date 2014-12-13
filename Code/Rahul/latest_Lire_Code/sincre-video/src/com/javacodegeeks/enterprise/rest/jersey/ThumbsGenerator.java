package com.javacodegeeks.enterprise.rest.jersey;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.Utils;

/**
 * @author Rahul
 *
 */
public class ThumbsGenerator {
	
	static int FRAME_INTERVAL = 5;
    static String inputVideoURL = "C:\\Users\\Rahul\\Desktop\\download\\";
    static String outputFrameLocation = "C:\\Users\\Rahul\\Desktop\\download\\frames\\";
    static int VideoStreamStartIndex = 4;
    static String currentVideoName = "";
    static long mLastPtsWrite = Global.NO_PTS;
    static int counter=0;

    public static final long MICRO_SECOND_INTERVAL = Global.DEFAULT_PTS_PER_SECOND * FRAME_INTERVAL;
	
    /**
	 * @return
	 */
	private static boolean createImageDirectory() {
		File theDir = new File(outputFrameLocation + currentVideoName);
		  if (!theDir.exists()) {
		    System.out.println("creating directory: " + currentVideoName);
		    boolean result = false;
		    try{
		        theDir.mkdir();
		        result = true;
		     } catch(SecurityException se){
		     }        
		     if(result) {    
		       System.out.println("DIR created");
		       return true;
		     }
		  }
		  return false;
	}
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
    	File videoFolder = new File(inputVideoURL);
		for (final File fileEntry : videoFolder.listFiles()) {
    		if(fileEntry.getName().equals("Thumbs.db")){
    			continue;
    		}
    		System.out.println("reading file" + fileEntry.getAbsolutePath());
    		currentVideoName = fileEntry.getName();
    		if(!createImageDirectory()){
    			continue;
    		}
    		try {
    			main1(inputVideoURL + "\\" + currentVideoName);
    		} catch (NumberFormatException | IOException e) {
    			// TODO Auto-generated catch block
    			//e.printStackTrace();
    			continue;
    		} catch (InterruptedException e) {		
    			// TODO Auto-generated catch block
    			//e.printStackTrace();
    			continue;
    		}
    		//break;
        }
		System.exit(0);
    }

	/**
	 * Write the video frame out to a PNG file. The files are written out to the
	 * system's temporary directory.
	 * 
	 * @param picture
	 *            the video frame which contains the time stamp.
	 * @param image
	 *            the buffered image to write out
	 */
	private static String dumpImageToFile(BufferedImage image) {
        try {
        	 String outputFilename = outputFrameLocation + currentVideoName +"\\" + System.currentTimeMillis() + ".jpg";
             ImageIO.write(image, "jpg", new File(outputFilename));
            return outputFilename;
        } 
        catch (Exception e) {
        	System.out.println("Error !");
        	return "Found Exception";
        }
    }
	
	/**
	 * @param picture
	 * @param image
	 */
	private static void processFrame(IVideoPicture picture, BufferedImage image) {
		try {
			dumpImageToFile(image);
			// indicate file written
			double seconds = ((double) picture.getPts())
					/ Global.DEFAULT_PTS_PER_SECOND;
			System.out.printf("at elapsed time of %6.3f seconds wrote: %s\n",
					seconds, "C:\\Users\\Rahul\\Desktop\\LireBasedCloudProject\\AllFrames\\" + "3.mp4"+ "\\" + System.currentTimeMillis() + ".jpg");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * generates frames for a given video and returns a single frame that lire can search
	 * @param path
	 * @param videoName
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public String generateFrames(String path,String videoName) throws NumberFormatException, IOException, InterruptedException{
		currentVideoName = videoName;
		createImageDirectory();
		File frameFolder = new File(outputFrameLocation + currentVideoName);
		main1(path);
		long count = frameFolder.listFiles().length;
		System.out.println(count);
		long i =0;
		String name = "";
		for (final File fileEntry : frameFolder.listFiles()) {
    		if(fileEntry.getName().equals("Thumbs.db")){
    			continue;
    		}
    		i++;
    		if(i == count/2){
    			name = fileEntry.getName();
    			System.out.println(name);
    			break;
    		}
		}	
		return name;
	}
	
	/**
	 * Takes a media container (file) as the first argument, opens it, reads
	 * through the file and captures a video framesThe frames are written as PNG
	 * files into the system's temporary directory.
	 * 
	 * @param args
	 *            must contain one string which represents a filename
	 * @throws IOException
	 * @throws NumberFormatException
	 * @throws InterruptedException 
	 */

	@SuppressWarnings("deprecation") 
	public static void main1(String video) throws NumberFormatException, 
	IOException, InterruptedException { 
		/*if (args.length <= 1) 
			throw new IllegalArgumentException( 
					"arguments needed: [fileName] [offsetForSnapshot]");*/ 

		String filename = video; 
		String seconds = "5"; 

		// make sure that we can actually convert video pixel formats 
		if (!IVideoResampler 
				.isSupported(IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION)) 
			throw new RuntimeException( "you must install the GPL version of Xuggler (with 	IVideoResampler" 
							+ " support) for this demo to work"); 

		
		
		IContainer container = IContainer.make();
		if (container.open(filename, IContainer.Type.READ, null) < 0) 
			throw new IllegalArgumentException("could not open file: " 
					+ filename); 

		// query how many streams the call to open found 
		int numStreams = container.getNumStreams(); 

		// and iterate through the streams to find the first video stream 
		int videoStreamId = -1; 
		IStreamCoder videoCoder = null; 
		for (int i = 0; i < numStreams; i++) { 
			// find the stream object 
			IStream stream = container.getStream(i); 
			// get the pre-configured decoder that can decode this stream; 
			IStreamCoder coder = stream.getStreamCoder(); 

			if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) { 
				videoStreamId = i; 
				videoCoder = coder; 
				break; 
			} 
		} 

		if (videoStreamId == -1) 
			throw new RuntimeException( 
					"could not find video stream in container: " + filename); 

		if (videoCoder.open() < 0) 
			throw new RuntimeException( 
					"could not open video decoder for container: " + filename); 

		IVideoResampler resampler = null; 
		if (videoCoder.getPixelType() != IPixelFormat.Type.BGR24) { 

			resampler = IVideoResampler.make(videoCoder.getWidth(), videoCoder 
					.getHeight(), IPixelFormat.Type.BGR24, videoCoder 
					.getWidth(), videoCoder.getHeight(), videoCoder 
					.getPixelType()); 
			if (resampler == null) 
				throw new RuntimeException( 
						"could not create color space resampler for: " 
								+ filename); 
		} 

		// We create a new packet. 
		IPacket packet = IPacket.make(); 

		// Let's Check the timeBase of this container 
		IRational timeBase = 
				container.getStream(videoStreamId).getTimeBase(); 

		// With the stream timebase we can calculate the timestamp 
		System.out.println("Timebase " + timeBase.toString()); 

		// Calculate the timeStamp offset 
		long timeStampOffset = (timeBase.getDenominator() / 
				timeBase.getNumerator()) 
				* Integer.parseInt(seconds); 
		System.out.println("TimeStampOffset " + timeStampOffset); 

		// we go directly to our target timestamp + startTime 
		long target = container.getStartTime() + timeStampOffset; 

		// Let's seek up to the target key frame 
		container.seekKeyFrame(videoStreamId, target, 0); 

		boolean isFinished = false; 
		int interval = 0;
		
		while(container.readNextPacket(packet) >= 0 && !isFinished ) { 
			
			// Now we have a packet, let's see if it belongs to our video stream 
			if (packet.getStreamIndex() == videoStreamId) { 
				// We allocate a new picture to get the data out of Xuggle 

				IVideoPicture picture = IVideoPicture.make(videoCoder 
						.getPixelType(), videoCoder.getWidth(), videoCoder 
						.getHeight()); 

				int offset = 0; 
				while (offset < packet.getSize()) { 
					// Now, we decode the video, checking for any errors. 

					int bytesDecoded = videoCoder.decodeVideo(picture, packet, 
							offset); 
					if (bytesDecoded < 0) { 
						System.err.println("WARNING!!! got no data decoding " + 
								"video in one packet"); 
					} 
					offset += bytesDecoded; 

					if (picture.isComplete()) { 

						IVideoPicture newPic = picture; 

						if (resampler != null) { 
							newPic = IVideoPicture.make(resampler 
									.getOutputPixelFormat(), picture.getWidth(), 
									picture.getHeight()); 
							if (resampler.resample(newPic, picture) < 0) 
								throw new RuntimeException( 
										"could not resample video from: " 
												+ filename); 
						} 

						if (newPic.getPixelType() != IPixelFormat.Type.BGR24) 
							throw new RuntimeException( 
									"could not decode video as BGR 24 bit data in: " 
											+ filename); 

						// convert the BGR24 to an Java buffered image 
						BufferedImage javaImage = Utils.videoPictureToImage(newPic); 

						// process the video frame 
						if(interval == 100){
							interval=0;
							processFrame(newPic, javaImage);
						}
						interval++;
						 
						
						//isFinished = true; 
					} 
				} 
			} 
		} 
		if (videoCoder != null) { 
			videoCoder.close(); 
			videoCoder = null; 
		} 
		if (container != null) { 
			container.close(); 
			container = null; 
		}
		}	
	}