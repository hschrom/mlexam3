import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;


public class TestPorperties {

	public TestPorperties() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Writer writer = null;
		Reader reader = null;

		try
		{
//		  writer = new FileWriter( "properties.txt" );
//
//		  Properties prop1 = new Properties( System.getProperties() );
//		  prop1.setProperty( "MeinNameIst", "Forrest Gump" );
//		  prop1.store( writer, "Eine Insel mit zwei Bergen" );

		  reader = new FileReader( "modelselection.properties" );

		  Properties prop2 = new Properties();
		  prop2.load( reader );
		  prop2.list( System.out );
		}
		catch ( IOException e )
		{
		  e.printStackTrace();
		}
		finally
		{
		  try { writer.close(); } catch ( Exception e ) { }
		  try { reader.close(); } catch ( Exception e ) { }
		}
		
	}

}
