package com.jeecms.cms.entity.main.base;

import java.io.Serializable;

/**
 * 
 * @author Administrator
 * table="jc_custom_form_filed"
 */
public class BaseCustomFormFiled implements Serializable {
			
			public static String REF = "CustomFormFiled";
			public static String PROP_DATA_TYPE = "dataType";
			public static String PROP_OPT_VALUE = "optValue";
			public static String PROP_DESCRIPTION = "description ";
			public static String PROP_PRIORITY = "priority";
			public static String PROP_FIELD = "field";
			public static String PROP_LABEL = "label";
			public static String PROP_MODEL = "form";
			public static String PROP_DEF_VALUE = "defValue";		
			public static String PROP_SIZE = "size";
			public static String PROP_DISPLAY_IN_LIST = "displayInList";
			public static String PROP_ID = "id";
		
		
			// constructors
			public BaseCustomFormFiled() {
				initialize();
			}
		
			/**
			 * Constructor for primary key
			 */
			public BaseCustomFormFiled (java.lang.Integer id) {
				this.setId(id);
				initialize();
			}
		
			/**
			 * Constructor for required fields
			 */
			public BaseCustomFormFiled (
				java.lang.Integer id,
				com.jeecms.cms.entity.main.CustomForm form,
				java.lang.String field,
				java.lang.String label,
				java.lang.Integer dataType,
				java.lang.Boolean displayInList) {
		
				this.setId(id);
				this.setForm(form);
				this.setField(field);
				this.setLabel(label);
				this.setDataType(dataType);
				this.setDisplayInList(displayInList);
				initialize();
			}

		
	
		protected void initialize () {}
	
		private int hashCode = Integer.MIN_VALUE;

		// primary key
		private java.lang.Integer id;

		// fields
		private java.lang.String field;
		private java.lang.String label;
		private java.lang.Integer priority;
		private java.lang.String defValue;
		private java.lang.String optValue;
		private java.lang.String size;
		private java.lang.String description;
		private java.lang.Integer dataType;
		private java.lang.Boolean displayInList;
		private java.lang.Boolean required;

		// many to one
		private com.jeecms.cms.entity.main.CustomForm form;
		
		public java.lang.Integer getId() {
			return id;
		}

		public void setId(java.lang.Integer id) {
			this.id = id;
		}

		public java.lang.String getField() {
			return field;
		}

		public void setField(java.lang.String field) {
			this.field = field;
		}

		public java.lang.String getLabel() {
			return label;
		}

		public void setLabel(java.lang.String label) {
			this.label = label;
		}

		public java.lang.Integer getPriority() {
			return priority;
		}

		public void setPriority(java.lang.Integer priority) {
			this.priority = priority;
		}

		public java.lang.String getDefValue() {
			return defValue;
		}

		public void setDefValue(java.lang.String defValue) {
			this.defValue = defValue;
		}

		public java.lang.String getOptValue() {
			return optValue;
		}

		public void setOptValue(java.lang.String optValue) {
			this.optValue = optValue;
		}

		public java.lang.String getSize() {
			return size;
		}

		public void setSize(java.lang.String size) {
			this.size = size;
		}

		public java.lang.String getDescription() {
			return description;
		}

		public void setDescription(java.lang.String description) {
			this.description = description;
		}

		public java.lang.Integer getDataType() {
			return dataType;
		}

		public void setDataType(java.lang.Integer dataType) {
			this.dataType = dataType;
		}

		public java.lang.Boolean getDisplayInList() {
			return displayInList;
		}

		public void setDisplayInList(java.lang.Boolean displayInList) {
			this.displayInList = displayInList;
		}

		public java.lang.Boolean getRequired() {
			return required;
		}

		public void setRequired(java.lang.Boolean required) {
			this.required = required;
		}

		public com.jeecms.cms.entity.main.CustomForm getForm() {
			return form;
		}

		public void setForm(com.jeecms.cms.entity.main.CustomForm form) {
			this.form = form;
		}
		
		public boolean equals (Object obj) {
			if (null == obj) return false;
			if (!(obj instanceof com.jeecms.cms.entity.main.CustomForm)) return false;
			else {
				com.jeecms.cms.entity.main.CustomFormFiled customFormFiled = (com.jeecms.cms.entity.main.CustomFormFiled) obj;
				if (null == this.getId() || null == customFormFiled.getId()) return false;
				else return (this.getId().equals(customFormFiled.getId()));
			}
		}

		public int hashCode () {
			if (Integer.MIN_VALUE == this.hashCode) {
				if (null == this.getId()) return super.hashCode();
				else {
					String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
					this.hashCode = hashStr.hashCode();
				}
			}
			return this.hashCode;
		}


		public String toString () {
			return super.toString();
		}
		
}
