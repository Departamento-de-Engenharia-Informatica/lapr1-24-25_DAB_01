public class UnitTests {
    
    	//===================Testes Unitarios===================//
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

    public static boolean testMatrixAdd(double[][] matrix1, double[][] matrix2, double[][] expected)
	{
		matrix1 = matrixAdd(matrix1, matrix2);
		return (matrixEquals(matrix1, expected));
	}

	public static boolean testMatrixMulti(double[][] matrix1, double[][] matrix2, double[][] expected)
	{
		matrix1 = matrixMult(matrix1, matrix2);
		return (matrixEquals(matrix1, expected));
	}

	public static boolean testMatrixDivConst(double[][] matrix1, double[][] expected, int value)
	{

		matrix1 = matrixMultConst(matrix1, value);
		return (matrixEquals(matrix1, expected));
	}

	public static boolean testMatrixMultiConst(double[][] matrix1, double[][] expected, int value)
	{

		matrix1 = matrixMultConst(matrix1, value);
		return (matrixEquals(matrix1, expected));

	}

	public static boolean testMatrixTranspose(double[][] matrix1,  double[][] expected)
	{
		matrix1 = matrixTranspose(matrix1);
		return (matrixEquals(matrix1, expected));
	}

	public static boolean testVectorAdd(double[] vector1, double[] vector2, double[] expected)
	{
		vector1 = vectorAdd(vector1, vector2);
		return (vectorEquals(vector1,expected));
	}

	public static boolean testVectorMulti(double[] vector1, double[] vector2, double expected)
	{
		double result = vectorMult(vector1, vector2);
		
		if(result == expected)
			return true;
		else
			return false;
	}

	public static boolean testVectorDivConst(double[] vector1, double[] expected, int value)
	{
		double[] result = vectorMultConst(vector1, value);
		return (vectorEquals(result,expected));
	}

	public static boolean testVectorMultiConst(double[] vector1, double[] expected, int value)
	{
		double[] result = vectorMultConst(vector1, 1);
		return (vectorEquals(result,expected));
	}

	public static void testCSVtoMatrix(String filename)
	{
		double[][]	matrix;

		matrix = CSVtoMatrix("test.csv");

		printMatrix(matrix);
	}

	public static void testReadingCsv(String filename)
	{
		double[][] matrix = ReadingCsv("test.csv");

		printMatrix(matrix);
	}

	public static void testReadingDir(String dirPath){

		String[] csvFiles = ReadingDir("db");

		for(String file : csvFiles){
			System.out.printf("%s ", file);
		}
	}

	public static void testTransformDoubleMatrixToRealMatrix(double[][] matrix)
	{
		RealMatrix result = transformDoubleMatrixToRealMatrix(matrix);

		for(int i = 0; i < result.getRowDimension(); i++){
			for(int j = 0; j < result.getColumnDimension(); j++){
				System.out.printf("%.3f ", result.getEntry(i, j));
			}
			System.out.println();
		}
	}

	public static boolean testIsValueInArray() {
		
		int[] array = {1, 2, 3};

		if(isValueInArray(2, array))
			return true;
		else	
			return false;
		
	}

	public static boolean testGetCoordinatesOfMinValuesOfDiagonalMatrix(double[][] matrix, int NumberOfvalues, int[] expected)
	{

		int[] result = getCoordinatesOfMinValuesOfDiagonalMatrix(matrix, 2);

		for(int i = 0; i < NumberOfvalues; i++){
			if(result[i] != expected[i]){
				return false;
			}
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
    */
}
