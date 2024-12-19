
import	java.util.Scanner;
import java.io.*;

import org.apache.commons.math3.linear.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.sound.sampled.SourceDataLine;

public class Main{
	public static boolean			hasOutputInFile = false;
	public static String			outputFilePath;
	public static BufferedWriter	outputFile;

	//=======new values=====//
	public static final int FUNC_1 = 1;
	public static final int FUNC_2 = 2;
	public static final int FUNC_3 = 3;
	public static final int MAX_SIZE_IMG = 256;
	public static final int MAX_VALUE_IN_CSV = 255;
	public static final int MIN_VALUE_IN_CSV = 0;
	public static final String PATH_WRITE_JPG = "Identificacao/";
	public static final String PATH_WRITE_CSV = "Output/";
	public static final String PATH_RECONSTRUCTION = "ImagensReconstruidas/";
	public static final String PATH_EIGENFACES = "Eigenfaces/";

	public static final int MIN_OWN_VALUE = -1;
	public static final int MIN_TYPE_EXEC = 0;
	public static final int MAX_TYPE_EXEC = 3;

	public static final Scanner input = new Scanner(System.in);

	public static void main(String[] args)
	{
		if (args.length > 0)
			nonInteractive(args);
		else
			interactive();

		input.close();
	}

	//===========Non-Iterative exec and reading===========//
	// compilation == java -jar nome programa.jar -f X -k Y -i Z -d W
	public static void nonInteractive(String[] arguments)
	{
		int				type;
		int				ownValues;
		String			path;
		String			dirPath;
		String			pathWrite;

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
				if (!isOwnValueValid(ownValues)){
					System.out.println("Valor próprio inválido (k), este tem que ser um número inteiro positivo ou -1");
					return;
				}
				path = arguments[5];
				pathWrite = arguments[6];
				try{
					outputFile = new BufferedWriter(new FileWriter(pathWrite));
					doingFunctionOne(ownValues, CSVtoMatrix(path));
					outputFile.close();
				}catch (IOException e) {
					outputFunction("Not possible to write, closing program!");
				}
				break ;
			case 2:
				if (!CheckingArgs(arguments, 2))
				{
					outputFunction("You've entered some wrong argument!! Check it and try again!!\n");
					return ;
				}

				ownValues = Integer.parseInt(arguments[3]);
				if (!isOwnValueValid(ownValues)){
					System.out.println("Valor próprio inválido (k), este tem que ser um número inteiro positivo ou -1");
					return;
				}
				dirPath = arguments[5];
				pathWrite = arguments[6];
				try{
					outputFile = new BufferedWriter(new FileWriter(pathWrite));
					doingFunctionTwo(ownValues, dirPath);
					outputFile.close();
				} catch (IOException e) {
					outputFunction("Not possible to write, closing program!");
				}

				break ;
			case 3:
				if (!CheckingArgs(arguments, 3))
				{
					outputFunction("You've entered some wrong argument!! Check it and try again!!\n");
					return ;
				}

				ownValues = Integer.parseInt(arguments[3]);
				if (!isOwnValueValid(ownValues)){
					System.out.println("Valor próprio inválido (k), este tem que ser um número inteiro positivo ou -1");
					return;
				}
				path = arguments[5];
				dirPath = arguments[7];
				pathWrite = arguments[8];
				try{
					outputFile = new BufferedWriter(new FileWriter(pathWrite));
					SearchClosest(ownValues, path, dirPath);
					outputFile.close();
				} catch (IOException e) {
					outputFunction("Not possible to write, closing program!");
				}

				break ;
			default:
				outputFunction("You've entered some wrong argument!! Check it and try again!!\n");
				return ;
			}
		return ;
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
			if(!(arguments[4].equals("-d")))
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
	public static void interactive()
	{
		int			type;
		int			ownValues;
		String		path;
		String		dirPath;

		type = 1;
		outputFile = null;
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
					if(matrix == null)
					{
						outputFunction("You've entered a bad csv file \n");
						return ;
					}
					doingFunctionOne(ownValues, matrix);
					break;
				case 2:
					dirPath = GetPath("|Enter the dir PATH of execution:|\n");
					doingFunctionTwo(ownValues, dirPath);
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
		outputFunction("|Enter the type of execution:   |\n");
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
		outputFunction(String.format("%s", searching));
		outputFunction("-----------------------------------\n");
		path = input.nextLine();
		return (path);
	}

	//==============Output==============//
	public static void outputFunction(String toPrint)
	{
		if (outputFile == null)
			System.out.printf(toPrint);
		else {
			try {
				outputFile.write(toPrint);
			} catch (IOException e) {

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
			outputFunction("File does not exist or bad file!\n");
			return (null);
		}
		return (matrix);
	}

	// Return (the double matinvalidmatrix/invalidmatrix.csv
	public static double[][] ReadingCsv(String filename)
	{
		File		file = new File(filename);
		double[][]	toReturn = null;
		Scanner		ReadFile;
		int			noOfLines;
		String[]	Csv;


		noOfLines = GetNumLinesNonEmpty(file); // not square and numlines

		if (noOfLines <= 0)
			return null;
		try {
			ReadFile = new Scanner(file);
		} catch (FileNotFoundException e) {
			return null;
		}

		for (int j = 0; j < noOfLines; j++)
		{
			Csv = (ReadFile.nextLine()).split("[,]");
			if (j == 0)
				toReturn = new double[noOfLines][Csv.length];
			for (int i = 0; i < Csv.length; i++)
			{
				toReturn[j][i] = Double.parseDouble(Csv[i]);
				System.out.printf("%.2f ", toReturn[j][i]);
			}
			System.out.println();
		}

		return (toReturn);
	}
	
	public static int GetNumLinesNonEmpty(File file)
	{
		String[]	splitedRead;
		String		read;
		int		noOfLines;
		int		fstLine;
		Scanner		ReadFile;

		noOfLines = 0;
		fstLine = 0;
		try {
			ReadFile = new Scanner(file);
		} catch (FileNotFoundException e) {
			return -1;
		}
		while (ReadFile.hasNextLine())
		{
			read = ReadFile.nextLine();
			if (read.isEmpty())
				break ;
			splitedRead = (read.trim()).split("[,]");
			if (fstLine == 0)
				fstLine = splitedRead.length;
			if (fstLine != splitedRead.length)
				return (-1);
			noOfLines++;
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
			minValue = matrix[0][0];
			coordinates = 0;
			for (int j = 0; j < matrix[0].length; j++) {
				if (Math.abs(matrix[j][j]) < minValue && !isValueInArray(j, arrayOfCoordinates)) {
					minValue = matrix[j][j];
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
		outputFunction(String.format("//Foram calculados %d valores e vetores próprios//\n", ownVs[1].length));

		outputFunction("\nMatriz de vetores próprios::\n");
		printMatrix(ownVs[0]);

		outputFunction("Matriz de valores próprios::\n");
		printMatrix(ownVs[1]);

		outputFunction("Matriz resultante da multiplicação das matrizes decompostas::\n");

		for (int i = 0; i < decompressedMatrix.length; i++)
			for (int j = 0; j < decompressedMatrix[0].length; j++)
				decompressedMatrix[i][j] = Math.round(decompressedMatrix[i][j]);

		printMatrix(decompressedMatrix);

		if (ownVs[0][0].length == decompressedMatrix.length || ownValues == -1) {
			outputFunction("O Erro Absoluto Médio é :: 0\n");
		} else {
			outputFunction("O Erro Absoluto Médio é :: " + String.format("%.2f",avgAbsolutError(matrix, decompressedMatrix)) + "\n");
		}
	}

    public static void doingFunctionOne(int ownValues, double[][] matrix)
    {
        double[][]         decompressedMatrix;
        double[][][]     ownVs;

        if (!(isSimetric(matrix)) && !(validMatrix(matrix)))
        {
            System.out.println("\nThis matrix isn't valid. Please make sure you have written the path to the right matrix.\n");
            return ;
        }

        outputFunction("\n|======================================================|");
        outputFunction("\n|             Output from functionality 1:             |");
        outputFunction("\n|======================================================|\n");

        ownVs = Decomposition(ownValues, matrix);
        decompressedMatrix = calculateDecompressedMatrix(ownVs[0], ownVs[1]);

        printDecomposition(ownVs, decompressedMatrix, ownValues, matrix);
        matrixToCSV(decompressedMatrix, PATH_WRITE_CSV + "outputFromExec.csv");
    }

	//=========2=========//
	public static void doingFunctionTwo(int precisionValues, String dirPath)
	{
		double[][]            reconstructionMatrix;


        double                averageAbsoluteError;
        double[]            averageVector;
        double[]            phi;
        double[][]            matrixInVector;
        double[][]            reverseCovarianceMatrix;
        double[][]            eigenVectors;
        String[]            PathToCompare;
        String[]            csvFilesInFolder;


        csvFilesInFolder = ReadingDir(dirPath);
        if(AllImgsInVector(csvFilesInFolder) == null){
            return ; 
        }
        matrixInVector = AllImgsInVector(csvFilesInFolder);
        averageVector = CalculateMediumVector(matrixInVector);
        reverseCovarianceMatrix = buildReverseCovarianceMatrix(matrixInVector, averageVector);
        eigenVectors = getEigenVectorsOfCovarianceMatrix(reverseCovarianceMatrix, matrixInVector, averageVector, precisionValues);
        reconstructionMatrix = BuildReconstructionMatrix(eigenVectors, precisionValues, averageVector, matrixInVector);

    for(int i = 0; i < reconstructionMatrix.length; i++){
        try{
            matrixToJPG(vectorToMatrix(reconstructionMatrix[i]), String.format("Output/img%d.jpg", i));
        }catch (IOException e){

        }
    }
        printMatrix(reconstructionMatrix);
	}

	public static void outputFunctionTwo(double[] avgVector, double[][] covMatrix, double[][] allWeights, double[][] reconstructionMatrix, double[][] eigenVectors ,int precisionValues){

		outputFunction("\n|======================================================|");
		outputFunction("\n|             Output from functionality 2:             |");
		outputFunction("\n|======================================================|\n");

		outputFunction(String.format("Number of eigenfaces used: %d", precisionValues));

		outputFunction("\nAverage Vector:\n");
		printVector(avgVector);

		outputFunction("\nCovariance Matrix\n");
		printMatrix(covMatrix);

		outputFunction("Weights used: \n");
		printMatrix(allWeights);

		matrixToCSV(eigenVectors, "Eigenfaces/eigenfaces.csv");


		for(int i = 0; i < reconstructionMatrix.length; i++){
			try{
				matrixToCSV(vectorToMatrix(reconstructionMatrix[i]), String.format(PATH_RECONSTRUCTION +"img%d.csv", i));
				matrixToJPG(vectorToMatrix(reconstructionMatrix[i]), String.format(PATH_RECONSTRUCTION +"img%d.jpg", i));
			}catch (IOException e){

			}
		}
	}


	public static double[][] AllImgsInVector(String[] files)
    {
        double[][]        allImgsInVector;
		double[][]			imgInMatrix;
        int                fileLength;

        fileLength = files.length;
        allImgsInVector = new double[fileLength][];
        for (int i = 0; i < fileLength; i++)
        { 
			imgInMatrix = CSVtoMatrix(files[i]);
			System.out.println(i);
            if(!(validMatrix(imgInMatrix)))
            {	
                System.out.println("\nThis matrix isn't valid. Please make sure you have written the path to the right directory.\n");
                return null;
            }


            allImgsInVector[i] = MatrixToVerticalVector(imgInMatrix);
        }
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

		return (mediumVector);
	}

	public static double[][] calculateAllPhis(double[][] allImagesMatrix, double[] mediumVector){
		int 			height;
		int 			width;
		double[][] 		matrix;
		double[]		adjustedMediumVector;

		height = allImagesMatrix.length;
		width = allImagesMatrix[0].length;
		matrix = new double[height][width];

		adjustedMediumVector = vectorMultConst(mediumVector, -1);
		for(int imgIndex = 0; imgIndex < height; imgIndex++)
			matrix[imgIndex] = vectorAdd(allImagesMatrix[imgIndex], adjustedMediumVector);

        return matrix;
	}

	public static double[][] buildReverseCovarianceMatrix(double[][] allImagesMatrix, double[] mediumVector)
	{
		double[][] 		matrix;
		double[][] 		reverseCovarianceMatrix;

		matrix = calculateAllPhis(allImagesMatrix, mediumVector);

		reverseCovarianceMatrix = matrixMulti(matrix, matrixTranspose(matrix));

		return reverseCovarianceMatrix;
	}

	public static double[][] getEigenVectorsOfCovarianceMatrix(double[][] reverseCovarianceMatrix,double[][] allImagesMatrix, double[] mediumVector,int ownValues){

		double[][][]		decomposedReverseCovarianceMatrix;
		double[][]			eigenVectorsOfReverseCovarianceMatrix;
		double[][]			eigenVectorsOfCovarianceMatrix;
		double[][]			allPhis;

		decomposedReverseCovarianceMatrix = Decomposition(ownValues, reverseCovarianceMatrix);
		eigenVectorsOfReverseCovarianceMatrix = decomposedReverseCovarianceMatrix[0];

		allPhis = calculateAllPhis(allImagesMatrix, mediumVector);

		eigenVectorsOfCovarianceMatrix = matrixMulti(matrixTranspose(allPhis), eigenVectorsOfReverseCovarianceMatrix);


		eigenVectorsOfCovarianceMatrix = matrixTranspose(eigenVectorsOfCovarianceMatrix);


		for (int i = 0; i < eigenVectorsOfCovarianceMatrix.length; i++) {
			eigenVectorsOfCovarianceMatrix[i] = normalizeVector(eigenVectorsOfCovarianceMatrix[i]);
		}

		return eigenVectorsOfCovarianceMatrix;
	}

	public static double[] normalizeVector(double[] vector){

		return vectorDivConst(vector, getVectorNorm(vector));
	}

	public static double getVectorNorm(double[] vector){
		double		sum;

		sum = 0;
		for (int i = 0; i < vector.length; i++) {
			sum += Math.pow(vector[i], 2);
		}
		if (sum == 0){
			return 0;
		}
		return Math.sqrt(sum);
	}

	public static double[][] BuildReconstructionMatrix(double[][] eigenVectors, int eigenVectorLength, double[] averageVector, double[][] allImagesInVector)
	{
		double[][] 		reconstructionMatrix;
		double[][] 		allWeights;
		double[][]		allPhis;


		reconstructionMatrix = new double[allImagesInVector.length][allImagesInVector[0].length];

		allPhis = calculateAllPhis(allImagesInVector, averageVector);
		allWeights = calculateAllWeights(allPhis, eigenVectors, allImagesInVector);


        for (int i = 0; i < allImagesInVector.length; i++) {
			for (int j = 0; j < eigenVectorLength; j++) {
				reconstructionMatrix[i] = vectorAdd(vectorMultConst(eigenVectors[j], allWeights[i][j]), reconstructionMatrix[i]);
			}
			reconstructionMatrix[i] = vectorAdd(reconstructionMatrix[i], averageVector);
		}

		return reconstructionMatrix;
	}

	public static double[][] calculateAllWeights(double[][] allPhis, double[][] eigenVectors, double[][] allImagesVector){

		double[][] 		allWeights;


		allWeights = new double[allImagesVector.length][eigenVectors.length];

		for (int i = 0; i < allImagesVector.length; i++) {
			allWeights[i] = calculateWeights(eigenVectors, allPhis[i]);
		}

		return allWeights;
	}

	//=========3=========//
	public static double[] calculateWeights(double[][] eigenVectors, double[] phi){
		double[] 		weights;

		weights = new double[eigenVectors.length];

		for (int i = 0; i < eigenVectors.length; i++) {
			weights[i] = vectorMulti(eigenVectors[i],phi);
		}

		return weights;
	}

    // change the function to receive the [][]matrix and no the path, so we can verify that earlier, before creating the allImgVectors
	public static double[] calculateNewPhi(String csvPath, double[] mediumVector){
        double[][]         imageMatrix;
        double[]         newPhi;
        double[]         imageVector;
        double[]        adjustedMediumVector;

        imageMatrix = CSVtoMatrix(csvPath);
        if (!validMatrix(imageMatrix))
            return (null);

        imageVector = MatrixToVerticalVector(imageMatrix);

        adjustedMediumVector = vectorMultConst(mediumVector, -1);

        newPhi = vectorAdd(imageVector, adjustedMediumVector); //subtraction

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

	public static void outputsThirdFunctionality(String csvPath, String[] files, int own_values, double[] allEuclideanDistances, int indexOfMinEuclideanDistance, double[] newWeights, double[][] allWeights)
	{
		outputFunction("\n|======================================================|");
		outputFunction("\n|             Output from functionality 3:             |");
		outputFunction("\n|======================================================|\n");

		outputFunction("Own values = " + own_values + "\n");

		outputFunction("\n\nOmega new vector:\n");
		printVector(newWeights);

		outputFunction("\n\nAll weights:\n");
		for(int i = 0; i < allWeights.length; i++){
			outputFunction(files[i]+":\t");
			printVector(allWeights[i]);
		}

		outputFunction("\n\nEuclidian Distances between images:\n");
		for (int i = 0; i < allEuclideanDistances.length; i++)
			outputFunction(files[i] + "\tand\t" + csvPath + " = " + allEuclideanDistances[i] + "\n");

		try {
			matrixToJPG(ReadingCsv(files[indexOfMinEuclideanDistance]), PATH_WRITE_JPG + "outputImgFromExec.jpg");
		} catch (IOException e) {
		}
	}

	public static void SearchClosest(int own_values, String csvPath, String dirPath)
	{
        String[] 		files;
        double[][] 		allImagesVector;
		double[] 		mediumVector;

		double[] 		newPhi;

        double[][] 		covarianceMatrix;
        double[][]		eiganVectors;

		double[] 		newWeights;
		double[][] 		allPhis;
		double[][] 		allWeights;

		double[] 		allEuclideanDistances;

		int 			indexOfMinEuclideanDistance;

		files = ReadingDir(dirPath);
		allImagesVector = AllImgsInVector(files);	
		if(allImagesVector == null){
			return ;
		}
		mediumVector = CalculateMediumVector(allImagesVector);
		newPhi = calculateNewPhi(csvPath, mediumVector);
		if(newPhi == null)
		{	
			return ;
		}

		covarianceMatrix = buildReverseCovarianceMatrix(allImagesVector, mediumVector);

		eiganVectors = getEigenVectorsOfCovarianceMatrix(covarianceMatrix, allImagesVector, mediumVector, own_values);

		newWeights = calculateWeights(eiganVectors, newPhi);

		allPhis = calculateAllPhis(allImagesVector, mediumVector);

		allWeights = new double[allImagesVector.length][allImagesVector[0].length];

		allEuclideanDistances = new double[allWeights.length];

		for (int i = 0; i < allImagesVector.length; i++) {
			allWeights[i] = calculateWeights(eiganVectors,allPhis[i]);
		}

		for (int i = 0; i < allEuclideanDistances.length; i++) {
			allEuclideanDistances[i] = calculateEuclideanDistance(newWeights, allWeights[i]);
		}

		indexOfMinEuclideanDistance = getIndexOfMinValueInArray(allEuclideanDistances);
		outputsThirdFunctionality(csvPath, files, own_values, allEuclideanDistances, indexOfMinEuclideanDistance, newWeights, allWeights);
	}

	public static double[][] vectorToMatrix(double[] vector){
		int matrixSidesLen;

		matrixSidesLen = (int) Math.sqrt(vector.length);

		double[][] matrix = new double[matrixSidesLen][matrixSidesLen];

		int columnOffset = 0;
		for(int i = 0; i < matrixSidesLen; i++){
			for(int j = 0; j < matrixSidesLen; j++){
				matrix[j][i] = vector[columnOffset+j];
			}
			columnOffset += matrixSidesLen;
		}

		return matrix;
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
		int 	 columnNumber;
		int 	 rowNumber;
		int 	 numberOfElements;
		double[] verticalMatrix;
		int 	 index = 0;

		if(matrix == null)
			return (null);
		columnNumber = matrix.length;
		rowNumber = matrix.length;
		numberOfElements = (columnNumber * rowNumber);
		verticalMatrix = new double[numberOfElements];
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

		if(vector1.length != vector2.length){
			return null;
		}

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
		double[]	multipliedVector;

		vectorLen = vector1.length;
		multipliedVector = new double[vectorLen];
		for (int i = 0; i < vectorLen; i++)
			multipliedVector[i] = vector1[i] * value;
		return (multipliedVector);
	}

	//=============Display Matrix=============//
	public static void printMatrix(double[][] matrix)
	{
		for (int rows = 0; rows < matrix.length; rows++) {
			for (int columns = 0; columns < matrix[0].length; columns++)
			{
				outputFunction(String.format("%.0f ", matrix[rows][columns]));
            }
			outputFunction("\n");

		}
		outputFunction("\n");
	}

	public static void printVector(double[] vector)
	{
		for (int i = 0; i < vector.length; i++) {
			outputFunction(String.format("%.0f ", vector[i]));
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

					if(j != width - 1){
						line += ",";
					}
				}
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

	public static void matrixToJPG(double[][] matrix, String outputFilePath) throws IOException
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
				int intensity = (int) Math.round(matrix[y][x]);
				if (intensity < MIN_VALUE_IN_CSV ) {
					intensity = 0;
				}else if (intensity > MAX_VALUE_IN_CSV){
					intensity = 255;
				}
				int rgb = (intensity << 16) | (intensity << 8) | intensity; // Set the same value for R, G, B
				image.setRGB(x, y, rgb);
			}
		}

		// Write the image to the file
		File outputFile = new File(outputFilePath);
		ImageIO.write(image, "jpg", outputFile);
	}

	//===================Status Verifier===================//
	public static boolean isSimetric(double[][] matrix) {
		if (matrixEquals(matrix, matrixTranspose(matrix))) {
			return true;
		}
		return false;
	}

	public static boolean isSquared(double[][] matrix){
		if (matrix.length == matrix[0].length) {
			return true;
		}
		return false;
	}

	public static boolean matrixEquals(double[][] matrix1, double[][] matrix2)
	{
		if(matrix1.length != matrix2.length)
			return (false);
		if(matrix1[0].length != matrix2[0].length)
			return (false);

		for (int i = 0; i < matrix1.length; i++)
		{
			for (int k = 0; k < matrix1[i].length; k++)
			{
				if(matrix1[i][k] != matrix2[i][k])
					return (false);
			}
		}
		return (true);
	}

	public static boolean vectorEquals(double[] vector1, double[] vector2)
	{
		if(vector1.length != vector2.length)
			return (false);

		for (int i = 0; i < vector1.length; i++)
		{
			if(vector1[i] != vector2[i])
				return (false);
		}
		return (true);
	}

	public static boolean isOwnValueValid(int ownValue){

		if (ownValue == MIN_OWN_VALUE){
			return true;
		} else return ownValue > 0;
	}

	// true if valid
	public static boolean validMatrix(double[][] matrix)
    {
		if (matrix == null)
			return false;
        if (isSquared(matrix) && matrix.length <= 256)
            return true;
        return false;
    }
}