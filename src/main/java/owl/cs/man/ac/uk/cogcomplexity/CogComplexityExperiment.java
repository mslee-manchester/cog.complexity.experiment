package owl.cs.man.ac.uk.cogcomplexity;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owl.explanation.complexity.KuduComplexityCalculator;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.Version;

import owl.cs.man.ac.uk.experiment.experiment.ReasonerExperiment;



public class CogComplexityExperiment extends ReasonerExperiment{

	public CogComplexityExperiment(File ontfile, File csvfile,
			 String reasonername, int reasoner_timeout) {
		super(ontfile, csvfile, null, reasonername, reasoner_timeout);
	}

	@Override
	protected void process() throws Exception {
		// TODO Auto-generated method stub
		KuduComplexityCalculator calc = new KuduComplexityCalculator(this.getReasonerFactory());
		System.out.println("Reasoner: " + getReasonerName());
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		long startload = System.nanoTime();
		OWLOntology o = manager.loadOntologyFromOntologyDocument(getOntologyFile());
		long endload = System.nanoTime();
		OWLAxiom ax = this.getAxiom(o);
		Set<OWLAxiom> set = new HashSet<OWLAxiom>();
		for(OWLAxiom la:o.getLogicalAxioms())
		{
			set.add(la);
		}
		Explanation<OWLAxiom> exp = new Explanation<OWLAxiom>(ax,set);
		addResult("filename",getOntologyFile().getName());
		addResult("entailment",ax.toString());
		long startcalc = System.nanoTime();
		addResult("cog_complexity", ""+ calc.computeComplexity(exp, ax, set));
		long endcalc = System.nanoTime();
	}

	@Override
	protected Version getExperimentVersion() {
		// TODO Auto-generated method stub
		return new Version(1,0,0,1);
	}
	
	//Reused method from isomatch, will need a better method to deal
		//with more complex entailments.
		private OWLAxiom getAxiom(OWLOntology o) {
			OWLDataFactory df = OWLManager.getOWLDataFactory();
			Set<OWLClass> anjuClass = o.getClassesInSignature();
			anjuClass.add(df.getOWLThing());
			anjuClass.add(df.getOWLNothing());
			OWLClass subclass = null;
			OWLClass supclass = null;
			Boolean cond1 = false;
			Boolean cond2 = false;
			for(OWLClass cl:anjuClass)
			{
				Set<OWLAnnotationAssertionAxiom> anno = cl.getAnnotationAssertionAxioms(o);
				if(!anno.isEmpty())
				{
					for(OWLAnnotationAssertionAxiom aax : anno)
					{
						if(aax.getAnnotation().getValue().toString().contains("superclass") && !cond1)
						{
								cond1 = true;
								supclass = cl;
						}
						else if(aax.getAnnotation().getValue().toString().contains("subclass") && !cond2)
						{
								cond2 = true;
								subclass = cl;
						}
					}
					
				}
			}
			if(cond1 && cond2)
			{
				return df.getOWLSubClassOfAxiom(subclass, supclass);
			}
			else 
			{
				return null;
			}
		}
	
}
