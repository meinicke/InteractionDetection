import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

class Configuration {
	Selection[] selections;
	
	boolean fails = false;
	
	public Configuration(Selection[] selections) {
		this.selections = selections;
	}
	
	public Configuration(Collection<FeatureSelection> possibleFeatureSelections, int size) {
		selections = new Selection[size]; 
		for (FeatureSelection featureSelection : possibleFeatureSelections) {
			selections[featureSelection.position] = featureSelection.selection;
		}
	}
	
	

	@Override
	public String toString() {
		return fails + " Conf: " + Arrays.toString(selections);
	}
	
	static Configuration createRandomConfiguration(int size) {
		Selection[] newSelections = new Selection[size]; 
		Random rand = new Random();
		for (int i = 0; i < newSelections.length; i++) {
			newSelections[i] = rand.nextBoolean() ? Selection.Selected : Selection.Deselected; 
		}
		return new Configuration(newSelections);
		
	}

	public static Configuration createConfigurationFromInteractions(Collection<Interaction> interactions, int configurationSize) {
		Selection[] newSelections = new Selection[configurationSize];
		for (Interaction interaction : interactions) {
			for (FeatureSelection selection : interaction.selections) {
				newSelections[selection.position] = selection.selection;
			}
		}
		return new Configuration(newSelections);
	}
}