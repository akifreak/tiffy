package tiffy;

public class VideoStream extends DataStream {
		int x,y; //resolution
		
				
		private String rebef(String s, char a, char b){
			
			StringBuilder tmp = new StringBuilder();		
			boolean copy = true;		
			boolean first_one = true;
			boolean first_two = true;
		
			for (int i = 0; i < s.length(); ++i){
				
				if (s.charAt(i) == a){
					if(first_one){
						first_one = false;
						tmp.append(s.charAt(i));
					} else {
						copy = false;
					}
					continue;
				} else if(s.charAt(i) == b){
					if(first_two)  tmp.append(s.charAt(i));
					first_two = false; copy = true; continue;
				}
				if(copy) tmp.append(s.charAt(i));
			}
			
			return tmp.toString();
		}
		
		
		VideoStream(String key){
			super(-1,-1,"unknown");
			x = -1; y = -1;
			String key_m = rebef(key,'(',')');
			String[] parts = key_m.split(",");
			
			for (int i = 0; i < parts.length; ++i){
				parts[i] = parts[i].replaceAll("Video","");
				parts[i] = parts[i].replaceAll(" ","");
				if(parts[i].startsWith("Stream#")){
					String sub = parts[i].substring(7, parts[i].length());	
					String[] ab = sub.split(":");
				
					
					int fap = ab[1].indexOf("(");
					a = new Integer(ab[0]).intValue();
					if(fap != -1)
						b = new Integer(ab[1].substring(0, fap)).intValue();
					else 
						b = new Integer(ab[1]).intValue();

					String[] tmp = ab[3].split("\\(");
					codec = tmp[0];
				}
			}
			
			for (int i = 0; i < parts.length; ++i)
				parts[i] = parts[i].replaceAll(" ","");
			{
				String[] t = parts[2].split("\\[");
							
				x = new Integer(t[0].substring(0, t[0].indexOf('x') )).intValue();
				y = new Integer(t[0].substring(t[0].indexOf('x')+1, t[0].length() )).intValue();
			}
			//this.print(); System.out.println();
		}
		
		public void print(){
			System.out.println("VideoStream: "+a+":"+b);
			System.out.println("Resolution: "+x+"x"+y);
			System.out.println("Codec: "+codec);
		}


		int[] appearances(String s) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String representation() {
			String r = x+"x"+y+" "+codec;
			return r;
		}
}
