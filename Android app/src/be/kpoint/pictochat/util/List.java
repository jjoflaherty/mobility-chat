package be.kpoint.pictochat.util;

import java.util.Iterator;


public abstract class List
{
	public static <T, U> void updateList(java.util.List<T> currentList, java.util.List<U> newList, IConverts<T, U> converter)
	{
		for (U newItem : newList) {
			if (!currentList.contains(newItem))
				currentList.add(converter.convert(newItem));
		}

		for (Iterator<T> it = currentList.iterator(); it.hasNext();) {
			T currentItem = it.next();
			if (!newList.contains(currentItem))
				it.remove();
		}
	}

	public interface IConverts<T, U> {
		T convert(U newValue);
	}
}
