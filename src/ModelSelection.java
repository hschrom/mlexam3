/**
 * ModelSelection
 * @author Helmut Schrom-Feiertag
 * @author Andreas Ejupi
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.Collator;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Debug.Random;
import weka.core.Instances;
import weka.core.Utils;

public class ModelSelection {

	protected static final String PROPERTY_DATASET = "DATASET"; // property name
																// of property
																// holding data
																// file
	protected static final String PROPERTY_DATASPLIT = "DATASPLIT"; // property name
	
	protected static final String PROPERTY_SEED = "SEED";		// property name for seed value

	protected int m_RandomSeed = 1;

	/** the classifier used internally */
	protected Vector<Classifier> m_Classifiers = new Vector<Classifier>();

	protected Vector<Evaluation> m_TestEvaluation = new Vector<Evaluation>();

	protected Vector<Evaluation> m_GeneralizationEvaluation = new Vector<Evaluation>();
	
	Vector<String> m_OrderedClassifierWithProperties = new Vector<String>();
	
	Double m_SplitTraining = 0.2;		// 30 % of data default setings
	Double m_SplitTest = 0.4;			// 30 % of data
	Double m_SplitGeneralization = 0.4; // 40 % of data


	/** the training file */
	protected String m_DatasetFile = null;

	/** the training instances */
	protected Instances m_Training = null;
	protected Instances m_Testing = null;
	protected Instances m_Generalization = null;

	/** complete instances **/
	protected Instances m_Instances = null;

	/** for evaluating the classifier */
	protected Evaluation m_Evaluation = null;

	public ModelSelection() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * set the split values from properties
	 * @param properties
	 * @throws Exception
	 */
	protected void setSplit(Properties properties) throws Exception {
		String values[] = Utils.splitOptions(properties.getProperty(PROPERTY_DATASPLIT)); // split
		
		m_SplitTraining = Double.parseDouble(values[0]);
		m_SplitTest = Double.parseDouble(values[1]);
		m_SplitGeneralization = Double.parseDouble(values[2]);
				
	}


	/**
	 * sets the classifiers to use
	 * 
	 * @param name
	 *            the classname of the classifier
	 * @param options
	 *            the options for the classifier
	 * @throws Exception
	 */
	public void setClassifiers(Properties properties) throws Exception {
		// get property name list
		Set<String> propertyNames = properties.stringPropertyNames(); // properties.propertyNames();

		// create orderd set with CLASSIFIER properties only
		TreeSet<String> orderedProperties = new TreeSet<String>(Collator.getInstance());
		for (String p : propertyNames) {
			String compare = p.toUpperCase();
			System.out.println("test classifier name: "+p);
			if (compare.startsWith("CLASSIFIER"))
				orderedProperties.add(p);
		}

		// split property strings into classifier name (1st part in string) and
		// options (remaining parts)
		// i.e.
		// weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0
		// name: weka.classifiers.functions.supportVector.PolyKernel
		// options: -C 250007 -E 1.0
		//
		System.out.println("-- listing classifiers --");
		for (String p : orderedProperties) {
			m_OrderedClassifierWithProperties.add(properties.getProperty(p));
			String options[] = Utils.splitOptions(properties.getProperty(p)); // split
			String classifierName = options[0]; // [0] name

			System.out
					.println("classifier name : " + properties.getProperty(p));

			String classifierOptions[] = null;
			if (options.length > 1) {
				classifierOptions = new String[options.length - 1]; // allocate
																	// options
																	// array

				System.arraycopy(options, 1, classifierOptions, 0,
						options.length - 1); // copy the options

				// int n = 0;
				// for (String s : classifierOptions) {
				// System.out.println("   option[" + n + "] " + s);
				// n++;
				// }
			}

			// create classifier with specified options and append to classifier
			// pool

			m_Classifiers.add((Classifier) Utils.forName(Classifier.class,
					classifierName, classifierOptions));
		}
		System.out.println("======================");

	}

	/**
	 * sets the file to use for training
	 */
	public void loadInstances(String name) throws Exception {

		m_DatasetFile = name;
		m_Instances = new Instances(new BufferedReader(new FileReader(
				m_DatasetFile)));
		m_Instances.setClassIndex(m_Instances.numAttributes() - 1);

		m_Instances.randomize(m_Instances.getRandomNumberGenerator(m_RandomSeed));

		// Create subsets of instances for train, test 1 and test 2
		int num = m_Instances.numInstances();
		int numTraining = (int) (num * m_SplitTraining);
		int numTesting = (int) (num * m_SplitTest);
		int numGen = num - numTraining - numTesting;

		System.out.println("Data split: " + numTraining + " training, "
				+ numTesting + " testing," + numGen + " test generalization");

		m_Training = new Instances(m_Instances, 0, numTraining);
		m_Testing = new Instances(m_Instances, numTraining, numTesting);
		m_Generalization = new Instances(m_Instances, numTraining + numTesting,
				numGen);

	}
	
	/**
	 * set the random seed with value from property file
	 * @param properties
	 */
	protected void setRandomSeed(Properties properties) {
		m_RandomSeed = (int)(Double.parseDouble(properties.getProperty(PROPERTY_SEED)));
	}

	/**
	 * Load the properties list from the file with the specified property file
	 * name.
	 * 
	 * @param propertyFilename
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static Properties loadProperties(String propertyFilename)
			throws FileNotFoundException, IOException {
		// read the property file
		Properties properties = new Properties();
		properties.load(new FileReader(propertyFilename));
		properties.list(System.out);
		System.out.println("======================");

		return properties;
	}

	/**
	 * loop over all classifier and train the models.
	 * 
	 * @throws Exception
	 */
	protected void trainClassifiers() throws Exception {

		for (Classifier classifier : m_Classifiers) {
			classifier.buildClassifier(m_Training);
		}
	}

	/**
	 * loop over all classifier and evaluate the models on test set 1
	 * 
	 * @throws Exception
	 */
	protected void evaluateModels() throws Exception {

		System.out.println("\n\n-- Model Evaluation --\n");
		for (Classifier classifier : m_Classifiers) {

			Evaluation eval = new Evaluation(m_Testing);
			eval.evaluateModel(classifier, m_Testing);
			m_TestEvaluation.add(eval);

			System.out.println(eval.toSummaryString("\n\n== RESULTS: "
					+ classifier.toString() + "=============", true));

			System.out.println("fMeasure = " + eval.fMeasure(1)
					+ ", Area under ROC = " + eval.areaUnderROC(1)
					+ ", precision = " + eval.precision(1) + ", recall = "
					+ eval.recall(1));

		}
	}

	/**
	 * loop over all classifier and evaluate the models on test set 2 for generalization
	 * 
	 * @throws Exception
	 */

	protected void evaluateGeneralization() throws Exception {

		System.out.println("\n\n-- Model Generalization Evaluation --\n");
		for (Classifier classifier : m_Classifiers) {

			Evaluation eval = new Evaluation(m_Generalization);
			eval.evaluateModel(classifier, m_Generalization);
			m_GeneralizationEvaluation.add(eval);

			System.out.println(eval.toSummaryString(
					"RESULTS: " + classifier.toString() + "=============",
					false));
		}

	}

	/**
	 * create formatted summary string of classifier evaluation result using weighted average.
	 * @param eval
	 * @param classifier
	 * @return
	 */
	protected String toSummaryString(Evaluation eval, String classifier) {
		StringBuffer text = new StringBuffer();

		text.append("Weighted Avg.  "
				+ Utils.doubleToString(eval.weightedTruePositiveRate(), 7, 3));
		text.append("  "
				+ Utils.doubleToString(eval.weightedFalsePositiveRate(), 7, 3));
		text.append("  " + Utils.doubleToString(eval.weightedPrecision(), 7, 3));
		text.append("    " + Utils.doubleToString(eval.weightedRecall(), 7, 3));
		text.append(" " + Utils.doubleToString(eval.weightedFMeasure(), 7, 3));
		text.append("    "
				+ Utils.doubleToString(eval.weightedMatthewsCorrelation(), 7, 3));
		text.append(""
				+ Utils.doubleToString(eval.weightedAreaUnderROC(), 7, 3));
		text.append("   "
				+ Utils.doubleToString(eval.weightedAreaUnderPRC(), 7, 3));
		text.append("     "+classifier);//classifier.getClass().getName());



		return text.toString();

	}

	/**
	 * print the evaluation summary of all classifier as table
	 * @throws Exception
	 */
	protected void summary() throws Exception {

		double maxAreaUnderROC = 0.;
		int classifierIndex = 0;

		System.out.println("Model evaluation on 2nd set");

		System.out.println("\n                 TP Rate  FP Rate"
				+ "  Precision  Recall"
				+ "  F-Measure  MCC    ROC Area  PRC Area  Classifier + Options\n");

		int i = 0;
		for (Evaluation eval : m_TestEvaluation) {
			if ( eval.weightedAreaUnderROC() > maxAreaUnderROC ) {
				maxAreaUnderROC = eval.weightedAreaUnderROC();
				classifierIndex = i;
			}
			System.out.println(toSummaryString(eval, m_OrderedClassifierWithProperties.get(i++)));
			// System.out.println(toSummaryString(eval, m_Classifiers.get(i++)));
		}
		
		System.out.println("\nBest classifier on test set with ROC Area " +maxAreaUnderROC+" "+ m_OrderedClassifierWithProperties.get(classifierIndex));
		
		i = 0;
		System.out.println("\nGeneralization on 3rd set");
		
		System.out.println("\n                 TP Rate  FP Rate"
				+ "  Precision  Recall"
				+ "  F-Measure  MCC    ROC Area  PRC Area  Classifier + Options\n");

		for (Evaluation eval : m_GeneralizationEvaluation) {
			if ( eval.weightedAreaUnderROC() > maxAreaUnderROC ) {
				maxAreaUnderROC = eval.weightedAreaUnderROC();
				classifierIndex = i;
			}
//			System.out.println(toSummaryString(eval, m_Classifiers.get(i++)));
			System.out.println(toSummaryString(eval, m_OrderedClassifierWithProperties.get(i++)));
		}

		System.out.println("\nBest classifier on evaluation set with ROC Area " +maxAreaUnderROC+" "+ m_OrderedClassifierWithProperties.get(classifierIndex));

	}
	
	
	/**
	 * entry point for model selection, args has to specify the porperty filename "-f <filename>"
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		if (args.length < 2) {
			System.out.println(usage());
			System.exit(0);
		}

		ModelSelection modelSelection = new ModelSelection();

		try {

			String propertyFilename = Utils.getOption('f', args);
			if (propertyFilename.length()==0) {
				System.out.println("Option -f <filenam> not found.");
				System.out.println(usage());
				
				System.exit(0);
			}
			Properties properties = loadProperties(propertyFilename);

			modelSelection.setSplit(properties);
			modelSelection.setRandomSeed(properties);
			
			modelSelection.setClassifiers(properties); // create classifier and
														// add to list

			// load data set and split data set for training, testing and
			// generalization
			String datasetName = properties.getProperty(PROPERTY_DATASET);
			modelSelection.loadInstances(datasetName);

			// train classifiers with training dataset
			modelSelection.trainClassifiers();

			// evaluation with test data
			modelSelection.evaluateModels();

			// evaluation of the generalization error
			modelSelection.evaluateGeneralization();

			modelSelection.summary();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				// reader.close();
			} catch (Exception e) {
			}
		}

	}

	/**
	 * returns the usage of the class
	 */
	public static String usage() {
		return "\nusage:\n  " + ModelSelection.class.getName()
				+ "  -f <propertyfile> \n\ne.g., \n"
				+ "  java -classpath \".:weka.jar\" ModelSelection \n"
				+ "    -f modelselection.properties ";
	}

}

