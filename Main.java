
import	java.util.Scanner;
import java.io.*;

import org.apache.commons.math3.linear.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Main{
	public static boolean			hasOutputInFile = false;
	public static String			outputFilePath;
	public static BufferedWriter	outputFile;

	//=======new values=====//
	public static final int FUNC_1 = 1;
	public static final int FUNC_2 = 2;
	public static final int FUNC_3 = 3;
	public static final int FUNC_4 = 4;
	public static final int MAX_SIZE_IMG = 256;
	public static final int MAX_VALUE_IN_CSV = 255;
	public static final int MIN_VALUE_IN_CSV = 0;
	public static final String PATH_WRITE_JPG = "Identificacao/";
	public static final String PATH_WRITE_CSV = "Output/";
	public static final String PATH_RECONSTRUCTION = "ImagensReconstruidas/";
	public static final String PATH_EIGENFACES = "Eigenfaces/eigenfaces.csv";

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
				if (!CheckingArgs(arguments, FUNC_1))
				{
					outputFunction("You've entered some wrong argument!! Check it and try again!!\n");
					return;
				}

				ownValues = Integer.parseInt(arguments[3]);
				if (!isOwnValueValid(ownValues)){
					System.out.println("Valor próprio inválido (k), este tem que ser um número inteiro positivo ou <-1>");
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
				if (!CheckingArgs(arguments, FUNC_2))
				{
					outputFunction("You've entered some wrong argument!! Check it and try again!!\n");
					return ;
				}

				ownValues = Integer.parseInt(arguments[3]);
				if (!isOwnValueValid(ownValues)){
					System.out.println("Valor próprio inválido (k), este tem que ser um número inteiro positivo ou -1");
					return ;
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

				break;
            case FUNC_3:
				if (!CheckingArgs(arguments, FUNC_3))
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
        }
    }

	public static boolean CheckingArgs(String[] arguments, int whichExec)
	{
		if (whichExec == FUNC_1)
		{
			if(!(arguments[2].equals("-k")))
				return (false);
			if(!(arguments[4].equals("-i")))
				return (false);
			if (arguments.length != 7)
				return (false);
		}
		else if (whichExec == FUNC_2)
		{
			if(!(arguments[2].equals("-k")))
				return (false);
			if(!(arguments[4].equals("-d")))
				return (false);
			if (arguments.length != 7)
				return (false);
		}
		else if (whichExec == FUNC_3)
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
			switch (type) {
				case FUNC_1:
					double[][] matrix;
					ownValues = OwnValues();
					path = GetPath("|Entre o caminho para o ficheiro:|\n");
					matrix = CSVtoMatrix(path);
					if(matrix == null)
					{
						outputFunction("Entrou um mal ficheiro .csv \n");
						return ;
					}
					doingFunctionOne(ownValues, matrix);
					break;
				case FUNC_2:
					ownValues = OwnValues();
					dirPath = GetPath("|Entre caminho para o diretório:|\n");
					doingFunctionTwo(ownValues, dirPath);
					break;
				case FUNC_3:
					ownValues = OwnValues();
					path = GetPath("|Entre o caminho para o ficheiro:|\n");
					dirPath = GetPath("|Entre caminho para o diretório:|\n");
					SearchClosest(ownValues, path, dirPath);
					break ;
				case FUNC_4:
					unitTest();
					break;
			}
		}
		outputFunction("Program ending... Thanks for using it!!\n");
	}

	public static int TypeOfExecution()
	{
		int	read;

		read = 100;
		outputFunction("-----------------------------------------\n");
		outputFunction("|            Menu of execution          |\n");
		outputFunction("-----------------------------------------\n");
		outputFunction("|Entre Tipo de Execução:                |\n");
		outputFunction("|(0) Sair do Programa                   |\n");
		outputFunction("|(1) Decomposição de Imagens            |\n");
		outputFunction("|(2) Reconstrução de imagens            |\n");
		outputFunction("|(3) Identificar mais próximo           |\n");
		outputFunction("|(4) Testes Unitários                   |\n");
		outputFunction("-----------------------------------------\n");

		while(!(read <= MAX_TYPE_EXEC && read >= MIN_TYPE_EXEC))
		{
			read = input.nextInt();
			if (!(read <= MAX_TYPE_EXEC && read >= MIN_TYPE_EXEC))
				outputFunction("Entrou um argumento errado. Tente novamente: \n");
		}
		return (read);
	}

	public static int OwnValues()
	{
		int	read;

		read = MIN_OWN_VALUE - 1;
		outputFunction("---------------------------------------------------------------\n");
		System.out.print("|Entre o número de valores próprios a serem utilizadosEnter:|\n");
		outputFunction("---------------------------------------------------------------\n");
		while (read == 0 || read < MIN_OWN_VALUE)
		{
			read = input.nextInt();
			input.nextLine();
			if(read <= 0 && read != MIN_OWN_VALUE)
				outputFunction("Vocẽ precisa de entrar um número positivo ou <-1>. Tente novamente!\n");
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

	//=============Testes Unitarios==============//
	public static void unitTest()
	{
		System.out.println("==================================");
		System.out.println("\t\tTeste Unitarios\t\t");
		System.out.println("==================================");
		System.out.println("1-testIsSimetric");
			if (testIsSimetric(null, hasOutputInFile) == true)
				System.out.println("✅");
			else
				System.out.println("❌");
			if (testIsSimetric(null, hasOutputInFile) == true) //
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("2-testIsSquared");
			if (testIsSquared() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
			if (testIsSquared() == true) //
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("3-testIsValueInArray");
			if (testIsValueInArray() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
			if (testIsValueInArray() == true) //
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("4-testGetCoordinatesOfMinValuesOfDiagonalMatrix");
			if (testGetCoordinatesOfMinValuesOfDiagonalMatrix() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
			if (testGetCoordinatesOfMinValuesOfDiagonalMatrix() == true) //
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("5-testGetEigenValuesSubMatrix");
			if (testGetEigenValuesSubMatrix() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("6-testGetEigenVectorsSubMatrix");
			if (testGetEigenVectorsSubMatrix() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("7-testAvgAbsolutError");
			if (testAvgAbsolutError() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("8-testCalculateDecompressedMatrix");
			if (testCalculateDecompressedMatrix() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("9-testCalculateMediumVector");
			if (testCalculateMediumVector() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("11-testCalculateAllPhis");
			if (testCalculateAllPhis() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("12-testBuildReverseCovarianceMatrix");
			if (testBuildReverseCovarianceMatrix() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("13-testGetEigenVectorsOfCovarianceMatrix");
			if (testGetEigenVectorsOfCovarianceMatrix() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("14-testBuildReconstructionMatrix");
			if (testBuildReconstructionMatrix() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("15-testCalculateAllWeights");
			if (testCalculateAllWeights() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("16-TestCalculateWeights");
			if (TestCalculateWeights() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("17-testCalculateNewPhi");
			if (testCalculateNewPhi() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("18-testCalculateEuclideanDistance");
			if (testCalculateEuclideanDistance() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("19-getIndexOfMinValueInArray");
			if (getIndexOfMinValueInArray() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("20-testMatrixAdd");
			if (testMatrixAdd() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("21-testMatrixMulti");
			if (testMatrixMulti() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("22-testMatrixDivConst");
			if (testMatrixDivConst() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("23-testMatrixTranspose");
			if (testMatrixTranspose() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("24-testVectorAdd");
			if (testVectorAdd() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("25-testScalarProduct");
			if (testScalarProduct() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("26-testVectorDivConst");
			if (testVectorDivConst() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("27-testVectorMultConst");
			if (testVectorMultConst() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("28-testNormalizeVector");
			if (testNormalizeVector() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("29-testGetVectorNorm");
			if (testGetVectorNorm() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
		System.out.println("30-testVectorToMatrix");
		if (testVectorToMatrix() == true)
				System.out.println("✅");
			else
				System.out.println("❌");
	}

	//=========Matrix Read=========//
	public static double[][] CSVtoMatrix(String filename)
	{
		double[][]	matrix;

		matrix = ReadingCsv(filename);
		if (matrix == null)
		{
			outputFunction("Ficheiro não existe ou formato incorreto!\n");
			return (null);
		}
		return (matrix);
	}

	// Return (the double matrix) if it has something in the file, (null) if it has nothing
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
			}
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

		double[][] 		eigenVectorsSubMatrix;
		double[][] 		eigenValuesSubMatrix;

		int[] 			arrayOfCoordinatesOfMinOwnValues;
		int 			numberValuesToRemove;
		int 			totalNumberOfOwnValues;


		EigenDecomposition eigenDecompositor;
		RealMatrix 		eigenVectorsApache;
		RealMatrix 		eigenValuesApache;

		eigenDecompositor = new EigenDecomposition(transformDoubleMatrixToRealMatrix(covarianceMatrix));

		eigenVectorsApache = eigenDecompositor.getV();
		eigenValuesApache = eigenDecompositor.getD();


		eiganVectors = eigenVectorsApache.getData();
		eiganValues = eigenValuesApache.getData();

		totalNumberOfOwnValues = eiganValues.length;


		if (own_values < totalNumberOfOwnValues && own_values != -1) {
			numberValuesToRemove = totalNumberOfOwnValues - own_values;
			arrayOfCoordinatesOfMinOwnValues = getCoordinatesOfMinValuesOfDiagonalMatrix(eiganValues, numberValuesToRemove);

			eigenVectorsSubMatrix = getEigenVectorsSubMatrix(arrayOfCoordinatesOfMinOwnValues, eiganVectors);
			eigenValuesSubMatrix = getEigenValuesSubMatrix(arrayOfCoordinatesOfMinOwnValues, eiganValues);

			resultMatrix = new double[][][]{eigenVectorsSubMatrix, eigenValuesSubMatrix};
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
		double[][] 		intermediaryMatrix;

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
            System.out.println("\nEsta matriz não é válida. Por favor certifica-se que o caminho dado corresponde à matriz correta.\n");
            return ;
        }

        ownVs = Decomposition(ownValues, matrix);
        decompressedMatrix = calculateDecompressedMatrix(ownVs[0], ownVs[1]);

        printDecomposition(ownVs, decompressedMatrix, ownValues, matrix);
        matrixToCSV(decompressedMatrix, PATH_WRITE_CSV + "outputFromExec.csv");
    }

	//=========2=========//
	public static void doingFunctionTwo(int precisionValues, String dirPath)
	{
		double[][]		reconstructionMatrix;

        double[]		averageVector;
        double[][]		matrixInVector;
		double[][]		allPhis;
		double[][]		allWeights;
        double[][]      reverseCovarianceMatrix;
        double[][]      eigenVectors;
        String[]        csvFilesInFolder;


        csvFilesInFolder = ReadingDir(dirPath);
        if(AllImgsInVector(csvFilesInFolder) == null){
            return ;
        }

        matrixInVector = AllImgsInVector(csvFilesInFolder);

        averageVector = CalculateMediumVector(matrixInVector);
		allPhis = calculateAllPhis(matrixInVector, averageVector);

		reverseCovarianceMatrix = buildReverseCovarianceMatrix(allPhis);

		eigenVectors = getEigenVectorsOfCovarianceMatrix(reverseCovarianceMatrix, allPhis, precisionValues);
		allWeights = calculateAllWeights(allPhis, eigenVectors, matrixInVector);


        reconstructionMatrix = BuildReconstructionMatrix(eigenVectors, allPhis, allWeights, averageVector, eigenVectors.length);

		outputFunctionTwo(averageVector, reverseCovarianceMatrix, allWeights, reconstructionMatrix, eigenVectors);
	}

	public static void outputFunctionTwo(double[] avgVector, double[][] reverseCovMatrix, double[][] allWeights, double[][] reconstructionMatrix, double[][] eigenVectors){

		outputFunction("\n|======================================================|");
		outputFunction("\n|              Saída da funcionalidade 2:              |");
		outputFunction("\n|======================================================|\n");

		outputFunction(String.format("Número de eigenfaces usadas: %d", eigenVectors.length));

        outputFunction("\nVetor Médio:\n");
		printVector(avgVector);

		outputFunction("\nMatriz de Covariância\n");
		printMatrix(reverseCovMatrix);

		outputFunction("Pesos Usados: \n");
		printMatrix(allWeights);

//		matrixToCSV(eigenVectors, PATH_EIGENFACES);

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
            if(!(validMatrix(imgInMatrix)))
            {
                System.out.println("\nEsta matriz não é válida. Por favor certifique-se de que introduziu o caminho correto para o diretório pretendido.\n");
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

		vectorDivConst(mediumVector, manyVectors);

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

	public static double[][] buildReverseCovarianceMatrix(double[][] allPhis)
	{
		double[][] 		matrix;

		matrix = matrixMulti(allPhis, matrixTranspose(allPhis));

		return matrix;
	}

	public static double[][] getEigenVectorsOfCovarianceMatrix(double[][] reverseCovarianceMatrix, double[][] allPhis, int precisionValues){

		double[][][]		decomposedReverseCovarianceMatrix;
		double[][]			eigenVectorsOfReverseCovarianceMatrix;
		double[][]			eigenVectorsOfCovarianceMatrix;

		decomposedReverseCovarianceMatrix = Decomposition(precisionValues, reverseCovarianceMatrix);
		eigenVectorsOfReverseCovarianceMatrix = decomposedReverseCovarianceMatrix[0];

		eigenVectorsOfCovarianceMatrix = matrixMulti(matrixTranspose(allPhis), eigenVectorsOfReverseCovarianceMatrix);


		eigenVectorsOfCovarianceMatrix = matrixTranspose(eigenVectorsOfCovarianceMatrix);

		matrixToCSV(eigenVectorsOfCovarianceMatrix, PATH_EIGENFACES);


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
        for (double value : vector) {
            sum += Math.pow(value, 2);
        }
		if (sum == 0){
			return 0;
		}
		return Math.sqrt(sum);
	}

	public static double[][] BuildReconstructionMatrix(double[][] eigenVectors, double[][] allPhis, double[][] allWeights, double[] averageVector, int eigenVectorLength)
	{
		double[][] 		reconstructionMatrix;

		reconstructionMatrix = new double[allPhis.length][allPhis[0].length];

        for (int i = 0; i < allPhis.length; i++) {
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
			weights[i] = scalarProduct(eigenVectors[i],phi);
		}

		return weights;
	}

	public static double[] calculateNewPhi(double[] imageVector, double[] mediumVector){
        double[]         newPhi;
        double[]        adjustedMediumVector;

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
		outputFunction("\n|              Saída da funcionalidade 3:              |");
		outputFunction("\n|======================================================|\n");

		outputFunction("Valores Próprios = " + own_values + "\n");

		outputFunction("\n\nVetor Omega novo:\n");
		printVector(newWeights);

		outputFunction("\n\nTodos os Pesos:\n");
		for(int i = 0; i < allWeights.length; i++){
			outputFunction(files[i]+":\t");
			printVector(allWeights[i]);
		}

		outputFunction("\n\nDistância Euclidiana entre imagens:\n");
		for (int i = 0; i < allEuclideanDistances.length; i++)
			outputFunction(files[i] + "\te\t" + csvPath + " = " + allEuclideanDistances[i] + "\n");

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
		double[][]		imageMatrix;
		double[]		imageVector;

        double[][] 		reverseCovarianceMatrix;
        double[][]		eigenVectors;

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

		imageMatrix = CSVtoMatrix(csvPath);
		if (!validMatrix(imageMatrix))
			return ;

		imageVector = MatrixToVerticalVector(imageMatrix);

		newPhi = calculateNewPhi(imageVector, mediumVector);
		if(newPhi == null)
		{
			return ;
		}

		allPhis = calculateAllPhis(allImagesVector, mediumVector);

		reverseCovarianceMatrix = buildReverseCovarianceMatrix(allPhis);

		eigenVectors = getEigenVectorsOfCovarianceMatrix(reverseCovarianceMatrix, allPhis, own_values);

		newWeights = calculateWeights(eigenVectors, newPhi);


		allWeights = new double[allImagesVector.length][allImagesVector[0].length];

		allEuclideanDistances = new double[allWeights.length];

		for (int i = 0; i < allImagesVector.length; i++) {
			allWeights[i] = calculateWeights(eigenVectors,allPhis[i]);
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

	public static double scalarProduct(double[] vector1, double[] vector2)
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
			outputFunction("Não foi possível criar o ficheiro!\n");
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
	public static boolean isSimetric(double[][] matrix){
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
        if (isSquared(matrix) && matrix.length <= MAX_SIZE_IMG)
            return true;
        return false;
    }

	//===================Unit tests===================//
	//=============General Operations=============//

	public static boolean testIsSimetric(double[][] matrix, boolean expected)
	{
		boolean result = isSimetric(matrix);

		return (result == expected);
	}

	public static boolean testIsSquared(double[][] matrix, boolean expected)
	{
		boolean result = isSquared(matrix);

		return result == expected;
	}

	public static boolean testIsValueInArray(double value, int[] array, boolean expected) {

		return (expected == isValueInArray(value, array));

	}

	public static boolean testGetCoordinatesOfMinValuesOfDiagonalMatrix(double[][] matrix, int value, int[] expected)
	{

		int[] result = getCoordinatesOfMinValuesOfDiagonalMatrix(matrix, value);

		if(result.length == expected.length){
			for(int i = 0; i < result.length; i++){
				if(result[i] != expected[i])
					return false;
			}
		}else{
			return false;
		}

		return true;
	}

	public static boolean testGetEigenValuesSubMatrix(int[] arrayOfCoordinates, double[][] matrix, double[][] expected)
	{
		double[][] result = getEigenValuesSubMatrix(arrayOfCoordinates, matrix);

		return matrixEquals(result, expected);
	}

	public static boolean  testGetEigenVectorsSubMatrix(int[] arrayOfCoordinates, double[][] matrix, double[][] expected){
		double[][] result = getEigenVectorsSubMatrix(arrayOfCoordinates, matrix);

		return matrixEquals(result, matrix);
	}

	public static boolean testAvgAbsolutError(double[][] matrix1, double[][] matrix2, double expected)
	{
		double result = avgAbsolutError(matrix1, matrix2);

		if(result == expected)
			return true;
		else
			return false;
	}

	public static boolean testCalculateDecompressedMatrix(double[][] eigenVectors, double[][] eigenValues, double[][] expected)
	{
		double[][] result = calculateDecompressedMatrix(eigenVectors, eigenValues);

		if(matrixEquals(result, expected))
			return true;
		else
			return false;
	}

	public static boolean testCalculateMediumVector(double[][] allImgsInVector, double[] expected){
		double[] result = CalculateMediumVector(allImgsInVector);

		return vectorEquals(result, expected);
	}

	public static boolean testCalculateAllPhis(double[][] allImagesMatrix, double[] mediumVector, double[][] expected){

		double[][] result = calculateAllPhis(allImagesMatrix, mediumVector);

		return matrixEquals(result, expected);
	}

	public static boolean testBuildReverseCovarianceMatrix(double[][] allPhis, double[][] expected){
		double[][] result = buildReverseCovarianceMatrix(allPhis);

		return matrixEquals(result, expected);
	}

	public static boolean testGetEigenVectorsOfCovarianceMatrix(double[][] reverseCovarianceMatrix,double[][] allPhis, int ownValues, double[][] expected){
		double[][] result = getEigenVectorsOfCovarianceMatrix(reverseCovarianceMatrix, allPhis, ownValues);

		return matrixEquals(result, expected);
	}

	public static boolean testBuildReconstructionMatrix(double[][] eigenVectors, int eigenVectorLength, double[][] allPhis, double[][] allWeights, double[]averageVector, double[][] expected){
		double[][] result = BuildReconstructionMatrix(eigenVectors, allPhis, allWeights, averageVector, eigenVectorLength);

		return matrixEquals(result, expected);
	}

	public static boolean testCalculateAllWeights(double[][] allPhis, double[][] eigenVectors, double[][] allImagesVector, double[][] expected){
		double[][] result = calculateAllWeights(allPhis, eigenVectors, allImagesVector);

		return matrixEquals(result, expected);
	}

	public static boolean TestCalculateWeights(double[][] eigenVectors, double[] phi, double[] expected){
		double[] result = calculateWeights(eigenVectors, phi);

		return vectorEquals(result, expected);
	}

	public static boolean testCalculateNewPhi(double[] imageVector, double[] mediumVector, double[] expected){
		double[] result = calculateNewPhi(imageVector, mediumVector);

		return vectorEquals(result, expected);
	}

	public static boolean testCalculateEuclideanDistance(double[] vector1, double[] vector2, double expected){
		double result = calculateEuclideanDistance(vector1, vector2);

		return (result == expected);
	}

	public static boolean getIndexOfMinValueInArray(double[] array, int expected){
		int result = getIndexOfMinValueInArray(array);

		return (result == expected);
	}

	//===================Matrix===================//

	public static boolean testMatrixAdd(double[][] matrix1, double[][] matrix2, double[][] expected)
	{
		matrix1 = matrixAdd(matrix1, matrix2);
		return (matrixEquals(matrix1, expected));
	}

	public static boolean testMatrixMulti(double[][] matrix1, double[][] matrix2, double[][] expected)
	{
		matrix1 = matrixMulti(matrix1, matrix2);
		return (matrixEquals(matrix1, expected));
	}

	public static boolean testMatrixDivConst(double[][] matrix1, int value, double[][] expected)
	{

		matrix1 = matrixMultiConst(matrix1, value);
		return (matrixEquals(matrix1, expected));
	}


	public static boolean testMatrixTranspose(double[][] matrix1,  double[][] expected)
	{
		matrix1 = matrixTranspose(matrix1);
		return (matrixEquals(matrix1, expected));
	}

	//===================Vectors===================//

	public static boolean testVectorAdd(double[] vector1, double[] vector2, double[] expected)
	{
		vector1 = vectorAdd(vector1, vector2);
		return (vectorEquals(vector1,expected));
	}

	public static boolean testScalarProduct(double[] vector1, double[] vector2, double expected)
	{
		double result = scalarProduct(vector1, vector2);

		if(result == expected)
			return true;
		else
			return false;
	}

	public static boolean testVectorDivConst(double[] vector, int value, double[] expected)
	{
		double[] result = vectorDivConst(vector, value);
		return (vectorEquals(result, expected));
	}

	public static boolean testVectorMultConst(double[] vector1, int value, double[] expected)
	{
		double[] result = vectorMultConst(vector1, value);
		return (vectorEquals(result,expected));
	}

	public static boolean testNormalizeVector(double[] vector, double[] expected){
		double[] result = normalizeVector(vector);

		return vectorEquals(vector, expected);
	}

	public static boolean testGetVectorNorm(double[] vector, double expected){
		double result = getVectorNorm(vector);

		return (result == expected);
	}

	public static boolean testVectorToMatrix(double[] vector, double[][] expected){
		double[][] result = vectorToMatrix(vector);

		return matrixEquals(result, expected);
	}



}
