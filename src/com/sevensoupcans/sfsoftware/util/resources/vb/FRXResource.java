package com.sevensoupcans.sfsoftware.util.resources.vb;

import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public final class FRXResource 
{
	private String fileName;
	private String fileExtension;
	private String frxFile;
	private String frxFilePath;
	private long fileOffset;
	private int fileSize;
	
	public FRXResource(String fileName, String fileExtension, String parentFile, String parentFilePath, long fileOffset, int fileSize)
	{
		this.fileName = fileName;
		this.fileExtension = fileExtension.toLowerCase();
		this.frxFile = parentFile;
		this.frxFilePath = parentFilePath;
		this.fileOffset = fileOffset;
		this.fileSize = fileSize;
	}
	
	public DataOutputStream extractToFile() throws IOException
	{
		return extractToFile(getFilename());
	}
	
	public DataOutputStream extractToFile(String filename) throws IOException
	{
		DataOutputStream newFile = new DataOutputStream(new FileOutputStream(filename));						
		newFile.write(getFileContents());									
		newFile.close();
		
		return newFile;
	}
	
	public byte[] getFileContents() throws IOException
	{
		byte[] contents = new byte[fileSize];
		RandomAccessFile file = new RandomAccessFile(frxFilePath + frxFile, "r");

		// 12 bytes are reserved for the file header
		file.seek(fileOffset + 12);
		for(int i = 1; i <= fileSize; i++)
		{					
			try 
			{
				contents[i - 1] = file.readByte();
			}
			catch(EOFException e) 
			{
				System.out.println("The specified file length " + fileSize + " from offset "
						+ fileOffset + " exceeds EOF.");
			}
		}	
		
		file.close();		
		return contents;
	}
	
	public String getFilename()
	{
		return frxFile.concat("_").concat(fileName).concat(".").concat(fileExtension);
	}
	
	@Override
	public String toString()
	{
		return fileName + ", " + frxFile + ", " + fileOffset + ", " + fileSize;		
	}	
}
