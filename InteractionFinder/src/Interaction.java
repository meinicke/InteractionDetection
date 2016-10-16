import java.util.Arrays;

class Interaction implements Comparable<Interaction>{
	public FeatureSelection[] selections;

	public Interaction(FeatureSelection... selections) {
		this.selections = selections;
		Arrays.sort(this.selections);
	}
	
	@Override
	public String toString() {
		return Arrays.toString(selections);
	}
	
	boolean containsInteraction(Configuration c) {
		for (FeatureSelection selection : selections) {
			if (c.selections[selection.position] != selection.selection) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(selections);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Interaction other = (Interaction) obj;
		if (!Arrays.equals(selections, other.selections))
			return false;
		return true;
	}

	@Override
	public int compareTo(Interaction other) {
		for (FeatureSelection featureSelection : selections) {
			for (FeatureSelection otherFeatureSelection : other.selections) {
				int compareTo = featureSelection.compareTo(otherFeatureSelection);
				if (compareTo != 0) {
					return compareTo;
				}
			}
		}
		return 0;
	}
	
	
}