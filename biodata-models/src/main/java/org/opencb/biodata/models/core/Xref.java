/*
 * <!--
 *   ~ Copyright 2015-2017 OpenCB
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~     http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License.
 *   -->
 *
 */

package org.opencb.biodata.models.core;

public class Xref {
	
	private String id;
	private String dbName;
	private String dbDisplayName;
	private String description;

    public Xref() {
    }

    public Xref(String id, String dbName, String dbDisplayName) {
        this(id, dbName, dbDisplayName, "");
    }

    public Xref(String id, String dbName, String dbDisplayName, String description) {
		this.id = id;
		this.dbName = dbName;
		this.dbDisplayName = dbDisplayName;
		this.description = description;
	}
	
	@Override
	public boolean equals(Object obj) {
		Xref xrefObj = (Xref)obj;
		return id.equals(xrefObj.id) && dbName.equals(xrefObj.dbName);
	}
	
	@Override
	public int hashCode() {
		return (id+ dbName).hashCode();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Xref{");
		sb.append("id='").append(id).append('\'');
		sb.append(", dbName='").append(dbName).append('\'');
		sb.append(", dbDisplayName='").append(dbDisplayName).append('\'');
		sb.append(", description='").append(description).append('\'');
		sb.append('}');
		return sb.toString();
	}

	public String getId() {
		return id;
	}

	public Xref setId(String id) {
		this.id = id;
		return this;
	}

	public String getDbName() {
		return dbName;
	}

	public Xref setDbName(String dbName) {
		this.dbName = dbName;
		return this;
	}

	public String getDbDisplayName() {
		return dbDisplayName;
	}

	public Xref setDbDisplayName(String dbDisplayName) {
		this.dbDisplayName = dbDisplayName;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Xref setDescription(String description) {
		this.description = description;
		return this;
	}
}


