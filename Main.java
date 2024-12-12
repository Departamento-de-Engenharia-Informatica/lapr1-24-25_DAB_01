
import	java.util.Scanner;
import java.io.*;
import	java.lang.Object;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;


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

		type = 1;
		while(type != 0)
		{
			type = TypeOfExecution();
			if (type == 0)
				break ;
			own_values = OwnValues();
			switch (type) {
				case 1:
					path = GetPath("|Enter the file PATH of execution:|\n");
					Decomposition(own_values, path);
					break;
				case 2:
					dirPath = GetPath("|Enter the dir PATH of execution:|\n");
					Recomposition(own_values, dirPath);
					break;
				case 3:
					path = GetPath("|Enter the file PATH of execution:|\n");
					dirPath = GetPath("|Enter the dir PATH of execution:|\n");
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
		System.out.println("|        Menu of execution      |");
		System.out.println("----------------------------------");
		System.out.printf("|Enter the type of execution:   |\n");
		System.out.println("|(0) Exit Program               |");
		System.out.println("|(1) Decomposition of images    |");
		System.out.println("|(2) Rebuild images             |");
		System.out.println("|(3) Identify closest           |");
		System.out.println("----------------------------------");

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

		read = 0;
		System.out.println("---------------------------------------------------------------");
		System.out.printf("|Enter the number of own values that you want to use(columns):|\n");
		System.out.println("---------------------------------------------------------------");
		while (read <= 0)
		{
			read = input.nextInt();
			input.nextLine();
			if(read <= 0)
				System.out.println("You need to enter a non zero positive number. Try again!");
		}
		return read;
	}

	public static String GetPath(String searching)
	{
		String	path;

		System.out.println("-----------------------------------");
		System.out.printf("%s", searching);
		System.out.println("-----------------------------------");
		path = input.nextLine();
		return (path);
	}

	//=========Matrix Read=========//
	public static double[][] CSVtoMatrix(String filename)
	{
		double[][]	matrix;

		matrix = ReadingCsv(filename);
		if (matrix == null)
		{
			System.out.println("File does not exist!");
			return (null);
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
		/*
		// this will desapear
		for (int i = 0; i < noOfLines; i++) {
			for (int k = 0; k < noOfColumns; k++) {
				System.out.printf("%.1f ", toReturn[i][k]);
			}
			System.out.println();
		}
		// until here */
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
		//RealMatrix			matrix;
		//RealVector			mediumVector;
		double[][][]	MatrixInVector ;
		String[]			PathToCompare;
		String[]			csvFilesInFolder;
		int					height;


		csvFilesInFolder = ReadingDir(dirPath);
		MatrixInVector = AllImgsInVector(csvFilesInFolder);
/*
		System.out.println("\n\nnow build the medium vector:");
		mediumVector = CalculateMediumVector(MatrixInVector);
		System.out.println(mediumVector);

		System.out.println("\n\nnow it Will build Fi :");
		matrix = MatrixOfFi(MatrixInVector, mediumVector);
		System.out.println("\n" + matrix + "\n"); */
	}

 	public static double[][][] AllImgsInVector(String[] files)
	{
		double[][]			allImgsInVector;
		int					fileLength;

		fileLength = files.length;
		allImgsInVector = new double[fileLength][][];
		for (int i = 0; i < fileLength; i++)
			allImgsInVector[i] = ImgToVector(CSVtoMatrix(files[i]));
		for (int i = 0; i < fileLength; i++)
				System.out.println(allImgsInVector[i]);
		return (allImgsInVector);
	}

	public static double[][] ImgToVector(double[][] matrix)
	{
		double[][]			matrixInVector;
		int					matrixManyRows;
		int					matrixManyColumns;
		int					pos = 0;

		matrixManyColumns = matrix[0].length;
		matrixManyRows = matrix.length;
		matrixInVector = new double[matrixManyColumns][matrixManyRows];
		for (int i = 0; i < matrixManyColumns; i++)
			for (int j = 0; j < matrixManyRows; j++)
				matrixInVector[i][j] = matrix[j][i];

																									//writing vector
		System.out.println("----------	new vector	------------");
		for (int i = 0; i < matrixManyColumns; i++)
		{
				for (int j = 0; j < matrixManyRows; j++)
					System.out.printf("%.2f ", matrixInVector[i][j]);
			System.out.println();
		}
																									//writing vector
		return (matrixInVector);
	}

	public static RealVector CalculateMediumVector(ArrayRealVector[] AllImgsInVector)
	{
		RealVector	mediumVector;
		int			manyVectors;

		manyVectors = AllImgsInVector.length;
		mediumVector = new ArrayRealVector(AllImgsInVector[0].getDimension());
		for (int i = 0; i < manyVectors; i++)
			mediumVector = mediumVector.add(AllImgsInVector[i]);
		mediumVector = mediumVector.mapDivide(manyVectors);
		return mediumVector;
	}





	public static RealMatrix MatrixOfFi(ArrayRealVector[] allImgsInVector, RealVector mdiumVector)
	{
		RealMatrix			fiMatrix;
		ArrayRealVector[]	allFiVectors;
		int					manyVectors;
		int					manyColumns;

		manyVectors = allImgsInVector.length;
		manyColumns = allImgsInVector[0].getDimension();
		allFiVectors = FiVectors(allImgsInVector, mdiumVector, manyColumns, manyVectors);
		fiMatrix = transformToMatrix(allFiVectors, manyVectors, manyColumns);
		return (fiMatrix);
	}

	public static RealMatrix transformToMatrix(ArrayRealVector[] allFiVectors, int manyVectors, int manyColumns)
	{
		RealMatrix			fiMatrix;

		fiMatrix = new Array2DRowRealMatrix(manyColumns, manyVectors);
		for (int i = 0; i < manyVectors; i++)
			for (int j = 0; j < manyColumns; j++)
				fiMatrix.addToEntry(j, i, allFiVectors[i].getEntry(j));
		return (fiMatrix);
	}

	public static ArrayRealVector[] FiVectors(ArrayRealVector[] allImgsInVector, RealVector mdiumVector, int manyColumns, int manyVectors)
	{
		ArrayRealVector[] fiVectors;

		fiVectors = new ArrayRealVector[manyVectors];
		for (int i = 0; i < manyVectors; i++)
			fiVectors[i] = new ArrayRealVector(CalculatingFi(allImgsInVector[i], mdiumVector, manyColumns));
		return (fiVectors);
	}

	public static RealVector CalculatingFi(RealVector vector, RealVector mdiumVector, int manyColumns)
	{
		RealVector fi;

		fi = new ArrayRealVector(manyColumns);
		fi = vector.add(mdiumVector.mapMultiply(-1));
		System.out.println(fi);
		return (fi);
	}

	//=============Matrix Operations=============//
	public static double[][] matrixAdd(double[][] matrix1, double[][] matrix2)
	{
		int			martixLen;
		int			matrixHeight;

		martixLen = matrix1[0].length;
		matrixHeight = matrix1.length;
		for (int i = 0; i < matrixHeight; i++)
		{
			for (int j = 0; j < martixLen; j++)
				matrix1[i][j] = matrix1[i][j] + matrix2[i][j];
		}
		return (matrix1);
	}

	public static double[][] matrixAddConst(double[][] matrix1, int value)
	{
		int			martixLen;
		int			matrixHeight;

		martixLen = matrix1[0].length;
		matrixHeight = matrix1.length;
		for (int i = 0; i < matrixHeight; i++)
			for (int j = 0; j < martixLen; j++)
				matrix1[i][j] = matrix1[i][j] + value;
		return (matrix1);
	}

	public static double[][] matrixMulti(double[][] matrix1, double[][] matrix2)
	{
		double[][]	matrixResult;
		int			martixLen;
		int			matrixHeight;

		martixLen = matrix1[0].length;
		matrixHeight = matrix1.length;
		matrixResult = new double[martixLen][matrixHeight];
		for (int k = 0; k < matrixResult.length; k++)
		{
			for (int i = k; i < matrixHeight; i++)
			{
				for (int j = 0; j < martixLen; j++)
					matrixResult[k][i] += matrix1[i][j] * matrix2[j][i];
			}
		}
		return (matrix1);
	}

	public static double[][] matrixMultiConst(double[][] matrix1, int value)
	{
		int			martixLen;
		int			matrixHeight;

		martixLen = matrix1[0].length;
		matrixHeight = matrix1.length;
		for (int i = 0; i < matrixHeight; i++)
			for (int j = 0; j < martixLen; j++)
				matrix1[i][j] = matrix1[i][j] * value;
		return (matrix1);
	}

	public static double[][] matrixTranspose(double[][] matrix)
	{
		double[][]	matrixResult;
		int			martixLen;
		int			matrixHeight;

		martixLen = matrix[0].length;
		matrixHeight = matrix.length;
		matrixResult = new double[martixLen][matrixHeight];
		for (int i = 0; i < matrixResult.length; i++)
			for (int j = 0; j < matrixResult.length; j++)
				matrixResult[i][j] = matrix[j][i];
		return (matrixResult);
	}

	//=============Vector Operations=============//
	public static double[] MatrixToVerticalVector(double[][] matrix)
	{
		int columnNumber = matrix.length;
		int rowNumber = matrix.length;
		int numberOfElements = (columnNumber * rowNumber);
		double[] verticalMatrix = new double[numberOfElements];
		int index = 0;

		for (int i = 0; i < rowNumber; i++)
		{
			for (int j = 0; j < columnNumber; j++)
			{
				verticalMatrix[index] =  matrix[i][j];
				index++;
			}
		}
		return verticalMatrix;
	}

	public static double[] vectorAdd(double[] vector1, double[] vector2)
	{
		int			vectorLen;

		vectorLen = vector1.length;
		for (int i = 0; i < vectorLen; i++)
			vector1[i] = vector1[i] + vector2[i];
		return (vector1);
	}

	public static double[] vectorAddConst(double[] vector1, int value)
	{
		int			vectorLen;

		vectorLen = vector1.length;
		for (int i = 0; i < vectorLen; i++)
			vector1[i] = vector1[i] + value;
		return (vector1);
	}

	public static double[] vectorMult(double[] vector1, double[] vector2)
	{
		int			vectorLen;

		vectorLen = vector1.length;
		for (int i = 0; i < vectorLen; i++)
			vector1[i] = vector1[i] * vector2[i];
		return (vector1);
	}

	public static double[] vectorMultConst(double[] vector1, int value)
	{
		int			vectorLen;

		vectorLen = vector1.length;
		for (int i = 0; i < vectorLen; i++)
			vector1[i] = vector1[i] * value;
		return (vector1);
	}
}
