import java.util.Scanner;
import java.io.*;
import java.lang.Object;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
// equals -> compare matrixes

public class Main {
    public static final int MIN_OWN_VALUE = -1;

    public static final int MIN_TYPE_EXEC = 0;
    public static final int MAX_TYPE_EXEC = 3;

    public static final Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        if (args.length > 0)
            NonIterative(args);
        else
            Iterative();
    }

    // compilation == java -jar nome programa.jar -f X -k Y -i Z -d W
    public static void NonIterative(String[] arguments) {
        int type;
        int own_values;
        String path;
        String dirPath;

        if (!(CheckingArgs(arguments))) {
            System.out.println("You've entered some wrong argument!! Check it and try again!!");
            return;
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

    public static boolean CheckingArgs(String[] arguments) {
        if (arguments.length != 8)
            return (false);
        if (!(arguments[0].equals("-f")))
            return (false);
        if (!(arguments[2].equals("-k")))
            return (false);
        if (!(arguments[4].equals("-i")))
            return (false);
        if (!(arguments[6].equals("-d")))
            return (false);
        return (true);
    }

    //=========Iterative exec and reading=========//
    public static void Iterative() {
        int type;
        int own_values;
        String path;
        String dirPath;

        type = 1;
        while (type != 0) {
            type = TypeOfExecution();
            if (type == 0)
                break;
            own_values = OwnValues();
            switch (type) {
                case 1:
                    path = GetPath("Enter the file PATH of execution:\n");
                    RealMatrix[] ownVs = Decomposition(own_values, path);

                    System.out.printf("Número de valores e vetores próprio: %d\n", ownVs[0].getColumnDimension());
                    printMatrix(ownVs[0]);
                    printMatrix(ownVs[1]);

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

    public static int TypeOfExecution() {
        int read;

        read = 100;
        System.out.println("----------------------------------");
        System.out.printf("Enter the type of execution:\n");
        System.out.println("(0) Exit Program");
        System.out.println("(1) Decomposition of images");
        System.out.println("(2) Rebuild images");
        System.out.println("(3) Identify closest");

        while (!(read <= MAX_TYPE_EXEC && read >= MIN_TYPE_EXEC)) {
            read = input.nextInt();
            if (!(read <= 3 && read >= 0))
                System.out.println("You've entered a wrong value. Try it again: ");
        }
        return (read);
    }

    public static int OwnValues() {
        int read;

        read = -2;
        System.out.println("----------------------------------");
        System.out.print("Enter the number of own values that you want to use(columns):\n");
        while (read < -1) {
            read = input.nextInt();
            input.nextLine();
            if (read < -1)
                System.out.println("You need to enter a positive number or <-1>. Try again!");
        }
        return read;
    }

    public static String GetPath(String searching) {
        String path;

        System.out.println("----------------------------------");
        System.out.printf("%s", searching);
        path = input.nextLine();
        return (path);
    }

    //=========Matrix Read=========//
    public static RealMatrix CSVtoMatrix(String filename) {
        double[][] csv;
        RealMatrix matrix;

        csv = ReadingCsv(filename);
        if (csv == null) {
            System.out.println("File does not exist!");
            return (null);
        }
        matrix = new Array2DRowRealMatrix(csv);
        return (matrix);
    }

    // Return (the double matrix) if it has something in the file, (null) if it has nothing
    public static double[][] ReadingCsv(String filename) {
        File file = new File(filename);
        Scanner ReadFile;
        String regex = "[,]";
        String[] Csv;
        double[][] toReturn = null;
        int noOfColumns = 0;
        int noOfLines;
        int j;

        j = 0;
        try {
            ReadFile = new Scanner(file);
        } catch (FileNotFoundException e) {
            return null;
        }

        noOfLines = GetNumLines(file);
        if (noOfLines == -1)
            return (null);
        while (ReadFile.hasNextLine()) {
            Csv = ReadFile.nextLine().split(regex);
            if (j == 0) {
                noOfColumns = Csv.length;
                toReturn = new double[noOfLines][noOfColumns];
            }
            for (int i = 0; i < noOfColumns; i++)
                toReturn[j][i] = Double.parseDouble(Csv[i]);
            j++;
        }

		/*	// this will desapear
		for (int i = 0; i < noOfLines; i++) {
			for (int k = 0; k < noOfColumns; k++) {
				System.out.printf("%.1f ", toReturn[i][k]);
			}
			System.out.println();
		}
		// until here */
        return (toReturn);
    }

    public static int GetNumLines(File file) {
        int noOfLines;

        noOfLines = -1;
        try (LineNumberReader lnr = new LineNumberReader(new FileReader(file))) {
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
    public static String[] ReadingDir(String dirPath) {
        File folder = new File(dirPath);
        File[] listOfFiles = folder.listFiles();
        String[] csvFiles;
        String all_names;
        String name;

        all_names = null;
        for (File file : listOfFiles) {
            if (file.isFile()) {
                name = file.getName();
                if (CheckingExtension(name))
                    all_names = String.join("\t", all_names, name);
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
    public static boolean CheckingExtension(String toCheck) {
        int length;

        length = toCheck.length();
        if (length > 4)
            if (toCheck.charAt(length - 1) == 'v' && toCheck.charAt(length - 2) == 's' && toCheck.charAt(length - 3) == 'c' && toCheck.charAt(length - 4) == '.')
                return (true);
        return (false);
    }

    //==================Functionalities==================//
    //=========1=========//
    public static boolean isValueInArray(double value, int[] array) {
        for (double number : array) {
            if (value == number) {
                return true;
            }
        }
        return false;
    }

    public static int[] getCoordinatesOfMinValuesOfDiagonalMatrix(RealMatrix matrix, int numberOfValues) {
        double minValue;
        int coordinates;
        int[] arrayOfCoordinates = new int[numberOfValues];

        for (int i = 0; i < numberOfValues; i++) {
            minValue = Math.abs(matrix.getEntry(0, 0));
            coordinates = 0;
            for (int j = 0; j < matrix.getColumnDimension(); j++) {
                if (Math.abs(matrix.getEntry(j, j)) < minValue && !isValueInArray(j, arrayOfCoordinates)) {
                    minValue = Math.abs(matrix.getEntry(j, j));
                    coordinates = j;
                }
            }
            arrayOfCoordinates[i] = coordinates;
            //System.out.println(coordinates);
        }

        return arrayOfCoordinates;
    }

    public static void printMatrix(RealMatrix matrix) {

        for (int rows = 0; rows < matrix.getRowDimension(); rows++) {
            for (int columns = 0; columns < matrix.getColumnDimension(); columns++) {
                System.out.printf("%8.3f ", matrix.getEntry(rows, columns));
            }
            System.out.println();
        }
        System.out.println();
    }

    public static int[] getArrayOfRowsAndColumnsToCopy(int[] arrayOfCoordinates, RealMatrix matrix) {
        int numberOfCoordinates = matrix.getColumnDimension() - arrayOfCoordinates.length;
        int[] arrayOfRowsAndColumnsToCopy = new int[numberOfCoordinates];
        int index = 0;
        for (int i = 0; index < numberOfCoordinates; i++) {
            if (!isValueInArray(i, arrayOfCoordinates)) {
                arrayOfRowsAndColumnsToCopy[index] = i;
                System.out.println(i);
                index++;
            }
        }

        return arrayOfRowsAndColumnsToCopy;
    }

    public static RealMatrix createSubMatrixForOwnValues(int[] arrayOfRowsAndColumnsToCopy, RealMatrix eigenValues) {

        return eigenValues.getSubMatrix(arrayOfRowsAndColumnsToCopy, arrayOfRowsAndColumnsToCopy);
    }

    public static RealMatrix createSubMatrixForOwnVectors(int[] arrayOfColumnsToCopy, RealMatrix eigenVectors) {
        int[] arrayOfRowsToCopy = new int[eigenVectors.getRowDimension()];
        for (int i = 0; i < eigenVectors.getRowDimension(); i++) {
            arrayOfRowsToCopy[i] = i;
        }

        return eigenVectors.getSubMatrix(arrayOfRowsToCopy, arrayOfColumnsToCopy);
    }

    public static RealMatrix[] Decomposition(int own_values, String csvPath) {
        RealMatrix matrix = CSVtoMatrix(csvPath);
        RealMatrix[] resultMatrix;

        EigenDecomposition eiganDecompositor = new EigenDecomposition(matrix);

        RealMatrix eiganVectors = eiganDecompositor.getV();
        RealMatrix eiganValues = eiganDecompositor.getD();

//        printMatrix(eiganVectors);
//        printMatrix(eiganValues);
        int totalNumberOfOwnValues = eiganValues.getColumnDimension();

        //System.out.printf("\n%d ahhah\n",totalNumberOfOwnValues);

        if (own_values >= totalNumberOfOwnValues || own_values == -1) {
            resultMatrix = new RealMatrix[]{eiganVectors, eiganValues};
        } else {
            int numberValuesToRemove = totalNumberOfOwnValues - own_values;
            int[] arrayOfCoordinatesOfMinOwnValues = getCoordinatesOfMinValuesOfDiagonalMatrix(eiganValues, numberValuesToRemove);
            int[] arrayOfCoordinatesToCopy = getArrayOfRowsAndColumnsToCopy(arrayOfCoordinatesOfMinOwnValues, eiganValues);
            RealMatrix eiganVectorsSubMatrix = createSubMatrixForOwnVectors(arrayOfCoordinatesToCopy, eiganVectors);
            RealMatrix eiganValuesSubMatrix = createSubMatrixForOwnValues(arrayOfCoordinatesToCopy, eiganValues);

//            printMatrix(eiganVectorsSubMatrix);
//            printMatrix(eiganValuesSubMatrix);
            resultMatrix = new RealMatrix[]{eiganVectorsSubMatrix, eiganValuesSubMatrix};
        }

        return resultMatrix;
    }

    //=========2=========//
    public static void Recomposition(int own_values, String csvPath) {

    }

    //=========3=========//
    public static void SearchClosest(int own_values, String csvPath, String dirPath) {
/* 		CRSMatrix identifying = CSVtoMatrix(inputPath);

		Vector avgDb; // vetor coluna

		CSRMatrix covarianceMatrix; // matriz de covariancia

		// calcular valores proprios matriz covariancia

		EigenDecompositor decompositor = new EigenDecompositor(matrix);
        Matrix[] decomposition = decompositor.decompose();

        // Valores próprios (matriz diagonal)
        Matrix eigenValues = decomposition[0];
        System.out.println("Valores Próprios (Diagonal):");
        System.out.println(eigenValues);

		CSRMatrix matrix_ = matrix.transform((i, j, value) -> value - columnVector.get(i));
 */

    }

}

