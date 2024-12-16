
import	java.util.Scanner;
import java.io.*;
import	java.lang.Object;

import org.apache.commons.math3.linear.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Main{
	public static boolean hasOutputInFile = false;
	public static String outputFilePath;
	public static String pathWrite;

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

		input.close();
	}

	//===========Non-Iterative exec and reading===========//
	// compilation == java -jar nome programa.jar -f X -k Y -i Z -d W
	public static void NonIterative(String[] arguments)
	{
		int			type;
		int			ownValues;
		String		path;
		String		dirPath;

		if (!arguments[0].equals("-f"))
		{
			outputFunction("You've entered some wrong argument!! Check it and try again!!\n");
			return ;
		}
		type = Integer.parseInt(arguments[1]);
		switch (type) {
			case 1:
				if (!CheckingArgs(arguments, 1))
				{
					outputFunction("You've entered some wrong argument!! Check it and try again!!\n");
					return ;
				}
				ownValues = Integer.parseInt(arguments[3]);
				path = arguments[5];
				pathWrite = arguments[6];
				System.out.printf("arguments received:\nownValue= %d\npath = %s\npathWrite = %s\n", ownValues, path, pathWrite);
				// function
				break ;
			case 2:
				if (!CheckingArgs(arguments, 2))
				{
					outputFunction("You've entered some wrong argument!! Check it and try again!!\n");
					return ;
				}
				ownValues = Integer.parseInt(arguments[3]);
				dirPath = arguments[5];
				pathWrite = arguments[6];
				System.out.printf("arguments received:\nownValue= %d\ndirPath = %s\npathWrite = %s\n", ownValues, dirPath, pathWrite);
				// function
				break ;
			case 3:
				if (!CheckingArgs(arguments, 3))
				{
					outputFunction("You've entered some wrong argument!! Check it and try again!!\n");
					return ;
				}
				ownValues = Integer.parseInt(arguments[3]);
				path = arguments[5];
				dirPath = arguments[7];
				pathWrite = arguments[8];
				System.out.printf("arguments received:\nownValue= %d\npath = %s\ndirPath = %s\npathWrite = %s\n", ownValues, path, dirPath, pathWrite);
				// function
				break ;
			default:
				outputFunction("You've entered some wrong argument!! Check it and try again!!\n");
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
		pathWrite = null;
		while(type != 0)
		{
			type = TypeOfExecution();
			if (type == 0)
				break ;
			ownValues = OwnValues();
			switch (type) {
				case 1:
					double[][] matrix;

					path = GetPath("|Enter the file PATH of execution:|\n");
					matrix = CSVtoMatrix(path);

					doingFunctionOne(ownValues, matrix);
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
		outputFunction("Program ending... Thanks for using it!!\n");
	}

	public static int TypeOfExecution()
	{
		int	read;

		read = 100;
		outputFunction("----------------------------------\n");
		outputFunction("|        Menu of execution      |\n");
		outputFunction("----------------------------------\n");
		System.out.print("|Enter the type of execution:   |\n");
		outputFunction("|(0) Exit Program               |\n");
		outputFunction("|(1) Decomposition of images    |\n");
		outputFunction("|(2) Rebuild images             |\n");
		outputFunction("|(3) Identify closest           |\n");
		outputFunction("----------------------------------\n");

		while(!(read <= MAX_TYPE_EXEC && read >= MIN_TYPE_EXEC))
		{
			read = input.nextInt();
			if (!(read <= 3 && read >= 0))
				outputFunction("You've entered a wrong value. Try it again: \n");
		}
		return (read);
	}

	public static int OwnValues()
	{
		int	read;

		read = MIN_OWN_VALUE - 1;
		outputFunction("---------------------------------------------------------------\n");
		System.out.print("|Enter the number of own values that you want to use(columns):|\n");
		outputFunction("---------------------------------------------------------------\n");
		while (read < MIN_OWN_VALUE)
		{
			read = input.nextInt();
			input.nextLine();
			if(read <= 0 && read != MIN_OWN_VALUE)
				outputFunction("You need to enter a non zero positive number or <-1>. Try again!\n");
		}
		return read;
	}

	public static String GetPath(String searching)
	{
		String	path;

		outputFunction("-----------------------------------\n");
		System.out.printf("%s", searching);
		outputFunction("-----------------------------------\n");
		path = input.nextLine();
		return (path);
	}

	//==============Output==============//
	public static void outputFunction(String toPrint)
	{
		if (pathWrite == null)
			System.out.printf("%s", toPrint);
		else
		{
			BufferedWriter 	outputFile;
			try {
				outputFile = new BufferedWriter(new FileWriter(pathWrite));
				outputFile.write(toPrint);
				outputFile.close();
			} catch (IOException e) {
				outputFunction("Not possible to create the file!\n");
			}
		}
	}


	//=========Matrix Read=========//
	public static double[][] CSVtoMatrix(String filename)
	{
		double[][]	matrix;

		matrix = ReadingCsv(filename);
		if (matrix == null)
		{
			outputFunction("File does not exist!\n");
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
	public static RealMatrix transformDoubleMatrixToRealMatrix(double[][] matrixDouble)
	{
		RealMatrix matrixReal;

		matrixReal = new Array2DRowRealMatrix(matrixDouble.length, matrixDouble[0].length);

		for (int rows = 0; rows < matrixDouble.length; rows++) {
			for (int colums = 0; colums < matrixDouble[0].length; colums++) {
				matrixReal.setEntry(rows, colums, matrixDouble[rows][colums]);
			}
		}
		return matrixReal;
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
		double 		minValue;
		int 		coordinates;
		int[] 		arrayOfCoordinates;

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

    public static double[][][] Decomposition(int own_values, double[][] covarianceMatrix)
	{
		double[][][] 	resultMatrix;

		double[][] 		eiganVectors;
		double[][] 		eiganValues;

		double[][] 		eiganVectorsSubMatrix;
		double[][] 		eiganValuesSubMatrix;

		int[] 			arrayOfCoordinatesOfMinOwnValues;
		int 			numberValuesToRemove;

		int 			totalNumberOfOwnValues;

		EigenDecomposition eiganDecompositor;

		RealMatrix 		eiganVectorsApache;
		RealMatrix 		eiganValuesApache;

		eiganDecompositor = new EigenDecomposition(transformDoubleMatrixToRealMatrix(covarianceMatrix));

		eiganVectorsApache = eiganDecompositor.getV();
		eiganValuesApache = eiganDecompositor.getD();

		eiganVectors = eiganVectorsApache.getData();
		eiganValues = eiganValuesApache.getData();

		totalNumberOfOwnValues = eiganValues.length;

		eiganVectorsSubMatrix = new double[totalNumberOfOwnValues][totalNumberOfOwnValues];
		eiganValuesSubMatrix = new double[eiganVectors.length][totalNumberOfOwnValues];


		if (own_values < totalNumberOfOwnValues && own_values != -1) {
			numberValuesToRemove = totalNumberOfOwnValues - own_values;
			arrayOfCoordinatesOfMinOwnValues = getCoordinatesOfMinValuesOfDiagonalMatrix(eiganValues, numberValuesToRemove);

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
		int 		height;
		int 		width;
		double 		absolutSum;

		height = originalMatrix.length;
		width = originalMatrix[0].length;
		absolutSum = 0;

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
		double[][] 		intermediaryMatrix;;

		intermediaryMatrix = matrixMulti(eigenVectors, eigenValues);

		return matrixMulti(intermediaryMatrix, matrixTranspose(eigenVectors));
	}

	public static void printDecomposition(double[][][] ownVs, double[][] decompressedMatrix, int ownValues, double[][] matrix)
	{
		System.out.printf("//Foram calculados %d valores e vetores próprios//\n", ownVs[1].length);

		outputFunction("Matriz de vetores próprios::\n");
		printMatrix(ownVs[0]);

		outputFunction("Matriz de valores próprios::\n");
		printMatrix(ownVs[1]);

		System.out.print("Matriz resultante da multiplicação das matrizes decompostas::\n");
		printMatrix(decompressedMatrix);

		if (ownVs[0].length == decompressedMatrix.length || ownValues == -1) {
			outputFunction("O Erro Absoluto Médio é :: 0\n");
		} else {
			System.out.printf("O Erro Absoluto Médio é :: %.3f\n", avgAbsolutError(matrix, decompressedMatrix));
		}
	}

	public static void doingFunctionOne(int ownValues, double[][] matrix)
	{
		double[][] 		decompressedMatrix;
		double[][][] 	ownVs;

		ownVs = Decomposition(ownValues, matrix);
		decompressedMatrix = calculateDecompressedMatrix(ownVs[0], ownVs[1]);
		printDecomposition(ownVs, decompressedMatrix, ownValues, matrix);
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
		outputFunction();
		printMatrix(covarianceMatrix);
		*/
		VerticalVectorMatrix(ownValues, dirPath);
	}

	public static void VerticalVectorMatrix(int OwnValues, String dirPath)
	{
		String[] 		dirCsvFiles;
		double[][] 		verticalVectorMatrix;

		dirCsvFiles = ReadingDir(dirPath);
		verticalVectorMatrix = AllImgsInVector(dirCsvFiles);
		//printMatrix(verticalVectorMatrix);
	}

	public static double[][] AllImgsInVector(String[] files)
	{
		double[][]		allImgsInVector;
		int				fileLength;

		fileLength = files.length;
		allImgsInVector = new double[fileLength][];
		for (int i = 0; i < fileLength; i++)
			allImgsInVector[i] = MatrixToVerticalVector(CSVtoMatrix(files[i]));
		//printMatrix(allImgsInVector);
		return (allImgsInVector);
	}

	public static double[] CalculateMediumVector(double[][] allImgsInVector)
	{
		double[]	mediumVector;
		int			vectorItens;
		int			manyVectors;

		manyVectors = allImgsInVector.length;
		vectorItens = allImgsInVector[0].length;
		mediumVector = new double[vectorItens];
		for (int i = 0; i < vectorItens; i++)
			for (int j = 0; j < manyVectors; j++)
				mediumVector[i] += allImgsInVector[j][i];
		mediumVector = vectorDivConst(mediumVector, manyVectors);
		/*outputFunction();
		for (int i = 0; i < vectorItens; i++)
			System.out.printf("%.1f ",mediumVector[i]);
		outputFunction();
		outputFunction();*/
		return (mediumVector);
	}

	public static double[][] calculateAllPhis(double[][] allImagesMatrix, double[] mediumVector){
		int 			height;
		int 			width;
		double[][] 		matrix;

		height = allImagesMatrix.length;
		width = allImagesMatrix[0].length;
		matrix = new double[height][width];

		mediumVector = vectorMultConst(mediumVector, -1);
		for(int imgIndex = 0; imgIndex < height; imgIndex++)
			matrix[imgIndex] = vectorAdd(allImagesMatrix[imgIndex], mediumVector);

		mediumVector = vectorMultConst(mediumVector, -1);

		return matrix;
	}

	public static double[][] buildCovarianceMatrix(double[][] allImagesMatrix, double[] mediumVector)
	{
		double[][] 		matrix;
		double[][] 		intermediaryMatrix;
		double[][] 		covarianceMatrix;

		matrix = calculateAllPhis(allImagesMatrix, mediumVector);

		outputFunction("A^t Matrix\n");
		//printMatrix(matrix);

		intermediaryMatrix = matrixMulti(matrixTranspose(matrix), matrix);
		outputFunction("intermediaryMatrix\n");

		//printMatrix(intermediaryMatrix);
		covarianceMatrix = matrixDivConst(intermediaryMatrix, allImagesMatrix.length);
		return covarianceMatrix;
	}

	//=========3=========//
    public static double[] calculateWeights(double[][] eigenVectors, double[] phi){
        double[] 		weights;

        weights = new double[eigenVectors.length];

		eigenVectors = matrixTranspose(eigenVectors);

        for (int i = 0; i < eigenVectors.length; i++) {
            weights[i] = vectorMulti(eigenVectors[i],phi);
        }

        return weights;
    }

    public static double[] calculateNewPhi(String csvPath, double[] mediumVector){
        double[][] 		imageMatrix;
        double[] 		newPhi;
        double[] 		imageVector;

        imageMatrix = CSVtoMatrix(csvPath);

        imageVector = MatrixToVerticalVector(imageMatrix);

        //multiplying by -1 so that the addition is a subtraction
        mediumVector = vectorMultConst(mediumVector, -1);

        newPhi = vectorAdd(imageVector, mediumVector); //subtraction

		mediumVector = vectorMultConst(mediumVector, -1);

        return newPhi;
    }

	public static double calculateEuclideanDistance(double[] vector1, double[] vector2){
		double 		distance;

		distance = 0;
		for (int i = 0; i < vector2.length; i++) {
			distance += Math.pow(vector1[i] - vector2[i], 2);
		}

		distance = Math.sqrt(distance);

		return distance;
	}

	public static int getIndexOfMinValueInArray(double[] array){
		int 		index;
		double 		minValue;

		index = 0;
		minValue = array[index];

		for (int i = 1; i < array.length; i++) {
			if (array[i] < minValue){
				minValue = array[i];
				index = i;
			}
		}
		return index;
	}

	public static void SearchClosest(int own_values, String csvPath, String dirPath)
	{
        String[] 		files;
        double[][] 		allImagesVector;
		double[] 		mediumVector;

		double[] 		newPhi;

        double[][] 		covarianceMatrix;
        double[][][] 	decomposedCovarianceMatrix;

		double[] 		newWeights;
		double[][] 		allPhis;
		double[][] 		allWeights;

		double[] 		allEuclideanDistances;

		int 			indexOfMinEuclideanDistance;


        files = ReadingDir(dirPath);
        allImagesVector = AllImgsInVector(files);
        mediumVector = CalculateMediumVector(allImagesVector);

        newPhi = calculateNewPhi(csvPath, mediumVector);

        covarianceMatrix = buildCovarianceMatrix(allImagesVector, mediumVector);
        decomposedCovarianceMatrix = Decomposition(own_values, covarianceMatrix);

        newWeights = calculateWeights(decomposedCovarianceMatrix[0], newPhi);
		allPhis = calculateAllPhis(allImagesVector, mediumVector);
		allWeights = new double[allImagesVector.length][allImagesVector[0].length];

		allEuclideanDistances = new double[allWeights.length];


		for (int i = 0; i < allImagesVector.length; i++) {
			allWeights[i] = calculateWeights(decomposedCovarianceMatrix[0],allPhis[i]);
		}

		for (int i = 0; i < allEuclideanDistances.length; i++) {
			allEuclideanDistances[i] = calculateEuclideanDistance(newWeights, allWeights[i]);
		}


		indexOfMinEuclideanDistance = getIndexOfMinValueInArray(allEuclideanDistances);

		outputFunction(indexOfMinEuclideanDistance+"");
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
		int			matrixLen;
		int			matrixHeight;

		matrixHeight = matrix1.length;
		matrixLen =  matrix2[0].length;

		matrixResult = new double[matrixHeight][matrixLen];
		for (int k = 0; k < matrixResult.length; k++)
		{
			for (int i = 0; i < matrixResult[0].length; i++)
			{
				for (int j = 0; j < matrix1[0].length && j < matrix2.length; j++)
				{
					matrixResult[k][i] += matrix1[k][j] * matrix2[j][i];
				}
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
		int			martixLen;
		int			matrixHeight;
		double[][]	matrixResult;

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
		int 	 columnNumber = matrix.length;
		int 	 rowNumber = matrix.length;
		int 	 numberOfElements = (columnNumber * rowNumber);
		double[] verticalMatrix = new double[numberOfElements];
		int 	 index = 0;

		for (int i = 0; i < rowNumber; i++)
		{
			for (int j = 0; j < columnNumber; j++)
			{
				verticalMatrix[index] =  matrix[j][i];
				index++;
			}
		}

		return verticalMatrix;
	}

	public static double[] vectorAdd(double[] vector1, double[] vector2)
	{
		int		 vectorLen;

		double[] addedVector;

		vectorLen = vector1.length;
		addedVector = new double[vectorLen];

		for (int i = 0; i < vectorLen; i++)
			addedVector[i] = vector1[i] + vector2[i];
		return (addedVector);
	}

	public static double vectorMulti(double[] vector1, double[] vector2)
	{
		double	valueResult;

		valueResult = 0;
		for (int i = 0; i < vector1.length; i++)
		{
			valueResult += vector1[i] * vector2[i];
		}
		return (valueResult);
	}

	public static double[] vectorDivConst(double[] vector1, double value)
	{
		int			vectorLen;

		vectorLen = vector1.length;
		for (int i = 0; i < vectorLen; i++)
			vector1[i] = vector1[i] / value;
		return (vector1);
	}

	public static double[] vectorMultConst(double[] vector1, double value)
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
			for (int columns = 0; columns < matrix[0].length; columns++)
			{
				outputFunction(matrix[rows][columns]+"  ");
			}
			outputFunction("\n");
		}
		outputFunction("\n");
	}

	public static boolean matrixToCSV(double[][] matrix, String outputPath)
	{
		int 			height;
		int 			width;

		BufferedWriter 	outputFile;
		String 			line;


		height = matrix.length;
		width = matrix[0].length;

		try {
			outputFile = new BufferedWriter(new FileWriter(outputPath));
			for(int i = 0; i < height; i++){
				line = "";
				for(int j = 0; j < width; j++){
					line += (int) matrix[i][j];

					if(j != width-1){
						line += ",";
					}
				}
				outputFunction(line);
				outputFile.write(line);
				outputFile.newLine();
			}
			outputFile.close();
			return true;
		} catch (IOException e) {
			outputFunction("Not possible to create the file!\n");
			return false;
		}
	}

	public void matrixToJPG(int[][] matrix, String outputFilePath) throws IOException
	{
		int 			height;
		int 			width;

		BufferedImage 	image;


		height = matrix.length;
		width = matrix[0].length;

		image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);


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
}
