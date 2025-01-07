
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

	//=============Input Values=============//
	public static final double[][] MAINMATRIX = {{2,0}, {0,3}};
	public static final double[][] NONSQUAREMATRIX = {{4,5},{1}};
	public static final double[][] SIMETRICMATRIX = {{3,5}, {5,3}};
	public static final int ARBITRARYVALUE1 = 63;
	public static final int ARBITRARYVALUE2 = 999;
	public static final int[] VECTOR1 = {80,63,2};
	public static final double[] VECTOR2 = {2,0,0,3};
	public static final double[] VECTOR3 = {2,3};
	public static final double[] VECTOR4 = {2,0,0,3};
	public static final double[] VECTOR5 = {2,0,0,3};
	public static final double[][] TESTALLIMAGES = {{5,6}, {10,12}};
	public static final double[][] MATRIXOFEIGENVECTORS = {{5,9}, {1,4}};
	public static final double[][] TESTALLPHIS = {{1,1}, {2,2}};
	public static final double[][] REVERSECOVARIANCEMATRIX = {{2,4}, {15,8}};
	public static final double SQUAREROOT13 = Math.sqrt(13);




	//=============Expected Values=============//
	public static final int[] expectedResultForTestGetCoordinatesOfMinValuesOfDiagonalMatrix1 = {0};
	public static final int[] expectedResultForTestGetCoordinatesOfMinValuesOfDiagonalMatrix2 = {0,1};
	public static final double[][] expectedResultForTestGetEigenValuesSubMatrix = {{3}};
	public static final double[][] expectedResultForTestGetEigenVectorsSubMatrix = {{9},{4}};
	public static final double expectedResultForTestAvgAbsolutError = 0;
	public static final double[][] expectedResultForTestCalculateDecompressedMatrix = {{293,118}, {118,50}};
	public static final double[] expectedResultForTestCalculateMediumVector = {7.5, 9};
	public static final double[][] expectedResultForTestCalculateAllPhis = {{-2.5,-3}, {2.5,3}};
	public static final double[][] expectedResultForTestBuildReverseCovarianceMatrix= {{2,4}, {4,8}};
	public static final double[][] expectedResultForTestGetEigenVectorsOfCovarianceMatrix = {{0.7071067811865475, 0.7071067811865475}, {-0.7071067811865476, -0.7071067811865476}};
	public static final double[][] expectedResultForTestBuildReconstructionMatrix = {{82.5, 155.0}, {157.5, 301.0}};

	public static final double[][] expectedResultForTestCalculateAllWeights = {{14.0, 5.0}, {28.0, 10.0}};
	public static final double[] expectedResultForTestNormalizeVector = {2/SQUAREROOT13,0,0,3/SQUAREROOT13};
	public static final double expectedResultForTestScalarProduct = 13;
	public static final double[] expectedResultForTestVectorAdd = {4,0,0,6};
	public static final double[][] expectedResultForTestMatrixMulti = {{4,0},{0,9}};
	public static final double[] expectedResultForTestCalculateNewPhi = {-5.5,-6};
	public static final double[] expectedResultForTestCalculateWeights = {-81.5, -29.5};
	public static final double[][] MAINMATRIXTRANSPOSE = {{2,0}, {0,3}};


	public static void unitTest()
	{
		System.out.println("==================================");
		System.out.println("\t\tTeste Unitarios\t\t");
		System.out.println("==================================");
		System.out.println("1-testIsSimetric");
		if (testIsSimetric(SIMETRICMATRIX, true))
			System.out.println("✅");
		else
			System.out.println("❌");
		if (testIsSimetric(MATRIXOFEIGENVECTORS, false)) //
			System.out.println("✅");
		else
			System.out.println("❌");
		System.out.println("2-testIsSquared");
		if (testIsSquared(MAINMATRIX, true))
			System.out.println("✅");
		else
			System.out.println("❌");
		if (testIsSquared(NONSQUAREMATRIX, false)) //
			System.out.println("✅");
		else
			System.out.println("❌");
		System.out.println("3-testIsValueInArray");
		if (testIsValueInArray(ARBITRARYVALUE1, VECTOR1, true))
			System.out.println("✅");
		else
			System.out.println("❌");
		if (testIsValueInArray(ARBITRARYVALUE2, VECTOR1, false)) //
			System.out.println("✅");
		else
			System.out.println("❌");
		System.out.println("4-testGetCoordinatesOfMinValuesOfDiagonalMatrix");
		if (testGetCoordinatesOfMinValuesOfDiagonalMatrix(MAINMATRIX, 1, expectedResultForTestGetCoordinatesOfMinValuesOfDiagonalMatrix1))
			System.out.println("✅");
		else
			System.out.println("❌");
		if (testGetCoordinatesOfMinValuesOfDiagonalMatrix(MAINMATRIX, 2, expectedResultForTestGetCoordinatesOfMinValuesOfDiagonalMatrix2)) //
			System.out.println("✅");
		else
			System.out.println("❌");
		System.out.println("5-testGetEigenValuesSubMatrix");
		if (testGetEigenValuesSubMatrix(expectedResultForTestGetCoordinatesOfMinValuesOfDiagonalMatrix1, MAINMATRIX, expectedResultForTestGetEigenValuesSubMatrix))
			System.out.println("✅");
		else
			System.out.println("❌");
		System.out.println("6-testGetEigenVectorsSubMatrix");
		if (testGetEigenVectorsSubMatrix(expectedResultForTestGetCoordinatesOfMinValuesOfDiagonalMatrix1, MATRIXOFEIGENVECTORS, expectedResultForTestGetEigenVectorsSubMatrix))
			System.out.println("✅");
		else
			System.out.println("❌");
		System.out.println("7-testAvgAbsolutError");
		if (testAvgAbsolutError(MAINMATRIX, MAINMATRIX, expectedResultForTestAvgAbsolutError))
			System.out.println("✅");
		else
			System.out.println("❌");
		System.out.println("8-testCalculateDecompressedMatrix");
		if (testCalculateDecompressedMatrix(MATRIXOFEIGENVECTORS, MAINMATRIX, expectedResultForTestCalculateDecompressedMatrix))
			System.out.println("✅");
		else
			System.out.println("❌");
		System.out.println("9-testCalculateMediumVector");
		if (testCalculateMediumVector(TESTALLIMAGES, expectedResultForTestCalculateMediumVector))
			System.out.println("✅");
		else
			System.out.println("❌");
		System.out.println("10-testCalculateAllPhis");
		if (testCalculateAllPhis(TESTALLIMAGES, expectedResultForTestCalculateMediumVector, expectedResultForTestCalculateAllPhis))
			System.out.println("✅");
		else
			System.out.println("❌");
		System.out.println("11-testBuildReverseCovarianceMatrix");
		if (testBuildReverseCovarianceMatrix(TESTALLPHIS, expectedResultForTestBuildReverseCovarianceMatrix))
			System.out.println("✅");
		else
			System.out.println("❌");
		System.out.println("12-testGetEigenVectorsOfCovarianceMatrix");
		if (testGetEigenVectorsOfCovarianceMatrix(REVERSECOVARIANCEMATRIX, TESTALLPHIS, ARBITRARYVALUE1, expectedResultForTestGetEigenVectorsOfCovarianceMatrix))
			System.out.println("✅");
		else
			System.out.println("❌");
		System.out.println("13-testBuildReconstructionMatrix");
		if (testBuildReconstructionMatrix(MATRIXOFEIGENVECTORS, MATRIXOFEIGENVECTORS.length, TESTALLPHIS, Main.calculateAllWeights(TESTALLPHIS, MATRIXOFEIGENVECTORS, TESTALLIMAGES), expectedResultForTestCalculateMediumVector, expectedResultForTestBuildReconstructionMatrix))
			System.out.println("✅");
		else
			System.out.println("❌");
		System.out.println("14-testCalculateAllWeights");
		if (testCalculateAllWeights(TESTALLPHIS, MATRIXOFEIGENVECTORS, TESTALLPHIS, expectedResultForTestCalculateAllWeights))
			System.out.println("✅");
		else
			System.out.println("❌");
		System.out.println("15-TestCalculateWeights");
		if (testCalculateWeights(MATRIXOFEIGENVECTORS, expectedResultForTestCalculateNewPhi, expectedResultForTestCalculateWeights))
			System.out.println("✅");
		else
			System.out.println("❌");
		System.out.println("16-testCalculateNewPhi");
		if (testCalculateNewPhi(VECTOR3, expectedResultForTestCalculateMediumVector, expectedResultForTestCalculateNewPhi))
			System.out.println("✅");
		else
			System.out.println("❌");
		System.out.println("17-testCalculateEuclideanDistance");
		if (testCalculateEuclideanDistance(VECTOR2, VECTOR2, 0))
			System.out.println("✅");
		else
			System.out.println("❌");
		System.out.println("18-getIndexOfMinValueInArray");
		if (testGetIndexOfMinValueInArray(VECTOR2, 1))
			System.out.println("✅");
		else
			System.out.println("❌");
		System.out.println("19-testMatrixMulti");
		if (testMatrixMulti(MAINMATRIX, MAINMATRIX, expectedResultForTestMatrixMulti))
			System.out.println("✅");
		else
			System.out.println("❌");
		System.out.println("20-testMatrixTranspose");
		if (testMatrixTranspose(MAINMATRIX, MAINMATRIXTRANSPOSE))
			System.out.println("✅");
		else
			System.out.println("❌");
		System.out.println("21-testVectorAdd");
		if (testVectorAdd(VECTOR2, VECTOR2, expectedResultForTestVectorAdd))
			System.out.println("✅");
		else
			System.out.println("❌");
		System.out.println("22-testScalarProduct");
		if (testScalarProduct(VECTOR2, VECTOR2, expectedResultForTestScalarProduct))
			System.out.println("✅");
		else
			System.out.println("❌");
		System.out.println("23-testVectorDivConst");
		if (testVectorDivConst(VECTOR2, 1, VECTOR2))
			System.out.println("✅");
		else
			System.out.println("❌");
		System.out.println("24-testVectorMultConst");
		if (testVectorMultConst(VECTOR2, 1, VECTOR2))
			System.out.println("✅");
		else
			System.out.println("❌");
		System.out.println("25-testNormalizeVector");
		if (testNormalizeVector(VECTOR2, expectedResultForTestNormalizeVector))
			System.out.println("✅");
		else
			System.out.println("❌");
		System.out.println("26-testGetVectorNorm");
		if (testGetVectorNorm(VECTOR4, SQUAREROOT13))
			System.out.println("✅");
		else
			System.out.println("❌");
		System.out.println("27-testVectorToMatrix");
		if (testVectorToMatrix(VECTOR5, MAINMATRIX))
			System.out.println("✅");
		else
			System.out.println("❌");
	}

	//=============General Operations=============//

	public static boolean testIsSimetric(double[][] matrix, boolean expected)
	{
		boolean result = Main.isSimetric(matrix);

		return (result == expected);
	}

	public static boolean testIsSquared(double[][] matrix, boolean expected)
	{
		boolean result = Main.isSquared(matrix);

		return (result == expected);
	}

	public static boolean testIsValueInArray(double value, int[] array, boolean expected) {

		return (expected == Main.isValueInArray(value, array));

	}

	public static boolean testGetCoordinatesOfMinValuesOfDiagonalMatrix(double[][] matrix, int value, int[] expected)
	{

		int[] result = Main.getCoordsMinValuesOfDiagonalMatrix(matrix, value);

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
		double[][] result = Main.getEigenValuesSubMatrix(arrayOfCoordinates, matrix);

		return matrixEquals(result, expected);
	}

	public static boolean  testGetEigenVectorsSubMatrix(int[] arrayOfCoordinates, double[][] matrix, double[][] expected){
		double[][] result = Main.getEigenVectorsSubMatrix(arrayOfCoordinates, matrix);

		return matrixEquals(result, expected);
	}

	public static boolean testAvgAbsolutError(double[][] matrix1, double[][] matrix2, double expected)
	{
		double result = Main.avgAbsolutError(matrix1, matrix2);

        return result == expected;
	}

	public static boolean testCalculateDecompressedMatrix(double[][] eigenVectors, double[][] eigenValues, double[][] expected)
	{
		double[][] result = Main.calculateDecompressedMatrix(eigenVectors, eigenValues);

		return matrixEquals(result, expected);
	}

	public static boolean testCalculateMediumVector(double[][] allImgsInVector, double[] expected){
		double[] result = Main.CalculateMediumVector(allImgsInVector);

		return vectorEquals(result, expected);
	}

	public static boolean testCalculateAllPhis(double[][] allImagesMatrix, double[] mediumVector, double[][] expected){

		double[][] result = Main.calculateAllPhis(allImagesMatrix, mediumVector);

		return matrixEquals(result, expected);
	}

	public static boolean testBuildReverseCovarianceMatrix(double[][] allPhis, double[][] expected){
		double[][] result = Main.buildReverseCovarianceMatrix(allPhis);

		return matrixEquals(result, expected);
	}

	public static boolean testGetEigenVectorsOfCovarianceMatrix(double[][] reverseCovarianceMatrix,double[][] allPhis, int ownValues, double[][] expected){
		double[][] result = Main.getEigenVectorsOfCovarianceMatrix(reverseCovarianceMatrix, allPhis, ownValues);

		return matrixEquals(result, expected);
	}

	public static boolean testBuildReconstructionMatrix(double[][] eigenVectors, int eigenVectorLength, double[][] allPhis, double[][] allWeights, double[]averageVector, double[][] expected){
		double[][] result = Main.BuildReconstructionMatrix(eigenVectors, allPhis, allWeights, averageVector, eigenVectorLength);

		return matrixEquals(result, expected);
	}

	public static boolean testCalculateAllWeights(double[][] allPhis, double[][] eigenVectors, double[][] allImagesVector, double[][] expected){
		double[][] result = Main.calculateAllWeights(allPhis, eigenVectors, allImagesVector);

		return matrixEquals(result, expected);
	}

	public static boolean testCalculateWeights(double[][] eigenVectors, double[] phi, double[] expected){
		double[] result = Main.calculateWeights(eigenVectors, phi);

		return vectorEquals(result, expected);
	}

	public static boolean testCalculateNewPhi(double[] imageVector, double[] mediumVector, double[] expected){
		double[] result = Main.calculateNewPhi(imageVector, mediumVector);

		return vectorEquals(result, expected);
	}

	public static boolean testCalculateEuclideanDistance(double[] vector1, double[] vector2, double expected){
		double result = Main.calculateEuclideanDistance(vector1, vector2);

		return (result == expected);
	}

	public static boolean testGetIndexOfMinValueInArray(double[] array, int expected){
		int result = Main.getIndexOfMinValueInArray(array);

		return (result == expected);
	}
	//===================Matrix===================//

	public static boolean testMatrixMulti(double[][] matrix1, double[][] matrix2, double[][] expected)
	{
		matrix1 = Main.matrixMulti(matrix1, matrix2);
		return (matrixEquals(matrix1, expected));
	}


	public static boolean testMatrixTranspose(double[][] matrix1,  double[][] expected)
	{
		double[][] matrix2 = Main.matrixTranspose(matrix1);
		return (matrixEquals(matrix2, expected));
	}

	//===================Vectors===================//

	public static boolean testVectorAdd(double[] vector1, double[] vector2, double[] expected)
	{
		vector1 = Main.vectorAdd(vector1, vector2);
		return (vectorEquals(vector1,expected));
	}

	public static boolean testScalarProduct(double[] vector1, double[] vector2, double expected)
	{
		double result = Main.scalarProduct(vector1, vector2);

		return result == expected;
	}

	public static boolean testVectorDivConst(double[] vector, int value, double[] expected)
	{
		double[] result = Main.vectorDivConst(vector, value);
		return (vectorEquals(result, expected));
	}

	public static boolean testVectorMultConst(double[] vector1, int value, double[] expected)
	{
		double[] result = Main.vectorMultConst(vector1, value);
		return (vectorEquals(result,expected));
	}

	public static boolean testNormalizeVector(double[] vector, double[] expected){
		double[] result = Main.normalizeVector(vector);

		return vectorEquals(result, expected);
	}

	public static boolean testGetVectorNorm(double[] vector, double expected){
		double result = Main.getVectorNorm(vector);

		return (result == expected);
	}

	public static boolean testVectorToMatrix(double[] vector, double[][] expected){
		double[][] result = Main.vectorToMatrix(vector);

		return matrixEquals(result, expected);
	}

	//===================Test Execution===================//

	public static void main(String[] args){
		unitTest();
	}

}

