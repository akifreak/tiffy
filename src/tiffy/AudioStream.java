package tiffy;

public class AudioStream extends DataStream {
	String lang; //language string
	int bitrate; //bitrate in kb/s
	
	AudioStream(String key) {
		super(-1,-1,"unknown");
		lang = "unknown"; bitrate = -1;
		String[] parts = key.split(",");
		for (int i = 0; i < parts.length; ++i){
			parts[i] = parts[i].replaceAll("Audio","");
			parts[i] = parts[i].replaceAll(" ","");
			if(parts[i].startsWith("Stream#")){
				String sub = parts[i].substring(7, parts[i].length());	
				String[] ab = sub.split(":");
								
				int fap = ab[1].indexOf("(");
				int lap = ab[1].indexOf(")");

				a = new Integer(ab[0]).intValue();
				b = new Integer(ab[1].substring(0, fap)).intValue();
				lang = ab[1].substring(fap+1, lap);

				String[] tmp = ab[3].split("\\(");
				codec = tmp[0];
			} else if (parts[i].contains("kb/s")) {
				String[] tmp = parts[i].split("kb/s");
				bitrate = new Integer(tmp[0]).intValue();
			}
		}
		//this.print(); System.out.println();
	}
	
	public void print(){
		System.out.println("AudioStream: "+a+":"+b);
		System.out.println("Language: "+lang);
		System.out.println("Codec: "+codec);
		System.out.println("Bitrate: "+bitrate);
	}

	int[] appearances(String s, char c) {
		int cnt = 0;
		for (int i = 0; i < s.length(); ++i){
			if(s.charAt(i) == c) cnt++;
		} int[] res = new int[cnt];
		cnt = 0;
		for (int i = 0; i < s.length(); ++i){
			if(s.charAt(i) == c) res[cnt++]=i;
		}
		return res;
	}
}
