package com.example.mywatch.phoneAppData;

public class TextEntry implements Entry {

	private final Entry.Type type;
	public String text;

	public TextEntry(String text) {
		type = Entry.Type.TEXT_ENTRY;
		if (text != null) {
			this.text = text;
		} else {
			this.text = "";
		}
	}

	public String getText() {
		return text;
	}

	public void setText(String textString) {
		this.text = textString;
	}

	/**
	 * Returns the Entry.Type of this class
	 * 
	 * @return Entry.Type.TEXT_ENTRY;
	 */
	@Override
	public Entry.Type getType() {
		return this.type;
	}
}
