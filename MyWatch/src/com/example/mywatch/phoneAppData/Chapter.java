package com.example.mywatch.phoneAppData;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Chapter {
	private String name;
	private List<Entry> entryList;

	public List<Entry> getEntryList() {
		return entryList;
	}

	public List<ImageEntry> getImageEntries() {
		List<ImageEntry> eList = new ArrayList<ImageEntry>();
		for (Entry e : entryList) {
			if (e.getType().equals(Entry.Type.IMAGE_ENTRY)) {
				eList.add((ImageEntry) e);
			}
		}
		return eList;
	}

	public Chapter(String name) {
		this.entryList = new ArrayList<Entry>();
		this.name = name;
	}
	
	public void setEntryList(List<Entry> eList) {
		this.entryList = eList;
	}

	public String getName() {
		return name;
	}
}
