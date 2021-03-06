package tiffy;

public class SubtitleStream extends DataStream {

	String lang;
	
	SubtitleStream(String key) {
		super(-1,-1,"unknown");
		lang = "unknown";
		String[] parts = key.split(",");
		
		for (int i = 0; i < parts.length; ++i){
			parts[i] = parts[i].replaceAll("Subtitle","");
			parts[i] = parts[i].replaceAll(" ","");
			if(parts[i].startsWith("Stream#")){
				String sub = parts[i].substring(7, parts[i].length());	
				String[] ab = sub.split(":");
								
				int fap = ab[1].indexOf("(");
				int lap = ab[1].indexOf(")");

				a = new Integer(ab[0]).intValue();
				if(fap != -1){
					b = new Integer(ab[1].substring(0, fap)).intValue();
					lang = ab[1].substring(fap+1, lap);
				}
				else 
					b = new Integer(ab[1]).intValue();
				
				String[] tmp = ab[3].split("\\(");
				codec = tmp[0];
			}
		}
		
		//this.print();  System.out.println();

	}

	@Override
	public void print() {
		System.out.println("SubtitleStream: "+a+":"+b);
		System.out.println("Language: "+lang);
	}

	@Override
	public String representation() {
		String r = lang;
		return r;
	}

}
