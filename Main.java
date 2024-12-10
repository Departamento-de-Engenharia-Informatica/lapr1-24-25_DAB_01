import org.la4j.Matrix;
import org.la4j.matrix.sparse.CRSMatrix;
import org.la4j.*;
import java.io.*;
import	java.util.Scanner;
import	java.lang.Object;

public class Main{
	public static final int MIN_OWN_VALUE = -1;

	public static final int MIN_TYPE_EXEC = 0;
	public static final int MAX_TYPE_EXEC = 3;

	public static final Scanner input = new Scanner(System.in);

	public static void main(String[] args)
	{
		CSVtoMatrix("test.csv");

			// Print the matrix
/*		if (args.length > 0)
			NonIterative(args);
		else
			Iterative(); */
	}

	public static void NonIterative(String[] arguments)
	{
	}

	public static void Iterative()
	{
		int		type;
		int		own_values;
		String	path;

		type = TypeOfExecution();
		own_values = OwnValues();
		path = FilePath();
	}

	//=========Prints & Scan=========//
	public static int TypeOfExecution()
	{
		int	read;

		read = 100;
		System.out.printf("Enter the type of execution:\n");
		System.out.println("(0) Exit Program");
		System.out.println("(1) Decomposition of images");
		System.out.println("(2) Rebuild images");
		System.out.println("(3) Identify closest");

		while(!(read <= MAX_TYPE_EXEC && read >= MIN_TYPE_EXEC))
		{
			read = input.nextInt();
			if (!(read <= 3 && read > 0))
				System.out.println("You've entered a wrong value. Try it again: ");
		}
		return (read);
	}

	public static int OwnValues()
	{
		int	read;

		System.out.printf("Enter the own value(columns):\n");
		read = input.nextInt();
		input.nextLine();
		return read;
	}

	public static String FilePath()
	{
		String	path;

		System.out.printf("Enter the file PATH of execution:\n");
		path = input.nextLine();
		return (path);
	}

	public static CRSMatrix CSVtoMatrix(String filename)
	{
		String	all_csv;
		double[][] csv;

		csv = ReadingCsv(filename);
		if(csv == null)
		{
			System.out.println("File does not exist!");
			return (null);
		}
		CRSMatrix matrix = new CRSMatrix(csv.length, csv[0].length);

		for (int i = 0; i < csv.length; i++) {
			for (int j = 0; j < csv[i].length; j++) {
				if (csv[i][j] != 0.0) {
					matrix.set(i, j, csv[i][j]);
				}
			}
		}
		return (matrix);
	}

	public static double[][] ReadingCsv(String filename)
	{
		File		file = new File(filename);
		Scanner		ReadFile;
		String		regex = "[,]";
		String[]	Csv;
		double[][]	toReturn;
		int			length;
		int			noOfLines;
		int			j;

		j = 0;
		try {
			ReadFile = new Scanner(file);
		} catch (FileNotFoundException e) {
			return null;
		}
		noOfLines = GetNumLines(file);

		//System.out.println(noOfLines);
		// alocar memoria para CSV, falta só pegar a largura a ser alocada

		toReturn = new double[4][5];
		while (ReadFile.hasNextLine())
		{
			Csv = ReadFile.nextLine().split(regex);
			for (int i = 0; i < 5; i++)
				toReturn[j][i] = Double.parseDouble(Csv[i]);
			j++;
		}
		for (int i = 0; i < 4; i++) {
			for (int k = 0; k < 5; k++) {
				System.out.printf("%.1f ", toReturn[i][k]);
			}
			System.out.println();
		}
		return (null);
	}

	public static int GetNumLines(File file)
	{
		int			noOfLines;

		noOfLines = -1;
		try (LineNumberReader	lnr = new LineNumberReader(new FileReader(file))){
			lnr.skip(Long.MAX_VALUE);
			noOfLines = lnr.getLineNumber();
		} catch (IOException e) {
			System.err.println("An I/O error occurred");
			return (noOfLines);
		}
		return (noOfLines);
	}

	public static String[][] ReadingDir()
	{
		return null;
	}

	//==================Functionalities==================//
	//=========1=========//
	public static void Decomposition()
	{

	}

	//=========2=========//
	public static void Recomposition()
	{

	}

	//=========3=========//
	public static void SearchClosest()
	{

	}

}

