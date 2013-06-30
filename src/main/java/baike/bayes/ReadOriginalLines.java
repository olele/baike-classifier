package baike.bayes;
import java.util.*;
import java.io.*;
public class ReadOriginalLines {
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader( new FileReader( "D:\\baike\\baidu-dump.dat" ) );
		BufferedWriter bw = new BufferedWriter( new FileWriter( "D:\\baike\\baidu-dump.dat.50" ));
		for ( int i=0; i<50000; i++ ){
			bw.write(br.readLine());
			bw.newLine();
		}
		br.close();
		bw.close();
	}
}
