package edu.mssm.icahn.mapreduce.normalize;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import seth.ner.wrapper.Type;
import de.hu.berlin.wbi.objects.DatabaseConnection;
import de.hu.berlin.wbi.objects.Gene;
import de.hu.berlin.wbi.objects.MutationMention;
import de.hu.berlin.wbi.objects.MutationMention.Tool;
import de.hu.berlin.wbi.objects.UniprotFeature;
import de.hu.berlin.wbi.objects.dbSNP;
import de.hu.berlin.wbi.objects.dbSNPNormalized;



public class SETHNerReducer  extends Reducer<Text, Text, Text, Text> {
	private final String SETH_property = "seth.property";
	private static DatabaseConnection mysql;
	private static final Log LOG = LogFactory.getLog(SETHNerReducer.class);
	
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
		LOG.info("Phil: Setting up Reducer");
		Configuration conf = context.getConfiguration();

		//Database initialization for normalization
		final Properties property = new Properties();
		try {
			LOG.info("Phil: Loading properties");
			property.loadFromXML(FileSystem.get(context.getConfiguration()).open(new Path(conf.get(SETH_property))));
		} catch (Exception e) {
			LOG.info("Phil: Properties not loaded");
			e.printStackTrace();
			System.exit(1);
		}
		LOG.info("Phil: Properties loaded");
		mysql = new DatabaseConnection(property);	//Only used for normalization
		mysql.connect();
		try {
			LOG.info("Phil: Connecting to database");
			dbSNP.init(mysql, property.getProperty("database.PSM"), property.getProperty("database.hgvs_view"));
			Gene.init(mysql, property.getProperty("database.geneTable"), property.getProperty("database.gene2pubmed"));
			UniprotFeature.init(mysql, property.getProperty("database.uniprot"));
		} catch (SQLException e) {
			LOG.info("Phil: Connecting failed");
			LOG.info(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
		mysql.disconnect();
	}

	@Override
	public void reduce (Text pmid, Iterable<Text> values, Context context) throws IOException, InterruptedException {

		//Initialize this information for dbSNP
		final Set<Gene> genes = Gene.queryGenesForArticle(Integer.parseInt(pmid.toString()));
		final Map<Gene, List<dbSNP>> geneToDbSNP = new HashMap<Gene, List<dbSNP>>(); 		
		final Map<Gene, List<UniprotFeature>> geneToUniProtFeature = new HashMap<Gene, List<UniprotFeature>>(); 
		for (Gene gene : genes) { 		
			final List<dbSNP> potentialSNPs = dbSNP.getSNP(gene.getGeneID()); 		
			final List<UniprotFeature> features = UniprotFeature.getFeatures(gene.getGeneID()); 		

			geneToDbSNP.put(gene, potentialSNPs); 		
			geneToUniProtFeature.put(gene, features); 		
		}
		
		
		
		for(Text value : values){
			String [] split = value.toString().split("\t");

			MutationMention mm  = new MutationMention(Integer.parseInt(split[1]), Integer.parseInt(split[2]), split[3], split[4], split[5], split[6], split[7], Type.valueOf(split[8]), Tool.valueOf(split[9]));
			
			for (Gene gene : genes) {
				final List<dbSNP> potentialSNPs = geneToDbSNP.get(gene);
				final List<UniprotFeature> features = geneToUniProtFeature.get(gene);

				mm.normalizeSNP(potentialSNPs, features, true);
			}
			
			if(mm.getNormalized() != null && mm.getNormalized().size() > 0){
				final Set<Integer> rsIDs = new HashSet<Integer>();
				for (dbSNPNormalized norm : mm.getNormalized()) {
					rsIDs.add(norm.getRsID());
				}
									
				StringBuilder sb = new StringBuilder(rsIDs.size() * 10);
				for (Integer rsID : rsIDs) {
					if (sb.length() > 0)
						sb.append("|");
					sb.append(rsID);
				}	
				context.write(value, new Text(sb.toString()));
			}			
		}	
	}
}
