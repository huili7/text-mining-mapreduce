package edu.mssm.icahn.playground;

import scala.collection.immutable.List;
//import simplexnlp.example.Disease;
//import de.berlin.hu.eumed.DiseaseTaggerObject;

public class DiseaseTest {
	
	private static String pathToCRFModel ="/home/philippe/workspace/mssm/resources/tim/130708-crf.bin";
	private static String pathToDictionary ="/home/philippe/workspace/mssm/resources/tim/umls.zip";
	private static String pathToSentenceModel ="/home/philippe/workspace/mssm/resources/tim/SentDetectGenia.bin";
	private static String pathToPosModel ="/home/philippe/workspace/mssm/resources/tim/Tagger_Genia.bin";


    private static String pathToCargegina = "/lddb/cartagenia.tsv";
    private static String pathToCargeginaUmls = "/lddb/cartageniaUMLS.tsv";
    private static String pathTolddb2pho= "/lddb/lddb2hpo.tsv";
    private static String pathTo_hpo2UMLS = "/lddb/hpo2umls.tsv";

	
	public static void main(String[] args) {

		/*DiseaseTaggerObject tagger = new
				DiseaseTaggerObject(pathToCRFModel, pathToDictionary,
						pathToSentenceModel, pathToPosModel, pathToCargegina,
						pathToCargeginaUmls, pathTolddb2pho, pathTo_hpo2UMLS);		
		
        List<Disease> result = tagger.extractDiseases("This is a test sentence containing breast cancer and other horrible diseases.");
        System.out.println(result.mkString("\n"));*/

	}

}
