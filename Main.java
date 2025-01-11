
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
	public static final String PATH_WRITE_JPG = "Identificacao/";
	public static final String PATH_WRITE_CSV = "Output/";
	public static final String PATH_RECONSTRUCTION = "ImagensReconstruidas/";
	public static final String PATH_EIGENFACES = "Eigenfaces/eigenfaces";
	public static final int INITIAL_IMAGE_NUMBER = 0;

	public static final int MIN_OWN_VALUE = -1;
	public static final int MIN_TYPE_EXEC = 0;
	public static final int MAX_TYPE_EXEC = 4;

    public static final boolean PRINTDECIMAL = true;
    public static final boolean PRINTINT = false;

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
		int				imageNumber;

		if (arguments[0].equals("-h"))
		{
			outputFunction(
					"\033[31m" + "\nUsar programa desta forma: java -jar nome programa.jar -f X -k Y -i Z -d W saida.txt:\n\n" +
					"- o valor associado ao parâmetro f identifica a funcionalidade a executar. X toma os\n" +
					"valores 1, 2, 3, ou 4 representando, respetivamente, as funcionalidades de decomposição\n" +
					"própria de matriz, a reconstrução de imagens utilizando eigenfaces, a identificação\n" +
					"de imagens utilizando pesos das eigenfaces e gerar uma imagem.\n\n" +
					"- o valor associado ao parâmetro k identifica o número de vetores próprios / eigenfaces\n" +
					"a utilizar na decomposição/reconstrução/identificação. Y toma valores inteiros\n" +
					"positivos e -1. Caso o valor de Y seja -1 ou um valor superior ao número de valores\n" +
					"próprios reais existentes, na decomposição/reconstrução/identificação/geração devem ser\n" +
					"utilizados todos os valores próprios reais da matriz.\n\n" +
					"- o valor associado ao parâmetro i identifica a localização do ficheiro CSV onde está\n" +
					"localizada a matriz/imagem de input a utilizar nas funcionalidade 1 e 3. Para\n" +
					"executar a funcionalidade 2 e 4 não é necessário especificar este parâmetro.\n\n" +
					"- o valor associado ao parâmetro d identifica a localização da base de imagens a\n" +
					"utilizar nas funcionalidade 2, 3 e 4. Para executar a funcionalidade 1 não é necessário\n" +
					"especificar este parâmetro.\n\n" +
							"Em caso de dúvidas, use o método interativo!\n\n" + "\033[0m");
			return ;
		}
		else if (!arguments[0].equals("-f"))
		{
			outputFunction("Foi introduzido um argumento errado. Verique e teste novamente!\n");
			return ;
		}
		type = Integer.parseInt(arguments[1]);
		imageNumber = INITIAL_IMAGE_NUMBER;
		switch (type) {
			case FUNC_1:
				if (!CheckingArgs(arguments, FUNC_1))
				{
					outputFunction("Foi introduzido um argumento errado. Verique e teste novamente!\n");
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
					printProgramName();
					doingFunctionOne(ownValues, CSVtoMatrix(path));
					outputFile.close();
				}catch (IOException e) {
					outputFunction("Foi impossível escrever no ficheiro, a fechar o programa!");
				}
				break ;
			case FUNC_2:
				if (!CheckingArgs(arguments, FUNC_2))
				{
					outputFunction("Foi introduzido um argumento errado. Verique e teste novamente!\n");
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
					printProgramName();
					doingFunctionTwo(ownValues, dirPath);
					outputFile.close();
				} catch (IOException e) {
					outputFunction("Foi impossível escrever no ficheiro, a fechar o programa!");
				}

				break;
            case FUNC_3:
				if (!CheckingArgs(arguments, FUNC_3))
				{
					outputFunction("Foi introduzido um argumento errado. Verique e teste novamente!\n");
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
					printProgramName();
					SearchClosest(ownValues, path, dirPath);
					outputFile.close();
				} catch (IOException e) {
					outputFunction("Foi impossível escrever no ficheiro, a fechar o programa!");
				}

				break ;
			case FUNC_4:
				if (!CheckingArgs(arguments, FUNC_4))
				{
					outputFunction("Entrou um argumento errado. Cheque e teste novamente!\n");
					return ;
				}

				ownValues = Integer.parseInt(arguments[3]);
				if (!isOwnValueValid(ownValues)){
					System.out.println("Valor próprio inválido (k), este tem que ser um número inteiro positivo ou -1");
					return ;
				}
				dirPath = arguments[5];

                generateImage(ownValues, dirPath, imageNumber);

                break;
			default:
				outputFunction("Foi introduzido um argumento errado. Verique e teste novamente!\n");
        }
    }

	public static boolean CheckingArgs(String[] arguments, int whichExec)
	{
		if (whichExec == FUNC_1)
		{
			if (arguments.length != 7)
				return (false);
			if(!(arguments[2].equals("-k")))
				return (false);
			if(!(arguments[4].equals("-i")))
				return (false);
		}
		else if (whichExec == FUNC_2)
		{
			if (arguments.length != 7)
				return (false);
			if(!(arguments[2].equals("-k")))
				return (false);
			if(!(arguments[4].equals("-d")))
				return (false);
		}
		else if (whichExec == FUNC_3)
		{
			if (arguments.length != 9)
				return (false);
			if(!(arguments[2].equals("-k")))
				return (false);
			if(!(arguments[4].equals("-i")))
				return (false);
			if(!(arguments[6].equals("-d")))
				return (false);
		}
		else if (whichExec == FUNC_4)
		{
			if (arguments.length != 6)
				return (false);
			if(!(arguments[2].equals("-k")))
				return (false);
			if(!(arguments[4].equals("-d")))
				return (false);
		}
		return (true);
	}

	//============Iterative exec and reading=============//
	public static void printProgramName()
	{
		outputFunction("\u001B[32m" +
				"===================================================\n" +
				" _____ _                  _____ _               _   \n" +
				"|  ___(_)                |  __ \\ |             | |  \n" +
				"| |__  _  __ _  ___ _ __ | |  \\/ |__   ___  ___| |_ \n" +
				"|  __|| |/ _` |/ _ \\ '_ \\| | __| '_ \\ / _ \\/ __| __|\n" +
				"| |___| | (_| |  __/ | | | |_\\ \\ | | | (_) \\__ \\ |_ \n" +
				"\\____/|_|\\__, |\\___|_| |_|\\____/_| |_|\\___/|___/\\__|\n" +
				"          __/ |                                     \n" +
				"         |___/                                      \n" +
				"====================================================\n" +
				"\u001B[0m");
	}

	public static void interactive()
	{
		int			type;
		int			ownValues;
		String		path;
		String		dirPath;
		int			imageNumber;

		printProgramName();
		type = 1;
		outputFile = null;
		imageNumber = INITIAL_IMAGE_NUMBER;
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
						outputFunction("Entrou um ficheiro .csv incorreto\n");
						return ;
					}
					doingFunctionOne(ownValues, matrix);
					break;
				case FUNC_2:
					ownValues = OwnValues();
					dirPath = GetPath("|Indique caminho para o diretório:|\n");
					doingFunctionTwo(ownValues, dirPath);
					break;
				case FUNC_3:
					ownValues = OwnValues();
					path = GetPath("|Indique o caminho para o ficheiro:|\n");
					dirPath = GetPath("|Indique caminho para o diretório:|\n");
					SearchClosest(ownValues, path, dirPath);
					break;
				case FUNC_4:
					ownValues = OwnValues();
					dirPath = GetPath("|Indique caminho para o diretório:|\n");
					imageNumber = generateImage(ownValues, dirPath, imageNumber);
					break;
				default:
			}
		}
		outputFunction("A fechar... Obrigado por usar o nosso programa!\n");
	}

	public static int TypeOfExecution()
	{
		int	read;

		read = 100;
		outputFunction("-----------------------------------------\n");
		outputFunction("|            Menu de Execução           |\n");
		outputFunction("-----------------------------------------\n");
		outputFunction("|Entre Tipo de Execução:                |\n");
		outputFunction("|(0) Sair do Programa                   |\n");
		outputFunction("|(1) Decomposição de Imagens            |\n");
		outputFunction("|(2) Reconstrução de imagens            |\n");
		outputFunction("|(3) Identificar mais próximo           |\n");
		outputFunction("|(4) Gerar Imagem                       |\n");
		outputFunction("-----------------------------------------\n");

		while(!(read <= MAX_TYPE_EXEC && read >= MIN_TYPE_EXEC))
		{
			read = input.nextInt();
			if (!(read <= MAX_TYPE_EXEC && read >= MIN_TYPE_EXEC))
				outputFunction("Argumento errado encontrado. Tente novamente: \n");
		}
		return (read);
	}

	public static int OwnValues()
	{
		int	read;

		read = MIN_OWN_VALUE - 1;
		outputFunction("--------------------------------------------------------\n");
		System.out.print("|Entre o número de valores próprios a serem utilizados:|\n");
		outputFunction("--------------------------------------------------------\n");
		while (read == 0 || read < MIN_OWN_VALUE)
		{
			read = input.nextInt();
			input.nextLine();
			if(read <= 0 && read != MIN_OWN_VALUE)
				outputFunction("É necessário introduzir um número positivo ou <-1>. Tente novamente!\n");
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

	public static int[] getCoordsMinValuesOfDiagonalMatrix(double[][] matrix, int numberOfValues)
	{
		double 		minValue;
		int 		coordinates;
		int[] 		arrayOfCoordinates;

		arrayOfCoordinates = new int[numberOfValues];
		for (int i = 0; i < numberOfValues; i++) {
			arrayOfCoordinates[i] = -1;
		}
		for (int i = 0; i < numberOfValues; i++) {
			minValue = Integer.MAX_VALUE;
			coordinates = 0;
			for (int j = 0; j < matrix[0].length; j++) {
				if (!isValueInArray(j, arrayOfCoordinates) && Math.abs(matrix[j][j]) < minValue) {
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
			arrayOfCoordinatesOfMinOwnValues = getCoordsMinValuesOfDiagonalMatrix(eiganValues, numberValuesToRemove);

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
		outputFunction("\n|======================================================|");
		outputFunction("\n|              Saída da funcionalidade 1:              |");
		outputFunction("\n|======================================================|\n");

		outputFunction(String.format("//Foram calculados %d valores e vetores próprios//\n", ownVs[1].length));

		outputFunction("\nMatriz de vetores próprios::\n");
		printMatrix(ownVs[0], PRINTDECIMAL);

		outputFunction("Matriz de valores próprios::\n");
		printMatrix(ownVs[1], PRINTDECIMAL);

		outputFunction("Matriz resultante da multiplicação das matrizes decompostas::\n");

		for (int i = 0; i < decompressedMatrix.length; i++)
			for (int j = 0; j < decompressedMatrix[0].length; j++)
				decompressedMatrix[i][j] = Math.round(decompressedMatrix[i][j]);

		printMatrix(decompressedMatrix, PRINTINT);

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
		double[]		allAverageAbsoulteError;


        csvFilesInFolder = ReadingDir(dirPath);
        if(AllImgsInVector(csvFilesInFolder) == null){
            return ;
        }

        matrixInVector = AllImgsInVector(csvFilesInFolder);

        averageVector = CalculateAverageVector(matrixInVector);
		allPhis = calculateAllPhis(matrixInVector, averageVector);

		reverseCovarianceMatrix = buildReverseCovarianceMatrix(allPhis);

		eigenVectors = getEigenVectorsOfCovarianceMatrix(reverseCovarianceMatrix, allPhis, precisionValues);
		allWeights = calculateAllWeights(allPhis, eigenVectors, matrixInVector);


        reconstructionMatrix = BuildReconstructionMatrix(eigenVectors, allPhis, allWeights, averageVector, eigenVectors.length);


		allAverageAbsoulteError = new double[reconstructionMatrix.length];

		for (int i = 0; i < allAverageAbsoulteError.length; i++) {
			allAverageAbsoulteError[i] = avgAbsolutError(vectorToMatrix(matrixInVector[i]), vectorToMatrix(reconstructionMatrix[i]));
		}


		outputFunctionTwo(averageVector, reverseCovarianceMatrix, allWeights, reconstructionMatrix, eigenVectors, allAverageAbsoulteError);
	}

	public static void outputFunctionTwo(double[] avgVector, double[][] reverseCovMatrix, double[][] allWeights, double[][] reconstructionMatrix, double[][] eigenVectors,double[] allAverageAbsoluteError){

		outputFunction("\n|======================================================|");
		outputFunction("\n|              Saída da funcionalidade 2:              |");
		outputFunction("\n|======================================================|\n");

		outputFunction(String.format("Número de eigenfaces usadas: %d", eigenVectors.length));

        outputFunction("\nVetor Médio:\n");
		printVector(avgVector);

		outputFunction("\nMatriz de Covariância\n");
		printMatrix(reverseCovMatrix, PRINTINT);

		outputFunction("Pesos Usados: \n");
		printMatrix(allWeights, PRINTINT);

		//Print da matriz "original" das eigenfaces que arredondas dá tudo zero
		for (int k = 0; k < eigenVectors.length; k++) {
			matrixToCSV(vectorToMatrix(eigenVectors[k]), String.format(PATH_EIGENFACES + "%d.csv", k));
		}

		//Colocar os valores das eiganfaces entre 0 e 255
		normalizePixelIntensityOfMatrix(eigenVectors);

		//Criar os ficheiros correspondentes
		for (int j = 0; j < eigenVectors.length; j++) {
			try {
				matrixToCSV(vectorToMatrix(eigenVectors[j]), String.format(PATH_EIGENFACES + "%d_transformada.csv", j));
				matrixToJPG(vectorToMatrix(eigenVectors[j]), String.format(PATH_EIGENFACES + "%d.jpg", j));
			} catch (IOException e) {
			}
		}

		outputFunction("Erro Absoluto Médio entre::\n");
		//Criar os ficheiros das imagens reconstruidas
		for(int i = 0; i < reconstructionMatrix.length; i++) {
			try {
				matrixToCSV(vectorToMatrix(reconstructionMatrix[i]), String.format(PATH_RECONSTRUCTION + "img%d.csv", i));
				matrixToJPG(vectorToMatrix(reconstructionMatrix[i]), String.format(PATH_RECONSTRUCTION + "img%d.jpg", i));

				outputFunction(String.format("img%d original e reconstruida = %.2f\n", i, allAverageAbsoluteError[i]));
			} catch (IOException e) {
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


	public static double[] CalculateAverageVector(double[][] allImgsInVector)
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
		double[] 		averageVector;

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
		averageVector = CalculateAverageVector(allImagesVector);

		imageMatrix = CSVtoMatrix(csvPath);
		if (!validMatrix(imageMatrix))
			return ;

		imageVector = MatrixToVerticalVector(imageMatrix);

		newPhi = calculateNewPhi(imageVector, averageVector);
		if(newPhi == null)
		{
			return ;
		}

		allPhis = calculateAllPhis(allImagesVector, averageVector);

		reverseCovarianceMatrix = buildReverseCovarianceMatrix(allPhis);

		eigenVectors = getEigenVectorsOfCovarianceMatrix(reverseCovarianceMatrix, allPhis, own_values);

		newWeights = calculateWeights(eigenVectors, newPhi);


		allWeights = calculateAllWeights(allPhis,eigenVectors, allImagesVector);

		allEuclideanDistances = new double[allWeights.length];


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


	//=========4=========//
	public static int generateImage(int precisionValues, String dirPath, int imageNumber){

		double[]		generatedImage;

		double[]		averageVector;
		double[][]		matrixInVector;
		double[][]		allPhis;
		double[][]      reverseCovarianceMatrix;
		double[][]      eigenVectors;
		double[][]		eigenValues;

		String[]        csvFilesInFolder;




		csvFilesInFolder = ReadingDir(dirPath);
		if(AllImgsInVector(csvFilesInFolder) == null){
			return -1;
		}

		matrixInVector = AllImgsInVector(csvFilesInFolder);

		averageVector = CalculateAverageVector(matrixInVector);
		allPhis = calculateAllPhis(matrixInVector, averageVector);

		reverseCovarianceMatrix = buildReverseCovarianceMatrix(allPhis);

		eigenVectors = getEigenVectorsOfCovarianceMatrix(reverseCovarianceMatrix, allPhis, precisionValues);
		eigenValues = Decomposition(precisionValues, reverseCovarianceMatrix)[1];


		generatedImage = new double[eigenVectors[0].length];


		for (int j = 0; j < eigenVectors.length; j++) {
			generatedImage = vectorAdd(vectorMultConst(eigenVectors[j], getRandomNumber(eigenValues[j][j])), generatedImage);
		}
		generatedImage = vectorAdd(generatedImage, averageVector);

		imageNumber = outputFunctionFour(generatedImage, imageNumber);
		return imageNumber;
	}


	public static int outputFunctionFour(double[] generatedImage, int imageNumber){

		double[][]			generatedImageMatrix;

		generatedImageMatrix = vectorToMatrix(generatedImage);

		try {
			matrixToCSV(generatedImageMatrix, String.format("Output/generatedImage%d.csv", imageNumber));
			normalizePixelIntensityOfMatrix(generatedImageMatrix);
			matrixToJPG(generatedImageMatrix, String.format("Output/generatedImage%d.jpg", imageNumber));
			imageNumber++;
			outputFunction("Geração de imagem finalizada\n");
		} catch (IOException e) {
		}
		return imageNumber;
	}

	public static double getRandomNumber(double lambda){

		double			lowerRange;
		double			upperRange;
		double			randomNumber;

		lowerRange = -1 * Math.sqrt(lambda);
		upperRange = Math.sqrt(lambda);

		randomNumber = (Math.random() * (upperRange - lowerRange)) + lowerRange;

		return randomNumber;
	}

	//=============Matrix Operations=============//
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
		double[] addedVector;

		addedVector = new double[vector1.length];

		if(vector1.length != vector2.length){
			return null;
		}

		for (int i = 0; i < vector1.length; i++)
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
		for (int i = 0; i < vector1.length; i++)
			vector1[i] = vector1[i] / value;
		return (vector1);
	}

	public static double[] vectorMultConst(double[] vector1, double value)
	{
		double[]	multipliedVector;

		multipliedVector = new double[vector1.length];
		for (int i = 0; i < vector1.length; i++)
			multipliedVector[i] = vector1[i] * value;
		return (multipliedVector);
	}

	//=============Display Matrix=============//
	public static void printMatrix(double[][] matrix, boolean isDecimal)
	{
		for (int rows = 0; rows < matrix.length; rows++) {
			for (int columns = 0; columns < matrix[0].length; columns++)
			{
                if (isDecimal){
                    outputFunction(String.format("%.2f ", matrix[rows][columns]));
                }else {
				    outputFunction(String.format("%.0f ", matrix[rows][columns]));
                }
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

		normalizePixelIntensityOfMatrix(matrix);

		// Set the pixel intensities
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int intensity = (int) Math.round(matrix[y][x]);
				int rgb = (intensity << 16) | (intensity << 8) | intensity; // Set the same value for R, G, B
				image.setRGB(x, y, rgb);
			}
		}

		// Write the image to the file
		File outputFile = new File(outputFilePath);
		ImageIO.write(image, "jpg", outputFile);
	}

    public static void normalizePixelIntensityOfMatrix(double[][] matrix){
        double minEigenface;
        double maxEigenface;
        double amplitude;


        minEigenface = getMinValueOfMatrix(matrix);
        maxEigenface = getMaxValueOfMatrix(matrix);
        amplitude = maxEigenface - minEigenface;



        for (int vetor = 0; vetor < matrix.length; vetor++) {
            for (int value = 0; value < matrix[0].length; value++) {
                matrix[vetor][value] = MAX_VALUE_IN_CSV * ((matrix[vetor][value] - minEigenface) / amplitude);
            }
        }
    }

    public static double getMinValueOfMatrix(double[][] matrix){

        double minValue;
        minValue = matrix[0][0];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] < minValue){
                    minValue = matrix[i][j];
                }
            }
        }
        return minValue;
    }

    public static double getMaxValueOfMatrix(double[][] matrix){

        double maxValue;
        maxValue = matrix[0][0];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] > maxValue){
                    maxValue = matrix[i][j];
                }
            }
        }
        return maxValue;
    }


	//===================Status Verifier===================//
	public static boolean isSimetric(double[][] matrix){
		if (matrixEquals(matrix, matrixTranspose(matrix))) {
			return true;
		}
		return false;
	}

	public static boolean isSquared(double[][] matrix){
		for (int i = 0; i < matrix.length; i++) {
			if (matrix.length != matrix[i].length) {
				return false;
			}
		}
		return true;
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


}
