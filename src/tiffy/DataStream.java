package tiffy;

public abstract class DataStream {
		int a,b; //id 0:0
		String codec; //codec
				
		public abstract void print();
		
		DataStream(int _a, int _b, String _codec){
			a = _a; b = _b; codec = _codec;
		}
}
