package seth;


import de.hu.berlin.wbi.objects.DatabaseConnection;
import de.hu.berlin.wbi.objects.MutationMention;
import de.hu.berlin.wbi.objects.MutationMention.Tool;
import de.hu.berlin.wbi.objects.UniprotFeature;
import de.hu.berlin.wbi.objects.dbSNP;
import de.hu.berlin.wbi.objects.dbSNPNormalized;
import edu.uchsc.ccp.nlp.ei.mutation.Mutation;
import edu.uchsc.ccp.nlp.ei.mutation.MutationException;
import edu.uchsc.ccp.nlp.ei.mutation.MutationFinder;
import edu.uchsc.ccp.nlp.ei.mutation.PointMutation;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import seth.ner.wrapper.SETHNER;
import seth.ner.wrapper.Type;

public class testMutationExtraction
{
  private final SETHNER seth;
  private final MutationFinder mf;
  private final OldNomenclature bl;
  private final dbSNPRecognizer snpRecognizer;
  
  public  testMutationExtraction(String regexFile, boolean exactGrammar, boolean oldNomenclature)
  {
    this.mf = new MutationFinder(regexFile);
    this.seth = new SETHNER(exactGrammar);
    this.snpRecognizer = new dbSNPRecognizer();
    if (oldNomenclature) {
      this.bl = new OldNomenclature();
    } else {
      this.bl = null;
    }
  }
  
  public List<MutationMention> findMutations(String text)
  {
    Set<MutationMention> mutations = new HashSet();
    
    mutations.addAll(this.seth.extractMutations(text));
    
    mutations.addAll(this.snpRecognizer.extractMutations(text));
    Map<Mutation, Set<int[]>> map;
    if (this.bl != null) {
      mutations.addAll(this.bl.extractMutations(text));
    }
    try
    {
       map = this.mf.extractMutations(text);
      for (Mutation mutation : map.keySet())
      {
        PointMutation pm = (PointMutation)mutation;
        for (int[] location : map.get(mutation))
        {
          String originalMatch = text.substring(location[0], location[1]);
          
          MutationMention tmpMutation = new MutationMention(location[0], location[1], originalMatch, null, pm.getPosition(), String.valueOf(pm.getWtResidue()), String.valueOf(pm.getMutResidue()), Type.SUBSTITUTION, MutationMention.Tool.MUTATIONFINDER);
          
          tmpMutation.setPatternId(pm.getId());
          if (pm.isMatchesLongForm())
          {
            tmpMutation.setPsm(true);
            tmpMutation.setAmbiguous(false);
            tmpMutation.setNsm(false);
          }
          mutations.add(tmpMutation);
        }
      }
    }
    catch (MutationException e)
    {
      //Map<Mutation, Set<int[]>> map;
      PointMutation pm;
      e.printStackTrace();
      System.exit(1);
    }
    List<MutationMention> result = new ArrayList(mutations.size());
    for (MutationMention mm : mutations)
    {
      boolean contained = false;
      for (MutationMention m : result)
      {
        boolean equal = false;
        try
        {
          equal = (mm.getPosition().equals(m.getPosition())) && (mm.getMutResidue().equals(m.getMutResidue())) && (mm.getWtResidue().equals(m.getWtResidue()));
        }
        catch (NullPointerException npe) {}
        if ((mm.getStart() == m.getStart()) && (mm.getEnd() == m.getEnd()))
        {
          if ((mm.getTool() == MutationMention.Tool.SETH) && (m.getTool() == MutationMention.Tool.DBSNP))
          {
            contained = true;
            break;
          }
          if ((m.getTool() == MutationMention.Tool.SETH) && (mm.getTool() == MutationMention.Tool.DBSNP))
          {
            result.remove(m);
            break;
          }
        }
        else
        {
          if ((mm.getStart() >= m.getStart()) && (mm.getEnd() <= m.getEnd()) && (equal))
          {
            contained = true;
            break;
          }
          if ((m.getStart() >= mm.getStart()) && (m.getEnd() <= mm.getEnd()) && (equal))
          {
            result.remove(m);
            break;
          }
        }
      }
      if (!contained) {
        result.add(mm);
      }
    }
    return result;
  }
  
  public static void main(String[] args)
    throws SQLException, IOException
  {
	 // epsilon1369delG 
    String text ="n1369delG";//The results showed that the overall trend was CLiver>CKidney>CHeart>CSpleen>CLung, CC-30min>CM-30min>CM-60min>CC-5min>CM-5min>CC-60min>CM-240min>CC-240min.";
    
    testMutationExtraction seth = new testMutationExtraction("C://Users/lih16/workspace/TextMining/mutations.txt", true, true);
    List<MutationMention> mutations = seth.findMutations(text);
    try
    {
      for (MutationMention mutation : mutations) {
        System.out.println(mutation.toNormalized());
       // mutation.
      }
    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }
    /*Properties property = new Properties();
    property.loadFromXML(new FileInputStream(new File("myProperty.xml")));
    DatabaseConnection mysql = new DatabaseConnection(property);
    
    mysql.connect();
    dbSNP.init(mysql, property.getProperty("database.PSM"), property.getProperty("database.hgvs_view"));
    UniprotFeature.init(mysql, property.getProperty("database.uniprot"));
    
    int gene = 1312;
    List<dbSNP> potentialSNPs = dbSNP.getSNP(gene);
    List<UniprotFeature> features = UniprotFeature.getFeatures(gene);
    for (Iterator i$ = mutations.iterator(); i$.hasNext();)
    {
      MutationMention mutation = (MutationMention)i$.next();
      System.out.println(mutation);
      mutation.normalizeSNP(potentialSNPs, features, false);
      List<dbSNPNormalized> normalized = mutation.getNormalized();
      for (dbSNPNormalized snp : normalized) {
        System.out.println(mutation + " --- rs" + snp.getRsID());
      }
    }
    MutationMention mutation;*/
  }
}
