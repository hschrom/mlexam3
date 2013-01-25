import java.io.FileNotFoundException;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;


public class FirstTry {

	public FirstTry() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws Exception {
		// BufferedReader dataReader = new BufferedReader(new FileReader("dataset/iris.arff"));
		// Instances train = new Instances(dataReader);

		// The DataSource class is not limited to ARFF files. It can also read CSV files and other formats (basically all file formats that Weka can import via its converters).
		DataSource dataSource = new DataSource("/some/where/data.arff");
		Instances train = dataSource.getDataSet();
		
		// Setting the class attribute, by default, in an ARFF file, it is the last attribute
		train.setClassIndex(train.numAttributes() - 1);
		
		// first 
		// String[] options = weka.core.Utils.splitOptions("-R 1");

		// classifiers implement the weka.core.OptionHandler interface
		
		NaiveBayes nB = new NaiveBayes();
		nB.buildClassifier(train);
		
		Evaluation eval = new Evaluation(train);
		eval.crossValidateModel(nB, train, 10, new Random(1));
		
		System.out.println(eval.toSummaryString("\nResults Naive Bayes\n======\n", true));
		System.out.println("fMeasure = " + eval.fMeasure(1) + ", precision = " + eval.precision(1)+ ", recall = "+ eval.recall(1));
		
		
		
		// Start given classifier
		// like from command line it would look like
		/*
			java weka.classifiers.meta.ClassificationViaRegression \
			-W "weka.classifiers.functions.LinearRegression -S 1" \
			-t data/iris.arff -x 2
		*/
		/*
		   * String classifierName = Utils.getOption('W', options);
		   * Classifier c = (Classifier)Utils.forName(Classifier.class,
		   *                                          classifierName,
		   *                                          options);
		*/
		
		//String cmdline = new String("java weka.classifiers.meta.ClassificationViaRegression	-W \"weka.classifiers.functions.LinearRegression\" -t data/iris.arff -x 2)");
		String cmdline = new String("-S 1 -t data/iris.arff -x 2)");
		String options[] = Utils.splitOptions(cmdline);
		String classifierName = Utils.getOption('W', options);
		
		Classifier c = (Classifier)Utils.forName(Classifier.class,	"weka.classifiers.trees.RandomForest", null);
		
		c.buildClassifier(train);
		eval = new Evaluation(train);
		eval.crossValidateModel(c,  train,  10,  new Random(1));
		
		System.out.println(eval.toSummaryString("\nResults J48\n======\n", true));
		System.out.println("fMeasure = " + eval.fMeasure(1) + ", precision = " + eval.precision(1)+ ", recall = "+ eval.recall(1));
	
		System.out.println("done");
		
		LinearRegression lr = null;
		
	}

}
