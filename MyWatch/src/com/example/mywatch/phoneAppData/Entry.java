package com.example.mywatch.phoneAppData;

public interface Entry {
	public enum Type {
		TEXT_ENTRY, IMAGE_ENTRY
	}
	public Entry.Type getType();
}
