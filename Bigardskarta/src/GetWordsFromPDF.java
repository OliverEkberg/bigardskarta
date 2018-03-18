


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;



public class GetWordsFromPDF extends PDFTextStripper{


	public static String replaceChars(String in, char toReplace, char replaceWith) {

		char[] charArray = in.toCharArray();

		for(int i = 0; i < charArray.length; i++) {
			if(charArray[i] == toReplace) {
				charArray[i] = replaceWith;
			}
		}
		return new String(charArray);
	}




	public GetWordsFromPDF() throws IOException {
		super();
	}

	public static void main(String args[]) throws IOException {

		ArrayList<Point> points = new ArrayList<>();
		ArrayList<String> badRows = new ArrayList<>();

		PDFParser parser = null;
		PDDocument pdDoc = null;
		COSDocument cosDoc = null;
		PDFTextStripper pdfStripper;

		String parsedText;

		File file = new File("/Users/oliverekberg/Desktop/bigårdar.pdf");

		if(file.exists()) {

			try {
				parser = new PDFParser(new RandomAccessFile(file, "r"));
				parser.parse();
				cosDoc = parser.getDocument();
				pdfStripper = new PDFTextStripper();
				pdDoc = new PDDocument(cosDoc);
				pdfStripper.setSortByPosition(true);
				parsedText = pdfStripper.getText(pdDoc);

				/*
				 * Debugging
				 */
				System.out.println(parsedText);

				//Splits the raw data into lines
				String lines[] = parsedText.split("\\r?\\n");


				for(int i = 0; i< lines.length-3; i++) {

					String line = lines[i];
					
					//If the line starts with these it will be ignored
					if(line.charAt(0) == '0' || line.charAt(0) == 'O') {
						continue;
					}

					String firstname = "";
					String surname = "";
					String longitude = "";
					String latitude = "";
					String address = "";
					String telephone = "";


					//Finds firstname and surname
					Pattern pattern = Pattern.compile("[abcdefghijklmnopqrstuvwxyzåäöABCDEFGHIJKLMOPQRSTUVWXYZÅÄÖ-]*\\s");
					Matcher matcher = pattern.matcher(line);

					for(int t = 0; matcher.find(); t++){
						if(t == 0)
							firstname = matcher.group();
						if(t == 1) 
							surname = matcher.group();
						if(t == 2)
							break;
					}


					//Finds the coordinates
					pattern = Pattern.compile("\\d{2}[.,]\\d{3}[—]?\\d{0,3}");
					matcher = pattern.matcher(line);

					for(int t = 0; matcher.find(); t++){
						if(t == 0)
							longitude = matcher.group();
						if(t == 1) 
							latitude = matcher.group();
						if(t == 2) 
							break;
					}

					//Finds the adress
					pattern = Pattern.compile("[abcdefghijklmnopqrstuvwxyzåäöABCDEFGHIJKLMOPQRSTUVWXYZÅÄÖ-]*\\s[123456789]\\d*\\s");
					matcher = pattern.matcher(line);

					for(int t = 0; matcher.find(); t++) {
						if(t == 0)
							address = matcher.group();
						if(t == 1)
							break;
					}




					//Formats the coordinates
					longitude = replaceChars(longitude, ',', '.');
					latitude = replaceChars(latitude, ',', '.');


					Double longitudeD = 1.0;
					Double latitudeD = 1.0;

					try {
						longitudeD = Double.parseDouble(longitude);
						latitudeD = Double.parseDouble(latitude);
					}catch (Exception e){

					}



					//Filters out bad points
					if(latitudeD >= 55.336733 && latitudeD <= 57.7217526 && longitudeD >= 11.552124 && longitudeD <= 17.2705078) {

						points.add(new Point(firstname, surname, longitude, latitude, address, telephone));

					}else {

						badRows.add(line);

					}



				}



			} catch (Exception e) {
				e.printStackTrace();
				try {
					if (cosDoc != null)
						cosDoc.close();
					if (pdDoc != null)
						pdDoc.close();
				} catch (Exception e1) {
					e.printStackTrace();
				}

			}



		}

		File csvFile = new File("bigårdar.csv");

		if (csvFile.exists()){
			csvFile.delete();
		}  

		FileWriter fw = new FileWriter(csvFile,true);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter pw = new PrintWriter(bw);
		pw.println("Longitude, Latitude, Namn, Telefon, Adress");

		for(Point point : points) {
			pw.println(point);
		}

		pw.flush();
		pw.close();

		System.out.println("\n");
		System.out.println("\n");
		System.out.println("\n");
		System.out.println("\n");
		System.out.println("BAD ROWS: ");
		System.out.println("\n");

		for(int i = 0; i < badRows.size(); i++) {
			System.out.println(badRows.get(i));
		}
		//Kommentar


	}



}