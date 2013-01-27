import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Debug.Random;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class ModelSelection {

	protected static final String PROPERTY_DATASET = "DATASET"; // property name
																// of property
																// holding data
																// file
	/** the classifier used internally */
	protected Vector<Classifier> m_Classifiers = new Vector<Classifier>();
	
	protected Vector<Evaluation> m_TestEvaluation = new Vector<Evaluation>();
	
	protected Vector<Evaluation> m_GeneralizationEvaluation = new Vector<Evaluation>();

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
		TreeSet<String> orderdProperties = new TreeSet<String>();
		for (String p : propertyNames) {
			String compare = p.toUpperCase();
			if (compare.startsWith("CLASSIFIER"))
				orderdProperties.add(p);
		}

		// split property strings into classifier name (1st part in string) and
		// options (remaining parts)
		// i.e.
		// weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0
		// name: weka.classifiers.functions.supportVector.PolyKernel
		// options: -C 250007 -E 1.0
		//
		System.out.println("-- listing classifiers --");
		for (String p : orderdProperties) {
			String options[] = Utils.splitOptions(properties.getProperty(p)); // split
			String classifierName = options[0]; // [0] name

			System.out.println("classifier name : " + properties.getProperty(p));

			String classifierOptions[] = null;
			if (options.length > 1) {
				classifierOptions = new String[options.length - 1]; // allocate
																	// options
																	// array

				System.arraycopy(options, 1, classifierOptions, 0,
						options.length - 1); // copy the options

//				int n = 0;
//				for (String s : classifierOptions) {
//					System.out.println("   option[" + n + "] " + s);
//					n++;
//				}
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
		
		Random random = new Random();
		m_Instances.randomize(random);
		
	    // Create subsets of instances for train, test 1 and test 2
		int num = m_Instances.numInstances();
		int numTraining = (int) (num*0.33);
		int numTesting 	= (int) (num*0.33);
		int numGen   	= num - numTraining - numTesting;
		
		System.out.println("Data split: "+numTraining+" training, "+numTesting+" testing,"+numGen+" test generalization");
		
	    m_Training 		= new Instances(m_Instances, 0, numTraining);
	    m_Testing     	= new Instances(m_Instances, numTraining, numTesting);
	    m_Generalization = new Instances(m_Instances, numTraining + numTesting, numGen );

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
	 * @throws Exception
	 */
	protected void trainClassifiers() throws Exception {
		
		for(Classifier classifier : m_Classifiers) {
			classifier.buildClassifier(m_Training);
		}
	}
	
	protected void evaluateModels() throws Exception {

		System.out.println("\n\n-- Model Evaluation --\n");
		for(Classifier classifier : m_Classifiers) {
			
			Evaluation eval = new Evaluation(m_Testing);
			eval.evaluateModel(classifier, m_Testing);
			m_TestEvaluation.add(eval);
			
			System.out.println(eval.toSummaryString("RESULTS: "+classifier.toString() + "=============", true));

			System.out.println("fMeasure = " + eval.fMeasure(1) + ", Area under ROC = "+eval.areaUnderROC(1)+", precision = " + eval.precision(1)+ ", recall = "+ eval.recall(1));

		}
	}
	
	protected void evaluateGeneralization() throws Exception {

		System.out.println("\n\n-- Model Generalization Evaluation --\n");
		for(Classifier classifier : m_Classifiers) {
			
			Evaluation eval = new Evaluation(m_Generalization);
			eval.evaluateModel(classifier, m_Generalization);
			m_GeneralizationEvaluation.add(eval);
			
			System.out.println(eval.toSummaryString("RESULTS: "+classifier.toString() + "=============", false));

			System.out.println("fMeasure = " + eval.fMeasure(1) + ", Area under ROC = "+eval.areaUnderROC(1)+", precision = " + eval.precision(1)+ ", recall = "+ eval.recall(1));
//			System.out.println("correctly classified: "+ eval.correct()+"incorrect classified: "+eval.incorrect());
		}
		
	}
 
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		if (args.length < 2) {
			System.out.println(usage());
			System.exit(0);
		}

		ModelSelection modelSelection = new ModelSelection();

		// TODO Auto-generated method stub
		try {

			// String propertyFilename = Utils.getOption('f', args);
			// Properties properties = loadProperties(propertyFilename);

			Properties properties = loadProperties("modelselection.properties");
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
			


			System.out.println("done");

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

// http://weka.wikispaces.com/Use+WEKA+in+your+Java+code
// Train/test set
// In case you have a dedicated test set, you can train the classifier and then
// evaluate it on this test set. In the following example, a J48 is
// instantiated, trained and then evaluated. Some statistics are printed to
// stdout:
// import weka.core.Instances;
// import weka.classifiers.Evaluation;
// import weka.classifiers.trees.J48;
// ...
// Instances train = ... // from somewhere
// Instances test = ... // from somewhere
// // train classifier
// Classifier cls = new J48();
// cls.buildClassifier(train);
// // evaluate classifier and print some statistics
// Evaluation eval = new Evaluation(train);
// eval.evaluateModel(cls, test);
// System.out.println(eval.toSummaryString("\nResults\n======\n", false));
