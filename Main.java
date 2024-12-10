import org.la4j.matrix.sparse.CRSMatrix;
import org.la4j.*;
import java.io.*;
import	java.util.Scanner;
import	java.lang.Object;

// equals -> compare matrixes

public class Main{
	public static final int MIN_OWN_VALUE = -1;

	public static final int MIN_TYPE_EXEC = 0;
	public static final int MAX_TYPE_EXEC = 3;

	public static final Scanner input = new Scanner(System.in);

	public static void main(String[] args)
	{
		if (args.length > 0)
			NonIterative(args);	
		else
			Iterative();
	}

	// compilation == java -jar nome programa.jar -f X -k Y -i Z -d W
	public static void NonIterative(String[] arguments)
	{
		int			type;
		int			own_values;
		String		path;
		String		dirPath;

		if (!(CheckingArgs(arguments)))
		{
			System.out.println("You've entered some wrong argument!! Check it and try again!!");
			return ;
		}
		type = Integer.parseInt(arguments[1]);
		own_values = Integer.parseInt(arguments[3]);
		switch (type) {
			case 1:
				path = arguments[6];
				Decomposition(own_values, path);
			case 2:
				dirPath = arguments[8];
				Recomposition(own_values, dirPath);
			case 3:
				path = arguments[6];
				dirPath = arguments[8];
				SearchClosest(own_values, path, dirPath);
		}
	}

	public static boolean CheckingArgs(String[] arguments)
	{
		if (arguments.length != 8)
			return (false);
		if(!(arguments[0].equals("-f")))
			return (false);
		if(!(arguments[2].equals("-k")))
			return (false);
		if(!(arguments[4].equals("-i")))
			return (false);
		if(!(arguments[6].equals("-d")))
			return (false);
		return (true);
	}

	//=========Iterative exec and reading=========//
	public static void Iterative()
	{
		int			type;
		int			own_values;
		String		path;
		String		dirPath;
		CRSMatrix	matrix;

		type = 1;
		while(type != 0)
		{
			type = TypeOfExecution();
			if (type == 0)
				break ;
			own_values = OwnValues();
			switch (type) {
				case 1:
					path = GetPath("Enter the file PATH of execution:\n");
					Decomposition(own_values, path);
					break;
				case 2:
					dirPath = GetPath("Enter the dir PATH of execution:\n");
					Recomposition(own_values, dirPath);
					break;
				case 3:
					path = GetPath("Enter the file PATH of execution:\n");
					dirPath = GetPath("Enter the dir PATH of execution:\n");
					SearchClosest(own_values, path, dirPath);
					break;
			}
		}
		System.out.println("Program ending... Thanks for using it!!");
	}

	public static int TypeOfExecution()
	{
		int	read;

		read = 100;
		System.out.println("----------------------------------");
		System.out.printf("Enter the type of execution:\n");
		System.out.println("(0) Exit Program");
		System.out.println("(1) Decomposition of images");
		System.out.println("(2) Rebuild images");
		System.out.println("(3) Identify closest");

		while(!(read <= MAX_TYPE_EXEC && read >= MIN_TYPE_EXEC))
		{
			read = input.nextInt();
			if (!(read <= 3 && read >= 0))
				System.out.println("You've entered a wrong value. Try it again: ");
		}
		return (read);
	}

	public static int OwnValues()
	{
		int	read;

		System.out.println("----------------------------------");
		System.out.printf("Enter the number of own values that you want to use(columns):\n");
		read = input.nextInt();
		input.nextLine();
		return read;
	}

	public static String GetPath(String searching)
	{
		String	path;

		System.out.println("----------------------------------");
		System.out.printf("%s", searching);
		path = input.nextLine();
		return (path);
	}

	//=========Matrix Read=========//
	public static CRSMatrix CSVtoMatrix(String filename)
	{
		double[][]	csv;
		CRSMatrix	matrix;

		csv = ReadingCsv(filename);
		if(csv == null)
		{
			System.out.println("File does not exist!");
			return (null);
		}

		matrix = new CRSMatrix(csv.length, csv[0].length);
		for (int i = 0; i < csv.length; i++) {
			for (int j = 0; j < csv[i].length; j++) {
				if (csv[i][j] != 0.0) {
					matrix.set(i, j, csv[i][j]);
				}
			}
		}
		return (matrix);
	}

	// Return (the double matrix) if it has something in the file, (null) if it has nothing
	public static double[][] ReadingCsv(String filename)
	{
		File		file = new File(filename);
		Scanner		ReadFile;
		String		regex = "[,]";
		String[]	Csv;
		double[][]	toReturn = null;
		int			noOfColumns = 0;
		int			noOfLines;
		int			j;

		j = 0;
		try {
			ReadFile = new Scanner(file);
		} catch (FileNotFoundException e) {
			return null;
		}

		noOfLines = GetNumLines(file);
		if (noOfLines == -1)
			return (null);
		while (ReadFile.hasNextLine())
		{
			Csv = ReadFile.nextLine().split(regex);
			if (j == 0)
			{
				noOfColumns = Csv.length;
				toReturn = new double[noOfLines][noOfColumns];
			}
			for (int i = 0; i < noOfColumns; i++)
				toReturn[j][i] = Double.parseDouble(Csv[i]);
			j++;
		}

		// this will desapear
		for (int i = 0; i < noOfLines; i++) {
			for (int k = 0; k < noOfColumns; k++) {
				System.out.printf("%.1f ", toReturn[i][k]);
			}
			System.out.println();
		}
		// until here
		return (toReturn);
	}

	public static int GetNumLines(File file)
	{
		int	noOfLines;

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

	// Just use it with the path without the '/' in the end, it will not work
	// Return, with no .csv files(null) else string[] with the path to csv files
	public static String[] ReadingDir(String dirPath)
	{
		File		folder = new File(dirPath);
		File[]		listOfFiles = folder.listFiles();
		String[]	csvFiles;
		String		all_names;
		String		name;

		all_names = null;
		for (File file : listOfFiles)
		{
			if (file.isFile())
			{
				name = file.getName();
				if (CheckingExtension(name))
					all_names = String.join("\t" , all_names, name);
			}
		}
		if (all_names == null)
			return (null);
		all_names = all_names.substring(5);
		csvFiles = all_names.split("[\\t]");
		for (int i = 0; i < csvFiles.length; i++)
			csvFiles[i] = String.join("/", dirPath, csvFiles[i]);

		for (int i = 0; i < csvFiles.length; i++) {
			System.out.println(csvFiles[i]);
		}
		return (csvFiles);
	}

	// Return (true) if the file has the extension, (false) if it don't
	public static boolean CheckingExtension(String toCheck)
	{
		int	length;

		length = toCheck.length();
		if (length > 4)
			if (toCheck.charAt(length - 1) == 'v' && toCheck.charAt(length - 2) == 's' && toCheck.charAt(length - 3) == 'c' && toCheck.charAt(length - 4) == '.')
				return (true);
		return (false);
	}

	//==================Functionalities==================//
	//=========1=========//
	public static void Decomposition(int own_values, String csvPath)
	{

	}

	//=========2=========//
	public static void Recomposition(int own_values, String csvPath)
	{

	}

	//=========3=========//
	public static void SearchClosest(int own_values, String csvPath, String dirPath)
	{

	}

}

