package tiffy;

public class VideoStream extends DataStream {
		int x,y; //resolution
		
		VideoStream(String key){
			super(-1,-1,"unknown");
			x = -1; y = -1;
		}
		
		public void print(){
			System.out.println("VideoStream #"+a+":"+b);
			System.out.println("Resolution: "+x+"x"+y);
			System.out.println("Codec: "+codec);
		}


		int[] appearances(String s) {
			// TODO Auto-generated method stub
			return null;
		}
}
