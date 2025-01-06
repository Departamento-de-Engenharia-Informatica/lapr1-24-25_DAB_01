
public class UnitTests {
    
	//===================Unit tests===================//

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

	public static boolean testIsValueInArray(int value, int array, boolean expected) {
		
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

	public static boolean testBuildReverseCovarianceMatrix(double[][] allImagesMatrix, double[] mediumVector, double[][] expected){
		double[][] result = buildReverseCovarianceMatrix(allImagesMatrix, mediumVector);

		return matrixEquals(result, expected);
	}

	public static boolean testGetEigenVectorsOfCovarianceMatrix(double[][] reverseCovarianceMatrix,double[][] allImagesMatrix, double[] mediumVector,int ownValues, double[][] expected){
		double[][] result = getEigenVectorsOfCovarianceMatrix(reverseCovarianceMatrix, allImagesMatrix, mediumVector, ownValues);

		return matrixEquals(result, expected);
	}

	public static boolean testBuildReconstructionMatrix(double[][] eigenVectors, int eigenVectorLength, double[] averageVector, double[][] allImagesInVector, double[][] expected){
		double[][] result = BuildReconstructionMatrix(eigenVectors, eigenVectorLength, averageVector, allImagesInVector);

		return matrixEquals(result, expected);
	}

	public static double[][] testCalculateAllWeights(double[][] allPhis, double[][] eigenVectors, double[][] allImagesVector, double[][] expected){
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
		matrix1 = matrixMult(matrix1, matrix2);
		return (matrixEquals(matrix1, expected));
	}

	public static boolean testMatrixDivConst(double[][] matrix1, int value, double[][] expected)
	{

		matrix1 = matrixMultConst(matrix1, value);
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

