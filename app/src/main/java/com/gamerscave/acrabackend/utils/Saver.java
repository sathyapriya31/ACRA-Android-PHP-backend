package com.gamerscave.acrabackend.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

public class Saver {

	private static FileOutputStream fos;
	private static FileInputStream fis;
	public static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize, availableBlocks;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			blockSize = stat.getBlockSizeLong();
			availableBlocks = stat.getAvailableBlocksLong();
		} else {
			blockSize = stat.getBlockSize();
			availableBlocks = stat.getAvailableBlocks();
		}
		return availableBlocks * blockSize;
	}
	public static void save(String filename, String data, Context c){
		long memLeft = getAvailableInternalMemorySize();
		if(data != null && filename != null) {
			int bytes = filename.length() + data.length();
			//Log.e("MemLeft", "Memory left: " + memLeft + ". File Size: " + bytes);

			try {
				//Create the file if not exists
				File file = new File(c.getFilesDir() + "/", filename);
				if (file.getParentFile().mkdirs())
					file.createNewFile();
				/*else //LOGGING
					System.out.println("File exists");*/

				//Access file and write
				fos = c.openFileOutput(filename, Context.MODE_PRIVATE);
				fos.write(data.getBytes());

				fos.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			throw new NullPointerException("Filename and/or data is null");
		}
	}

	String file;
	public String getFile(){return file;}
	public void setFile(String loc){
		file = loc;
	}
	String result;
	private static String mainLoad(String fn, Context c){
		String collected = null;

		try{
			
			fis = c.openFileInput(fn);
			byte[] dataArray = new byte[fis.available()];
			while(fis.read(dataArray) != -1){
				collected = new String(dataArray);
			}
			return collected;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			try {
				fis.close();
			} catch(NullPointerException npe){
				return null;
			} catch (Exception e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
			}
		}
	}
	
	public static int loadInt(String fn, Context c){
		if(mainLoad(fn,c) == null) return -Integer.MAX_VALUE;
		else return Integer.parseInt(mainLoad(fn,c));
	}
	public static double loadDouble(String fn, Context c){
		if(mainLoad(fn,c) == null) return 0;
		else return Double.parseDouble(mainLoad(fn,c));
	}

	public static float loadFloat(String fn, Context c){
		return Float.parseFloat(mainLoad(fn,c));
	}

	public static String loadString(String fn, Context c){
		return mainLoad(fn, c);
	}
	
	public static boolean loadBoolean(String fn, Context c){
		if(mainLoad(fn,c) == null) return false;
		else return Boolean.parseBoolean(mainLoad(fn,c));
	}

	public static BigInteger loadBigInteger(String fn, Context c){
		return new BigInteger(mainLoad(fn,c));
	}

	public static BigDecimal loadBigDecimal(String fn, Context c){
		return new BigDecimal(mainLoad(fn,c));
	}
	public static long loadLong(String fn, Context c){
		return Long.parseLong(mainLoad(fn, c));
	}
}

