package owl.cs.man.ac.uk.cogcomplexity;

import java.io.File;

import owl.cs.man.ac.uk.experiment.experiment.Experiment;
import owl.cs.man.ac.uk.experiment.experiment.ExperimentRunner;


public class CogComplexityExperimentRunner extends ExperimentRunner {

	public static void main(String[] args) {
		ExperimentRunner runner = new CogComplexityExperimentRunner();
		Experiment experiment = runner.configureExperiment(args);
		runner.runExperiment(experiment);
	}

	@Override
	protected Experiment prepare(String[] args) {
		if (args.length != 4) {
			throw new RuntimeException(
					"You need exactly four parameters: path to justification, path to experiment csv, reasoner and reasoner timeout.");
		}
		File just = new File(args[0]);
		File csv = new File(args[1]);
		String reasoner = args[2];
		int reasoner_timeout = Integer.valueOf(args[3]);
		this.setOntologyFile(just);
		this.setCSVFile(csv);
		return new CogComplexityExperiment(getOntologyFile(),getCSVFile(),reasoner,reasoner_timeout);
	}

}
