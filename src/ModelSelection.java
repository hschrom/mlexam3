import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

public class ModelSelection {

	/** the classifier used internally */
	protected Classifier m_Classifier = null;

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
	 */
	public void setClassifiers(Properties properties)  {
		// get property name list
		Set<String> propertyNames = properties.stringPropertyNames(); // properties.propertyNames();

		// create orderd set with CLASSIFIER properties only
		TreeSet<String> orderdClassifierProperties = new TreeSet<String>();
		
		for (String p : propertyNames) {
			String compare = p.toUpperCase();
			if(compare.startsWith("CLASSIFIER"))
				orderdClassifierProperties.add(p);
		}
		
		
        for (String p : orderdClassifierProperties) {
            System.out.println("property: "+p);
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
	 * @param args
	 */
	public static void main(String[] args) {
		
		if( args.length < 2) {
			System.out.println(usage());
			System.exit(0);
		}
		
		ModelSelection modelSelection = new ModelSelection();
		
		FileReader reader = null;
		// TODO Auto-generated method stub
		try {
			// writer = new FileWriter( "properties.txt" );
			//
			// Properties prop1 = new Properties( System.getProperties() );
			// prop1.setProperty( "MeinNameIst", "Forrest Gump" );
			// prop1.store( writer, "Eine Insel mit zwei Bergen" );

			reader = new FileReader("modelselection.properties");

			// read the property file
			Properties properties = new Properties();
			properties.load(reader);
			properties.list(System.out);

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
				reader.close();
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