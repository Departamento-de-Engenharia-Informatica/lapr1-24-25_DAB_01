
import	java.util.Scanner;
import java.io.*;
import	java.lang.Object;

import org.apache.commons.math3.linear.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Main{
	public static boolean hasOutputInFile = false;
	public static String outputFilePath;

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

	//===========Non-Iterative exec and reading===========//
	// compilation == java -jar nome programa.jar -f X -k Y -i Z -d W
	public static void NonIterative(String[] arguments)
	{
		int			type;
		int			ownValues;
		String		path;
		String		dirPath;
		String		whereToWrite;

		if (!arguments[0].equals("-f"))
		{
			System.out.println("You've entered some wrong argument!! Check it and try again!!");
			return ;
		}
		type = Integer.parseInt(arguments[1]);
		switch (type) {
			case 1:
				if (CheckingArgs(arguments, 1))
					break ;
				ownValues = Integer.parseInt(arguments[4]);
				path = arguments[6];
				whereToWrite = arguments[7];
				// function
			case 2:
				if (CheckingArgs(arguments, 2))
					break ;
				ownValues = Integer.parseInt(arguments[4]);
				dirPath = arguments[6];
				whereToWrite = arguments[7];
				// function
			case 3:
				if (CheckingArgs(arguments, 3))
					break ;
				ownValues = Integer.parseInt(arguments[4]);
				path = arguments[6];
				dirPath = arguments[8];
				whereToWrite = arguments[9];
				// function
			default:
				System.out.println("You've entered some wrong argument!! Check it and try again!!");
				break;
		}
	}

	public static boolean CheckingArgs(String[] arguments, int whichExec)
	{
		if (whichExec == 1)
		{
			if(!(arguments[2].equals("-k")))
				return (false);
			if(!(arguments[4].equals("-i")))
				return (false);
			if (arguments.length != 7)
				return (false);
		}
		else if (whichExec == 2)
		{
			if(!(arguments[2].equals("-k")))
				return (false);
			if(!(arguments[4].equals("-i")))
				return (false);
			if (arguments.length != 7)
				return (false);
		}
		else if (whichExec == 3)
		{
			if(!(arguments[2].equals("-k")))
				return (false);
			if(!(arguments[4].equals("-i")))
				return (false);
			if(!(arguments[6].equals("-d")))
				return (false);
			if (arguments.length != 9)
				return (false);
		}
		return (true);
	}

	//============Iterative exec and reading=============//
	public static void Iterative()
	{
		int			type;
		int			ownValues;
		String		path;
		String		dirPath;

		type = 1;
		while(type != 0)
		{
			type = TypeOfExecution();
			if (type == 0)
				break ;
			ownValues = OwnValues();
			switch (type) {
				case 1:
					path = GetPath("|Enter the file PATH of execution:|\n");
					doingFunctionOne(ownValues, path);
					break;
				case 2:
					dirPath = GetPath("|Enter the dir PATH of execution:|\n");
					Recomposition(ownValues, dirPath);
					break;
				case 3:
					path = GetPath("|Enter the file PATH of execution:|\n");
					dirPath = GetPath("|Enter the dir PATH of execution:|\n");
					SearchClosest(ownValues, path, dirPath);
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
	public static RealMatrix transformDoubleMatrixToRealMatrix(double[][] matrixdouble)
	{
		RealMatrix matrixreal = new Array2DRowRealMatrix(matrixdouble.length, matrixdouble[0].length);

		for (int rows = 0; rows < matrixdouble.length; rows++) {
			for (int colums = 0; colums < matrixdouble[0].length; colums++) {
				matrixreal.setEntry(rows, colums, matrixdouble[rows][colums]);
			}
		}
		return matrixreal;
	}

	public static boolean isValueInArray(double value, int[] array) {
		for (double number : array) {
			if (value == number) {
				return true;
			}
		}
		return false;
	}

	public static int[] getCoordinatesOfMinValuesOfDiagonalMatrix(double[][] matrix, int numberOfValues)
	{
		double minValue;
		int coordinates;
		int[] arrayOfCoordinates;

		arrayOfCoordinates = new int[numberOfValues];
		for (int i = 0; i < numberOfValues; i++) {
			minValue = Math.abs(matrix[0][0]);
			coordinates = 0;
			for (int j = 0; j < matrix[0].length; j++) {
				if (Math.abs(matrix[j][j]) < minValue && !isValueInArray(j, arrayOfCoordinates)) {
					minValue = Math.abs(matrix[j][j]);
					coordinates = j;
				}
			}
			arrayOfCoordinates[i] = coordinates;
		}
		return arrayOfCoordinates;
	}

	public static double[][] getEigenValuesSubMatrix(int[] arrayOfCoordinates, double[][] matrix)
	{
		int index = 0;
		int numberOfCoordinates = matrix.length - arrayOfCoordinates.length;
		double[][] eigenValuesSubMatrix = new double[numberOfCoordinates][numberOfCoordinates];

		for (int i = 0; index < numberOfCoordinates; i++) {
			if (!isValueInArray(i, arrayOfCoordinates)) {
				eigenValuesSubMatrix[index][index] = matrix[i][i];
				index++;
			}
		}
		return eigenValuesSubMatrix;
	}

	public static double[][] getEigenVectorsSubMatrix(int[] arrayOfCoordinates, double[][] matrix)
	{
		int indexOfColumns;
		int numberOfCoordinates = matrix.length - arrayOfCoordinates.length;
		double[][] eigenValuesSubMatrix = new double[matrix.length][numberOfCoordinates];

		for (int i = 0; i < matrix.length; i++) {
			indexOfColumns = 0;
			for (int j = 0; indexOfColumns < numberOfCoordinates; j++) {
				if (!isValueInArray(j, arrayOfCoordinates)) {
					eigenValuesSubMatrix[i][indexOfColumns] = matrix[i][j];
					indexOfColumns++;
				}
			}
		}

		return eigenValuesSubMatrix;
	}

	public static double[][][] Decomposition(int own_values, String csvPath)
	{
		double[][] matrix = CSVtoMatrix(csvPath);
		double[][][] resultMatrix;

		EigenDecomposition eiganDecompositor = new EigenDecomposition(transformDoubleMatrixToRealMatrix(matrix));

		RealMatrix eiganVectorsApache = eiganDecompositor.getV();
		RealMatrix eiganValuesApache = eiganDecompositor.getD();

		double[][] eiganVectors = eiganVectorsApache.getData();
		double[][] eiganValues = eiganValuesApache.getData();

		int totalNumberOfOwnValues = eiganValues.length;

		double[][] eiganVectorsSubMatrix = new double[totalNumberOfOwnValues][totalNumberOfOwnValues];
		double[][] eiganValuesSubMatrix = new double[eiganVectors.length][totalNumberOfOwnValues];

		if (own_values < totalNumberOfOwnValues && own_values != -1) {
			int numberValuesToRemove = totalNumberOfOwnValues - own_values;
			int[] arrayOfCoordinatesOfMinOwnValues = getCoordinatesOfMinValuesOfDiagonalMatrix(eiganValues, numberValuesToRemove);

			eiganVectorsSubMatrix = getEigenVectorsSubMatrix(arrayOfCoordinatesOfMinOwnValues, eiganVectors);
			eiganValuesSubMatrix = getEigenValuesSubMatrix(arrayOfCoordinatesOfMinOwnValues, eiganValues);

			resultMatrix = new double[][][]{eiganVectorsSubMatrix, eiganValuesSubMatrix};
		} else {
			resultMatrix = new double[][][]{eiganVectors, eiganValues};
		}

		return resultMatrix;
	}

	public static double avgAbsolutError(double[][] originalMatrix, double[][] partialMatrix)
	{
		int height = originalMatrix.length;
		int width = originalMatrix[0].length;

		double absolutSum = 0;

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (i < partialMatrix.length && j < partialMatrix[0].length) {
					absolutSum += Math.abs(originalMatrix[i][j] - partialMatrix[i][j]);
				} else {
					absolutSum += Math.abs(originalMatrix[i][j]);
				}
			}
		}

		absolutSum /= height * width;

		return absolutSum;
	}

	public static double[][] calculateDecompressedMatrix(double[][] eigenVectors, double[][] eigenValues)
	{
		double[][] intermediaryMatrix = matrixMulti(eigenVectors, eigenValues);

		return matrixMulti(intermediaryMatrix, matrixTranspose(eigenVectors));
	}

	public static void printDecomposition(double[][][] ownVs, double[][] decompressedMatrix, int ownValues, String path)
	{
		System.out.printf("//Foram calculados %d valores e vetores próprios//\n", ownVs[1].length);

		System.out.println("Matriz de vetores próprios::");
		printMatrix(ownVs[0]);

		System.out.println("Matriz de valores próprios::");
		printMatrix(ownVs[1]);

		System.out.print("Matriz resultante da multiplicação das matrizes decompostas::\n");
		printMatrix(decompressedMatrix);

		if (ownVs[0].length == decompressedMatrix.length || ownValues == -1) {
			System.out.println("O Erro Absoluto Médio é :: 0\n");
		} else {
			System.out.printf("O Erro Absoluto Médio é :: %.3f\n", avgAbsolutError(CSVtoMatrix(path), decompressedMatrix));
		}
	}

	public static void doingFunctionOne(int ownValues, String path)
	{
		double[][] decompressedMatrix;
		double[][][] ownVs;

		ownVs = Decomposition(ownValues, path);
		decompressedMatrix = calculateDecompressedMatrix(ownVs[0], ownVs[1]);
		printDecomposition(ownVs, decompressedMatrix, ownValues,  path);
	}

	//=========2=========//
	public static void Recomposition(int ownValues, String dirPath)
	{
		/*
		double[]			mediumVector;
		double[][]			matrixInVector;
		double[][]			covarianceMatrix;
		String[]			PathToCompare;
		String[]			csvFilesInFolder;
		int					height;


		csvFilesInFolder = ReadingDir(dirPath);
		matrixInVector = AllImgsInVector(csvFilesInFolder);
		mediumVector = CalculateMediumVector(matrixInVector);
		covarianceMatrix = buildCovarianceMatrix(matrixInVector, mediumVector);
		System.out.println();
		printMatrix(covarianceMatrix);
		*/
		VerticalVectorMatrix(ownValues, dirPath);
	}

	public static void VerticalVectorMatrix(int OwnValues, String dirPath)
	{
		String[] dirCsvFiles;
		double[][] verticalVectorMatrix;

		dirCsvFiles = ReadingDir(dirPath);
		verticalVectorMatrix = AllImgsInVector(dirCsvFiles);
		printMatrix(verticalVectorMatrix);
	}

	public static double[][] AllImgsInVector(String[] files)
	{
		double[][]			allImgsInVector;
		int					fileLength;

		fileLength = files.length;
		allImgsInVector = new double[fileLength][];
		for (int i = 0; i < fileLength; i++)
			allImgsInVector[i] = MatrixToVerticalVector(CSVtoMatrix(files[i]));
		printMatrix(allImgsInVector);
		return (allImgsInVector);
	}

	public static double[] CalculateMediumVector(double[][] allImgsInVector)
	{
		double[]	mediumVector;
		int			vectorItens;
		int			manyVectors;

		manyVectors = allImgsInVector.length;
		vectorItens = allImgsInVector[0].length;
		mediumVector = new double[manyVectors];
		for (int i = 0; i < vectorItens; i++)
			for (int j = 0; j < manyVectors; j++)
				mediumVector[i] += allImgsInVector[j][i];
		mediumVector = vectorDivConst(mediumVector, manyVectors);
		System.out.println();
		for (int i = 0; i < vectorItens; i++)
			System.out.printf("%.1f ",mediumVector[i]);
		System.out.println();
		System.out.println();
		return (mediumVector);
	}

	public static double[][] buildCovarianceMatrix(double[][] allImagesMatrix, double[] mediumVector)
	{
		int height = allImagesMatrix.length;
		int width = allImagesMatrix[0].length;

		double[][] matrix = new double[height][width];

		mediumVector = vectorMultConst(mediumVector, -1);
		for(int imgIndex = 0; imgIndex < height; imgIndex++)
			matrix[imgIndex] = vectorAdd(allImagesMatrix[imgIndex], mediumVector);

		mediumVector = vectorMultConst(mediumVector, -1);

		System.out.println("A^t Matrix");
		printMatrix(matrix);

		double[][] intermediaryMatrix = matrixMulti(matrixTranspose(matrix), matrix);
		System.out.println("intermediaryMatrix");

		printMatrix(intermediaryMatrix);
		double[][] covarianceMatrix = matrixDivConst(intermediaryMatrix, height);
		return covarianceMatrix;
	}

	//=========3=========//
	public static void SearchClosest(int own_values, String csvPath, String dirPath)
	{

	}

	//=============Matrix Operations=============//
	public static double[][] matrixAdd(double[][] matrix1, double[][] matrix2)
	{
		int			martixLen;
		int			matrixHeight;

		martixLen = matrix1[0].length;
		matrixHeight = matrix1.length;
		for (int i = 0; i < matrixHeight; i++)
			for (int j = 0; j < martixLen; j++)
				matrix1[i][j] = matrix1[i][j] + matrix2[i][j];
		return (matrix1);
	}

	public static double[][] matrixMulti(double[][] matrix1, double[][] matrix2)
	{
		double[][]	matrixResult;
		int			martixLen;
		int			matrixHeight;

		martixLen = matrix1[0].length;
		matrixHeight = matrix2.length;
		matrixResult = new double[martixLen][matrixHeight];
		for (int k = 0; k < matrixResult.length; k++)
		{
			for (int i = 0; i < matrixHeight; i++)
			{
				for (int j = 0; j < martixLen; j++)
					matrixResult[k][i] += matrix1[i][j] * matrix2[j][i];
			}
		}
		return (matrixResult);
	}

	public static double[][] matrixDivConst(double[][] matrix1, int value)
	{
		int			martixLen;
		int			matrixHeight;

		martixLen = matrix1[0].length;
		matrixHeight = matrix1.length;
		for (int i = 0; i < matrixHeight; i++)
			for (int j = 0; j < martixLen; j++)
				matrix1[i][j] = matrix1[i][j] / value;
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
		for (int i = 0; i < martixLen; i++)
			for (int j = 0; j < matrixHeight; j++)
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
				verticalMatrix[index] =  matrix[j][i];
				index++;
			}
		}

		System.out.println("------------------");
		for (int i = 0; i < verticalMatrix.length; i++)
				System.out.println(verticalMatrix[i]);
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

	public static double[] vectorMult(double[] vector1, double[] vector2)
	{
		int			vectorLen;

		vectorLen = vector1.length;
		for (int i = 0; i < vectorLen; i++)
			vector1[i] = vector1[i] * vector2[i];
		return (vector1);
	}

	public static double[] vectorDivConst(double[] vector1, int value)
	{
		int			vectorLen;

		vectorLen = vector1.length;
		for (int i = 0; i < vectorLen; i++)
			vector1[i] = vector1[i] / value;
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

	//=============Display Matrix=============//
	public static void printMatrix(double[][] matrix)
	{
		for (int rows = 0; rows < matrix.length; rows++) {
			for (int columns = 0; columns < matrix[0].length; columns++) {
				System.out.printf("%8.3f ", matrix[rows][columns]);
			}
			System.out.println();
		}
		System.out.println();
	}

	public static boolean matrixToCSV(double[][] matrix, String outputPath)
	{
		int height = matrix.length;
		int width = matrix[0].length;

		try {
			BufferedWriter outputFile = new BufferedWriter(new FileWriter(outputPath));
			for(int i = 0; i < height; i++){
				String line = "";
				for(int j = 0; j < width; j++){
					line += (int) matrix[i][j];

					if(j != width-1){
						line += ",";
					}
				}
				System.out.println(line);
				outputFile.write(line);
				outputFile.newLine();
			}
			outputFile.close();
			return true;
		} catch (IOException e) {
			System.out.println("Not possible to create the file!");
			return false;
		}
	}

	public void matrixToJPG(int[][] matrix, String outputFilePath) throws IOException
	{
		int height = matrix.length;
		int width = matrix[0].length;

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

		// Set the pixel intensities
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int intensity = matrix[y][x];
				if (intensity < 0 || intensity > 255) {
					throw new IllegalArgumentException("Pixel intensity must be between 0 and 255.");
				}
				int rgb = (intensity << 16) | (intensity << 8) | intensity; // Set the same value for R, G, B
				image.setRGB(x, y, rgb);
			}
		}

		// Write the image to the file
		File outputFile = new File(outputFilePath);
		ImageIO.write(image, "jpg", outputFile);
	}

	public static void displayOutput(String str)
	{
		if(hasOutputInFile)
			System.out.println(str);
		else{
			try {
				BufferedWriter outputFile = new BufferedWriter(new FileWriter(outputFilePath));

				outputFile.write(str);
				outputFile.newLine();

				outputFile.close();

			} catch (IOException e) {
				// nao sei
			}
		}
	}
}
