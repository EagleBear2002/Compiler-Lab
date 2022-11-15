import java.io.*;

public class Main {
	public static void main(String[] args) throws IOException {
		if (args.length != 1) return;
		
		String filename = args[0];
		File file = new File(filename);
		FileInputStream fileStream = new FileInputStream(file);
		InputStreamReader inReader = new InputStreamReader(fileStream, "UTF-8");
		BufferedReader bfReader = new BufferedReader(inReader);
		String contentLine = "";
		while ((contentLine = bfReader.readLine()) != null) {
			System.out.println(contentLine);
		}
	}
}