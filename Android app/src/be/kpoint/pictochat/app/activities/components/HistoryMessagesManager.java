package be.kpoint.pictochat.app.activities.components;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import be.kpoint.pictochat.app.domain.PastPictoMessage;

public class HistoryMessagesManager
{
	private SortedSet<PastPictoMessage> sorted;

	public HistoryMessagesManager() {
		this.sorted = new TreeSet<PastPictoMessage>(
			new Comparator<PastPictoMessage>() {
				@Override
				public int compare(PastPictoMessage lhs, PastPictoMessage rhs) {
					if (lhs.equals(rhs))
						return 0;

					return lhs.getTime().compareTo(rhs.getTime());
				}
			}
		);
	}

	public void addMessage(PastPictoMessage pictoMessage) {
		if (!this.sorted.contains(pictoMessage))
			this.sorted.add(pictoMessage);
	}
	public void clear() {
		this.sorted.clear();
	}

	public SortedSet<PastPictoMessage> getMessages() {
		return this.sorted;
	}
}