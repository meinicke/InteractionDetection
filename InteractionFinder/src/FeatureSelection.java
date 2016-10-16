
class FeatureSelection implements Comparable<FeatureSelection> {
	int position;
	Selection selection;
	
	public FeatureSelection(int position, Selection selection) {
		this.position = position;
		this.selection = selection;
	}

	@Override
	public String toString() {
		return "FeatureSelection [position=" + position + ", selection=" + selection + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + position;
		result = prime * result + ((selection == null) ? 0 : selection.hashCode());
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
		FeatureSelection other = (FeatureSelection) obj;
		if (position != other.position)
			return false;
		if (selection != other.selection)
			return false;
		return true;
	}

	@Override
	public int compareTo(FeatureSelection o) {
		return Integer.compare(position, o.position);
	}
	
	
}