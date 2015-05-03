package tiffy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class Settings {

	public static String[] getSetting(String file, String code) throws IOException{
		
		ArrayList<String> r = new ArrayList<String>();
		InputStream fis = null;

		fis = new FileInputStream(file);

		InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(isr);
		String line;
		String regex = "\\s*\\"+code+"\\b\\s*";
		while ((line = br.readLine()) != null) {
			if (line.startsWith(code)) {
				line = line.replaceFirst(regex,"");
				r.add(line);
			} 
		}

		String[] res = new String[r.size()];
		for (int i = 0; i < r.size(); ++i){
			res[i] = r.get(i);
		}
		
		return res;
	}
	
	public static void changeSetting(String file, String code, String option){
		String line;
		ArrayList<String> file_list = new ArrayList<String>();
		InputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
			return;
		}
		InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(isr);
		boolean code_found = false;
		try {
			while ((line = br.readLine()) != null) {
				if (!code_found && line.startsWith(code)) {
					file_list.add(code+option);
					code_found = true;
				} else {
					file_list.add(line);
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		Writer output = null;
		try {
			output = new BufferedWriter(new FileWriter(file));
			
			for(int i = 0; i < file_list.size();++i){
				output.append(file_list.get(i)+"\n");
			}
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} 
	}
	
	public static void appendSetting(String file, String code, String option){
		String line;
		ArrayList<String> file_list = new ArrayList<String>();
		
		System.out.println("appending "+code+option);
		
		InputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
			return;
		}
		InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(isr);
		try {
			while ((line = br.readLine()) != null) {
				file_list.add(line);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		Writer output = null;
		try {
			output = new BufferedWriter(new FileWriter(file));
			
			for(int i = 0; i < file_list.size();++i){
				output.append(file_list.get(i)+"\n");
			}
			output.append(code+option);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} 
	}
	
}
