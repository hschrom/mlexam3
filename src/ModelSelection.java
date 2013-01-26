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
import weka.core.Instances;
import weka.core.Utils;

public class ModelSelection {

	/** the classifier used internally */
	protected Vector<Classifier> m_Classifiers = null;

	/** the training file */
	protected String m_TrainingFile = null;

	/** the training instances */
	protected Instances m_TrainingInstances = null;

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
	public void setClassifiers(Properties properties) throws Exception  {
		// get property name list
		Set<String> propertyNames = properties.stringPropertyNames(); // properties.propertyNames();

		// create orderd set with CLASSIFIER properties only
		TreeSet<String> orderdProperties = new TreeSet<String>();
		for (String p : propertyNames) {
			String compare = p.toUpperCase();
			if(compare.startsWith("CLASSIFIER"))
				orderdProperties.add(p);
		}
		
		// split property strings into classifier name (1st part in string) and options (remaining parts)
		// i.e.
		//    weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0
		//       name: weka.classifiers.functions.supportVector.PolyKernel
		//       options: -C 250007 -E 1.0
		//
        for (String p : orderdProperties) {
            String options[] = Utils.splitOptions(properties.getProperty(p));		// split
            String classifierName = options[0];										// [0] name
            String classifierOptions[] = new String[options.length - 1];			// allocate options array
            
            System.arraycopy(options, 1, classifierOptions, 0, options.length-1);	// copy the options
            
            System.out.println("classifer name : " + classifierName);
            int n = 0;
            for( String s : classifierOptions ) {
            	System.out.println("   option["+n+"] "+s);
            	n++;
            }

            // create classifier with specified options and append to classifier pool
            m_Classifiers.add( (Classifier)Utils.forName(Classifier.class,	classifierName, classifierOptions) );
        }
		
        
// 		m_Classifier = Classifier.forName(name, options);
	}

	/**
	 * sets the file to use for training
	 */
	public void setTraining(String name) throws Exception {
		m_TrainingFile = name;
		m_TrainingInstances = new Instances(new BufferedReader(new FileReader(
				m_TrainingFile)));
		m_TrainingInstances.setClassIndex(m_TrainingInstances.numAttributes() - 1);
	

	}
	
	/**
	 * Load the properties list from the file with the specified property file name.
	 * @param propertyFilename
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static Properties loadProperties(String propertyFilename) throws FileNotFoundException, IOException {
		// read the property file
		Properties properties = new Properties();
		properties.load(new FileReader(propertyFilename));
		properties.list(System.out);
		
		return properties;
	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		if( args.length < 2) {
			System.out.println(usage());
			System.exit(0);
		}
		
		ModelSelection modelSelection = new ModelSelection();
		
		// TODO Auto-generated method stub
		try {
			// writer = new FileWriter( "properties.txt" );
			//
			// Properties prop1 = new Properties( System.getProperties() );
			// prop1.setProperty( "MeinNameIst", "Forrest Gump" );
			// prop1.store( writer, "Eine Insel mit zwei Bergen" );


			Properties properties = loadProperties("modelselection.properties");
			modelSelection.setClassifiers(properties);
			
			
			
			System.out.println("unsorted:");
						


			// Iterator props = properties.keySet().iterator();
			// while (props.hasNext()) {
			// System.out.println(props.);
			// props.next();
			// }

			System.out.println("done");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				//reader.close();
			} catch (Exception e) {
			}
		}

	}

	/**
	 * returns the usage of the class
	 */
	public static String usage() {
		return "\nusage:\n  " + ModelSelection.class.getName()
				+ "  -P <propertyfile> \n\ne.g., \n"
				+ "  java -classpath \".:weka.jar\" ModelSelection \n"
				+ "    -P modelselection.properties ";
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
