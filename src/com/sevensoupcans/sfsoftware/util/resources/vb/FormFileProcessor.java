package com.sevensoupcans.sfsoftware.util.resources.vb;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class FormFileProcessor {

	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
	
	public static void main(String[] args) {
		
		if(args.length < 2)
		{
			System.out.print("At least 2 arguments are required - an extraction path and files to extract.");
			System.exit(1);
		}
		
		for(int i = 1; i < args.length; i++)
		{
			ArrayList<FRXResource> vbr = getFormResourceList(args[i]);
			extractResources(args[0], vbr);	
		}
	}
	
	public static void extractResources(String path, ArrayList<FRXResource> resources)
	{
		for(FRXResource vbr : resources)
		{
			try 
			{
				vbr.extractToFile(path + vbr.getFilename());
				System.out.println("Extracted \"" + vbr.getFilename() + "\"");
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public static ArrayList<FRXResource> getFormResourceList(String formFile)
	{
		String path = formFile.substring(0, formFile.lastIndexOf('/') + 1);
		System.out.println(path);
		
		ArrayList<FRXResource> resourceList = new ArrayList<FRXResource>();
		
		ArrayList<String> usedNames = new ArrayList<String>();
		BufferedReader br;
		try 
		{
			br = new BufferedReader(new FileReader(formFile));
			
			String currentImageName = null;
			
			for (String line = br.readLine(); line != null; line = br.readLine()) 
			{
				if(currentImageName != null) 
				{					
					if(line.toLowerCase().contains("picture = ") && !(line.toLowerCase().contains("me.picture = ")))
					{
						try
						{
							String binaryFile = line.substring(line.toLowerCase().indexOf("\"") + 1, line.indexOf(":") - 1);
							long fileOffset = Long.valueOf(hexToDec(line.substring(line.indexOf(":") + 1)));
							
							RandomAccessFile file = new RandomAccessFile(path + binaryFile, "r");							
							
							// First 12 bytes contain the file header
							byte [] headerBytes = new byte[12];
							file.seek(Long.valueOf(fileOffset));
							
							for(int i = 11; i >= 0 ; i--)
							{
								headerBytes[i] = file.readByte();							
							}
							
							int fileSize = hexToDec(bytesToHex(headerBytes));
							
							String fileType = "";
							try 
							{
								fileType = FRXResource.getFileType(file.readByte());
							} 
							catch (UnknownFRXFileTypeByteException e) 
							{						
								System.out.println(e.getMessage());
							}
																			
							FRXResource vbr = new FRXResource(currentImageName, fileType, binaryFile, path, fileOffset, fileSize);
							resourceList.add(vbr);
							file.close();
							currentImageName = null;							
						}
						catch(FileNotFoundException e)
						{
							System.out.println("Filename parsing failed for line: " + line);
						}
					}
				}
				else if(line.toLowerCase().contains("begin image ") || line.toLowerCase().contains("begin picturebox "))
				{
					currentImageName = line.substring(line.toLowerCase().lastIndexOf(" ") + 1);

					// Prevents duplicate name entries from existing.
					int j = 1;
					while(usedNames.contains(currentImageName))
					{						
						currentImageName = line.substring(line.toLowerCase().lastIndexOf(" ") + 1) + "_" + j;
						j++;
					}
					usedNames.add(currentImageName);
				}				
			}	
			
			br.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return resourceList;
	}
	
	private static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}	
	
	private static int hexToDec(String s) 
	{
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }
        return val;
    }
}
