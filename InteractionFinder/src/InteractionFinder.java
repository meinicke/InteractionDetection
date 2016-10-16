import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;


/**
 * Detect interactions from given set of configurations.
 * 
 * @author Jens Meinicke
 *
 * TODO how to detect A || B?
 * TODO implement for higher T
 */
public class InteractionFinder {
	
	private Collection<Configuration> validConfs = new ArrayList<>();
	private Collection<Configuration> failingConfs = new ArrayList<>();
	
	public InteractionFinder(Collection<Configuration> confs) {
		for (Configuration configuration : confs) {
			if (configuration.fails) {
				failingConfs.add(configuration);
			} else {
				validConfs.add(configuration);
			}
		}
	}
	
	private Interaction findPairWise() {
		int length = failingConfs.iterator().next().selections.length;
		// create Interactions
		FeatureSelection[] array = failingSimilarities.toArray(new FeatureSelection[failingSimilarities.size()]);
		final Collection<Interaction> interactions = new HashSet<>();
		for (int i = 0; i < array.length - 1; i++) {
			for (int j = i + 1; j < array.length; j++) {// TODO nest this to higher T values
				interactions.add(new Interaction(array[i], array[j]));
			}
		}
		
		// remove interactions contained in validConfs
		final Collection<Interaction> interactions2 = new HashSet<>();
		LOOP : for (Interaction interaction : interactions) {
			for (Configuration configuration : validConfs) {
				if (interaction.containsInteraction(configuration)) {
					continue LOOP; 
				}
			}
			interactions2.add(interaction);
		}
		
		Collection<Interaction> result = binarySearch2(interactions2, length);
		assert result.size() == 1;
		return result.iterator().next();
	}
	
	private Collection<Interaction> binarySearch2(Collection<Interaction> possibleInteractions, int configurationSize) {
		while (possibleInteractions.size() > 1) {
			int numberOfFeatures = calculateNumberOfFeatures(possibleInteractions);
			final Collection<FeatureSelection> selections = new HashSet<>();
			final Collection<Interaction> interactionsLeft = new HashSet<>();
			LOOP: for (Interaction interaction : possibleInteractions) {
				if (selections.size() >= numberOfFeatures / 2) {
					for (FeatureSelection selection : interaction.selections) {// contains all
						if (!selections.contains(selection)) {
							continue LOOP; 
						}
					}
				} else {
					for (FeatureSelection selection : interaction.selections) {
						selections.add(selection);
					}
				}
				interactionsLeft.add(interaction);
			}
			// create configuration from first half
			Configuration c = Configuration.createConfigurationFromInteractions(interactionsLeft, configurationSize);
			if (Failure.check(c)) {
				HashSet<Interaction> interactionsRight = new HashSet<>(possibleInteractions);
				interactionsRight.removeAll(interactionsLeft);
				Configuration c2 = Configuration.createConfigurationFromInteractions(interactionsRight, configurationSize);
				if (Failure.check(c2)) {
					possibleInteractions.clear();
					
					for (Interaction interaction : interactionsLeft) {
						if (interaction.containsInteraction(c) &&
						    interaction.containsInteraction(c2)) {
							possibleInteractions.add(interaction);
						}
					}
					for (Interaction interaction : interactionsRight) {
						if (interaction.containsInteraction(c) &&
							    interaction.containsInteraction(c2)) {
							possibleInteractions.add(interaction);
						}
					}
				} else {
					// go on with first half
					possibleInteractions = interactionsLeft;
				}
			} else {
				// go on with second half 
				possibleInteractions.removeAll(interactionsLeft);
			}
		}
		Configuration c = Configuration.createConfigurationFromInteractions(possibleInteractions, configurationSize);
		if (Failure.check(c)) {// check wether the last found selection causes the fault
			return possibleInteractions;
		} else {
			return Collections.emptyList();
		}
	}
	
	private int calculateNumberOfFeatures(Collection<Interaction> possibleInteractions) {
		final Collection<FeatureSelection> allSelections = new HashSet<>();
		for (Interaction interaction : possibleInteractions) {
			for (FeatureSelection selection : interaction.selections) {
				allSelections.add(selection);
			}
		}
		return allSelections.size();
	}

	private final Collection<FeatureSelection> failingSimilarities = new HashSet<>();
		
	private Collection<FeatureSelection> findOneWise() {
		int length = failingConfs.iterator().next().selections.length;
		for (int i = 0; i < length; i++) {
			Selection selection = null;
			for (Configuration c : failingConfs) {
				if (selection == null) {
					selection = c.selections[i];
					failingSimilarities.add(new FeatureSelection(i, selection));
					continue;
				}
				if ((c.selections[i] == selection)) {
					continue;
				} else {
					failingSimilarities.remove(new FeatureSelection(i, selection));
					break;
				}
			}
		}
		
		final Collection<FeatureSelection> failingSelections = new HashSet<>(failingSimilarities);
		
		// remove valid selections
		for (FeatureSelection featureSelection : failingSimilarities) {
			for (Configuration configuration : validConfs) {
				if (configuration.selections[featureSelection.position] == featureSelection.selection) {
					failingSelections.remove(featureSelection);
					break;
				}
			}
		}

		if (failingSelections.isEmpty()) {
			return Collections.emptyList();
		}
		
		Collection<FeatureSelection> foundSelection = binarySearch(failingSelections, length);
		if (foundSelection.isEmpty()) {
			return Collections.emptyList();
		}
		return foundSelection;
	}

	private Collection<FeatureSelection> binarySearch(Collection<FeatureSelection> possibleFeatureSelections, int configurationSize) {
		while (possibleFeatureSelections.size() > 1) {
			Collection<FeatureSelection> checkList = new ArrayList<>(possibleFeatureSelections.size() / 2);
			
			int i = 0;
			for (FeatureSelection featureSelection : possibleFeatureSelections) {
				if (i++ == possibleFeatureSelections.size() / 2) {
					break;
				}
				checkList.add(featureSelection);
			}
			// create configuration from first half
			Configuration c = new Configuration(checkList, configurationSize);
			if (Failure.check(c)) {
				// go on with first half
				possibleFeatureSelections = checkList;
			} else {
				// go on with second half 
				possibleFeatureSelections.removeAll(checkList);
			}
		}
		Configuration c = new Configuration(possibleFeatureSelections, configurationSize);
		if (Failure.check(c)) {// check wether the last found selection causes the fault
			return possibleFeatureSelections;
		} else {
			return Collections.emptyList();
		}
	}

	public static void main(String[] args) {
		int nrOfConfs = 4;
		int nrOfFeatures = 10;
		
		int rounds = 100;
		
		Random rand = new Random();
		while (rounds-- > 0) {
			int nextInt = rand.nextInt(nrOfFeatures);
			int nextInt2 = rand.nextInt(nrOfFeatures);
			while (nextInt2 == nextInt) {
				nextInt2 = rand.nextInt(nrOfFeatures);
			}
			final Interaction interaction = new Interaction(new FeatureSelection(nextInt, Selection.Selected), new FeatureSelection(nextInt2, Selection.Deselected));
			Failure.interaction = interaction;
			
			Collection<Configuration> confs = new ArrayList<>(nrOfConfs);
			for (int i = 0; i < nrOfConfs; i++) {
				confs.add(Configuration.createRandomConfiguration(nrOfFeatures));
			}
			
			boolean foundFailure = false;
			for (Configuration configuration : confs) {
				configuration.fails = Failure.check(configuration);
				if (configuration.fails) {
					foundFailure = true;
				}
			}
			if (!foundFailure) {
				rounds++;
				continue;
			}
			Failure.nrOfChecks = 0;
			InteractionFinder IF = new InteractionFinder(confs);
			Collection<FeatureSelection> findOneWise = IF.findOneWise();
			if (!findOneWise.isEmpty()) {
				System.out.println(findOneWise);
				continue;
			}
			Interaction findPairWise = IF.findPairWise();
			assert findPairWise.equals(interaction);
			System.out.println(findPairWise);
			System.out.println("Additional Checks: " + Failure.nrOfChecks);
			System.out.println("-------------------------------------");
		}
	}



}

class Failure {
	
	static Interaction interaction;
	
	static int nrOfChecks = 0;
	
	static boolean check(Configuration c) {
		nrOfChecks++;
		return interaction.containsInteraction(c);
	}
}
