package download;

public class XAPKFile {
	public int FileVersion;
	public long size;
	public boolean mainFile;
	
	public XAPKFile(int FileVersion, long size, boolean mainFile){
		this.FileVersion = FileVersion;
		this.size = size;
		this.mainFile = mainFile;
	}
	
}
