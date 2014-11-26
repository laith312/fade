package com.messenger.fade.ui.model;

public interface ItemType extends Comparable{
	public static final int TYPE_HEADER = 0;
	public static final int TYPE_SECTION = 1;

	public abstract int getType();
}
